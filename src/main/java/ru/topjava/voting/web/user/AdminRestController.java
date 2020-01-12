package ru.topjava.voting.web.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.topjava.voting.model.User;
import ru.topjava.voting.repository.UserRepository;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.topjava.voting.util.UserUtil.prepareToSave;
import static ru.topjava.voting.util.ValidationUtil.*;

@Slf4j
@RequiredArgsConstructor
@RolesAllowed("ROLE_ADMIN")
@RestController
@RequestMapping(value = AdminRestController.USER_REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestController {

    static final String USER_REST_URL = "/rest/user";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public User get(@PathVariable("id") Long id) {
        log.info("Get user with id '{}'", id);
        return checkNotFound(userRepository.getById(id), "User not found with ID=" + id);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Get all users");
        return userRepository.getAllBy();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        log.info("Create new user '{}'", user.getEmail());
        checkNew(user);
        prepareToSave(user, passwordEncoder);
        User created = userRepository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("Delete user with id '{}'", id);
        userRepository.deleteById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") int id, @Valid @RequestBody User user) {
        log.info("Update user with id '{}'", id);
        assureIdConsistent(user, id);
        prepareToSave(user, passwordEncoder);
        userRepository.save(user);
    }

    @GetMapping("/by")
    public User getByEmail(@RequestParam("email") String email) {
        log.info("Get user by email '{}'", email);
        return checkNotFound(userRepository.getByEmail(email), "User not found with email=" + email);
    }
}
