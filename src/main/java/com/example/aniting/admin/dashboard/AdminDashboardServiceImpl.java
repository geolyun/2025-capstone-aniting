package com.example.aniting.admin.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.repository.CategoryRepository;
import com.example.aniting.repository.PetRepository;
import com.example.aniting.repository.RecommendHistoryRepository;
import com.example.aniting.repository.RecommendResponseRepository;
import com.example.aniting.repository.ScoreRepository;
import com.example.aniting.repository.UsersRepository;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardSerivce {

	@Autowired 
	private UsersRepository usersRepository;
	
    @Autowired 
    private PetRepository petRepository;
    
    @Autowired 
    private RecommendHistoryRepository recommendHistoryRepository;
    
    @Autowired 
    private RecommendResponseRepository recommendResponseRepository;
    
    @Autowired 
    private ScoreRepository scoreRepository;
    
    @Autowired 
    private CategoryRepository categoryRepository;
	
    @Override
    public Map<String, Object> getDashboardSummary() {
    	
        Map<String, Object> result = new HashMap<>();

        long totalUsers = usersRepository.count();
        int inactiveUsers = usersRepository.countByActiveYn("N");
        
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay();
        int newUsers7days = usersRepository.countByJoinAtGreaterThanEqual(sevenDaysAgo);

        long totalPets = petRepository.count();
        Map<String, Integer> petCounts = new HashMap<>();
        petCounts.put("강아지", petRepository.countBySpecies("강아지"));
        petCounts.put("고양이", petRepository.countBySpecies("고양이"));
        petCounts.put("특수동물", petRepository.countByIsSpecial("Y"));

        long totalRecommendations = recommendHistoryRepository.count();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = todayStart.plusDays(1);
        int today = recommendHistoryRepository.countByCreatedAtBetween(todayStart, tomorrowStart);

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        int thisWeek = recommendHistoryRepository.countByCreatedAtGreaterThanEqual(weekAgo);
        
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        int thisMonth = recommendHistoryRepository.countByCreatedAtGreaterThanEqual(monthAgo);
        
        long totalResponses = recommendResponseRepository.count();
        double recommendRate = (totalResponses == 0) ? 0 : (double) totalRecommendations / totalResponses * 100;

        int anomalyUsers = recommendHistoryRepository.countAnomalyUsers(10);

        result.put("totalUsers", totalUsers);
        result.put("inactiveUsers", inactiveUsers);
        result.put("newUsers7days", newUsers7days);
        result.put("totalPets", totalPets);
        result.put("petCounts", petCounts);
        result.put("totalRecommendations", totalRecommendations);
        result.put("today", today);
        result.put("thisWeek", thisWeek);
        result.put("thisMonth", thisMonth);
        result.put("recommendRate", String.format("%.1f", recommendRate));
        result.put("anomalyUsers", anomalyUsers);

        return result;
        
    }

    @Override
    public Map<String, Object> getUserChartData() {
    	
        int active = usersRepository.countByActiveYn("Y");
        int inactive = usersRepository.countByActiveYn("N");

        return Map.of(
            "type", "doughnut",
            "data", Map.of(
                "labels", List.of("활성 유저", "비활성 유저"),
                "datasets", List.of(Map.of(
                    "data", List.of(active, inactive),
                    "backgroundColor", List.of("#4CAF50", "#F44336")
                ))
            )
        );
        
    }

    @Override
    public Map<String, Object> getRecommendChartData() {
    	
        List<Object[]> results = recommendHistoryRepository.countRecommendationsByDayOfWeek();

        // 요일 인덱스를 한글 요일로 매핑
        String[] koreanDays = {"일", "월", "화", "수", "목", "금", "토"};
        Map<String, Integer> dayToCount = new LinkedHashMap<>();
        for (int i = 1; i <= 7; i++) {
            dayToCount.put(koreanDays[i - 1], 0); // 초기화
        }

        for (Object[] row : results) {
            int dayOfWeek = ((Number) row[0]).intValue(); // 1~7
            int count = ((Number) row[1]).intValue();
            dayToCount.put(koreanDays[dayOfWeek - 1], count);
        }

        Map<String, Object> chart = new HashMap<>();
        chart.put("type", "bar");
        chart.put("data", Map.of(
                "labels", new ArrayList<>(dayToCount.keySet()),
                "datasets", List.of(Map.of(
                        "label", "추천 횟수",
                        "data", new ArrayList<>(dayToCount.values()),
                        "borderWidth", 2
                ))
        ));
        return chart;
        
    }

    @Override
    public Map<String, Object> getScoreChartData() {
    	
        List<String> labels = List.of("1점", "2점", "3점", "4점", "5점");
        List<Integer> counts = new ArrayList<>(List.of(0, 0, 0, 0, 0));

        List<Object[]> scoreCounts = scoreRepository.countGroupedByScore();
        for (Object[] row : scoreCounts) {
            String scoreValue = row[0].toString(); // '1' ~ '5'
            int count = ((Number) row[1]).intValue();
            int index = Integer.parseInt(scoreValue) - 1;
            if (index >= 0 && index < 5) {
                counts.set(index, count);
            }
        }

        return Map.of(
            "type", "pie",
            "data", Map.of(
                "labels", labels,
                "datasets", List.of(
                    Map.of(
                        "data", counts,
                        "backgroundColor", List.of("#FF6384", "#36A2EB", "#FFCE56", "#66BB6A", "#BA68C8")
                    )
                )
            )
        );
        
    }

    @Override
    public Map<String, Object> getTopPetChartData() {
    	
        List<String> labels = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        recommendHistoryRepository.findTop3Pets().forEach(arr -> {
            labels.add((String) arr[0]); // breed
            counts.add(((Number) arr[1]).intValue()); // count
        });

        return Map.of(
            "type", "bar",
            "data", Map.of(
                "labels", labels,
                "datasets", List.of(Map.of(
                    "label", "추천 수",
                    "data", counts,
                    "backgroundColor", "#FFA726"
                ))
            )
        );
        
    }

    @Override
    public Map<String, Object> getCategoryScoreChartData() {
    	
        List<String> labels = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        categoryRepository.findAll().forEach(category -> {
            labels.add(category.getCategory());
            Double avg = scoreRepository.avgScoreByCategory(category.getCategoryId());
            scores.add(avg != null ? avg : 0.0);
        });

        return Map.of(
            "type", "bar",
            "data", Map.of(
                "labels", labels,
                "datasets", List.of(Map.of(
                    "label", "평균 점수",
                    "data", scores,
                    "backgroundColor", "#26A69A"
                ))
            )
        );
        
    }
	
}
