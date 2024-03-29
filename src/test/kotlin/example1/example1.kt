package example1

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional
import javax.persistence.*

// EXCEPTIONS
class MyCustomException(message: String) : RuntimeException(message)


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

    @Modifying
    @Query(value = "update Post p set p.score = p.score + 1 where p.id = :postId")
    fun upvote(@Param("postId") postId: Long)

    @Query(value = "select p.score from Post p where p.id = :postId")
    fun getScore(@Param("postId") postId: Long): Int?
}


// SERVICES

interface PostService {
    fun upvote(postId: Long)
}

@Service
open class PostServiceImpl(
        @Autowired
        val postRepository: PostRepository
) : PostService {

    @Transactional(rollbackFor = [MyCustomException::class])
    override fun upvote(postId: Long) {
        postRepository.upvote(postId)

        throw MyCustomException("Something wrong happend!")
    }

}

// CONFIGURATION

@EnableJpaRepositories(basePackages = ["example1"])
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = ["example1"])
open class FrameworkApplication


// TESTS

@SpringJUnitJupiterConfig(classes = [FrameworkApplication::class])
@DisplayName("Rollback test")
class NoRollbackForException(
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
    @DisplayName("Should rollback after exception")
    fun testUpvote() {
        //given
        var post = Post()
        post = postRepository.save(post)
        val postId = post.id!!

        //then
        Assertions.assertThrows(Exception::class.java) {
            postService.upvote(postId)
        }

        //then
        Assertions.assertEquals(0, postRepository.getScore(postId))
    }

}
