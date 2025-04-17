package com.example.aniting.mypage;

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
	
}
