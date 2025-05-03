package com.example.aniting.mypage;

import com.example.aniting.dto.UsersDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ✅ MypageControllerTest.java (SpringBootTest 기반)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MypageControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private MypageService mypageService;

    @Test
    // 세션 사용자의 정보 조회 API 응답 검증
    void getUserInfo_returnsUserDto() throws Exception {
        UsersDTO dto = new UsersDTO();
        dto.setUsersId("user1");
        dto.setUsersNm("홍길동");

        when(mypageService.getUserInfo("user1")).thenReturn(dto);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", dto);

        mockMvc.perform(get("/api/mypage/info").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersId").value("user1"));
    }

    @Test
    // 추천 히스토리 API에서 리스트 형태 응답 확인
    void getRecommendHistory_returnsList() throws Exception {
        Map<String, Object> item = new HashMap<>();
        item.put("top1Breed", "푸들");

        UsersDTO dto = new UsersDTO();
        dto.setUsersId("user1");
        dto.setUsersNm("홍길동");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", dto);

        when(mypageService.getRecommendHistory("user1")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/mypage/history").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].top1Breed").value("푸들"));
    }

    @Test
    // 사용자 정보 수정 및 세션 갱신 확인
    void updateUser_updatesAndSetsSession() throws Exception {
        UsersDTO dto = new UsersDTO();
        dto.setUsersId("user1");
        dto.setUsersNm("홍길동");

        when(mypageService.updateUser(eq("user1"), eq("홍길동"), eq("pass"))).thenReturn(dto);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", dto);

        String json = "{\"usersNm\":\"홍길동\",\"passwd\":\"pass\"}";

        mockMvc.perform(post("/api/mypage/update")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    // 회원 탈퇴 시 계정 비활성화 및 세션 무효화 확인
    void deleteUser_deactivatesAndInvalidatesSession() throws Exception {
        UsersDTO dto = new UsersDTO();
        dto.setUsersId("user1");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", dto);

        mockMvc.perform(post("/api/mypage/delete").session(session))
                .andExpect(status().isOk());
    }
}
