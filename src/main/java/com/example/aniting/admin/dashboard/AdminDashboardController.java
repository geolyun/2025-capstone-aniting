package com.example.aniting.admin.dashboard;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

	@Autowired
	private AdminDashboardSerivce adminDashboardSerivce;
	
	// 요약 통계 데이터
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return adminDashboardSerivce.getDashboardSummary();
    }

    // 차트 유저 활성/비활성
    @GetMapping("/chart/userStatus")
    public Map<String, Object> getUserChart() {
        return adminDashboardSerivce.getUserChartData();
    }

    // 차트 추천 요일별 통계
    @GetMapping("/chart/recommendWeek")
    public Map<String, Object> getRecommendChart() {
        return adminDashboardSerivce.getRecommendChartData();
    }

    // 차트 점수 분포
    @GetMapping("/chart/score")
    public Map<String, Object> getScoreChart() {
        return adminDashboardSerivce.getScoreChartData();
    }

    // 차트 TOP 3 반려동물
    @GetMapping("/chart/topPets")
    public Map<String, Object> getTopPetChart() {
        return adminDashboardSerivce.getTopPetChartData();
    }

    // 차트 카테고리별 점수 평균
    @GetMapping("/chart/categoryScore")
    public Map<String, Object> getCategoryScoreChart() {
        return adminDashboardSerivce.getCategoryScoreChartData();
    }
	
}
