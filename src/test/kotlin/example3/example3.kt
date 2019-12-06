package example3

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.*

// ENTITIES

@Entity
@Table(name = "posts", uniqueConstraints = [UniqueConstraint(name = "uk_title", columnNames = ["title"])])
class Post(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var id: Long? = null,

        @Version
        @Column(name = "version")
        var version: Long? = null,

        @Column(name = "title", unique = true)
        var title: String? = null
)

// REPOSITORIES


@Repository
interface PostRepository : PagingAndSortingRepository<Post, Long> {

    @Query(value = "select p.title from Post p where p.id = :postId")
    fun getTitle(@Param("postId") postId: Long): String

    @Modifying
    @Query(value = "update Post p  set p.title = :title where p.id = :postId")
    fun updateTitle(@Param("postId") postId: Long, @Param("title") title: String)
}

// SERVICES
//
//interface PostService {
//    fun rename(postId: Long, newName: String)
//}
//
//@Service
//open class PostServiceImpl(
//        @Autowired
//        val postRepository: PostRepository
//) : PostService {
//
//    @Transactional
//    override fun rename(postId: Long, newName: String) {
//        var post = postRepository.findById(postId).orElseThrow { IllegalArgumentException() }
//
//        try {
//            post.title = newName
//            post =  postRepository.save(post)
//        } catch (e: Exception) {
//            post.title = post.title + "_2"
//            post =  postRepository.save(post)
//        }
//    }
//}


interface PostService {
    fun rename(postId: Long, newName: String)
    fun rename(post: Post, newName: String)
}

@Service
open class PostServiceImpl(
        @Autowired
        val postRepository: PostRepository
) : PostService {
    @Autowired
    lateinit var postService: PostService

    @Transactional(transactionManager = "transactionManager")
    override fun rename(postId: Long, newName: String) {
        var post = postRepository.findOne(postId)

        postRepository.updateTitle(postId, "tmp")

        try {
            postRepository.updateTitle(postId, newName)
//            postService.rename(post, newName)
        } catch (e: Exception) {
            //according to my understanding this changes too should be rolled back because there is exception
            //thrown by postService.rename(post, newName) 2 lines before
//            postRepository.updateTitle(postId, post.title + "_2")
            postService.rename(post, post.title + "_2")
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    override fun rename(post: Post, newName: String) {
        post.title = newName
        postRepository.save(post)
//        postRepository.updateTitle(post.id!!, newName)
    }

}


// CONFIGURATION

@EnableJpaRepositories(basePackages = ["example3"])
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = ["example3"])
open class FrameworkApplication {

    @Bean(name = ["transactionManager"])
    open fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = emf
        transactionManager.isNestedTransactionAllowed = true

        return transactionManager
    }
}


// TESTS


@DisplayName("Testing transaction")
@SpringJUnitJupiterConfig(classes = [FrameworkApplication::class])
class ToMuchRolledBack(
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
    fun testRename() {
        //given
        val post = Post()
        val firstTitle = "title"
        post.title = firstTitle

        var post2 = Post()
        val secondTitle = "second_title"
        post2.title = secondTitle

        postRepository.save(post)
        post2 = postRepository.save(post2)
        val postId = post2.id!!

        //then
        postService.rename(postId, firstTitle)

        //then
        Assertions.assertEquals("title", postRepository.getTitle(postId))
    }

}
