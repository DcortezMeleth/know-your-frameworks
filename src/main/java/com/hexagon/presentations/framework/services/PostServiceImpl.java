package com.hexagon.presentations.framework.services;

import com.hexagon.presentations.framework.ResourceNotFoundException;
import com.hexagon.presentations.framework.entities.Post;
import com.hexagon.presentations.framework.exceptions.MyCustomException;
import com.hexagon.presentations.framework.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public void updateTextAndResetScore(long postId, String newText) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("example1.Post not found!"));

        postRepository.resetScore(postId);

        post.setText(newText);
        postRepository.save(post);
    }

    @Override
    public void doUpvote(long postId) throws MyCustomException {
        upvote(postId);
    }

    @Override
//    @Transactional(rollbackOn = example1.MyCustomException.class)
    public void upvote(long postId) throws MyCustomException {
        postRepository.upvote(postId);

        throw new MyCustomException("Something wrong happend!");
    }

}
