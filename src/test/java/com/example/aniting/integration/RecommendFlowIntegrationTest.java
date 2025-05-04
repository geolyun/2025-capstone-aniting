package com.example.aniting.integration;

import com.example.aniting.entity.Admin;
import com.example.aniting.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RecommendFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AdminRepository adminRepository;
    private MockHttpSession session;

    @BeforeEach
    void setupUser() throws Exception {

        Admin admin = new Admin();
        admin.setAdminId("admin");
        admin.setPasswd("admin123");
        adminRepository.save(admin);

        // 1. 회원가입
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "usersId": "testuser",
                      "passwd": "1234",
                      "usersNm": "홍길동",
                      "securityQuestion": "좋아하는 동물은?",
                      "securityAnswer": "고양이"
                    }
                """))
                .andExpect(status().isOk());

        // 2. 로그인 및 세션 생성
        session = new MockHttpSession();
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "usersId": "testuser",
                      "passwd": "1234"
                    }
                """)
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    void 사용자_추천_전체_흐름_및_관리자_요약_페이지_테스트() throws Exception {
        // 3. 질문 생성 요청
        mockMvc.perform(get("/recommend/questions").session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));


        // 4. GPT 추천 요청 (submitAnswers는 /recommend/submit, user-id 헤더 필요)
        String answersJson = """
            {
              "answers": [
                { "question": "당신의 하루 활동량은?", "answer": "보통" },
                { "question": "정서적으로 교감하길 원하나요?", "answer": "매우 그렇다" }
              ]
            }
        """;

        mockMvc.perform(post("/recommend/submit")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("user-id", "testuser")
                        .content(answersJson))
                .andExpect(status().isOk());

        // 5. 관리자 로그인
        MockHttpSession adminSession = new MockHttpSession();
        mockMvc.perform(post("/api/admin/adminLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "adminId": "admin",
                      "passwd": "admin123"
                    }
                """)
                        .session(adminSession))
                .andExpect(status().isOk());

        // 6. 관리자 요약 통계 확인
        mockMvc.perform(get("/api/admin/dashboard/summary")
                        .session(adminSession))
                .andExpect(status().isOk());
    }

    @Test
    void 추천_결과_페이지_및_지도페이지_리디렉션_테스트() throws Exception {
        session.setAttribute("recommendationResult", new Object()); // 가짜 추천 결과 세션

        mockMvc.perform(get("/recommend/result").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/recommend/map").session(session))
                .andExpect(status().isOk());
    }
}
