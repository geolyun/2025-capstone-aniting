package com.example.aniting.admin.recoResponse;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.dto.RecommendResponseDTO;
import com.example.aniting.entity.RecommendResponse;
import com.example.aniting.repository.RecommendResponseRepository;

@Service
public class AdminRecoResponseServiceImpl implements AdminRecoResponseService {

	@Autowired
	private RecommendResponseRepository recommendResponseRepository;
	
    @Override
    public List<RecommendResponseDTO> getFilteredResponses(String usersId, String keyword, LocalDateTime from, LocalDateTime to) {
        List<RecommendResponse> responses = recommendResponseRepository.findAll();

        return responses.stream()
                .filter(r -> usersId == null || r.getUsersId().contains(usersId))
                .filter(r -> keyword == null || r.getQuestion().contains(keyword) || r.getAnswer().contains(keyword))
                .filter(r -> {
                    if (from != null && r.getCreatedAt().isBefore(from)) return false;
                    if (to != null && r.getCreatedAt().isAfter(to)) return false;
                    return true;
                })
                .map(r -> new RecommendResponseDTO(
                        r.getResponseId(),
                        r.getUsersId(),
                        r.getQuestionOrder(),
                        r.getQuestion(),
                        r.getAnswer(),
                        r.getCategory(),
                        r.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        recommendResponseRepository.deleteById(id);
    }
	
}
