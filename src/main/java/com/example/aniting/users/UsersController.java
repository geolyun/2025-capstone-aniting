package com.example.aniting.users;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.dto.UsersDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
public class UsersController {

	@Autowired
	private UsersService usersService;
	
	@PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UsersDTO dto) {
        boolean success = usersService.register(dto);
        return success ?
            ResponseEntity.ok("회원가입 완료") :
            ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 아이디입니다.");
    }
	
	@GetMapping("/check-id")
	public ResponseEntity<String> checkDuplicateId(@RequestParam String usersId) {
	    boolean exists = usersService.checkDuplicateId(usersId);
	    return ResponseEntity.ok(exists ? "duplicate" : "available");
	}
	
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginInfo, HttpServletRequest request) {
		
        String usersId = loginInfo.get("usersId");
        String passwd = loginInfo.get("passwd");

        UsersDTO user = usersService.login(usersId, passwd);
        
        if (user != null) {
        	
            // 세션 저장
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("role", "user");

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "로그인 성공",
                "usersNm", user.getUsersNm(),
                "usersId", user.getUsersId()
            ));
            
        } 
        else {
        	
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "아이디 또는 비밀번호가 일치하지 않습니다."
            ));
            
        }
        
    }
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		
	    HttpSession session = request.getSession(false);
	    
	    if (session != null) {
	        session.invalidate();
	    }
	    
	    return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
	    
	}
	
	@PostMapping("/verify_security")
    public ResponseEntity<String> verifySecurity(@RequestBody Map<String, String> data) {
		
        String usersId = data.get("usersId");
        String question = data.get("securityQuestion");
        String answer = data.get("securityAnswer");

        boolean match = usersService.verifySecurityAnswer(usersId, question, answer);
        return match ?
            ResponseEntity.ok("success") :
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("정보가 일치하지 않습니다.");
        
    }
	
	@PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> data) {
		
        String usersId = data.get("usersId");
        String newPassword = data.get("newPassword");

        boolean result = usersService.resetPassword(usersId, newPassword);
        return result ?
            ResponseEntity.ok("비밀번호 재설정 완료") :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패");
        
    }

}
