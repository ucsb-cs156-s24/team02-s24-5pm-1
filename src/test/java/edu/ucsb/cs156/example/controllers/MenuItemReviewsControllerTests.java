package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.MenuItemReviews;
import edu.ucsb.cs156.example.repositories.MenuItemReviewsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = MenuItemReviewsController.class)
@Import(TestConfig.class)
public class MenuItemReviewsControllerTests extends ControllerTestCase {
    @MockBean
    MenuItemReviewsRepository menuItemReviewsRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/menuitemreviews/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/menuitemreviews/all"))
                .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_menuitemreviews() throws Exception {

                // arrange
                LocalDateTime review1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReviews menuItemReviews1 = MenuItemReviews.builder()
                    .itemId(1)
                    .reviewerEmail("cgaucho@gmail.com")
                    .stars(3)
                    .comments("decent dish")
                    .dateReviewed(review1)
                    .build();


                LocalDateTime review2 = LocalDateTime.parse("2022-03-11T00:00:00");

                MenuItemReviews menuItemReviews2 = MenuItemReviews.builder()
                .itemId(2)
                .reviewerEmail("cgaucho@gmail.com")
                .stars(1)
                .comments("overall just bad")
                .dateReviewed(review2)
                .build();

                ArrayList<MenuItemReviews> expectedMenuItemReviews = new ArrayList<>();
                expectedMenuItemReviews.addAll(Arrays.asList(menuItemReviews1, menuItemReviews2));

                when(menuItemReviewsRepository.findAll()).thenReturn(expectedMenuItemReviews);

                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreviews/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(menuItemReviewsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedMenuItemReviews);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

    // Tests for POST /api/ucsbdates/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/menuitemreviews/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/menuitemreviews/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_menuitemreviews() throws Exception {
        // arrange

        LocalDateTime review1 = LocalDateTime.parse("2022-01-03T00:00:00");

        MenuItemReviews menuItemReviews1 = MenuItemReviews.builder()
            .itemId(3)
            .reviewerEmail("cgaucho@gmail.com")
            .stars(5)
            .comments("amazing")
            .dateReviewed(review1)
            .build();

        when(menuItemReviewsRepository.save(eq(menuItemReviews1))).thenReturn(menuItemReviews1);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/menuitemreviews/post?itemId=3&reviewerEmail=cgaucho@gmail.com&stars=5&comments=amazing&dateReviewed=2022-01-03T00:00:00")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert()
        verify(menuItemReviewsRepository, times(1)).save(menuItemReviews1);
        String expectedJson = mapper.writeValueAsString(menuItemReviews1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Tests for GET /api/ucsbdates?id=...

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/menuitemreviews?id=7"))
                .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

        // arrange
        LocalDateTime review1 = LocalDateTime.parse("2022-01-03T00:00:00");

        MenuItemReviews menuItemReviews = MenuItemReviews.builder()
        .itemId(3)
        .reviewerEmail("cgaucho@gmail.com")
        .stars(5)
        .comments("amazing")
        .dateReviewed(review1)
        .build();

        when(menuItemReviewsRepository.findById(eq(7L))).thenReturn(Optional.of(menuItemReviews));

        // act
        MvcResult response = mockMvc.perform(get("/api/menuitemreviews?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(menuItemReviewsRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(menuItemReviews);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

        // arrange

        when(menuItemReviewsRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/menuitemreviews?id=7"))
                .andExpect(status().isNotFound()).andReturn();

        // assert

        verify(menuItemReviewsRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("MenuItemReviews with id 7 not found", json.get("message"));
    }

    // Tests for DELETE /api/ucsbdates?id=...

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_menuitemreviews() throws Exception {
        // arrange

        LocalDateTime review1 = LocalDateTime.parse("2022-01-03T00:00:00");

        MenuItemReviews menuItemReviews1 = MenuItemReviews.builder()
        .itemId(3)
        .reviewerEmail("cgaucho@gmail.com")
        .stars(4)
        .comments("solid noodle dishes")
        .dateReviewed(review1)
        .build();

        when(menuItemReviewsRepository.findById(eq(15L))).thenReturn(Optional.of(menuItemReviews1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/menuitemreviews?id=15")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(menuItemReviewsRepository, times(1)).findById(15L);
        verify(menuItemReviewsRepository, times(1)).delete(any());

        Map<String, Object> json = responseToJson(response);
        assertEquals("menuItemReviews with itemId 15 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_menuitemreviews_and_gets_right_error_message()
            throws Exception {
        // arrange

        when(menuItemReviewsRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/menuitemreviews?id=15")
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(menuItemReviewsRepository, times(1)).findById(15L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("MenuItemReviews with id 15 not found", json.get("message"));
    }

    // Tests for PUT /api/ucsbdates?id=...

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_menuitemreviews() throws Exception {
        // arrange

        LocalDateTime review1 = LocalDateTime.parse("2022-01-03T00:00:00");
        LocalDateTime review2 = LocalDateTime.parse("2023-01-03T00:00:00");

        MenuItemReviews menuItemReviewsOrig = MenuItemReviews.builder()
        .itemId(3)
        .reviewerEmail("cgaucho@gmail.com")
        .stars(5)
        .comments("amazing")
        .dateReviewed(review1)
        .build();

        MenuItemReviews menuItemReviewsEdited = MenuItemReviews.builder()
        .itemId(6)
        .reviewerEmail("hello@gmail.com")
        .stars(4)
        .comments("best veggie pizza ever")
        .dateReviewed(review2)
        .build();

        String requestBody = mapper.writeValueAsString(menuItemReviewsEdited);

        when(menuItemReviewsRepository.findById(eq(67L))).thenReturn(Optional.of(menuItemReviewsOrig));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/menuitemreviews?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(menuItemReviewsRepository, times(1)).findById(67L);
        verify(menuItemReviewsRepository, times(1)).save(menuItemReviewsEdited); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_menuitemreviews_that_does_not_exist() throws Exception {
        // arrange

        LocalDateTime review1 = LocalDateTime.parse("2022-01-03T00:00:00");

        MenuItemReviews menuItemEditedReviews = MenuItemReviews.builder()
        .itemId(3)
        .reviewerEmail("ldelplaya@ucsb.edu")
        .stars(0)
        .comments("got food poisoning")
        .dateReviewed(review1)
        .build();

        String requestBody = mapper.writeValueAsString(menuItemEditedReviews);

        when(menuItemReviewsRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/menuitemreviews?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(menuItemReviewsRepository, times(1)).findById(67L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("MenuItemReviews with id 67 not found", json.get("message"));

    }
}
