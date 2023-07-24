package com.jdum.projector.database

import com.jdum.projector.database.model.User
import com.jdum.projector.database.repository.UserRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.*


@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
class DatabaseApplication(
    @Value("\${application.db.total-users}")
    private val totalUsers: Int,
    @Value("\${application.db.insert-number}")
    private val insertNumber: Int,

    private val repository: UserRepository

) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(javaClass)
    private val zoneId = ZoneId.of("America/Edmonton")

    override fun run(vararg args: String?) {
        generateScript()
//        insert()
    }

    fun generateScript() {
        val iterations = (0 until (totalUsers)).asSequence()
        val users = iterations.map {
            User(
                id = it,
                firstName = "firstName${it}",
                dateOfBirth = getDate(zoneId),
            )
        }.toList()
        File("data.sql").printWriter().use { out ->
            out.println("insert into users (id, first_name, date_of_birth) values")
        }
        File("data.sql").printWriter().use { out ->
            out.println("insert into users (id, first_name, date_of_birth) values")
            users.forEachIndexed { index, user ->
                if (index != users.size - 1) out.println("$user,")
                else out.println("$user;")
            }
        }
    }

    fun insert() {
        log.info("Create $insertNumber users")
        val executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        val iterations = (totalUsers + insertNumber*2 + 1 until (insertNumber * 3 + totalUsers + 1)).asSequence()
        val users = iterations.map {
            User(
                id = it,
                firstName = "firstName${it}",
                dateOfBirth = getDate(zoneId),
            )
        }.toList()
        log.info("Insert $insertNumber users")
        val start = Instant.now()

        val tasks = users.chunked(Runtime.getRuntime().availableProcessors()).map { userList ->
            Callable { repository.saveAll(userList) }
        }.toList()
        executors.invokeAll(tasks)

        Duration.between(start, Instant.now())
        awaitTerminationAfterShutdown(executors)
        log.info("Insert duration: ${Duration.between(start, Instant.now()).toMillis()}")
    }

    fun awaitTerminationAfterShutdown(threadPool: ExecutorService) {
        threadPool.shutdown()
        try {
            if (!threadPool.awaitTermination(180, TimeUnit.SECONDS)) {
                threadPool.shutdownNow()
            }
        } catch (ex: InterruptedException) {
            threadPool.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    private fun getDate(zoneId: ZoneId?) =
        Instant.ofEpochSecond(ThreadLocalRandom.current().nextInt().toLong()).atZone(zoneId).toLocalDate()
}

fun main(args: Array<String>) {
    runApplication<DatabaseApplication>(*args)
}

