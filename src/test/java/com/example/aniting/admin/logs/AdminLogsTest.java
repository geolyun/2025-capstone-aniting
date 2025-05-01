package com.example.aniting.admin.logs;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.aniting.entity.RecommendLog;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.RecommendLogRepository;
import com.example.aniting.repository.UsersRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminLogsTest {

	@Autowired 
	private MockMvc mockMvc;
    
	@Autowired 
	private RecommendLogRepository recommendLogRepository;
    
	@Autowired 
	private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
        recommendLogRepository.deleteAll();

        // 테스트 유저 생성
        Users user = usersRepository.save(Users.builder()
                .usersId("logUser")
                .usersNm("로그테스터")
                .passwd("pw")
                .securityQuestion("질문")
                .securityAnswer("답변")
                .joinAt(LocalDateTime.now())
                .activeYn("Y")
                .build());

        // 테스트 로그 생성
        recommendLogRepository.save(RecommendLog.builder()
                .usersId(user.getUsersId())
                .aiPrompt("추천 질문: 성격은 어떤가요?")
                .aiResponse("{\"추천\":\"고양이\"}")
                .createdAt(LocalDateTime.of(2025, 5, 1, 14, 0))
                .build());
    }

    @Test
    void 추천로그_전체조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usersId").value("logUser"))
                .andExpect(jsonPath("$[0].aiPrompt").value("추천 질문: 성격은 어떤가요?"))
                .andExpect(jsonPath("$[0].aiResponse").value("{\"추천\":\"고양이\"}"));
    }

    @Test
    void 추천로그_필터조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/logs")
                .param("usersId", "logUser")
                .param("keyword", "성격")
                .param("from", "2025-05-01T00:00:00")
                .param("to", "2025-05-01T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].aiResponse").value("{\"추천\":\"고양이\"}"));
    }

    @Test
    void 추천로그_존재하지않는_필터_조회시_빈배열() throws Exception {
        mockMvc.perform(get("/api/admin/logs")
                .param("usersId", "noUser")
                .param("keyword", "없음"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
	
}
