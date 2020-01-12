package ru.topjava.voting.service;

import ru.topjava.voting.model.Menu;

import java.time.LocalDate;
import java.util.List;

public interface MenuService {

    Menu create(Menu menu, long restaurantId);

    Menu getById(long id);

    List<Menu> getBy(long restaurantId, LocalDate date);

    Menu update(Menu menu, long restaurantId);

    void delete(long id);

    List<Menu> getAll();
}
