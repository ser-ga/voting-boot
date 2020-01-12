package ru.topjava.voting.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.topjava.voting.model.Restaurant;
import ru.topjava.voting.repository.restaurant.RestaurantRepository;
import ru.topjava.voting.util.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.topjava.voting.util.ValidationUtil.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = RestaurantRestController.RESTAURANT_REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {

    static final String RESTAURANT_REST_URL = "/rest/restaurant";

    private final RestaurantRepository restaurantRepository;

    @GetMapping
    public List<Restaurant> getAll() {
        log.info("Get all restaurants");
        return restaurantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable("id") Long id) {
        log.info("Get restaurant with id '{}'", id);
        Restaurant restaurant = restaurantRepository.getByIdWithMenuByDate(id, LocalDate.now());
        checkNotFound(restaurant, "No Restaurant found for ID " + id);
        return ResponseEntity.ok(restaurant);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody Restaurant restaurant) {
        log.info("Create new restaurant");
        checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(RESTAURANT_REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @Valid @RequestBody Restaurant restaurant) {
        log.info("Update restaurant with id '{}'", id);
        assureIdConsistent(restaurant, id);
        Restaurant found = restaurantRepository.findById(id).orElseThrow(() -> new NotFoundException("Restaurant not found for ID " + id));
        found.setName(restaurant.getName());
        found.setCity(restaurant.getCity());
        found.setDescription(restaurant.getDescription());
        restaurantRepository.save(found);
    }

    @RolesAllowed("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("Delete restaurant with id '{}'", id);
        restaurantRepository.removeById(id);
    }

}
