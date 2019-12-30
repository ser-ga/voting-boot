package ru.topjava.voting.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "restaurants")
public class Restaurant extends AbstractNamedEntity {

    @NotBlank
    @Column(name = "CITY")
    private String city;

    @NotBlank
    @Column(name = "DESCRIPTION")
    private String description;

    @NotNull
    @Column(name = "ADDED")
    private LocalDate added  = LocalDate.now();

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    @OrderBy("added DESC")
    @Filter(name="added", condition = "added = :added")
    private List<Menu> menus = new ArrayList<>();

    public Restaurant(Long id, String name, String city, String description) {
        super(id, name);
        this.city = city;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", description='" + description + '\'' +
                ", added=" + added +
                '}';
    }
}
