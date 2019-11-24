package example2

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional
import javax.persistence.*

// EXCEPTIONS
class MyCustomException(message: String) : Exception(message)


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

        @Column(name = "text")
        var text: String? = null,

        @Column(name = "score")
        var score: Int? = null
)

@Entity
@Table(name = "comments")
class Comment(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var id: Long? = null,

        @Version
        @Column(name = "version")
        var version: Long? = null,

        @Column(name = "text")
        var text: String? = null,

        @JoinColumn(name = "post_id")
        @ManyToOne
        var post: Post? = null
)



// REPOSITORIES


@Repository
interface PostRepository : PagingAndSortingRepository<Post, Long> {

    @Modifying
    @Query(value = "update Post p set p.score = 0 where p.id = :postId")
    fun resetScore(@Param("postId") postId: Long)

    @Modifying
    @Query(value = "update Post p set p.score = p.score + 1 where p.id = :postId")
    fun upvote(@Param("postId") postId: Long)

    @Query(value = "select p.score from Post p where p.id = :postId")
    fun getScore(@Param("postId") postId: Long): Int?
}


// SERVICES

interface PostService {
    @Throws(MyCustomException::class)
    fun doUpvote(postId: Long)

    @Throws(MyCustomException::class)
    fun upvote(postId: Long)
}

@Service
open class PostServiceImpl(
        @Autowired
        val postRepository: PostRepository
) : PostService {
    @Throws(MyCustomException::class)
    override fun doUpvote(postId: Long) {
        upvote(postId)
    }

    @Throws(MyCustomException::class)
    @Transactional
    override fun upvote(postId: Long) {
        postRepository.upvote(postId)

        throw MyCustomException("Something wrong happend!")
    }

}

// CONFIGURATION

@EnableJpaRepositories(basePackages = ["example2"])
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = ["example2"])
open class FrameworkApplication

fun main(args: Array<String>) {
    runApplication<FrameworkApplication>(*args)
}


// TESTS

@SpringJUnitConfig(classes = [FrameworkApplication::class])
class TestClass2(
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
    fun testUpvote() {
        //given
        var post = Post()
        post.score = 0
        post = postRepository.save(post)
        val postId = post.id!!

        //then
        try {
            postService.upvote(postId)
        } catch (e: MyCustomException) {
            //expected - should roll back transaction
        }

        //then
        Assertions.assertEquals(0, postRepository.getScore(postId))
    }

}
