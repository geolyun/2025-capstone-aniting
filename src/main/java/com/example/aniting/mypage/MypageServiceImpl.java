package com.example.aniting.mypage;

import java.awt.Point;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.aniting.dto.RecommendHistoryDTO;
import com.example.aniting.dto.UsersDTO;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.RecommendHistoryRepository;
import com.example.aniting.repository.UsersRepository;

@Service
public class MypageServiceImpl implements MypageService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RecommendHistoryRepository recommendHistoryRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UsersDTO getUserInfo(String usersId) {

		Optional<Users> optional = usersRepository.findByUsersId(usersId);
		
		if (optional.isPresent()) {
			
			Users u = optional.get();
			return new UsersDTO(
				u.getUsersNo(), u.getUsersId(), u.getUsersNm(), null,
                u.getSecurityQuestion(), null,
                u.getJoinAt(), u.getUpdatedAt(), u.getActiveYn(), u.getInactiveAt()
			);
			
		}
		
		return null;
		
	}

	@Override
	public List<Map<String, Object>> getRecommendHistory(String usersId) {
	    return recommendHistoryRepository.findAll().stream()
	        .filter(h -> h.getUsersId().equals(usersId))
	        .map(h -> {
	            Map<String, Object> map = new HashMap<>();
	            map.put("createdAt", h.getCreatedAt());
	            map.put("aiReason", h.getAiReason());
	            map.put("top1Breed", h.getTop1Pet().getBreed());
	            map.put("top2Breed", h.getTop2Pet().getBreed());
	            map.put("top3Breed", h.getTop3Pet().getBreed());
	            return map;
	        })
	        .collect(Collectors.toList());
	}

	@Override
	public UsersDTO updateUser(String usersId, String usersNm, String passwd) {
		return usersRepository.findByUsersId(usersId)
		        .map(user -> {

		        	if (usersNm != null && !usersNm.isBlank()) {
		                user.setUsersNm(usersNm);
		            }

		            if (passwd != null && !passwd.isBlank()) {
		                user.setPasswd(passwordEncoder.encode(passwd));
		            }
		            
		            user.setUpdatedAt(LocalDateTime.now());
		            usersRepository.save(user);

		            return new UsersDTO(
		                user.getUsersNo(),
		                user.getUsersId(),
		                user.getUsersNm(),
		                null,
		                user.getSecurityQuestion(),
		                null,
		                user.getJoinAt(),
		                user.getUpdatedAt(),
		                user.getActiveYn(),
		                user.getInactiveAt()
		            );
		        })
		        .orElseThrow();
	}

	@Override
	public void deactivateUser(String usersId) {
		usersRepository.findByUsersId(usersId).ifPresent(user -> {
	        user.setActiveYn("N");
	        user.setInactiveAt(LocalDateTime.now());
	        usersRepository.save(user);
	    });
	}
	
}
