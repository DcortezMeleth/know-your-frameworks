package example2

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.AdviceMode
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig
import org.springframework.transaction.annotation.EnableTransactionManagement
//import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.persistence.*
import javax.transaction.Transactional

// ENTITIES

@Entity
@Table(name = "posts")
class Post(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var id: Long? = null,

        @Version
        @Column(name = "version")
        var version: Long? = null,

        @Column(name = "score")
        var score: Int = 0
)

// REPOSITORIES


@Repository
interface PostRepository : PagingAndSortingRepository<Post, Long> {

    @Transactional
    @Modifying
    @Query(value = "update Post p set p.score = p.score + 1 where p.id = :postId")
    fun upvote(@Param("postId") postId: Long)
}


// SERVICES

interface PostService {
    @Transactional
    fun upvote(postId: Long): Boolean
}

@Service
open class PostServiceImpl(
        @Autowired
        val postRepository: PostRepository
) : PostService {

    override fun upvote(postId: Long): Boolean {
        postRepository.upvote(postId)

        return TransactionSynchronizationManager.isActualTransactionActive()
    }

}

// CONFIGURATION

@EnableJpaRepositories(basePackages = ["example2"])
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = ["example2"])
open class FrameworkApplication


// TESTS

@DisplayName("Testing transaction")
@SpringJUnitJupiterConfig(classes = [FrameworkApplication::class])
class TransactionalAnnotation(
        @Autowired
        val postService: PostService,

        @Autowired
        val postRepository: PostRepository
) {

    @AfterEach
    fun cleanUp() {
        postRepository.deleteAll()
    }

    @Test
    @DisplayName("Transaction should be present")
    fun testUpvote() {
        //given
        var post = Post()
        post = postRepository.save(post)
        val postId = post.id!!

        //then
        val transactionActive = postService.upvote(postId)

        //then
        Assertions.assertTrue(transactionActive)
    }

}
