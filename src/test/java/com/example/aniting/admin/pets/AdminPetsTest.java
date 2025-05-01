package com.example.aniting.admin.pets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.example.aniting.entity.Pet;
import com.example.aniting.repository.PetRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminPetsTest {

	@Autowired 
	private MockMvc mockMvc;
	
    @Autowired 
    private PetRepository petRepository;

    private Long testPetId;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();

        Pet pet = petRepository.save(Pet.builder()
                .petNm("테스트말티즈")
                .species("강아지")
                .breed("말티즈")
                .personalityTags("활발,사교적")
                .careLevel("낮음")
                .isSpecial("N")
                .categoryIds("1")
                .description("작고 귀엽고 친화적인 강아지")
                .build());

        testPetId = pet.getPetId();
    }

    @Test
    void 반려동물_목록_조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petNm").value("테스트말티즈"));
    }

    @Test
    void 반려동물_검색_필터_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/pets")
                        .param("species", "강아지")
                        .param("keyword", "말티즈"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petNm").value("테스트말티즈"));
    }

    @Test
    void 반려동물_단건_조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/pets/" + testPetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petNm").value("테스트말티즈"));
    }

    @Test
    void 반려동물_수정_테스트() throws Exception {
        String updateJson = """
                {
                    "petNm": "수정된말티즈",
                    "species": "강아지",
                    "breed": "수정품종",
                    "personalityTags": "온순,얌전함",
                    "careLevel": "중간",
                    "isSpecial": "Y",
                    "categoryIds": "2,3",
                    "description": "수정된 설명입니다."
                }
                """;

        mockMvc.perform(put("/api/admin/pets/" + testPetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petNm").value("수정된말티즈"))
                .andExpect(jsonPath("$.isSpecial").value("Y"));
    }

    @Test
    void 반려동물_삭제_테스트() throws Exception {
        mockMvc.perform(delete("/api/admin/pets/" + testPetId))
                .andExpect(status().isOk());

        assertFalse(petRepository.findById(testPetId).isPresent());
    }

    @Test
    void 반려동물_전체_이름_ID_조회_테스트() throws Exception {
        mockMvc.perform(get("/api/admin/pets/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petNm").value("테스트말티즈"));
    }
	
}
