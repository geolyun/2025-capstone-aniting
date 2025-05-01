package com.example.aniting.admin.recoHistory;

import java.time.LocalDate;
import java.util.List;

import com.example.aniting.dto.RecommendHistoryDTO;

public interface AdminRecoHistoryService {

	public List<RecommendHistoryDTO> getFilteredHistory(String usersId, LocalDate from, LocalDate to);

}
