package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBHelpRequest;
import edu.ucsb.cs156.example.repositories.UCSBHelpRequestsRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;

@WebMvcTest(controllers = HelpRequestsController.class)
@Import(TestConfig.class)
public class UCSBHelpRequestsControllerTests extends ControllerTestCase {

     @MockBean
    UCSBHelpRequestsRepository ucsbHelpRequestsRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/ucsbdates/all
    
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_helprequest() throws Exception {

            // arrange
            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

            UCSBHelpRequest ucsbDate1 = UCSBHelpRequest.builder()
                            .requestTime(ldt1)
                            .solved(false)
                            .requesterEmail("test@test.com")
                            .explanation("test1")
                            .teamId("team1")
                            .tableOrBreakoutRoom("1-9")
                            .build();

            // UCSBHelpRequest ucsbHelpRequest = new UCSBHelpRequest();
            // ucsbHelpRequest.setRequestTime(localDateTime);
            // ucsbHelpRequest.setSolved(solved);
            // ucsbHelpRequest.setRequesterEmail(requesterEmail);
            // ucsbHelpRequest.setExplanation(explanation);
            // ucsbHelpRequest.setTeamId(teamId);
            // ucsbHelpRequest.setTableOrBreakoutRoom(tableOrBreakoutRoom);
            

            LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

            UCSBHelpRequest ucsbDate2 = UCSBHelpRequest.builder()
                            .requestTime(ldt2)
                            .solved(true)
                            .requesterEmail("test2@test.com")
                            .explanation("test2")
                            .teamId("team2")
                            .tableOrBreakoutRoom("2-9")
                            .build();

            ArrayList<UCSBHelpRequest> expectedHelpRequests = new ArrayList<>();
            expectedHelpRequests.addAll(Arrays.asList(ucsbDate1, ucsbDate2));

            when(ucsbHelpRequestsRepository.findAll()).thenReturn(expectedHelpRequests);

            // act
            MvcResult response = mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbHelpRequestsRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedHelpRequests);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // // Tests for POST /api/ucsbdates/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/helprequest/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/helprequest/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_ucsbdate() throws Exception {
            // arrange

            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

            UCSBHelpRequest ucsbDate1 = UCSBHelpRequest.builder()
                            .requestTime(ldt1)
                            .solved(true)
                            .requesterEmail("testemail")
                            .explanation("test1")
                            .teamId("team1")
                            .tableOrBreakoutRoom("table1")
                            .build();

            when(ucsbHelpRequestsRepository.save(eq(ucsbDate1))).thenReturn(ucsbDate1);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/helprequest/post?" +
                            "requesterEmail=testemail&teamId=team1&tableOrBreakoutRoom=table1&" +
                            "explanation=test1&solved=true&localDateTime=2022-01-03T00:00:00")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbHelpRequestsRepository, times(1)).save(ucsbDate1);
            String expectedJson = mapper.writeValueAsString(ucsbDate1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // // Tests for GET /api/ucsbdates?id=...

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/helprequest?id=7"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

            // arrange
            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            UCSBHelpRequest ucsbDate = UCSBHelpRequest.builder()
                            .requestTime(ldt)
                            .solved(false)
                            .requesterEmail("test@test.com")
                            .explanation("test1")
                            .teamId("team1")
                            .tableOrBreakoutRoom("1-9")
                            .build();

            when(ucsbHelpRequestsRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbDate));

            // act
            MvcResult response = mockMvc.perform(get("/api/helprequest?id=7"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbHelpRequestsRepository, times(1)).findById(eq(7L));
            String expectedJson = mapper.writeValueAsString(ucsbDate);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(ucsbHelpRequestsRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/helprequest?id=7"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(ucsbHelpRequestsRepository, times(1)).findById(eq(7L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("UCSBHelpRequest with id 7 not found", json.get("message"));
    }


    // // Tests for DELETE /api/ucsbdates?id=... 

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_date() throws Exception {
            // arrange

            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

            UCSBHelpRequest ucsbDate = UCSBHelpRequest.builder()
                            .requestTime(ldt1)
                            .solved(false)
                            .requesterEmail("test@test.com")
                            .explanation("test1")
                            .teamId("team1")
                            .tableOrBreakoutRoom("1-9")
                            .build();

            when(ucsbHelpRequestsRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbDate));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/helprequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbHelpRequestsRepository, times(1)).findById(15L);
            verify(ucsbHelpRequestsRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBHelpRequest with id 15 deleted", json.get("message"));
    }
    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_ucsbdate_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(ucsbHelpRequestsRepository.findById(eq(15L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/helprequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(ucsbHelpRequestsRepository, times(1)).findById(15L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBHelpRequest with id 15 not found", json.get("message"));
    }

    // // Tests for PUT /api/ucsbdates?id=... 

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_ucsbdate() throws Exception {
            // arrange

            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
            LocalDateTime ldt2 = LocalDateTime.parse("2023-01-04T00:00:00");

            UCSBHelpRequest ucsbDate1 = UCSBHelpRequest.builder()
                            .requestTime(ldt1)
                            .solved(true)
                            .requesterEmail("test@test.com")
                            .explanation("test1")
                            .teamId("team1")
                            .tableOrBreakoutRoom("1-9")
                            .build();

            UCSBHelpRequest ucsbDate2 = UCSBHelpRequest.builder()
                            .requestTime(ldt2)
                            .solved(false)
                            .requesterEmail("test2@test.com")
                            .explanation("test2")
                            .teamId("team2")
                            .tableOrBreakoutRoom("1-8")
                            .build();

            String requestBody = mapper.writeValueAsString(ucsbDate2);

            when(ucsbHelpRequestsRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbDate1));

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/helprequest?id=67")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbHelpRequestsRepository, times(1)).findById(67L);
            verify(ucsbHelpRequestsRepository, times(1)).save(ucsbDate2); // should be saved with correct user
            String responseString = response.getResponse().getContentAsString();
            assertEquals(requestBody, responseString);
    }

    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_ucsbdate_that_does_not_exist() throws Exception {
            // arrange

            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

            UCSBHelpRequest ucsbDate1 = UCSBHelpRequest.builder()
                            .requestTime(ldt1)
                            .solved(false)
                            .requesterEmail("test@test.com")
                            .explanation("test1")
                            .teamId("team1")
                            .tableOrBreakoutRoom("1-9")
                            .build();

            String requestBody = mapper.writeValueAsString(ucsbDate1);

            when(ucsbHelpRequestsRepository.findById(eq(67L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/helprequest?id=67")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(ucsbHelpRequestsRepository, times(1)).findById(67L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBHelpRequest with id 67 not found", json.get("message"));

    }
}
