package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationsController.class)
@Import(TestConfig.class)
public class UCSBOrganizationsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBOrganizationsRepository ucsbOrganizationsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbOrganizations/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbOrganizations/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbOrganizations/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbOrganizationsRepository.findById(eq("OSLI"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbOrganizations?orgCode=OSLI"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbOrganizationsRepository, times(1)).findById(eq("OSLI"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganizations with id OSLI not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbOrganizations() throws Exception {

                // arrange

                UCSBOrganizations osli = UCSBOrganizations.builder()
                                .orgCode("OSLI")
                                .orgTranslationShort("STUDENT_LIFE")
                                .orgTranslation("OFFICE_OF_STUDENT_LIFE")
                                .inactive(false)
                                .build();

                UCSBOrganizations sky = UCSBOrganizations.builder()
                        .orgCode("SKY")
                        .orgTranslationShort("SKYDIVING_CLUB")
                        .orgTranslation("SKYDIVING_CLUB_AT_UCSB")
                        .inactive(false)
                        .build();

                ArrayList<UCSBOrganizations> expectedOrgs = new ArrayList<>();
                expectedOrgs.addAll(Arrays.asList(osli, sky));

                when(ucsbOrganizationsRepository.findAll()).thenReturn(expectedOrgs);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbOrganizations/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedOrgs);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbOrganizations...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbOrganizations/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbOrganizations/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_Orgs() throws Exception {
                // arrange

                UCSBOrganizations sky = UCSBOrganizations.builder()
                        .orgCode("SKY")
                        .orgTranslationShort("SKYDIVING_CLUB")
                        .orgTranslation("SKYDIVING_CLUB_AT_UCSB")
                        .inactive(true)
                        .build();

                when(ucsbOrganizationsRepository.save(eq(sky))).thenReturn(sky);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbOrganizations/post?orgCode=SKY&orgTranslationShort=SKYDIVING_CLUB&orgTranslation=SKYDIVING_CLUB_AT_UCSB&inactive=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).save(sky);
                String expectedJson = mapper.writeValueAsString(sky);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }


        // Tests for GET /api/ucsbOrganizations?...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsbOrganizations?orgCode=OSLI"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                UCSBOrganizations orgs = UCSBOrganizations.builder()
                        .orgCode("OSLI")
                        .orgTranslationShort("STUDENT_LIFE")
                        .orgTranslation("OFFICE_OF_STUDENT_LIFE")
                        .inactive(false)
                        .build();

                when(ucsbOrganizationsRepository.findById(eq("OSLI"))).thenReturn(Optional.of(orgs));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbOrganizations?orgCode=OSLI"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationsRepository, times(1)).findById(eq("OSLI"));
                String expectedJson = mapper.writeValueAsString(orgs);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for DELETE /api/ucsbOrganizations?...

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_date() throws Exception {
                // arrange

                UCSBOrganizations zpr = UCSBOrganizations.builder()
                        .orgCode("ZPR")
                        .orgTranslationShort("ZETA_PHI_RHO")
                        .orgTranslation("ZETA_PHI_RHO")
                        .inactive(false)
                        .build();

                when(ucsbOrganizationsRepository.findById(eq("ZPR"))).thenReturn(Optional.of(zpr));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsbOrganizations?orgCode=ZPR")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("ZPR");
                verify(ucsbOrganizationsRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id ZPR deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_Orgs_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbOrganizationsRepository.findById(eq("OSLI"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsbOrganizations?orgCode=OSLI")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("OSLI");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id OSLI not found", json.get("message"));
        }

        // Tests for PUT /api/ucsbOrganizations?...

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_Orgs() throws Exception {
                // arrange

                UCSBOrganizations osliOrig = UCSBOrganizations.builder()
                        .orgCode("OSLI")
                        .orgTranslationShort("STUDENT_LIFE")
                        .orgTranslation("OFFICE_OF_STUDENT_LIFE")
                        .inactive(false)
                        .build();

                UCSBOrganizations osliEdited = UCSBOrganizations.builder()
                        .orgCode("OSLI")
                        .orgTranslationShort("STUDENT_LIFE1")
                        .orgTranslation("OFFICE_OF_STUDENT_LIFE1")
                        .inactive(true)
                        .build();

                String requestBody = mapper.writeValueAsString(osliEdited);

                when(ucsbOrganizationsRepository.findById(eq("OSLI"))).thenReturn(Optional.of(osliOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsbOrganizations?orgCode=OSLI")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("OSLI");
                verify(ucsbOrganizationsRepository, times(1)).save(osliEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_Orgs_that_does_not_exist() throws Exception {
                // arrange

                UCSBOrganizations editedOrgs = UCSBOrganizations.builder()
                        .orgCode("OSLI")
                        .orgTranslationShort("STUDENT_LIFE")
                        .orgTranslation("OFFICE_OF_STUDENT_LIFE")
                        .inactive(false)
                        .build();

                String requestBody = mapper.writeValueAsString(editedOrgs);

                when(ucsbOrganizationsRepository.findById(eq("OSLI"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsbOrganizations?orgCode=OSLI")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("OSLI");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id OSLI not found", json.get("message"));

        }
}
