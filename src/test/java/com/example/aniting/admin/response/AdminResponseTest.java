package com.example.aniting.admin.response;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.example.aniting.entity.RecommendResponse;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.RecommendResponseRepository;
import com.example.aniting.repository.UsersRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminResponseTest {

	@Autowired 
	private MockMvc mockMvc;
	
    @Autowired 
    private RecommendResponseRepository recommendResponseRepository;
    
    @Autowired 
    private UsersRepository usersRepository;

    private Long testResponseId;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
        recommendResponseRepository.deleteAll();

        // 테스트 유저 등록
        Users user = usersRepository.save(Users.builder()
                .usersId("resUser")
                .usersNm("응답테스터")
                .passwd("pw")
                .securityQuestion("질문")
                .securityAnswer("답")
                .joinAt(LocalDateTime.now())
                .activeYn("Y")
                .build());

        // 테스트 응답 등록
        RecommendResponse response = recommendResponseRepository.save(RecommendResponse.builder()
                .usersId(user.getUsersId())
                .questionOrder(1)
                .question("당신의 성격은?")
                .answer("조용하고 내성적이에요")
                .createdAt(LocalDateTime.of(2025, 5, 1, 10, 30))
                .build());

        testResponseId = response.getResponseId();
    }

    @Test
    void 추천응답_전체조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/responses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usersId").value("resUser"))
                .andExpect(jsonPath("$[0].questionOrder").value(1))
                .andExpect(jsonPath("$[0].question").value("당신의 성격은?"));
    }

    @Test
    void 추천응답_필터검색_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/responses")
                .param("usersId", "resUser")
                .param("keyword", "성격")
                .param("from", "2025-05-01T00:00:00")
                .param("to", "2025-05-01T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].answer").value("조용하고 내성적이에요"));
    }

    @Test
    void 추천응답_삭제_테스트() throws Exception {
        mockMvc.perform(delete("/api/admin/responses/" + testResponseId))
                .andExpect(status().isOk());

        assertFalse(recommendResponseRepository.findById(testResponseId).isPresent());
    }
	
}
