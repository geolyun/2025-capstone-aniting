package com.example.aniting.users;

import com.example.aniting.dto.UsersDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UsersController.class)
class UsersControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UsersService usersService;

    @Test
    // 회원가입 요청이 성공적으로 처리되는지 확인
    void register_returnsOkIfSuccess() throws Exception {
        when(usersService.register(any())).thenReturn(true);

        String json = "{\"usersId\":\"testuser\",\"usersNm\":\"홍길동\",\"passwd\":\"1234\",\"securityQuestion\":\"애완동물 이름은?\",\"securityAnswer\":\"치즈\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    // ID 중복 시 'duplicate' 응답 반환 확인
    void checkDuplicateId_returnsDuplicate() throws Exception {
        when(usersService.checkDuplicateId("id")).thenReturn(true);

        mockMvc.perform(get("/api/users/check-id?usersId=id"))
                .andExpect(status().isOk())
                .andExpect(content().string("duplicate"));
    }

    @Test
    // 잘못된 로그인 정보 시 401 오류 반환 확인
    void loginFails_whenInvalidCredentials_returns401() throws Exception {
        when(usersService.login(any(), any())).thenReturn(null);

        String loginJson = "{\"usersId\":\"wrong\",\"passwd\":\"wrongpass\"}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
    @Test
    // 보안 질문 검증 성공 시 'success' 응답 반환
    void verifySecurity_returnsSuccess() throws Exception {
        when(usersService.verifySecurityAnswer(any(), any(), any())).thenReturn(true);
        String json = "{\"usersId\":\"user1\",\"securityQuestion\":\"Q\",\"securityAnswer\":\"A\"}";

        mockMvc.perform(post("/api/users/verify_security")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    // 비밀번호 재설정 성공 시 메시지 응답 확인
    void resetPassword_returnsSuccess() throws Exception {
        when(usersService.resetPassword(any(), any())).thenReturn(true);
        String json = "{\"usersId\":\"user1\",\"newPassword\":\"newpass\"}";

        mockMvc.perform(post("/api/users/reset_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호 재설정 완료"));
    }

    @Test
    // 로그아웃 시 성공 메시지 반환 확인
    void logout_returnsSuccessMessage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UsersDTO());

        mockMvc.perform(post("/api/users/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다."));
    }
}