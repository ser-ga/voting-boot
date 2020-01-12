package ru.topjava.voting.util;

import ru.topjava.voting.model.Dish;
import ru.topjava.voting.to.DishTo;

public class DishUtil {

    private DishUtil() {}

    public static Dish createFromTo(DishTo dishTo) {
        return new Dish(dishTo.getId(), dishTo.getName(), dishTo.getPrice());
    }
}
