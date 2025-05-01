package com.example.aniting.admin.recoHistory;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.dto.RecommendHistoryDTO;
import com.example.aniting.entity.RecommendHistory;
import com.example.aniting.repository.RecommendHistoryRepository;

@Service
public class AdminRecoHistoryServiceImpl implements AdminRecoHistoryService {

	@Autowired
	private RecommendHistoryRepository recommendHistoryRepository;
	
	@Override
	public List<RecommendHistoryDTO> getFilteredHistory(String usersId, LocalDate from, LocalDate to) {
	    List<RecommendHistory> all = recommendHistoryRepository.findAllWithPets(); // fetch join

	    return all.stream()
	        .filter(h -> usersId == null || h.getUsersId().contains(usersId))
	        .filter(h -> {
	            if (from != null && h.getCreatedAt().toLocalDate().isBefore(from)) return false;
	            if (to != null && h.getCreatedAt().toLocalDate().isAfter(to)) return false;
	            return true;
	        })
	        .map(h -> new RecommendHistoryDTO(
	            h.getHistoryId(),
	            h.getUsersId(),
	            h.getTop1PetId() != null ? h.getTop1PetId().getPetId() : null,
	            h.getTop2PetId() != null ? h.getTop2PetId().getPetId() : null,
	            h.getTop3PetId() != null ? h.getTop3PetId().getPetId() : null,
	            h.getAiReason(),
	            h.getCreatedAt()
	        ))
	        .toList();
	}
	
}
