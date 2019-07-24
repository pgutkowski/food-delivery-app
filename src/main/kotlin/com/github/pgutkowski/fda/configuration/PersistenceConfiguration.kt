package com.github.pgutkowski.fda.configuration

import com.github.pgutkowski.fda.SuspendableDatabase
import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import javax.sql.DataSource

@Configuration
class PersistenceConfiguration {

    @Bean
    fun exposedDatabase(dataSource: DataSource): SuspendableDatabase {
        return SuspendableDatabase(Database.connect(dataSource), Executors.newCachedThreadPool())
    }

}