package ru.topjava.voting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.topjava.voting.model.Restaurant;
import ru.topjava.voting.repository.restaurant.RestaurantRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.topjava.voting.TestData.*;
import static ru.topjava.voting.TestUtil.*;

class RestaurantRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = RestaurantRestController.RESTAURANT_REST_URL + '/';

    @Autowired
    protected RestaurantRepository restaurantRepository;

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(getToMatcher(getAllRestaurants(), Restaurant.class, FIELDS_MENUS_AND_VOTES));
    }

    //TODO надо переделывать Entity Menu, попробовать с subselect
    @Test
    void getById() throws Exception {
        mockMvc.perform(get(REST_URL + RESTAURANT1_ID)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertMatch(readFromJsonMvcResult(result, Restaurant.class), RESTAURANT1))
                .andDo(print());
    }

    @Test
    void testGetUnauth() throws Exception {
        mockMvc.perform(get(REST_URL + RESTAURANT1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + 1)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        Restaurant created = getRestaurant();
        ResultActions action = mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(created)));
        Restaurant returned = readFromJsonResultActions(action, Restaurant.class);
        created.setId(returned.getId());
        assertMatch(returned, created);
        List<Restaurant> expectList = List.of(RESTAURANT1, RESTAURANT2, RESTAURANT3, RESTAURANT4, RESTAURANT5, RESTAURANT6, created);
        assertMatch(restaurantRepository.findAll(), expectList, FIELDS_MENUS_AND_VOTES);
    }

    @Test
    void testCreateByUser() throws Exception {
        Restaurant created = getRestaurant();
        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(USER1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(created)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdate() throws Exception {
        Restaurant updated = getRestaurant();
        updated.setId(RESTAURANT1_ID);
        mockMvc.perform(put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());

        assertMatch(restaurantRepository.findById(RESTAURANT1_ID).orElse(null), updated, FIELDS_MENUS_AND_VOTES);
    }

    @Test
    void testUpdateByUser() throws Exception {
        Restaurant updated = getRestaurant();
        updated.setId(RESTAURANT1_ID);

        mockMvc.perform(put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated))
                .with(userHttpBasic(USER1)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + RESTAURANT3.getId())
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());
        List<Restaurant> expectList = List.of(RESTAURANT1, RESTAURANT2, RESTAURANT4, RESTAURANT5, RESTAURANT6);
        assertMatch(restaurantRepository.findAll(), expectList, FIELDS_MENUS_AND_VOTES);
    }

    @Test
    void testDeleteByUser() throws Exception {
        mockMvc.perform(delete(REST_URL + RESTAURANT3.getId())
                .with(userHttpBasic(USER1)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testUpdateDuplicate() throws Exception {
        Restaurant invalid = new Restaurant(RESTAURANT1_ID, "KFC2", "Москва", "Куриные бургеры и картошка");
        mockMvc.perform(put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testCreateDuplicate() throws Exception {
        Restaurant invalid = new Restaurant(null, "KFC2", "Москва", "Куриные бургеры и картошка");
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateInvalid() throws Exception {
        Restaurant invalid = new Restaurant(null, "KFC2", null, "Куриные бургеры и картошка");
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        Restaurant invalid = new Restaurant(RESTAURANT1_ID, null, "Москва", "Куриные бургеры и картошка");
        mockMvc.perform(put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }
}