package com.example.aniting.admin.users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.dto.UsersDTO;
import com.example.aniting.entity.Users;
import com.example.aniting.repository.RecommendHistoryRepository;
import com.example.aniting.repository.RecommendLogRepository;
import com.example.aniting.repository.RecommendResponseRepository;
import com.example.aniting.repository.ScoreRepository;
import com.example.aniting.repository.UsersRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminUserServiceImpl implements AdminUserService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RecommendHistoryRepository recommendHistoryRepository;
	
	@Autowired
    private RecommendLogRepository recommendLogRepository;
    
	@Autowired
	private RecommendResponseRepository recommendResponseRepository;
	
	@Autowired
    private ScoreRepository scoreRepository;
	
	@Override
	public List<UsersDTO> getUserList(String activeYn, String joinStartDt, String joinEndDt, String inactiveStartDt,
			String inactiveEndDt, String keyword) {

		List<Users> usersList = usersRepository.findAll();
		
        return usersList.stream()
                .filter(user -> {
                    if (activeYn != null && !activeYn.isBlank()) {
                        return activeYn.equals(user.getActiveYn());
                    }
                    return true;
                })
                .filter(user -> {
                    if (joinStartDt != null && !joinStartDt.isBlank()) {
                        LocalDateTime start = LocalDate.parse(joinStartDt, DateTimeFormatter.ISO_DATE).atStartOfDay();
                        return !user.getJoinAt().isBefore(start);
                    }
                    return true;
                })
                .filter(user -> {
                    if (joinEndDt != null && !joinEndDt.isBlank()) {
                        LocalDateTime end = LocalDate.parse(joinEndDt, DateTimeFormatter.ISO_DATE).atTime(23, 59, 59);
                        return !user.getJoinAt().isAfter(end);
                    }
                    return true;
                })
                .filter(user -> {
                    if (inactiveStartDt != null && !inactiveStartDt.isBlank() && user.getInactiveAt() != null) {
                        LocalDateTime start = LocalDate.parse(inactiveStartDt, DateTimeFormatter.ISO_DATE).atStartOfDay();
                        return !user.getInactiveAt().isBefore(start);
                    }
                    return true;
                })
                .filter(user -> {
                    if (inactiveEndDt != null && !inactiveEndDt.isBlank() && user.getInactiveAt() != null) {
                        LocalDateTime end = LocalDate.parse(inactiveEndDt, DateTimeFormatter.ISO_DATE).atTime(23, 59, 59);
                        return !user.getInactiveAt().isAfter(end);
                    }
                    return true;
                })
                .filter(user -> {
                    if (keyword != null && !keyword.isBlank()) {
                        return user.getUsersId().contains(keyword) || user.getUsersNm().contains(keyword);
                    }
                    return true;
                })
                .map(user -> new UsersDTO(
                	    user.getUsersNo(),
                	    user.getUsersId(),
                	    user.getUsersNm(),
                	    null,
                	    null,
                	    null,
                	    user.getJoinAt(),
                	    user.getUpdatedAt(),
                	    user.getActiveYn(),
                	    user.getInactiveAt()
                ))
                .collect(Collectors.toList());
	
	}
	
	@Override
	public UsersDTO getUser(Long usersNo) {
	    Users user = usersRepository.findById(usersNo)
	        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

	    return new UsersDTO(
	        user.getUsersNo(),
	        user.getUsersId(),
	        user.getUsersNm(),
	        null,
	        null,
	        null,
	        user.getJoinAt(),
	        user.getUpdatedAt(),
	        user.getActiveYn(),
	        user.getInactiveAt()
	    );
	}

	@Override
	@Transactional
	public void updateUser(Long usersNo, UsersDTO dto) {
		
	    Users user = usersRepository.findById(usersNo)
	        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

	    user.setUsersNm(dto.getUsersNm());
	    user.setActiveYn(dto.getActiveYn());
	    user.setInactiveAt(dto.getInactiveAt());
	    user.setUpdatedAt(LocalDateTime.now());  // 수정 일자 업데이트

	    usersRepository.save(user);  // 수정사항 저장
	    
	}

    @Override
    @Transactional
    public void deleteUser(Long usersNo) {
        Users user = usersRepository.findById(usersNo)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        String usersId = user.getUsersId();

        // 1. 자식 테이블 먼저 삭제
        recommendHistoryRepository.deleteByUsersId(usersId);
        recommendLogRepository.deleteByUsersId(usersId);
        recommendResponseRepository.deleteByUsersId(usersId);
        scoreRepository.deleteByUsersId(usersId);

        // 2. 부모 테이블 삭제
        usersRepository.delete(user);
    }
    
    @Override
    @Transactional
    public void deleteUsers(List<Long> usersNos) {
        for (Long usersNo : usersNos) {
            deleteUser(usersNo);
        }
    }

}
