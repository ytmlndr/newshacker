package com.newshacker.web;

import com.newshacker.service.VoteService;
import com.newshacker.model.impl.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/post/{postId}/vote")
@RequestMapping("/post/{postId}/vote")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping()
    public Vote create(@PathVariable Long postId, @RequestBody Vote vote) {
        vote.setPostId(postId);
        return voteService.create(vote);
    }
}
