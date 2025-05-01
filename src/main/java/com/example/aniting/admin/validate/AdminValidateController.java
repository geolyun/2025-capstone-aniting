package com.example.aniting.admin.validate;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.dto.AdminDTO;
import com.example.aniting.entity.Admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminValidateController {

	@Autowired
	private AdminValidateService adminValidateService;
	
	@PostMapping("/adminLogin")
	public ResponseEntity<?> adminLogin(@RequestBody AdminDTO adminDTO, HttpServletRequest request) {
		
		Admin admin = adminValidateService.validateAdmin(adminDTO.getAdminId(), adminDTO.getPasswd());
		
		if (admin != null) {
			
			HttpSession session = request.getSession();
			session.setAttribute("admin", admin);
			session.setAttribute("role", "admin");
			
			return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "관리자 로그인 성공"
            ));
			
		}
		else {
			return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "아이디 또는 비밀번호가 일치하지 않습니다."
            ));
		}
		
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		
	    HttpSession session = request.getSession();
	    if (session != null) {
	        session.invalidate(); // 세션 무효화
	    }
	    return ResponseEntity.ok(Map.of(
	        "success", true,
	        "message", "로그아웃 완료"
	    ));
	    
	}
	
}
