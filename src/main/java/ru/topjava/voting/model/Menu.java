package ru.topjava.voting.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "menus")
@FilterDef(name = "added", parameters = @ParamDef(name = "added", type = "java.time.LocalDate"))
public class Menu extends AbstractBaseEntity {

    @NotNull
    @Column(name = "ADDED")
    private LocalDate added;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<Dish> dishes;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESTAURANT_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant;

    public void addDish(Dish dish) {
        dishes.add(dish);
        dish.setMenu(this);
    }

    public void removeDish(Dish dish) {
        dishes.remove(dish);
        dish.setMenu(null);
    }

    public Menu(LocalDate added, Restaurant restaurant) {
        this.added = added;
        this.restaurant = restaurant;
    }

    public Menu(Long id, LocalDate added, List<Dish> dishes, Restaurant restaurant) {
        super(id);
        this.added = added;
        this.dishes = dishes;
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "added=" + added +
                ", id=" + id +
                '}';
    }
}
