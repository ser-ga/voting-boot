package ru.topjava.voting.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "votes")
public class Vote extends AbstractBaseEntity {

    @NotNull
    @Column(name = "DATE")
    private LocalDate date = LocalDate.now();

    @NotNull
    @Column(name = "USER_EMAIL")
    private String userEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESTAURANT_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant;

    public Vote(Long id, String userEmail, Restaurant restaurant) {
        super(id);
        this.userEmail = userEmail;
        this.restaurant = restaurant;
    }

    public Vote(String userEmail, Restaurant restaurant) {
        this.userEmail = userEmail;
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", date=" + date +
                ", user=" + userEmail +
                ", restaurant=" + restaurant +
                '}';
    }
}
