package ru.topjava.voting.service;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.topjava.voting.model.Restaurant;
import ru.topjava.voting.model.Vote;
import ru.topjava.voting.util.VoteTime;
import ru.topjava.voting.util.exception.VoteException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.topjava.voting.TestData.*;
import static ru.topjava.voting.TestUtil.assertMatch;


public class VoteServiceImplTest extends AbstractServiceTest {

    @Autowired
    private VoteService service;

    @AfterEach
    void tearDown() {
        VoteTime.restore();
    }

    @Test
    void voteTest() {
        increaseVoteTime();
        service.vote(RESTAURANT1_ID, USER1.getEmail());
        Vote stored = service.findByUser_EmailAndDate(USER1.getEmail(), LocalDate.now());
        Restaurant restaurant = (Restaurant) Hibernate.unproxy(stored.getRestaurant());
        assertMatch(restaurant, RESTAURANT1, "menus", "votes");
        assertMatch(stored.getUserEmail(), USER1.getEmail(), "registered", "votes");
    }

    @Test
    void voteTimeTest() {
        expireVoteTime();
        // first vote
        service.vote(RESTAURANT1_ID, USER1.getEmail());
        // changed mind
        assertThrows(VoteException.class, () ->
                service.vote(RESTAURANT4.getId(), USER1.getEmail()));
    }
}
