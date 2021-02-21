package io.hatis

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT

class PgResource: QuarkusTestResourceLifecycleManager {
    companion object {
        val container = PostgreSQLContainer<Nothing>("postgres:11-alpine").apply {
            withDatabaseName("testdb")
            withUsername("postgres")
            withPassword("postgres")
        }
    }

    override fun start(): MutableMap<String, String> {
        container.start()
        val host = "${container.host}:${container.getMappedPort(POSTGRESQL_PORT)}"
        return mutableMapOf(
            "quarkus.datasource.jdbc.url" to "jdbc:postgresql://${host}/testdb",
            "quarkus.datasource.reactive.url" to "postgresql://${host}/testdb"
        )
    }

    override fun stop() {
        container.stop()
    }
}