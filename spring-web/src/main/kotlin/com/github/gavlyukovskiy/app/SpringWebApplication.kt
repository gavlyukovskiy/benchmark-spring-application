package com.github.gavlyukovskiy.app

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.coyote.ProtocolHandler
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAdder
import javax.sql.DataSource
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

fun main(args: Array<String>) {
    runApplication<SpringWebApplication>(*args)
}

@SpringBootApplication
@EnableScheduling
class SpringWebApplication {

    @Bean
    @Profile("loom")
    @ConditionalOnClass(name = [ "org.apache.coyote.ProtocolHandler" ])
    @Suppress("ObjectLiteralToLambda") // not compiling as lambda to avoid ClassNotFoundException
    fun tomcatLoomExecutor() = object : TomcatProtocolHandlerCustomizer<ProtocolHandler> {
        override fun customize(protocolHandler: ProtocolHandler) {
            protocolHandler.executor = Executors.newVirtualThreadPerTaskExecutor()
        }
    }

    @Bean
    @Profile("loom")
    @ConditionalOnClass(name = [ "org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory" ])
    @Suppress("ObjectLiteralToLambda") // not compiling as lambda to avoid ClassNotFoundException
    fun jettyLoomExecutor() =
        object : WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory> {
            override fun customize(factory: ConfigurableJettyWebServerFactory) {
                val threadPool = QueuedThreadPool()
                threadPool.isUseVirtualThreads = true
                factory.setThreadPool(threadPool)
            }
        }

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    @Profile("loom")
    fun asyncTaskExecutor() = TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())
}

@RestController
class Controller(val repository: Repository, val ioService: IoService) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(Controller::class.java)
        val requestCount = LongAdder()
        val previousRequestCount = AtomicLong(0)
        val concurrency = AtomicLong(0)
        val maxConcurrency = AtomicLong(0)
    }

    @GetMapping("/hello")
    fun hello(): World? = World(0, "Hello world").also { logger.info("Hello! Virtual thread: ${Thread.currentThread().isVirtual}") }

    @GetMapping("/db")
    fun db(): World? = record { repository.getWorld(ThreadLocalRandom.current().nextInt(1, 10001)) }

    @GetMapping("/cp")
    fun cp(): Int = record { ioService.copyFiles() }

    @GetMapping("/download")
    fun download(): Int = record { ioService.downloadFile() }

    private fun <T> record(block: () -> T): T {
        concurrency.incrementAndGet().let { sample -> maxConcurrency.updateAndGet { max -> max.coerceAtLeast(sample) } }
        requestCount.increment()
        try {
            return block()
        } finally {
            concurrency.decrementAndGet()
        }
    }

    @Scheduled(fixedDelay = 5000)
    private fun log() {
        val count = requestCount.sum()
        if (count > 0) {
            val previousRequestCount = previousRequestCount.get().also { previousRequestCount.set(count) }
            val maxConcurrency = maxConcurrency.get().also { maxConcurrency.set(concurrency.get()) }
            logger.info("""
                ---
                throughput[5s]: ${(count - previousRequestCount) / 5}req/s
                concurrency.max[5s]: $maxConcurrency
                total: $count
            """.trimIndent())
        }
    }
}

data class World(
    val id: Int,
    val message: String
)

@Component
class Repository(val dataSource: DataSource) {
    fun getWorld(id: Int): World? =
        dataSource.connection.use { connection ->
            connection.prepareStatement("select id, message from worlds where id = ?").use { preparedStatement ->
                preparedStatement.setInt(1, id)
                preparedStatement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        World(resultSet.getInt(1), resultSet.getString(2))
                    } else {
                        null
                    }
                }
            }
        }
}

@Component
class IoService(val client: OkHttpClient = OkHttpClient()) {
    fun copyFiles(chunkSize: Int = 1024, chunks: Int = 50): Int {
        val file = Files.createTempFile("benchmark_cp", ".data")
        val zeros = (1..chunkSize).map { 0.toByte() }.toList().toByteArray()
        repeat(chunks) {
            file.writeBytes(zeros, StandardOpenOption.APPEND, StandardOpenOption.DSYNC)
        }
        return file.readBytes().size.also {
            file.deleteIfExists()
        }
    }

    fun downloadFile(): Int {
        val request = Request.Builder().url("https://www.briandunning.com/sample-data/us-500.zip").build()
        val data1 = client.newCall(request)
            .execute()
            .use { response -> response.body!!.string() }
        val data2 = client.newCall(request)
            .execute()
            .use { response -> response.body!!.string() }
        return data1.length + data2.length
    }
}
