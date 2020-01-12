package ru.topjava.voting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends AbstractNamedEntity {

    @Email
    @NotBlank
    @Column(name = "EMAIL")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 5, max = 100)
    @Column(name = "PASSWORD")
    private String password;

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    @Column(name = "REGISTERED", columnDefinition = "timestamp default now()")
    private Date registered = new Date();

    @Column(name = "ENABLED", nullable = false, columnDefinition = "bool default true")
    private boolean enabled = true;

    @NotNull
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "ROLES", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "ROLE")
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Role> roles;

    public User(Long id, String name, String email, String password, Date registered, Role role) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.registered = registered;
        this.roles = Collections.singleton(role);
    }

    public User(Long id, String name, String email, String password, Collection<Role> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        setRoles(roles);
    }

    public User(Long id, String name, String email, String password, Date registered, Collection<Role> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.registered = registered;
        setRoles(roles);
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", registered=" + registered +
                ", enabled=" + enabled +
                '}';
    }
}
