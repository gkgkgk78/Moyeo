//package com.danim.controller;
//
//import com.danim.entity.User;
//import com.danim.service.UserServiceImpl;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.JUnitRestDocumentation;
//import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
//import org.springframework.restdocs.payload.JsonFieldType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.filter.CharacterEncodingFilter;
//import static org.assertj.core.api.Assertions.assertThat;
//@RunWith(SpringRunner.class)
////@SpringBootTest
////@AutoConfigureMockMvc
//@WebMvcTest({UserController.class})
//@MockBean(JpaMetamodelMappingContext.class)
//public class UserControllerTest {
//    @Rule
//    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
//    @Autowired
//    private MockMvc mock;
//    @Autowired
//    ObjectMapper objectMapper;
//    @MockBean
//    private UserServiceImpl userServiceImpl;
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//    @Before
//    public void setUp() {
//        this.mock = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .apply(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation).snippets()
////                        .withTemplateFormat(TemplateFormats.markdown()))
//                        .withDefaults(curlRequest()))
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))
//                .alwaysDo(print())
//                .build();
//    }
//
//    @Test
//    @DisplayName("User register")
//    public void insertUserTest()  throws Exception{
//        // given
//        User user = User.builder().userUid(21l).nickname("JiyulTest").clientId("1234").build();
//        // when
//        mock.perform(post("/api/auth/user/user")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(user)))
//                .andDo(MockMvcResultHandlers.print())
//
//                .andExpect(status().isOk())
//                .andDo(
//                        document("/user", requestFields(
//                                        fieldWithPath("userUid").type(JsonFieldType.NUMBER).description(" userUid "),
//                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description(" nickname => Must Not Be Null "),
//                                        fieldWithPath("clientId").type(JsonFieldType.STRING).description("clientId"),
//                                        fieldWithPath("role").type(JsonFieldType.NULL).description("role"),
//                                        fieldWithPath("refreshToken").type(JsonFieldType.NULL).description("refreshToken"),
//                                        fieldWithPath("profileImageUrl").type(JsonFieldType.NULL).description("profileImageUrl")
//                                )))
//                .andReturn();
////        assertThat(user.getUserUid()).isEqualTo(2l);
//    }
//}
