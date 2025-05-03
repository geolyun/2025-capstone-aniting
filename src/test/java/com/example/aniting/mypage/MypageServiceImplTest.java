package com.example.aniting.mypage;

import com.example.aniting.dto.UsersDTO;
import com.example.aniting.entity.Pet;
import com.example.aniting.entity.RecommendHistory;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.RecommendHistoryRepository;
import com.example.aniting.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MypageServiceImplTest {

    @InjectMocks private MypageServiceImpl mypageService;

    @Mock private UsersRepository usersRepository;
    @Mock private RecommendHistoryRepository recommendHistoryRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    // 존재하는 사용자의 정보 DTO로 반환 확인
    void getUserInfo_returnsDtoIfUserExists() {
        Users user = new Users();
        user.setUsersId("user1");
        user.setUsersNm("홍길동");
        user.setSecurityQuestion("Q");
        user.setJoinAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActiveYn("Y");

        when(usersRepository.findByUsersId("user1")).thenReturn(Optional.of(user));

        UsersDTO dto = mypageService.getUserInfo("user1");
        assertEquals("user1", dto.getUsersId());
        assertEquals("홍길동", dto.getUsersNm());
    }

    @Test
    // 추천 히스토리 매핑 결과 확인
    void getRecommendHistory_returnsMappedHistory() {
        RecommendHistory h = new RecommendHistory();
        h.setUsersId("user1");
        h.setCreatedAt(LocalDateTime.now());

        Pet p1 = new Pet(); p1.setPetNm("푸들");
        Pet p2 = new Pet(); p2.setPetNm("샴");
        Pet p3 = new Pet(); p3.setPetNm("골든");

        h.setTop1PetId(p1);
        h.setTop2PetId(p2);
        h.setTop3PetId(p3);
        h.setAiReason("성향에 잘 맞음");

        when(recommendHistoryRepository.findAll()).thenReturn(List.of(h));

        List<Map<String, Object>> result = mypageService.getRecommendHistory("user1");
        assertEquals(1, result.size());
        assertEquals("푸들", result.get(0).get("top1Breed"));
    }

    @Test
    // 사용자 이름/비밀번호 수정 반영 및 DTO 반환 확인
    void updateUser_updatesUserAndReturnsDto() {
        Users user = new Users();
        user.setUsersId("user1");
        user.setUsersNm("old");
        user.setSecurityQuestion("Q");
        user.setJoinAt(LocalDateTime.now());
        user.setActiveYn("Y");

        when(usersRepository.findByUsersId("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        UsersDTO dto = mypageService.updateUser("user1", "newName", "1234");
        assertEquals("newName", dto.getUsersNm());
    }

    @Test
    // 계정 비활성화 처리 및 저장 동작 확인
    void deactivateUser_setsInactiveFlag() {
        Users user = new Users();
        user.setUsersId("user1");
        user.setActiveYn("Y");

        when(usersRepository.findByUsersId("user1")).thenReturn(Optional.of(user));
        mypageService.deactivateUser("user1");

        assertEquals("N", user.getActiveYn());
        verify(usersRepository).save(user);
    }
}
