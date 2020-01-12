package ru.topjava.voting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.topjava.voting.model.Restaurant;
import ru.topjava.voting.model.Vote;
import ru.topjava.voting.repository.VoteRepository;
import ru.topjava.voting.repository.restaurant.RestaurantRepository;
import ru.topjava.voting.util.VoteTime;
import ru.topjava.voting.util.exception.VoteException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    private final RestaurantRepository restaurantRepository;

    private final VoteTime voteTime;

    @Override
    public void vote(long restaurantId, String email) {
        log.info("'{}' vote for restaurant with id '{}'", email, restaurantId);
        Vote vote = voteRepository.findByUserEmailAndDate(email, LocalDate.now());

        if (vote != null && restaurantId != vote.getRestaurant().getId() && LocalTime.now().isAfter(VoteTime.getTime())) {
            throw new VoteException("Time for change vote expired at " + voteTime + " by server time");
        }

        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        if (vote == null) {
            vote = new Vote();
            vote.setUserEmail(email);
        }
        vote.setRestaurant(restaurant);
        Vote saved = voteRepository.save(vote);
        log.debug("'{}' was saved successfully", saved);
    }

    @Override
    public List<Vote> getAll(String email) {
        log.debug("Load votes list by email '{}'", email);
        return voteRepository.getAllByUserEmail(email);
    }

    @Override
    public Vote findByUser_EmailAndDate(String email, LocalDate date) {
        log.debug("Load vote by email '{}' and date '{}'", email, date);
        return voteRepository.findByUserEmailAndDate(email, date);
    }
}