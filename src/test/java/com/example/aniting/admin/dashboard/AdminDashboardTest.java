package com.example.aniting.admin.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.example.aniting.entity.Category;
import com.example.aniting.entity.Pet;
import com.example.aniting.entity.RecommendHistory;
import com.example.aniting.entity.Score;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.CategoryRepository;
import com.example.aniting.repository.PetRepository;
import com.example.aniting.repository.RecommendHistoryRepository;
import com.example.aniting.repository.ScoreRepository;
import com.example.aniting.repository.UsersRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminDashboardTest {

	@Autowired 
	private MockMvc mockMvc;
	
    @Autowired 
    private UsersRepository usersRepository;
    
    @Autowired 
    private CategoryRepository categoryRepository;
    
    @Autowired 
    private PetRepository petRepository;
    
    @Autowired 
    private ScoreRepository scoreRepository;
    
    @Autowired 
    private RecommendHistoryRepository recommendHistoryRepository;

    @BeforeEach
    void setUp() {
        // 유저
        usersRepository.save(Users.builder()
                .usersId("user1")
                .usersNm("테스트유저1")
                .passwd("pw")
                .securityQuestion("질문")
                .securityAnswer("답")
                .joinAt(LocalDateTime.now().minusDays(1))
                .activeYn("Y")
                .build());

        usersRepository.save(Users.builder()
                .usersId("user2")
                .usersNm("테스트유저2")
                .passwd("pw")
                .securityQuestion("질문")
                .securityAnswer("답")
                .joinAt(LocalDateTime.now().minusDays(20))
                .activeYn("N")
                .inactiveAt(LocalDateTime.now().minusDays(20))
                .build());

        // 카테고리 생성
        var category = categoryRepository.save(Category.builder()
                .category("테스트카테고리")
                .scoreStandard("1~5")
                .standardDescription("기준")
                .build());

        // 점수 (scoreValue는 Integer 타입)
        scoreRepository.save(Score.builder()
                .usersId("user1")
                .categoryId(category.getCategoryId())
                .scoreValue(5)
                .build());

        // 반려동물
        var pet = petRepository.save(Pet.builder()
                .petNm("말티즈")
                .species("강아지")
                .breed("말티즈")
                .isSpecial("N")
                .categoryIds("1")
                .build());

        // 추천 히스토리 (top1PetId는 Long 타입)
        recommendHistoryRepository.save(RecommendHistory.builder()
                .usersId("user1")
                .top1PetId(pet)
                .createdAt(LocalDateTime.now())
                .build());
    }

	
    @Test
    void 대시보드_요약통계_API_응답_확인() throws Exception {
        System.out.println("[시작] 대시보드 요약 통계 API 응답 검증");

        mockMvc.perform(get("/api/admin/dashboard/summary")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalUsers").value(2))
            .andExpect(jsonPath("$.inactiveUsers").value(1))
            .andExpect(jsonPath("$.newUsers7days").value(1))
            .andExpect(jsonPath("$.totalPets").exists())
            .andExpect(jsonPath("$.petCounts").exists())
            .andExpect(jsonPath("$.totalRecommendations").exists())
            .andExpect(jsonPath("$.recommendRate").exists())
            .andExpect(jsonPath("$.anomalyUsers").exists());

        System.out.println("[성공] 대시보드 요약 통계 응답이 정상적으로 반환되었습니다.");
    }
    
}
