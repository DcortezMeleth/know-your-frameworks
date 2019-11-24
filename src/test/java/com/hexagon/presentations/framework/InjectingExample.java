//package com.hexagon.presentations.framework;
//
//import com.hexagon.presentations.framework.services.example1.PostService;
//import com.hexagon.presentations.framework.services.example1.PostServiceImpl;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//
//@SpringBootTest
//class InjectingExample {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Autowired
//    private example1.PostService postService1;
//
//    @Autowired
//    private example1.PostServiceImpl postServiceImpl1;
//
//    @Test
//    void inject() {
//        example1.PostServiceImpl postServiceImpl = applicationContext.getBean(example1.PostServiceImpl.class);
//        Assertions.assertNotNull(postServiceImpl);
//
//        example1.PostService postService = applicationContext.getBean(example1.PostService.class);
//        Assertions.assertNotNull(postService);
//
//        Assertions.assertTrue(postService instanceof example1.PostServiceImpl);
//
//        Assertions.assertNotEquals(example1.PostServiceImpl.class, postServiceImpl.getClass());
//    }
//
//}
