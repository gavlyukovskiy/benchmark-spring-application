package com.github.gavlyukovskiy.app

import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.LongAdder

fun main(args: Array<String>) {
    runApplication<BenchmarkSpringWebfluxApplication>(*args)
}

@SpringBootApplication
@EnableScheduling
class BenchmarkSpringWebfluxApplication {
    @Bean
    @Profile("dynamodb")
    fun dynamoDbAsyncClient() = DynamoDbAsyncClient.builder().build()

    @Bean
    @Profile("postgres && jooq")
    fun dslContext(connectionFactory: ConnectionFactory) = DSL.using(connectionFactory)
}

sealed interface Repository {
    suspend fun getWorld(id: Int): World?
}

@Component
@Profile("postgres && !jooq")
class PostgresRepository(val connectionFactory: ConnectionFactory) : Repository {
    override suspend fun getWorld(id: Int): World? {
        return Mono.usingWhen(
            connectionFactory.create(),
            { connection ->
                connection
                    .createStatement("select id, message from worlds where id = $1")
                    .bind("$1", 42)
                    .execute()
                    .toMono()
                    .flatMap { result ->
                        result.map { row, _ ->
                            World(row.get("id", Integer::class.java)!!.toInt(), row.get("message", String::class.java)!!)
                        }
                            .toMono()
                    }
            },
            Connection::close
        ).awaitFirstOrNull()
    }
}

@Component
@Profile("postgres && jooq")
class JooqRepository(val context: DSLContext) : Repository {
    override suspend fun getWorld(id: Int): World? {
        return Mono.from(
            context.select(DSL.field("id"), DSL.field("message"))
                .from(DSL.table("worlds"))
                .where(DSL.field("id").eq(id))
        )
            .map { it.into(World::class.java) }
            .awaitFirstOrNull()
    }
}

@Component
@Profile("dynamodb")
class DynamoDbRepository(val dynamoDbAsyncClient: DynamoDbAsyncClient) : Repository {
    override suspend fun getWorld(id: Int): World? {
        return dynamoDbAsyncClient.getItem {
            it.tableName("worlds")
            it.attributesToGet("id", "message")
            it.key(mapOf("id" to AttributeValue.fromN(id.toString())))
        }
            .await()
            .item()
            ?.let { World(it["id"]!!.n().toInt(), it["message"]!!.s()) }
    }
}

@RestController
class Controller(val repository: Repository) {
    companion object {
        val logger = LoggerFactory.getLogger(Controller::class.java)
        val requestCount = LongAdder()
        var previousRequestCount: Long = 0
    }

    @GetMapping("/db")
    suspend fun db(): World? = repository.getWorld(ThreadLocalRandom.current().nextInt(1, 10001))
        .also { requestCount.increment() }

    @GetMapping("/hello")
    fun hello(): World? = World(0, "Hello world")
        .also { requestCount.increment() }

    @Scheduled(fixedDelay = 5000)
    fun log() {
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
