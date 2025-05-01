package com.example.aniting.admin.history;

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

import com.example.aniting.entity.Pet;
import com.example.aniting.entity.RecommendHistory;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.PetRepository;
import com.example.aniting.repository.RecommendHistoryRepository;
import com.example.aniting.repository.UsersRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminHistoryTest {

	@Autowired 
	private MockMvc mockMvc;
    
	@Autowired 
	private RecommendHistoryRepository recommendHistoryRepository;
    
	@Autowired 
	private UsersRepository usersRepository;
    
	@Autowired 
	private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        recommendHistoryRepository.deleteAll();
        petRepository.deleteAll();
        usersRepository.deleteAll();

        // 유저 생성
        Users user = usersRepository.save(Users.builder()
                .usersId("historyUser")
                .usersNm("히스토리유저")
                .passwd("pw")
                .securityQuestion("질문")
                .securityAnswer("답")
                .joinAt(LocalDateTime.now())
                .activeYn("Y")
                .build());

        // 반려동물 생성
        Pet top1 = petRepository.save(Pet.builder()
                .petNm("푸들")
                .species("강아지")
                .isSpecial("N")
                .categoryIds("1")
                .build());

        Pet top2 = petRepository.save(Pet.builder()
                .petNm("코숏")
                .species("고양이")
                .isSpecial("N")
                .categoryIds("2")
                .build());

        // 추천 결과 히스토리 등록
        recommendHistoryRepository.save(RecommendHistory.builder()
                .usersId(user.getUsersId())
                .top1PetId(top1)
                .top2PetId(top2)
                .aiReason("성격이 내향적이고 혼자 있는 걸 좋아합니다.")
                .createdAt(LocalDateTime.of(2025, 5, 1, 12, 0))
                .build());
    }

    @Test
    void 추천결과_전체조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usersId").value("historyUser"))
                .andExpect(jsonPath("$[0].top1PetId").isNumber())
                .andExpect(jsonPath("$[0].aiReason").value("성격이 내향적이고 혼자 있는 걸 좋아합니다."));
    }

    @Test
    void 추천결과_필터조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/history")
                .param("usersId", "historyUser")
                .param("from", "2025-05-01")
                .param("to", "2025-05-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].top2PetId").isNumber());
    }

    @Test
    void 추천결과_잘못된필터_조회시_빈배열() throws Exception {
        mockMvc.perform(get("/api/admin/history")
                .param("usersId", "없음")
                .param("from", "2024-01-01")
                .param("to", "2024-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
	
}
