package example5

import org.hibernate.SessionFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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
        var version: Long? = null
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

        @JoinColumn(name = "post_id")
        @ManyToOne(fetch = FetchType.LAZY)
        var post: Post? = null
)


// REPOSITORIES

@Repository
interface PostRepository : PagingAndSortingRepository<Post, Long>

@Repository
interface CommentRepository : PagingAndSortingRepository<Comment, Long> {

    @Modifying
    @Query("delete from Comment c where c.post = :post")
    fun deleteByPost(@Param("post") post: Post)
}


// SERVICES

interface PostService {

    fun delete(post: Post)
}

@Service
open class PostServiceImpl(
        @Autowired
        val postRepository: PostRepository,

        @Autowired
        val commentRepository: CommentRepository
) : PostService {
    @Transactional
    override fun delete(post: Post) {
        commentRepository.deleteByPost(post)
        postRepository.delete(post)
    }

}

// CONFIGURATION

@EnableJpaRepositories(basePackages = ["example5"])
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = ["example5"])
open class FrameworkApplication


// TESTS

@SpringJUnitJupiterConfig(classes = [FrameworkApplication::class])
class CountQueries(
        @Autowired
        val postService: PostService,

        @Autowired
        val postRepository: PostRepository,

        @Autowired
        val commentRepository: CommentRepository,

        @Autowired
        val emFactory: EntityManagerFactory
) {

    @BeforeEach
    fun setUp() {
        System.setProperty("hibernate.generate_statistics", "true")
    }

    @AfterEach
    fun cleanUp() {
        commentRepository.deleteAll()
        postRepository.deleteAll()
    }

    @Test
    fun testDelete() {
        //given
        var post = Post()
        post = postRepository.save(post)
        val comments = mutableListOf<Comment>()
        for (i in 1..10000) {
            val comment = Comment()
            comment.post = post
            comments.add(comment)
        }
        commentRepository.save(comments)

        val sessionFactory = emFactory.unwrap(SessionFactory::class.java)
        val statistics = sessionFactory.statistics

        //then
        statistics.clear()
        val start = System.currentTimeMillis()
        postService.delete(post)
        val time = (System.currentTimeMillis() - start) / 1000
        println("Elapsed time: $time")

        val executedQueryCount = statistics.entityDeleteCount


        //then
        assertAll(
                { assertEquals(0, postRepository.count()) },
                { assertEquals(0, commentRepository.count()) },
                { assertEquals(2, executedQueryCount) }
        )
    }

}
