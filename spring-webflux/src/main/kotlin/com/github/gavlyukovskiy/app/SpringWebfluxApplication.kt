package com.github.gavlyukovskiy.app

import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAdder
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

fun main(args: Array<String>) {
    runApplication<SpringWebfluxApplication>(*args)
}

@SpringBootApplication
@EnableScheduling
class SpringWebfluxApplication

@RestController
class Controller(val repository: Repository, val ioService: IoService) {
    companion object {
        val logger = LoggerFactory.getLogger(Controller::class.java)
        val requestCount = LongAdder()
        val previousRequestCount = AtomicLong(0)
        val concurrency = AtomicLong(0)
        val maxConcurrency = AtomicLong(0)
    }

    @GetMapping("/hello")
    suspend fun hello(): World? = repository.getWorld(1).also { logger.info("Hello!") }

    @GetMapping("/db")
    suspend fun db(): World? = record { repository.getWorld(ThreadLocalRandom.current().nextInt(1, 10001)) }

    @GetMapping("/cp")
    suspend fun cp(): Int = record { ioService.copyFiles() }

    @GetMapping("/download")
    suspend fun download(): Int = record { ioService.downloadFile() }

    private suspend fun <T> record(block: suspend () -> T): T {
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
class Repository(val connectionFactory: ConnectionFactory) {
    suspend fun getWorld(id: Int): World? =
        Mono.usingWhen(
            connectionFactory.create(),
            { connection ->
                connection
                    .createStatement("select id, message from worlds where id = $1")
                    .bind("$1", 42)
                    .execute()
                    .toMono()
                    .flatMap { result ->
                        result.map { row, _ ->
                            World(
                                row.get("id", Integer::class.java)!!.toInt(),
                                row.get("message", String::class.java)!!
                            )
                        }
                            .toMono()
                    }
            },
            Connection::close
        )
            .awaitFirstOrNull()
}

@Component
class IoService(
    val client: OkHttpClient = OkHttpClient(),
    val zeros: ByteArray = (1..32 * 1024).map { 0.toByte() }.toList().toByteArray()
) {
    suspend fun copyFiles(chunks: Int = 32): Int = withContext(Dispatchers.IO) {
        val file = Files.createTempFile("benchmark", ".data")
        repeat(chunks) {
            file.writeBytes(zeros, StandardOpenOption.APPEND, StandardOpenOption.DSYNC)
        }
        file.readBytes().size.also {
            file.deleteIfExists()
        }
    }

    suspend fun downloadFile(): Int = withContext(Dispatchers.IO) {
        val request = Request.Builder().url("https://www.briandunning.com/sample-data/us-500.zip").build()
        val data1 = client.newCall(request)
            .execute()
            .use { response -> response.body!!.string() }
        val data2 = client.newCall(request)
            .execute()
            .use { response -> response.body!!.string() }
        data1.length + data2.length
    }
}
