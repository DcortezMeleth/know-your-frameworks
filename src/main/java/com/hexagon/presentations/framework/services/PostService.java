package com.hexagon.presentations.framework.services;

import com.hexagon.presentations.framework.exceptions.MyCustomException;
import org.springframework.transaction.annotation.Transactional;

//import javax.transaction.Transactional;

public interface PostService {

    void updateTextAndResetScore(long postId, String newText);

    void doUpvote(long postId) throws MyCustomException;

//    @Transactional(rollbackOn = example1.MyCustomException.class)
    @Transactional(noRollbackFor = MyCustomException.class)
    void upvote(long postId) throws MyCustomException;
}
