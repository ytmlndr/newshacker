package com.newshacker.web;

import com.newshacker.model.impl.Post;
import com.newshacker.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/post")
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping("/{postId}")
    public Post update(@PathVariable Long postId,
                       @RequestBody Post post) {
        return postService.update(postId, post);
    }

    @GetMapping
    public List<Post> read(@RequestParam Integer size) {
        return postService.read(size);
    }
}
