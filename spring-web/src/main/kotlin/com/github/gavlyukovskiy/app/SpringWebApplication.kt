package com.github.gavlyukovskiy.app

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
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.LongAdder
import javax.sql.DataSource


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

@RestController
class Controller(val repository: Repository) {
    companion object {
        val logger = LoggerFactory.getLogger(Controller::class.java)
        val requestCount = LongAdder()
        var previousRequestCount: Long = 0
    }
    @GetMapping("/db")
    fun db(): World? = repository.getWorld(ThreadLocalRandom.current().nextInt(1, 10001))
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
