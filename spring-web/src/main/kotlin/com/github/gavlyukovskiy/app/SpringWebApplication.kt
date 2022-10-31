package com.github.gavlyukovskiy.app

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.coyote.ProtocolHandler
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.LongAdder
import javax.sql.DataSource
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes


fun main(args: Array<String>) {
    runApplication<BenchmarkSpringWebApplication>(*args)
}

@SpringBootApplication
@EnableScheduling
class BenchmarkSpringWebApplication {
    @Bean
    @Profile("dynamodb")
    fun dynamoDbClient(): DynamoDbClient = DynamoDbClient.builder().build()

    @Bean
    @Profile("loom")
    fun protocolHandlerVirtualThreadExecutorCustomizer(): TomcatProtocolHandlerCustomizer<*>? {
        return TomcatProtocolHandlerCustomizer { protocolHandler: ProtocolHandler ->
            protocolHandler.executor = Executors.newVirtualThreadPerTaskExecutor()
        }
    }

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    @Profile("loom")
    fun asyncTaskExecutor(): AsyncTaskExecutor? {
        return TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())
    }
}

sealed interface Repository {
    fun getWorld(id: Int): World?
}

@Component
@Profile("postgres && !jooq")
class PostgresRepository(val dataSource: DataSource) : Repository {
    override fun getWorld(id: Int): World? {
        return dataSource.connection.use { connection ->
            connection.prepareStatement("select id, message from worlds where id = ?").use { preparedStatement ->
                preparedStatement.setInt(1, id)
                preparedStatement.executeQuery().use { resultSet ->
                    resultSet.next()
                    World(resultSet.getInt(1), resultSet.getString(2))
                }
            }
        }
    }
}

@Component
@Profile("postgres && jooq")
class JooqRepository(val context: DSLContext) : Repository {
    override fun getWorld(id: Int): World? {
        return context.select(field("id"), field("message"))
            .from(table("worlds"))
            .where(field("id").eq(id))
            .fetchOneInto(World::class.java)
    }
}

@Component
@Profile("dynamodb")
class DynamoDbRepository(val dynamoDbClient: DynamoDbClient) : Repository {
    override fun getWorld(id: Int): World? {
        return dynamoDbClient.getItem {
            it.tableName("worlds")
            it.attributesToGet("id", "message")
            it.key(mapOf("id" to AttributeValue.fromN(id.toString())))
        }
            .item()
            ?.let { World(it["id"]!!.n().toInt(), it["message"]!!.s()) }
    }
}

@Component
class IoService {

    val client = OkHttpClient()

    fun copyFiles(chunkSize: Int = 1024, chunks: Int = 50): Int {
        val file = Files.createTempFile("benchmark_cp", ".data")
        val zeros = (1..chunkSize).map { 0.toByte() }.toList().toByteArray()
        (1..chunks).forEach {
            file.writeBytes(zeros, StandardOpenOption.DSYNC)
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

@RestController
class Controller(val repository: Repository, val ioService: IoService) {
    companion object {
        val logger = LoggerFactory.getLogger(Controller::class.java)
        val requestCount = LongAdder()
        var previousRequestCount: Long = 0
    }

    @GetMapping("/hello")
    fun hello(): World? = record { World(0, "Hello world") }

    @GetMapping("/db")
    fun db(): World? = record { repository.getWorld(ThreadLocalRandom.current().nextInt(1, 10001)) }

    @GetMapping("/cp")
    fun cp(): Int = record { ioService.copyFiles() }

    @GetMapping("/download")
    fun download(): Int = record { ioService.downloadFile() }

    private fun <T> record(block: () -> T) = block().also { requestCount.increment() }

    @Scheduled(fixedDelay = 5000)
    private fun log() {
        val count = requestCount.sum()
        if (count > 0) {
            logger.info("Throughput[5s]: ${(count - previousRequestCount) / 5}req/s (total: $count)")
            previousRequestCount = count
        }
    }
}

data class World(
    val id: Int,
    val message: String,
)
