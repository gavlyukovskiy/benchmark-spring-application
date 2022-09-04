package com.github.gavlyukovskiy.bootstrap

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.impl.StaticLoggerBinder
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import kotlin.random.Random

const val loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque tincidunt feugiat mauris, molestie varius odio consequat in. Phasellus sit amet lectus est. Vivamus facilisis neque commodo, cursus tortor at, dictum sapien. Maecenas cursus id arcu eu vehicula. Vestibulum dignissim ligula eu mattis hendrerit. Nunc interdum purus in nulla imperdiet consectetur. Nunc ornare sagittis odio. Mauris id faucibus purus, eget volutpat est. Donec consectetur semper pretium. Nunc iaculis felis ligula, quis volutpat dui."

val logger: Logger = LoggerFactory.getLogger("Bootstrap")

fun main(args: Array<String>) {
    configureLogging()

    val random = Random(42)

    if (args.contains("postgres")) {
        bootstrapPostgres(random).apply {
            require(this == 2558795) { "Unexpected size, different random seed?" }
        }
    }

    if (args.contains("dynamodb")) {
        bootstrapDynamoDb(random).apply {
            require(this == 2558795) { "Unexpected size, different random seed?" }
        }
    }
}

private fun configureLogging() {
    val loggerContext = StaticLoggerBinder.getSingleton().loggerFactory as LoggerContext
    val logger = loggerContext.getLogger("root")
    logger.level = Level.INFO
}

private fun bootstrapPostgres(random: Random): Int {
    var totalSize = 0
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/hello_world"
        username = "postgres"
        password = "postgres"
        maximumPoolSize = 1
    }
    val dataSource = HikariDataSource(config)

    val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    jdbcTemplate.update(
        """
            drop table if exists worlds;
        """.trimIndent(), mapOf<String, Any>()
    )

    jdbcTemplate.update(
        """
            create table worlds(
              id      integer      not null,
              message varchar(512) not null,
              primary key  (id)
            );
        """.trimIndent(), mapOf<String, Any>()
    )

    for (i in 1..10000) {
        val startIndex = random.nextInt(0, loremIpsum.length / 2)
        val endIndex = random.nextInt(loremIpsum.length / 2, loremIpsum.length)
        totalSize += endIndex - startIndex
        jdbcTemplate.update(
            """
                insert into worlds (id, message) values (:id, :message);
            """.trimIndent(), mapOf("id" to i, "message" to loremIpsum.substring(startIndex, endIndex))
        )
        if (i % 500 == 0) {
            logger.info("Inserted $i entries, size: $totalSize")
        }
    }

    dataSource.close()
    return totalSize
}

private fun bootstrapDynamoDb(random: Random): Int {
    var totalSize = 0
    val dynamoDbClient = DynamoDbClient.builder().build()

    dynamoDbClient.deleteTable {
        it.tableName("worlds")
    }
    dynamoDbClient.createTable {
        it.tableName("worlds")
        it.keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
        it.attributeDefinitions(listOf(
            AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.N).build(),
            AttributeDefinition.builder().attributeName("message").attributeType(ScalarAttributeType.S).build()
        ))
    }

    for (i in 1..10000) {
        val startIndex = random.nextInt(0, loremIpsum.length / 2)
        val endIndex = random.nextInt(loremIpsum.length / 2, loremIpsum.length)
        totalSize += endIndex - startIndex
        dynamoDbClient.putItem {
            it.tableName("world")
            it.item(mapOf(
                "id" to AttributeValue.fromN(i.toString()),
                "message" to AttributeValue.fromS(loremIpsum.substring(startIndex, endIndex))
            ))
        }
        if (i % 500 == 0) {
            logger.info("Inserted $i entries, size: $totalSize")
        }
    }

    return totalSize
}