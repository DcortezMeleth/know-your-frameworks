package example2

//import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig


// SERVICES


@Service
open class Timer {

    @Scheduled(fixedDelay = 1000, initialDelay = 2000)
    fun scheduledOne() {
        var counter = 1
        while (true) {
            println("Scheduler one waiting for ${counter++}th time!")
            Thread.sleep(1000);
        }
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 5000)
    fun scheduledTwo() {
        var counter = 1
        while (true) {
            println("Scheduler two waiting for ${counter++}th time!")
            Thread.sleep(1000);
        }
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
        while (true) {
            Thread.sleep(1000);
        }
    }

}
