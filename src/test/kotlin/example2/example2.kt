package example2

//import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.AdviceMode
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig
import org.springframework.transaction.annotation.EnableTransactionManagement


// SERVICES


@Service
open class Timer(
        var lock: Boolean = false
) {

    @Scheduled(fixedDelay = 1000, initialDelay = 2000)
    fun scheduledOne() {
        var counter = 1
        while (!lock) {
            println("Scheduler one waiting for ${counter++}th time!")
            Thread.sleep(1000);
        }

        this.lock = true;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 5000)
    fun scheduledTwo() {
        this.lock = true;
    }
}

// CONFIGURATION

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["example2"])
open class FrameworkApplication


// TESTS

@DisplayName("Testing schedulers")
@SpringJUnitJupiterConfig(classes = [FrameworkApplication::class])
class Schedulers(
        @Autowired
        private val timer: Timer
) {

    @Test
    @DisplayName("Schedulers should work separately")
    fun waitForStateChanges() {
        while (!timer.lock) {
            Thread.sleep(1000);
        }
    }

}
