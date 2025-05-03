package com.example.aniting.ai;

import com.example.aniting.dto.UsersDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiChatControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    // 세션 없을 때 채팅 페이지 접근 시 리다이렉트 확인
    void chatPage_redirectsIfNoSession() throws Exception {
        MockHttpSession session = new MockHttpSession(); // ✅ 빈 세션 생성
        mockMvc.perform(get("/recommend/chat").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    // 세션 존재 시 채팅 페이지 정상 접근 확인
    void chatPage_returnsViewIfUserExists() throws Exception {
        UsersDTO user = new UsersDTO();
        user.setUsersId("user1");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(get("/recommend/chat").session(session))
                .andExpect(status().isOk());
    }

    @Test
    // 세션 존재 시 추천 결과/지도 페이지 접근 가능 여부 확인
    void resultPage_and_mapPage_accessibleIfSessionExists() throws Exception {
        UsersDTO user = new UsersDTO();
        user.setUsersId("user1");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(get("/recommend/result").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/recommend/map").session(session))
                .andExpect(status().isOk());
    }

    @Test
    // 세션 없을 때 결과/지도 페이지 접근 시 리다이렉트 되는지 확인
    void resultPage_and_mapPage_redirectIfNoSession() throws Exception {
        MockHttpSession session = new MockHttpSession(); // ✅ 세션 객체는 존재해야 함
        mockMvc.perform(get("/recommend/result").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/recommend/map").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
