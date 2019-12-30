package ru.topjava.voting.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.topjava.voting.AuthorizedUser;
import ru.topjava.voting.model.Role;
import ru.topjava.voting.model.User;
import ru.topjava.voting.repository.UserRepository;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

import static ru.topjava.voting.util.UserUtil.prepareToSave;
import static ru.topjava.voting.util.ValidationUtil.assureIdConsistent;
import static ru.topjava.voting.util.ValidationUtil.checkNew;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = ProfileRestController.PROFILE_REST_URL)
public class ProfileRestController {

    public static final String PROFILE_REST_URL = "/rest/profile";

    public static final String REGISTER = "/register";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(Authentication authentication) {
        AuthorizedUser authorizedUser = (AuthorizedUser) authentication.getPrincipal();
        return authorizedUser.getUser();
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication) {
        AuthorizedUser authorizedUser = (AuthorizedUser) authentication.getPrincipal();
        userRepository.deleteById(authorizedUser.getId());
    }

    @PostMapping(value = REGISTER, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        checkNew(user);
        prepareToSave(user, passwordEncoder);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        User created = userRepository.saveAndFlush(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PROFILE_REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody User user, Authentication authentication) {
        AuthorizedUser authorizedUser = (AuthorizedUser) authentication.getPrincipal();
        assureIdConsistent(user, authorizedUser.getId());
        prepareToSave(user, passwordEncoder);
        userRepository.save(user);
    }
}