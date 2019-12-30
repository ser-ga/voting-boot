package ru.topjava.voting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.topjava.voting.model.Menu;
import ru.topjava.voting.model.Restaurant;
import ru.topjava.voting.repository.MenuRepository;
import ru.topjava.voting.repository.restaurant.RestaurantRepository;
import ru.topjava.voting.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static ru.topjava.voting.util.ValidationUtil.checkNew;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final RestaurantRepository restaurantRepository;

    private final MenuRepository menuRepository;

    @Override
    public Menu create(Menu menu, long restaurantId) {
        log.debug("Create new menu in restaurant '{}'", restaurantId);
        checkNew(menu);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new NotFoundException("Not found restaurant with id=" + restaurantId)
        );
        menu.setRestaurant(restaurant);
        return menuRepository.save(menu);

    }

    @Override
    public Menu getById(long id) {
        log.debug("Get menu by id '{}'", id);
        return menuRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found menu with id=" + id));
    }

    @Override
    public List<Menu> getBy(long restaurantId, LocalDate date) {
        log.debug("Get menus in restaurant '{}' by date '{}'", restaurantId, date);
        if (date == null) return menuRepository.getByRestaurant_Id(restaurantId);
        return menuRepository.getByRestaurant_IdAndAdded(restaurantId, date);
    }

    @Override
    public Menu update(Menu menu, long restaurantId) {
        log.debug("Update menu '{}' in restaurant '{}'", menu.getId(), restaurantId);
        menuRepository.findById(menu.getId()).orElseThrow(() -> new NotFoundException("Not found menu with id=" + menu.getId()));
        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        menu.setRestaurant(restaurant);
        return  menuRepository.save(menu);
    }

    @Override
    public void delete(long id) {
        menuRepository.deleteById(id);
    }

    @Override
    public List<Menu> getAll() {
        return menuRepository.getAllByOrderByIdAsc();
    }
}
