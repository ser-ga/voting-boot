package ru.topjava.voting.to;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.topjava.voting.HasId;
import ru.topjava.voting.model.Dish;
import ru.topjava.voting.model.Menu;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MenuTo implements HasId {

    protected Long id;

    @NotNull
    private Long restaurantId;

    @NotNull
    private List<Dish> dishes;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate added;

    public MenuTo(Long restaurantId, List<Dish> dishes) {
        this.restaurantId = restaurantId;
        this.dishes = dishes;
    }

    public static MenuTo fromMenu(Menu menu) {
        return new MenuTo(menu.getId(), menu.getRestaurant().getId(), menu.getDishes(), menu.getAdded());
    }

}
