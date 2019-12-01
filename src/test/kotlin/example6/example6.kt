package example6

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Scope
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig
import org.springframework.transaction.annotation.EnableTransactionManagement

// SERVICES


@Service
@Scope("prototype")
open class PrototypeService(
        private var counter: Int = 0
) {
    fun inc(): Int {
        return this.counter++
    }
}

@Service
open class SingletonService(
        @Autowired
        private val prototypeService: PrototypeService
//        @Autowired
//        private val applicationContext: ApplicationContext
) {
    fun incPrototype(): Int {
        return prototypeService.inc()
//        return applicationContext.getBean(PrototypeService::class.java).inc()
    }
}


// CONFIGURATION

@SpringBootApplication(scanBasePackages = ["example6"])
open class FrameworkApplication


// TESTS

@SpringJUnitJupiterConfig(classes = [FrameworkApplication::class])
class PrototypeAndSingleton(
        @Autowired
        val singletonService: SingletonService
) {

    @Test
    fun testUpvote() {
        val i1 = singletonService.incPrototype()
        val i2 = singletonService.incPrototype()

        //then
        assertAll(
                { Assertions.assertEquals(0, i1, "First prototype should return 0") },
                { Assertions.assertEquals(0, i2, "Second prototype should return 0") }
        )
    }

}
