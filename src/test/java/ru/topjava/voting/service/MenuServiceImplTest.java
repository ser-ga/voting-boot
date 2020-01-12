package ru.topjava.voting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.topjava.voting.model.Menu;
import ru.topjava.voting.util.exception.IllegalRequestDataException;
import ru.topjava.voting.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.topjava.voting.TestData.*;
import static ru.topjava.voting.TestUtil.assertMatch;


class MenuServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MenuService menuService;

    @Test
    void testCreate() {
        Menu created = new Menu(null, LocalDate.now(), new ArrayList<>(), RESTAURANT1);
        getNewDishes().forEach(created::addDish);
        menuService.create(created, RESTAURANT1_ID);
        assertMatch(menuService.getAll(), List.of(MENU1, MENU2, MENU3, created), FIELDS_RESTAURANT_AND_DISHES);
    }

    @Test
    void testCreateNotFound() throws Exception {
        Menu created = new Menu(null, LocalDate.now(), getNewDishes(), RESTAURANT1);
        assertThrows(NotFoundException.class, () -> menuService.create(created, 1));
    }

    @Test
    void testCreateWithId() throws Exception {
        Menu created = new Menu(1L, LocalDate.now(), getNewDishes(), RESTAURANT1);
        assertThrows(IllegalRequestDataException.class, () -> menuService.create(created, RESTAURANT1_ID));
    }

    @Test
    void testGetById() {
        Menu actual = menuService.getById(MENU1_ID);
        assertMatch(actual, MENU1, FIELDS_RESTAURANT_AND_DISHES);
        assertMatch(actual.getDishes(), MENU1.getDishes(), FIELD_MENU);
    }

    @Test
    void testGetByRestaurantId() {
        List<Menu> actual = menuService.getBy(RESTAURANT3.getId(), null);
        assertMatch(actual, List.of(MENU1, MENU3), FIELDS_RESTAURANT_AND_DISHES);
    }

    @Test
    void testGetByRestaurantIdAndDate() {
        List<Menu> actual = menuService.getBy(RESTAURANT3.getId(), LocalDate.now());
        assertMatch(actual, List.of(MENU3), FIELDS_RESTAURANT_AND_DISHES);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testUpdate() {
        Menu updated = new Menu(MENU3.getId(), LocalDate.now(), new ArrayList<>(), RESTAURANT3);
        getNewDishes().forEach(updated::addDish);
        Menu actual = menuService.update(updated, RESTAURANT3.getId());
        assertMatch(actual, updated, FIELDS_RESTAURANT_AND_DISHES);
        assertMatch(actual.getDishes(), updated.getDishes(), FIELD_ID, FIELD_MENU);
    }

    @Test
    void testUpdateNotFound() throws Exception {
        Menu updated = new Menu(1L, LocalDate.now(), getNewDishes(), RESTAURANT3);
        assertThrows(NotFoundException.class, () -> menuService.update(updated, RESTAURANT3.getId()));
    }

    @Test
    void testDelete() {
        menuService.delete(MENU1_ID);
        assertMatch(menuService.getAll(), List.of(MENU2, MENU3), FIELDS_RESTAURANT_AND_DISHES);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        assertThrows(EmptyResultDataAccessException.class, () -> menuService.delete(1));
    }

    @Test
    void testGetAll() {
        List<Menu> actual = menuService.getAll();
        assertMatch(actual, getAllMenus(), FIELDS_RESTAURANT_AND_DISHES);
    }
}