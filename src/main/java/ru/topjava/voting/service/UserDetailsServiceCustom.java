package ru.topjava.voting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.topjava.voting.AuthorizedUser;
import ru.topjava.voting.model.User;
import ru.topjava.voting.repository.UserRepository;

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserDetailsServiceCustom implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public AuthorizedUser loadUserByUsername(String username) {
        log.debug("Load user '{}' from database", username);
        User user = userRepository.getByEmail(username);
        if (user == null) {
            log.debug("Not found user '{}'", username);
            throw new UsernameNotFoundException(username);
        }
        return new AuthorizedUser(user);
    }
}
