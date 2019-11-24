//package example1
//
//import com.hexagon.presentations.framework.ResourceNotFoundException
//import com.hexagon.presentations.framework.entities.Comment
//import com.hexagon.presentations.framework.entities.Post
//import com.hexagon.presentations.framework.exceptions.MyCustomException
//import com.hexagon.presentations.framework.repositories.PostRepository
//import com.hexagon.presentations.framework.services.PostService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.boot.runApplication
//import org.springframework.data.jpa.repository.Modifying
//import org.springframework.data.jpa.repository.Query
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories
//import org.springframework.data.repository.PagingAndSortingRepository
//import org.springframework.data.repository.query.Param
//import org.springframework.stereotype.Repository
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.EnableTransactionManagement
//import org.springframework.transaction.annotation.Transactional
//import javax.persistence.*
//
//// EXCEPTIONS
//class MyCustomException(message: String) : Exception(message)
//
//
//// ENTITIES
//
//@Entity
//@Table(name = "comments")
//class Comment(
//        @Id
//        @Column(name = "id")
//        @GeneratedValue(strategy = GenerationType.SEQUENCE)
//        var id: Long? = null,
//
//        @Version
//        @Column(name = "version")
//        var version: Long? = null,
//
//        @Column(name = "text")
//        var text: String? = null,
//
//        @JoinColumn(name = "post_id")
//        @ManyToOne
//        var post: Post? = null
//)
//
//
//@Entity
//@Table(name = "posts")
//class Post(
//        @Id
//        @Column(name = "id")
//        @GeneratedValue(strategy = GenerationType.SEQUENCE)
//        val id: Long? = null,
//
//        @Version
//        @Column(name = "version")
//        val version: Long? = null,
//
//        @Column(name = "text")
//        val text: String? = null,
//
//        @Column(name = "score")
//        val score: Int? = null,
//
//        @OneToMany(mappedBy = "post")
//        val comments: List<Comment>? = null
//)
//
//
//// REPOSITORIES
//
//
//@Repository
//interface PostRepository : PagingAndSortingRepository<Post, Long> {
//
//    @Modifying
//    @Query(value = "update example1.Post p set p.score = 0 where p.id = :postId")
//    fun resetScore(@Param("postId") postId: Long)
//
//    @Modifying
//    @Query(value = "update example1.Post p set p.score = p.score + 1 where p.id = :postId")
//    fun upvote(@Param("postId") postId: Long)
//
//    @Query(value = "select p.score from example1.Post p where p.id = :postId")
//    fun getScore(@Param("postId") postId: Long): Int?
//}
//
//
//// SERVICES
//
//interface PostService {
//
//    fun updateTextAndResetScore(postId: Long, newText: String)
//
//    @Throws(MyCustomException::class)
//    fun doUpvote(postId: Long)
//
//    //    @Transactional(rollbackOn = example1.MyCustomException.class)
//    @Transactional(noRollbackFor = [MyCustomException::class])
//    @Throws(MyCustomException::class)
//    fun upvote(postId: Long)
//}
//
//@Service
//open class PostServiceImpl(
//        @Autowired
//        val postRepository: PostRepository
//) : PostService {
//    override fun updateTextAndResetScore(postId: Long, newText: String) {
//        val post = postRepository.findById(postId)
//                .orElseThrow { ResourceNotFoundException("example1.Post not found!") }
//
//        postRepository.resetScore(postId)
//
//        post.text = newText
//        postRepository.save(post)
//    }
//
//    @Throws(MyCustomException::class)
//    override fun doUpvote(postId: Long) {
//        upvote(postId)
//    }
//
//    @Throws(MyCustomException::class)
//    //@Transactional(rollbackOn = example1.MyCustomException.class)
//    override fun upvote(postId: Long) {
//        postRepository.upvote(postId)
//
//        throw MyCustomException("Something wrong happend!")
//    }
//
//}
//
//// CONFIGURATION
//
//
//@EnableJpaRepositories(basePackages = ["com.hexagon.presentations.framework.repositories"])
//@EnableTransactionManagement
//@SpringBootApplication
//open class FrameworkApplication
//
//fun main(args: Array<String>) {
//    runApplication<FrameworkApplication>(*args)
//}
