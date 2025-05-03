package com.example.aniting.users;

import com.example.aniting.dto.UsersDTO;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsersServiceImplTest {

    @Autowired private UsersRepository usersRepository;
    @Autowired private UsersService usersService;

    @Test
    // 회원가입 시 모든 필드가 저장되는지 검증
    void register_savesUserWithAllFields() {
        UsersDTO dto = new UsersDTO();
        dto.setUsersId("registerUser");
        dto.setUsersNm("홍길동");
        dto.setPasswd("1234");
        dto.setSecurityQuestion("Q1");
        dto.setSecurityAnswer("A1");

        boolean result = usersService.register(dto);
        assertTrue(result);

        Users user = usersRepository.findByUsersId("registerUser").orElseThrow();
        assertEquals("홍길동", user.getUsersNm());
        assertEquals("Q1", user.getSecurityQuestion());
        assertEquals("A1", user.getSecurityAnswer());
        assertEquals("Y", user.getActiveYn());
        assertNotNull(user.getJoinAt());
        assertNotEquals("1234", user.getPasswd());
    }

    @Test
    // 아이디 중복 여부 확인 기능 테스트
    void checkDuplicateId_returnsTrueIfExists() {
        Users user = new Users();
        user.setUsersId("dupUser");
        user.setUsersNm("Test");
        user.setPasswd("pw");
        user.setSecurityQuestion("Q");
        user.setSecurityAnswer("A");
        user.setJoinAt(LocalDateTime.now());
        user.setActiveYn("Y");
        usersRepository.save(user);

        assertTrue(usersService.checkDuplicateId("dupUser"));
    }

    @Test
    // 비밀번호 일치 + 활성 계정 시 로그인 성공 검증
    void login_returnsDtoIfPasswordMatchesAndActive() {
        String rawPassword = "pw";
        String encoded = new BCryptPasswordEncoder().encode(rawPassword);
        Users user = new Users(null, "loginUser", "Tester", encoded, "Q", "A",
                LocalDateTime.now(), null, "Y", null);
        usersRepository.save(user);

        UsersDTO dto = usersService.login("loginUser", rawPassword);
        assertNotNull(dto);
        assertEquals("Tester", dto.getUsersNm());
    }

    @Test
    // 보안 질문 및 답변 일치 여부 검증
    void verifySecurityAnswer_returnsTrueIfMatches() {
        Users user = new Users(null, "verifyUser", "Tester", "pw", "Q", "A",
                LocalDateTime.now(), null, "Y", null);
        usersRepository.save(user);

        assertTrue(usersService.verifySecurityAnswer("verifyUser", "Q", "A"));
    }

    @Test
    // 비밀번호 재설정 및 암호화 저장 확인
    void resetPassword_updatesAndEncryptsPassword() {
        Users user = new Users();
        user.setUsersId("resetUser");
        user.setUsersNm("Tester");
        user.setPasswd(new BCryptPasswordEncoder().encode("old"));
        user.setSecurityQuestion("Q");
        user.setSecurityAnswer("A");
        user.setJoinAt(LocalDateTime.now());
        user.setActiveYn("Y");
        usersRepository.save(user);

        boolean success = usersService.resetPassword("resetUser", "newpass");
        assertTrue(success);

        Users updated = usersRepository.findByUsersId("resetUser").get();
        assertNotEquals("newpass", updated.getPasswd());
        assertNotNull(updated.getUpdatedAt());
    }
}