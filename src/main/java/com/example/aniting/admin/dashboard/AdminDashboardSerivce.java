package com.example.aniting.admin.dashboard;

import java.util.Map;

public interface AdminDashboardSerivce {

	public Map<String, Object> getDashboardSummary();
	public Map<String, Object> getUserChartData();
	public Map<String, Object> getRecommendChartData();
	public Map<String, Object> getScoreChartData();
	public Map<String, Object> getTopPetChartData();
	public Map<String, Object> getCategoryScoreChartData();
	
}
