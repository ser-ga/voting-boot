package ru.topjava.voting.service;

import ru.topjava.voting.model.Vote;

import java.time.LocalDate;
import java.util.List;

public interface VoteService {

    void vote(long restaurantId, String username);

    List<Vote> getAll(String email);

    Vote findByUser_EmailAndDate(String email, LocalDate date);

}
