package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERS_NO")
    private Long usersNo; // 회원 고유 번호

    @Column(name = "USERS_ID", nullable = false, length = 20)
    private String usersId; // 로그인 아이디

    @Column(name = "USERS_NM", nullable = false, length = 50)
    private String usersNm; // 이름

    @Column(name = "PASSWD", nullable = false, length = 1000)
    private String passwd; // 비밀번호 (암호화 저장)

    @Column(name = "SECURITY_QUESTION", nullable = false, length = 255)
    private String securityQuestion; // 비밀번호 찾기 질문

    @Column(name = "SECURITY_ANSWER", nullable = false, length = 255)
    private String securityAnswer; // 비밀번호 찾기 답변

    @Column(name = "JOIN_AT", nullable = false)
    private LocalDateTime joinAt; // 가입 일자

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt; // 수정 일자

    @Column(name = "ACTIVE_YN", nullable = false, length = 1)
    private String activeYn; // 계정 활성화 여부 (Y/N)

    @Column(name = "INACTIVE_AT")
    private LocalDateTime inactiveAt; // 계정 비활성화 일시
}

