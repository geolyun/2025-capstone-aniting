package com.example.aniting.users;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.aniting.dto.UsersDTO;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.UsersRepository;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public boolean register(UsersDTO dto) {
		
		if (usersRepository.existsByUsersId(dto.getUsersId())) return false;
		
		Users user = new Users();
        user.setUsersId(dto.getUsersId());
        user.setUsersNm(dto.getUsersNm());
        user.setPasswd(passwordEncoder.encode(dto.getPasswd()));
        user.setSecurityQuestion(dto.getSecurityQuestion());
        user.setSecurityAnswer(dto.getSecurityAnswer());
        user.setJoinAt(LocalDateTime.now());
        user.setActiveYn("Y");

        usersRepository.save(user);
		
		return true;
		
	}

	@Override
	public boolean checkDuplicateId(String usersId) {
		return usersRepository.existsByUsersId(usersId);
	}

	@Override
    public UsersDTO login(String usersId, String passwd) {
		
        return usersRepository.findByUsersId(usersId)
            .filter(user -> 
            	passwordEncoder.matches(passwd, user.getPasswd()) &&
            	"Y".equals(user.getActiveYn()))
            .map(user -> new UsersDTO(
                user.getUsersNo(),
                user.getUsersId(),
                user.getUsersNm(),
                null, // 비밀번호 제외
                user.getSecurityQuestion(),
                user.getSecurityAnswer(),
                user.getJoinAt(),
                user.getUpdatedAt(),
                user.getActiveYn(),
                user.getInactiveAt()
            ))
            .orElse(null);
        
    }

	@Override
	public boolean verifySecurityAnswer(String usersId, String question, String answer) {
		
        return usersRepository.findByUsersId(usersId)
            .filter(user ->
                user.getSecurityQuestion().equals(question) &&
                user.getSecurityAnswer().equals(answer)
            )
            .isPresent();
        
    }

	@Override
	public boolean resetPassword(String usersId, String newPassword) {
		
        return usersRepository.findByUsersId(usersId)
            .map(user -> {
                user.setPasswd(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(LocalDateTime.now());
                usersRepository.save(user);
                return true;
            })
            .orElse(false);
        
    }


}
