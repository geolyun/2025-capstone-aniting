package com.example.aniting.admin.recoLog;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.dto.RecommendLogDTO;
import com.example.aniting.entity.RecommendLog;
import com.example.aniting.repository.RecommendLogRepository;

@Service
public class AdminRecoLogServiceImpl implements AdminRecoLogService {

	@Autowired
	private RecommendLogRepository recommendLogRepository;
	
	@Override
    public List<RecommendLogDTO> getFilteredLogs(String usersId, String keyword, LocalDateTime from, LocalDateTime to) {
        List<RecommendLog> logs = recommendLogRepository.findAll();

        return logs.stream()
                .filter(l -> usersId == null || l.getUsersId().contains(usersId))
                .filter(l -> keyword == null || 
                        l.getAiPrompt().contains(keyword) || l.getAiResponse().contains(keyword))
                .filter(l -> {
                    if (from != null && l.getCreatedAt().isBefore(from)) return false;
                    if (to != null && l.getCreatedAt().isAfter(to)) return false;
                    return true;
                })
                .map(l -> new RecommendLogDTO(
                        l.getLogId(),
                        l.getUsersId(),
                        l.getAiPrompt(),
                        l.getAiResponse(),
                        l.getCreatedAt()
                ))
                .toList();
    }
	
}
