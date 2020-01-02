package ru.topjava.voting;

import lombok.Getter;
import ru.topjava.voting.model.User;

public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = 1L;

    @Getter
    private User user;

    public AuthorizedUser(User user) {
        super(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, user.getRoles());
        this.user = user;
    }

    public long getId() {
        return user.getId();
    }

    @Override
    public String toString() {
        return user.toString();
    }
}