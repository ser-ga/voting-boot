package ru.topjava.voting.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.topjava.voting.model.Vote;
import ru.topjava.voting.service.VoteService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = VoteRestController.VOTE_REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteRestController {

    static final String VOTE_REST_URL = "/rest/vote";

    private final VoteService voteService;

    @PostMapping(value = "/for")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void vote(@RequestParam("restaurantId") Integer restaurantId, Authentication authentication) {
        log.info("User '{}' vote for restaurant '{}'", authentication.getName(), restaurantId);
        voteService.vote(restaurantId, authentication.getName());
    }

    @GetMapping
    public List<Vote> getAll(Authentication authentication) {
        log.info("Get all votes for user '{}'", authentication.getName());
        return voteService.getAll(authentication.getName());
    }
}
