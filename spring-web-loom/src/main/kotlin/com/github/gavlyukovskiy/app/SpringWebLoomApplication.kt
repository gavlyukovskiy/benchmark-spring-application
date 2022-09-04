package com.github.gavlyukovskiy.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.concurrent.ThreadLocalRandom

fun main(args: Array<String>) {
    runApplication<BenchmarkSpringWebLoomApplication>(*args)
}

@SpringBootApplication
class BenchmarkSpringWebLoomApplication {
    @Bean
    @Profile("dynamodb")
    fun dynamoDbClient(): DynamoDbClient = DynamoDbClient.builder().build()
}

sealed interface Repository {
    fun getWorld(id: Int): World?
}

@Component
@Profile("postgres")
class PostgresRepository(val jdbcTemplate: NamedParameterJdbcTemplate) : Repository {
    override fun getWorld(id: Int): World? =
        jdbcTemplate.queryForObject("select id, message from worlds where id = :id", mapOf("id" to id)) { rs, _ ->
            World(rs.getInt(1), rs.getString(2))
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
    @GetMapping("/db")
    fun db(): World? {
        return repository.getWorld(ThreadLocalRandom.current().nextInt(1, 10001))
    }
}

data class World(
    val id: Int,
    val message: String,
)
