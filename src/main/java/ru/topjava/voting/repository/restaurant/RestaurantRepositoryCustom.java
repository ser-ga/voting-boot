package ru.topjava.voting.repository.restaurant;

import ru.topjava.voting.model.Restaurant;

import java.time.LocalDate;

public interface RestaurantRepositoryCustom {

    Restaurant getByIdWithMenuByDate(Long id, LocalDate date);

}
