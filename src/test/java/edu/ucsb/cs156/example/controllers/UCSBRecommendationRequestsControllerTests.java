package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBRecommendationRequests;
import edu.ucsb.cs156.example.repositories.UCSBRecommendationRequestsRepository;

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

@WebMvcTest(controllers = UCSBRecommendationRequestsController.class)
@Import(TestConfig.class)
public class UCSBRecommendationRequestsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBRecommendationRequestsRepository ucsbRecommendationRequestsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbdates/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbrecommendationrequests/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbrecommendationrequests/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbrecommendationrequests() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBRecommendationRequests ucsbRecommendationRequests1 = UCSBRecommendationRequests.builder()
                                .requesterEmail("shuhanlyu@ucsb.edu")
                                .professorEmail("professor_email1")
                                .explanation("Some_explanation1")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt1)
                                .done(true)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBRecommendationRequests ucsbRecommendationRequests2 = UCSBRecommendationRequests.builder()
                                .requesterEmail("anotherone@ucsb.edu")
                                .professorEmail("professor_email2")
                                .explanation("Some_explanation2")
                                .dateRequested(ldt2)
                                .dateNeeded(ldt2)
                                .done(true)
                                .build();

                ArrayList<UCSBRecommendationRequests> expectedRecommendation = new ArrayList<>();
                expectedRecommendation.addAll(Arrays.asList(ucsbRecommendationRequests1, ucsbRecommendationRequests2));

                when(ucsbRecommendationRequestsRepository.findAll()).thenReturn(expectedRecommendation);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbrecommendationrequests/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbRecommendationRequestsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRecommendation);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbdates/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbrecommendationrequests/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbrecommendationrequests/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ucsbrecommendationrequests() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBRecommendationRequests ucsbRecommendationRequests1 = UCSBRecommendationRequests.builder()
                                .requesterEmail("shuhanlyu@ucsb.edu")
                                .professorEmail("professor_email1")
                                .explanation("Some_explanation1")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt1)
                                .done(true)
                                .build();

                when(ucsbRecommendationRequestsRepository.save(eq(ucsbRecommendationRequests1))).thenReturn(ucsbRecommendationRequests1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbrecommendationrequests/post?requesterEmail=shuhanlyu@ucsb.edu&professorEmail=professor_email1&explanation=Some_explanation1&dateRequested=2022-01-03T00:00:00&dateNeeded=2022-01-03T00:00:00&done=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbRecommendationRequestsRepository, times(1)).save(ucsbRecommendationRequests1);
                String expectedJson = mapper.writeValueAsString(ucsbRecommendationRequests1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for GET /api/ucsbdates?id=...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsbrecommendationrequests?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime ldt2 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBRecommendationRequests ucsbRecommendationRequests2 = UCSBRecommendationRequests.builder()
                                .requesterEmail("anotherone@ucsb.edu")
                                .professorEmail("professor_email2")
                                .explanation("Some_explanation2")
                                .dateRequested(ldt2)
                                .dateNeeded(ldt2)
                                .done(true)
                                .build();

                when(ucsbRecommendationRequestsRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbRecommendationRequests2));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbrecommendationrequests?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbRecommendationRequestsRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(ucsbRecommendationRequests2);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbRecommendationRequestsRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbrecommendationrequests?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbRecommendationRequestsRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBRecommendationRequests with id 7 not found", json.get("message"));
        }


        // Tests for DELETE /api/ucsbdates?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_date() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBRecommendationRequests ucsbRecommendationRequests1 = UCSBRecommendationRequests.builder()
                                .requesterEmail("shuhanlyu@ucsb.edu")
                                .professorEmail("professor_email1")
                                .explanation("Some_explanation1")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt1)
                                .done(true)
                                .build();

                when(ucsbRecommendationRequestsRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbRecommendationRequests1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsbrecommendationrequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbRecommendationRequestsRepository, times(1)).findById(15L);
                verify(ucsbRecommendationRequestsRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBRecommendationRequests with id 15 deleted", json.get("message"));
        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_ucsbrecommendationrequests_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbRecommendationRequestsRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsbrecommendationrequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbRecommendationRequestsRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBRecommendationRequests with id 15 not found", json.get("message"));
        }

        // Tests for PUT /api/ucsbdates?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_ucsbrecommendationrequests() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

                UCSBRecommendationRequests ucsbRecommendationRequestsOrig = UCSBRecommendationRequests.builder()
                                .requesterEmail("shuhanlyu@ucsb.edu")
                                .professorEmail("professor_email1")
                                .explanation("Some_explanation1")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt1)
                                .done(true)
                                .build();

                UCSBRecommendationRequests ucsbRecommendationRequestsEdited = UCSBRecommendationRequests.builder()
                                .requesterEmail("anotherone@ucsb.edu")
                                .professorEmail("professor_email2")
                                .explanation("Some_explanation2")
                                .dateRequested(ldt2)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                String requestBody = mapper.writeValueAsString(ucsbRecommendationRequestsEdited);

                when(ucsbRecommendationRequestsRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbRecommendationRequestsOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsbrecommendationrequests?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbRecommendationRequestsRepository, times(1)).findById(67L);
                verify(ucsbRecommendationRequestsRepository, times(1)).save(ucsbRecommendationRequestsEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_ucsbrecommendationrequests_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBRecommendationRequests ucsbRecommendationRequestsEdited = UCSBRecommendationRequests.builder()
                                .requesterEmail("shuhanlyu@ucsb.edu")
                                .professorEmail("professor_email1")
                                .explanation("Some_explanation1")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt1)
                                .done(true)
                                .build();

                String requestBody = mapper.writeValueAsString(ucsbRecommendationRequestsEdited);

                when(ucsbRecommendationRequestsRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsbrecommendationrequests?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbRecommendationRequestsRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBRecommendationRequests with id 67 not found", json.get("message"));

        }


}
