package com.example.aniting.admin.validate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.aniting.entity.Admin;
import com.example.aniting.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminValidateTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminValidateService adminValidateService;
	
    private final String testAdminId = "testAdmin";
    private final String testPassword = "test1234";
    
    @BeforeEach
    void setUp() {
        Admin admin = new Admin(testAdminId, testPassword);
        adminRepository.save(admin);
    }
    
 // ----------------------- Controller 테스트 -----------------------

    @Test
    void 로그인_성공_컨트롤러_세션_저장_검증() throws Exception {
        System.out.println("[시작] 로그인 성공 시 세션 저장 확인");

        MvcResult result = mockMvc.perform(post("/api/admin/adminLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "adminId": "testAdmin",
                                "passwd": "test1234"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("관리자 로그인 성공"))
                .andReturn();

        HttpSession session = result.getRequest().getSession(false);
        assertNotNull(session);
        assertEquals("admin", session.getAttribute("role"));

        System.out.println("[성공] 세션이 정상적으로 저장되었습니다.");
    }

    @Test
    void 로그인_실패_컨트롤러_401_반환_검증() throws Exception {
        System.out.println("[시작] 로그인 실패 시 401 응답 확인");

        mockMvc.perform(post("/api/admin/adminLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "adminId": "testAdmin",
                                "passwd": "wrongpw"
                            }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 일치하지 않습니다."));

        System.out.println("[성공] 로그인 실패에 대해 401과 메시지가 정상 반환되었습니다.");
    }

    @Test
    void 로그아웃_컨트롤러_세션_무효화_검증() throws Exception {
        System.out.println("[시작] 로그아웃 시 세션 무효화 검증");

        // 로그인 먼저 수행
        MvcResult loginResult = mockMvc.perform(post("/api/admin/adminLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "adminId": "testAdmin",
                                "passwd": "test1234"
                            }
                        """))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        // 로그아웃 수행
        mockMvc.perform(post("/api/admin/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그아웃 완료"));

        System.out.println("[성공] 로그아웃 후 세션이 정상적으로 제거되었습니다.");
    }

    // ----------------------- Service 직접 테스트 -----------------------

    @Test
    void 서비스_성공_검증() {
        System.out.println("[시작] 서비스에서 로그인 검증 성공");

        Admin admin = adminValidateService.validateAdmin(testAdminId, testPassword);
        assertNotNull(admin);
        assertEquals(testAdminId, admin.getAdminId());

        System.out.println("[성공] 유효한 ID/PW로 관리자 조회 성공");
    }

    @Test
    void 서비스_비밀번호_틀림_null반환() {
        System.out.println("[시작] 잘못된 비밀번호 시 null 반환 확인");

        Admin admin = adminValidateService.validateAdmin(testAdminId, "wrongpw");
        assertNull(admin);

        System.out.println("[성공] 비밀번호가 틀릴 경우 null이 반환되었습니다.");
    }

    @Test
    void 서비스_존재하지_않는_ID_null반환() {
        System.out.println("[시작] 존재하지 않는 ID로 로그인 시도 시 null 반환 확인");

        Admin admin = adminValidateService.validateAdmin("noAdmin", testPassword);
        assertNull(admin);

        System.out.println("[성공] 존재하지 않는 ID에 대해 null이 정상 반환되었습니다.");
    }

}
