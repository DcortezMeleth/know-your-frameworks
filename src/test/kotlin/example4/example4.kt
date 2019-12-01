package example4

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

    @Transactional
    override fun upvote(postId: Long) {
        postRepository.upvote(postId)
    }

}

// CONFIGURATION

@EnableJpaRepositories(basePackages = ["example4"])
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = ["example4"])
open class FrameworkApplication


// TESTS

@SpringJUnitJupiterConfig(classes = [FrameworkApplication::class])
@DisplayName("Rollback test")
class InjectImpl(
        @Autowired
        val postService: PostServiceImpl,

        @Autowired
        val postRepository: PostRepository
) {

    @Test
    @DisplayName("Should upvote")
    fun testUpvote() {
        var post = Post()
        post = postRepository.save(post)
        val postId = post.id!!

        //given
        postService.upvote(postId)

        //then
        Assertions.assertEquals(1, postRepository.getScore(postId))
    }

}
