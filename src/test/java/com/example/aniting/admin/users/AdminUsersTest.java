package com.example.aniting.admin.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.aniting.entity.Users;
import com.example.aniting.repository.UsersRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminUsersTest {

	@Autowired 
	private MockMvc mockMvc;
	
    @Autowired 
    private UsersRepository usersRepository;

    private Long testUserNo;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();

        Users user = usersRepository.save(Users.builder()
                .usersId("adminUser1")
                .usersNm("관리자유저")
                .passwd("pw")
                .securityQuestion("질문")
                .securityAnswer("답")
                .joinAt(LocalDateTime.now().minusDays(3))
                .activeYn("Y")
                .build());

        testUserNo = user.getUsersNo();
    }

    @Test
    void 유저목록_조회_테스트() throws Exception {
        System.out.println("[시작] 유저 목록 전체 조회 테스트");

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usersId").value("adminUser1"));

        System.out.println("[성공] 유저 목록이 정상적으로 반환됨");
    }

    @Test
    void 유저목록_검색_테스트() throws Exception {
        System.out.println("[시작] 유저 검색 테스트 (keyword)");

        mockMvc.perform(get("/api/admin/users")
                .param("keyword", "adminUser1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usersId").value("adminUser1"));

        System.out.println("[성공] 검색어에 맞는 유저가 정상 조회됨");
    }

    @Test
    void 단일유저_조회_테스트() throws Exception {
        System.out.println("[시작] 단일 유저 조회 테스트");

        mockMvc.perform(get("/api/admin/users/" + testUserNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersId").value("adminUser1"));

        System.out.println("[성공] 유저 번호로 유저 상세 조회 성공");
    }

    @Test
    void 유저정보_수정_테스트() throws Exception {
        System.out.println("[시작] 유저 정보 수정 테스트");

        String updateJson = """
                {
                    "usersNm": "수정된이름",
                    "activeYn": "N"
                }
                """;

        mockMvc.perform(put("/api/admin/users/" + testUserNo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());

        Users updated = usersRepository.findById(testUserNo).orElseThrow();
        assertEquals("수정된이름", updated.getUsersNm());
        assertEquals("N", updated.getActiveYn());

        System.out.println("[성공] 유저 정보가 정상적으로 수정됨");
    }

    @Test
    void 유저_삭제_테스트() throws Exception {
        System.out.println("[시작] 유저 삭제 테스트");

        mockMvc.perform(delete("/api/admin/users/" + testUserNo))
                .andExpect(status().isOk());

        assertFalse(usersRepository.findById(testUserNo).isPresent());

        System.out.println("[성공] 유저가 정상적으로 삭제됨");
    }
	
}
