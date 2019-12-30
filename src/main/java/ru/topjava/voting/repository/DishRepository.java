package ru.topjava.voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.topjava.voting.model.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {}
