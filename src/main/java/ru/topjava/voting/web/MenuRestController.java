package ru.topjava.voting.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.topjava.voting.model.Menu;
import ru.topjava.voting.service.MenuService;
import ru.topjava.voting.to.MenuTo;
import ru.topjava.voting.util.MenuUtil;
import ru.topjava.voting.util.ValidationUtil;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static ru.topjava.voting.util.MenuUtil.createFromTo;
import static ru.topjava.voting.util.ValidationUtil.assureIdConsistent;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = MenuRestController.MENU_REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuRestController {

    static final String MENU_REST_URL = "/rest/menu";

    private final MenuService menuService;

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Validated @RequestBody MenuTo menuTo) {
        log.info("Create new menu");
        menuTo.getDishes().forEach(ValidationUtil::checkNew);

        Menu created = createFromTo(menuTo);
        Menu stored = menuService.create(created, menuTo.getRestaurantId());

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(MENU_REST_URL + "/{id}")
                .buildAndExpand(stored.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(stored);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getById(@PathVariable("id") long id) {
        log.info("Get menu with id '{}'", id);
        return ResponseEntity.ok().body(menuService.getById(id));
    }

    @GetMapping(value = "/by")
    public ResponseEntity getBy(@RequestParam("restaurantId") long restaurantId,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "date", required = false) LocalDate date) {
        log.info("Get menu by restaurant '{}' and date '{}'", restaurantId, date);
        return ResponseEntity.ok().body(menuService.getBy(restaurantId, date));
    }

    @GetMapping
    public List<MenuTo> getAll() {
        log.info("Get all menus");
        return menuService.getAll().stream()
                .map(MenuUtil::asTo)
                .collect(Collectors.toList());
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable("id") long id, @Validated @RequestBody MenuTo menuTo) {
        log.info("Update menu with id '{}'", id);
        assureIdConsistent(menuTo, id);
        Menu updated = createFromTo(menuTo);
        Menu menu = menuService.update(updated, menuTo.getRestaurantId());

        return ResponseEntity.ok().body(menu);
    }

    @RolesAllowed("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        log.info("Delete menu with id '{}'", id);
        menuService.delete(id);
    }

}
