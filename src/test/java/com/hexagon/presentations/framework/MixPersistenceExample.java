//package com.hexagon.presentations.framework;
//
//import com.hexagon.presentations.framework.entities.example1.Post;
//import com.hexagon.presentations.framework.repositories.example1.PostRepository;
//import com.hexagon.presentations.framework.services.example1.PostServiceImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Optional;
//
//@SpringBootTest
//class MixPersistenceExample {
//
//    @Autowired
//    private example1.PostRepository postRepository;
//
//    @Autowired
//    private example1.PostServiceImpl postService;
//
//    @AfterEach
//    private void cleanUp() {
//        postRepository.deleteAll();
//    }
//
//    @Test
//    void mix() {
//        //before
//        final String newText = "newText";
//        example1.Post p = new example1.Post();
//        p.setText("before");
//        p = postRepository.save(p);
//
//        //when
//        postService.updateTextAndResetScore(p.getId(), newText);
//
//        //then
//        Optional<example1.Post> opt = postRepository.findById(p.getId());
//        Assertions.assertTrue(opt.isPresent());
//        Assertions.assertEquals(newText, opt.get().getText());
//    }
//
//}
