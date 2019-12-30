package ru.topjava.voting;

import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.topjava.voting.model.*;
import ru.topjava.voting.to.MenuTo;
import ru.topjava.voting.util.VoteTime;
import ru.topjava.voting.web.json.JacksonObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.topjava.voting.TestUtil.getContent;
import static ru.topjava.voting.TestUtil.writeAdditionProps;
import static ru.topjava.voting.model.AbstractBaseEntity.START_SEQ;

public class TestData {

    public static final long ADMIN_ID = START_SEQ;
    public static final long USER1_ID = START_SEQ + 1;

    public static final User ADMIN = new User(ADMIN_ID, "Sergey", "admin@yandex.ru", "{noop}pass", new Date(), Role.ROLE_ADMIN);
    public static final User USER1 = new User(USER1_ID, "Ann", "user@ya.ru", "{noop}pass", new Date(), Role.ROLE_USER);
    public static final User USER2 = new User(USER1_ID + 1, "Ann2", "user2@ya.ru", "{noop}pass", new Date(), Role.ROLE_USER);
    public static final User USER3 = new User(USER1_ID + 2, "Ann3", "user3@ya.ru", "{noop}pass", new Date(), Role.ROLE_USER);

    public static final long RESTAURANT1_ID = START_SEQ + 4;

    public static final Restaurant RESTAURANT1 = new Restaurant(RESTAURANT1_ID, "KFC1", "Москва", "Куриные бургеры и картошка");
    public static final Restaurant RESTAURANT2 = new Restaurant(RESTAURANT1_ID + 1, "KFC2", "Москва", "Куриные бургеры и картошка");
    public static final Restaurant RESTAURANT3 = new Restaurant(RESTAURANT1_ID + 2, "KFC3", "Москва", "Куриные бургеры и картошка");
    public static final Restaurant RESTAURANT4 = new Restaurant(RESTAURANT1_ID + 3, "McDs1", "Москва", "Бургеры и картошка");
    public static final Restaurant RESTAURANT5 = new Restaurant(RESTAURANT1_ID + 4, "McDs2", "Москва", "Бургеры и картошка");
    public static final Restaurant RESTAURANT6 = new Restaurant(RESTAURANT1_ID + 5, "McDs3", "Москва", "Бургеры и картошка");

    public static final String[] FIELDS_MENUS_AND_VOTES = new String[]{"menus", "votes"};

    public static final long DISH1_ID = START_SEQ + 13;

    public static final Dish DISH1 = new Dish(DISH1_ID, "1Картошка", BigDecimal.valueOf(70.15));
    public static final Dish DISH2 = new Dish(DISH1_ID + 1, "2Бургер куриный", BigDecimal.valueOf(80.55));
    public static final Dish DISH3 = new Dish(DISH1_ID + 2, "3Салат", BigDecimal.valueOf(100.35));
    public static final Dish DISH4 = new Dish(DISH1_ID + 3, "1Картошка", BigDecimal.valueOf(72.55));
    public static final Dish DISH5 = new Dish(DISH1_ID + 4, "2Чизбургер", BigDecimal.valueOf(75.99));
    public static final Dish DISH6 = new Dish(DISH1_ID + 5, "3Бургер", BigDecimal.valueOf(79.49));
    public static final Dish DISH7 = new Dish(DISH1_ID + 6, "1Пицца", BigDecimal.valueOf(279.49));
    public static final Dish DISH8 = new Dish(DISH1_ID + 7, "2Пицца", BigDecimal.valueOf(279.49));

    public static final String FIELD_ID = "id";
    public static final String FIELD_MENU = "menu";

    public static final long MENU1_ID = START_SEQ + 10;

    public static final Menu MENU1 = new Menu(MENU1_ID, LocalDate.of(2018, 12, 14), List.of(DISH1, DISH2, DISH3), RESTAURANT3);
    public static final Menu MENU2 = new Menu(MENU1_ID + 1, LocalDate.of(2018, 12, 14), List.of(DISH4, DISH5, DISH6), RESTAURANT4);
    public static final Menu MENU3 = new Menu(MENU1_ID + 2, LocalDate.now(), List.of(DISH7, DISH8), RESTAURANT3);

    public static final String[] FIELDS_RESTAURANT_AND_DISHES = new String[]{"restaurant", "dishes"};

    public static final long VOTE1_ID = START_SEQ + 21;

    public static Vote getVote() {
        return new Vote(VOTE1_ID, USER1.getEmail(), RESTAURANT1);
    }

    public static Restaurant getRestaurant() {
        return new Restaurant(null, "Гусли", "Александров", "Ресторан русской кухни");
    }

    public static User getUser() {
        return new User(USER1_ID, USER1.getName(), USER1.getEmail(), USER1.getPassword(), USER1.getRegistered(), USER1.getRoles());
    }

    public static List<Restaurant> getAllRestaurants(Restaurant... restaurants) {
        List<Restaurant> restaurantList = new ArrayList<>(List.of(RESTAURANT1, RESTAURANT2, RESTAURANT3, RESTAURANT4, RESTAURANT5, RESTAURANT6));
        if (restaurants.length > 0) restaurantList.addAll(Arrays.asList(restaurants));
        return restaurantList;
    }

    public static List<Menu> getAllMenus(Menu... menus) {
        List<Menu> menuList = new ArrayList<>((List.of(MENU1, MENU2, MENU3)));
        if (menus.length > 0) menuList.addAll(Arrays.asList(menus));
        return menuList;
    }

    public static List<MenuTo> getAllMenuTo() {
        return List.of(MENU1, MENU2, MENU3).stream()
                .map(MenuTo::fromMenu)
                .collect(Collectors.toList());
    }

    public static List<Dish> getNewDishes() {
        return List.of(new Dish("Ролл1", BigDecimal.valueOf(150)), new Dish("Ролл2", BigDecimal.valueOf(150)));
    }

    public static void expireVoteTime() {
        VoteTime.setTime(LocalTime.now().minusSeconds(1));
    }

    public static void increaseVoteTime() {
        VoteTime.setTime(LocalTime.now().plusSeconds(1));
    }

    public static RequestPostProcessor userHttpBasic(User user) {
        return SecurityMockMvcRequestPostProcessors.httpBasic(user.getEmail(), user.getPassword().replace("{noop}", ""));
    }

    public static String jsonWithPassword(User user, String passw) {
        return writeAdditionProps(user, "password", passw);
    }

    public static <T> ResultMatcher getToMatcher(Iterable<T> expected, Class<T> clazz, String... ignoringFields) {
        return result -> assertThat(readListFromJsonMvcResult(result, clazz)).usingElementComparatorIgnoringFields(ignoringFields).isEqualTo(expected);
    }

    public static <T> List<T> readListFromJsonMvcResult(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException {
        return readValues(getContent(result), clazz);
    }

    public static <T> List<T> readValues(String json, Class<T> clazz) {
        ObjectReader reader = new JacksonObjectMapper().readerFor(clazz);
        try {
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read array from JSON:\n'" + json + "'", e);
        }
    }
}
