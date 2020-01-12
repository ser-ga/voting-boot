package ru.topjava.voting.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.topjava.voting.model.Vote;
import ru.topjava.voting.service.VoteService;
import ru.topjava.voting.util.VoteTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.topjava.voting.TestData.*;

class VoteRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = VoteRestController.VOTE_REST_URL + '/';

    @Autowired
    private VoteService voteService;

    @AfterEach
    void tearDown() {
        VoteTime.restore();
    }

    @Test
    void testVote() throws Exception {
        increaseVoteTime();
        mockMvc.perform(post(REST_URL + "for?restaurantId=" + RESTAURANT1_ID)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isNoContent())
                .andDo(print());

        List<Vote> actual = voteService.getAll(USER1.getEmail());
        assertEquals(1, actual.size());
        assertEquals(USER1.getEmail(), actual.get(0).getUserEmail());

        long actualRestaurantId = actual.get(0).getRestaurant().getId();
        assertEquals(RESTAURANT1_ID, actualRestaurantId);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testVoteInvalid() throws Exception {
        increaseVoteTime();
        mockMvc.perform(post(REST_URL + "for?restaurantId=" + 1)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isConflict());
    }

    @Test
    void testVoteAuth() throws Exception {
        mockMvc.perform(post(REST_URL + RESTAURANT1_ID))
                .andExpect(status().isUnauthorized());
    }
}