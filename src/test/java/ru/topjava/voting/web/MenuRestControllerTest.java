package ru.topjava.voting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.topjava.voting.model.Menu;
import ru.topjava.voting.repository.MenuRepository;
import ru.topjava.voting.to.MenuTo;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.topjava.voting.TestData.*;
import static ru.topjava.voting.TestUtil.*;
import static ru.topjava.voting.util.MenuUtil.createFromTo;

class MenuRestControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuRepository menuRepository;

    private static final String REST_URL = MenuRestController.MENU_REST_URL + '/';

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(getToMatcher(getAllMenuTo(), MenuTo.class, FIELDS_RESTAURANT_AND_DISHES))
                .andDo(print());
    }

    @Test
    void testCreate() throws Exception {
        MenuTo createdTo = new MenuTo(RESTAURANT1_ID, getNewDishes());

        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(writeValue(createdTo)))
                .andExpect(status().isCreated())
                .andDo(print());

        Menu returned = readFromJsonResultActions(action, Menu.class);
        Menu created = createFromTo(createdTo);

        created.setId(returned.getId());

        assertMatch(returned, created, FIELDS_RESTAURANT_AND_DISHES);
        assertMatch(menuRepository.getById(returned.getId()), created, FIELDS_RESTAURANT_AND_DISHES);
    }

    @Test
    void testCreateInvalid() throws Exception {
        MenuTo invalid = new MenuTo(RESTAURANT1_ID, null);

        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(writeValue(invalid)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testCreateByUser() throws Exception {
        MenuTo createdTo = new MenuTo(RESTAURANT1_ID, getNewDishes());

        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER1))
                .content(writeValue(createdTo)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void testGetById() throws Exception {
        mockMvc.perform(get(REST_URL + MENU1_ID)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertMatch(readFromJsonMvcResult(result, Menu.class), MENU1, FIELDS_RESTAURANT_AND_DISHES))
                .andDo(print());
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + 1)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetUnauth() throws Exception {
        mockMvc.perform(get(REST_URL + MENU1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdate() throws Exception {
        MenuTo updatedTo = new MenuTo(MENU1_ID, RESTAURANT1_ID, getNewDishes(), MENU1.getAdded());

        ResultActions action = mockMvc.perform(put(REST_URL + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(writeValue(updatedTo)))
                .andExpect(status().isOk())
                .andDo(print());

        Menu returned = readFromJsonResultActions(action, Menu.class);
        Menu created = createFromTo(updatedTo);

        created.setId(returned.getId());

        assertMatch(returned, created, FIELDS_RESTAURANT_AND_DISHES);
        assertMatch(menuRepository.getById(returned.getId()), created, FIELDS_RESTAURANT_AND_DISHES);
    }

    @Test
    void testUpdateInvalid() throws Exception {
        MenuTo invalid = new MenuTo(MENU1_ID, RESTAURANT1_ID, null, MENU1.getAdded());

        ResultActions action = mockMvc.perform(put(REST_URL + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(writeValue(invalid)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testUpdateForbidden() throws Exception {
        MenuTo updatedTo = new MenuTo(MENU1_ID, RESTAURANT1_ID, getNewDishes(), MENU1.getAdded());

        mockMvc.perform(put(REST_URL + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER1))
                .content(writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + MENU1_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());
        List<Menu> expectList = List.of(MENU2, MENU3);
        assertMatch(menuRepository.findAll(), expectList, FIELDS_RESTAURANT_AND_DISHES);
    }

    @Test
    void testDeleteByUser() throws Exception {
        mockMvc.perform(delete(REST_URL + MENU1_ID)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

}