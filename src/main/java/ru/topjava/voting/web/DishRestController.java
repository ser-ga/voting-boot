package ru.topjava.voting.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.topjava.voting.model.Dish;
import ru.topjava.voting.repository.DishRepository;
import ru.topjava.voting.repository.MenuRepository;
import ru.topjava.voting.to.DishTo;
import ru.topjava.voting.util.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.util.List;

import static ru.topjava.voting.util.DishUtil.createFromTo;
import static ru.topjava.voting.util.ValidationUtil.assureIdConsistent;
import static ru.topjava.voting.util.ValidationUtil.checkNew;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = DishRestController.DISH_REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DishRestController {

    static final String DISH_REST_URL = "/rest/dish";

    private final DishRepository dishRepository;

    private final MenuRepository menuRepository;

    @GetMapping
    public List<Dish> getAll() {
        log.info("Get all dishes");
        return dishRepository.findAll();
    }

    @GetMapping("/{id}")
    public Dish getById(@PathVariable("id") long id) {
        log.info("Get dish with id '{}'", id);
        return dishRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found Dish with id=" + id));
    }

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> create(@RequestBody @Validated DishTo dishTo) {
        log.info("Create new dish");
        checkNew(dishTo);
        Dish dish = createFromTo(dishTo);
        dish.setMenu(menuRepository.getOne(dishTo.getMenuId()));
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(DISH_REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @Validated @RequestBody Dish dish) {
        log.info("Update dish with id '{}'", id);
        assureIdConsistent(dish, id);
        Dish found = dishRepository.findById(id).orElseThrow(() -> new NotFoundException("Dish not found for ID " + id));
        found.setName(dish.getName());
        found.setPrice(dish.getPrice());
        dishRepository.save(found);
    }

    @RolesAllowed("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("Delete dish with id '{}'", id);
        dishRepository.deleteById(id);
    }

}
