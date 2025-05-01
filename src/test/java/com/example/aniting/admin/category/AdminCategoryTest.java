package com.example.aniting.admin.category;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.aniting.entity.Category;
import com.example.aniting.repository.CategoryRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminCategoryTest {

	@Autowired 
	private MockMvc mockMvc;
	
    @Autowired 
    private CategoryRepository categoryRepository;

    private Long testCategoryId;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        Category category = categoryRepository.save(Category.builder()
                .category("성격")
                .scoreStandard("1~5")
                .standardDescription("내향적~외향적")
                .build());

        testCategoryId = category.getCategoryId();
    }

    @Test
    void 카테고리_목록_조회() throws Exception {
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("성격"));
    }

    @Test
    void 카테고리_단건_조회() throws Exception {
        mockMvc.perform(get("/api/admin/categories/" + testCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreStandard").value("1~5"));
    }

    @Test
    void 카테고리_등록() throws Exception {
        String newCategoryJson = """
            {
                "category": "크기",
                "scoreStandard": "1~3",
                "standardDescription": "소형~대형"
            }
        """;

        mockMvc.perform(post("/api/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCategoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("크기"));
    }

    @Test
    void 카테고리_수정() throws Exception {
        String updateJson = """
            {
                "category": "성격(수정)",
                "scoreStandard": "1~7",
                "standardDescription": "내성적~외향적"
            }
        """;

        mockMvc.perform(put("/api/admin/categories/" + testCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("성격(수정)"))
                .andExpect(jsonPath("$.scoreStandard").value("1~7"));
    }

    @Test
    void 카테고리_삭제() throws Exception {
        mockMvc.perform(delete("/api/admin/categories/" + testCategoryId))
                .andExpect(status().isOk());

        assertFalse(categoryRepository.findById(testCategoryId).isPresent());
    }
	
}
