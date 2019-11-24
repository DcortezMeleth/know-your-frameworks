//package com.hexagon.presentations.framework;
//
//import com.hexagon.presentations.framework.entities.Post;
//import com.hexagon.presentations.framework.exceptions.MyCustomException;
//import com.hexagon.presentations.framework.repositories.PostRepository;
//import com.hexagon.presentations.framework.services.PostService;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {FrameworkApplication.class})
//public class MissingTransactionExample {
//
//    @Autowired
//    private PostService postService;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @After
//    public void cleanUp() {
//        postRepository.deleteAll();
//    }
//
//    @Test
//    public void testUpvote() {
//        //given
//        Post post = new Post();
//        post.setScore(0);
//        post = postRepository.save(post);
//        Long postId = post.getId();
//
//        //then
//        try {
//            postService.upvote(postId);
//        } catch (MyCustomException e) {
//            //expected - should roll back transaction
//        }
//
//        //then
//        Assert.assertEquals(new Integer(0), postRepository.getScore(postId));
//    }
//
//}
