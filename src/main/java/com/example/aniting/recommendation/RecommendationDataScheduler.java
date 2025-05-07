//package com.example.aniting.recommendation;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.example.aniting.ai.OpenAiClient;
//import com.example.aniting.dto.AnswerItemDTO;
//import com.example.aniting.dto.AnswerRequestDTO;
//import com.example.aniting.dto.RecommendationResultDTO;
//import com.example.aniting.entity.Users;
//import com.example.aniting.repository.RecommendLogRepository;
//import com.example.aniting.repository.UsersRepository;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class RecommendationDataScheduler {
//
//	private final RecommendationService recommendationService;
//	private final RecommendLogRepository recommendLogRepository;
//    private final OpenAiClient openAiClient;
//    private final UsersRepository usersRepository;
//
//    @Scheduled(cron = "0 */5 * * * *")
//    public void autoGenerateRecommendationDataFromGPT() {
//        try {
//        	
//        	long count = recommendLogRepository.countByUsersIdStartingWith("gpt_user_");
//            if (count >= 1000) {
//                log.info("[샘플 추천 종료] 이미 1000개의 샘플 데이터가 생성됨.");
//                return;
//            }
//        	
//            String userId = "gpt_user_" + UUID.randomUUID().toString().substring(0, 8);
//            registerSampleUser(userId); // ✨ 내부 전용 유저 생성
//
//            // 1. GPT로부터 질문 생성
//            String questionPrompt = RecommendationResponsePrompt.buildQuestionPrompt();
//            String rawQuestions = openAiClient.callGPTAPI(questionPrompt);
//            List<String> questions = RecommendationResponsePrompt.parseQuestionList(rawQuestions);
//
//            List<AnswerItemDTO> answerItems = new ArrayList<>();
//            for (String question : questions) {
//                String answerPrompt = RecommendationResponsePrompt.buildAnswerPrompt(question);
//                String gptAnswer = openAiClient.callGPTAPI(answerPrompt);
//                answerItems.add(new AnswerItemDTO(question, gptAnswer));
//            }
//
//            // 2. 추천 요청 및 DB 저장
//            AnswerRequestDTO request = new AnswerRequestDTO();
//            request.setAnswers(answerItems);
//            RecommendationResultDTO result = recommendationService.getRecommendations(userId, request);
//
//            // 추천 결과 리스트에서 1순위 동물 이름 추출
//            String top1PetName = result.getRecommendations().stream()
//                .filter(r -> r.getRank() == 1)
//                .findFirst()
//                .map(r -> r.getAnimal())
//                .orElse("Unknown");
//
//            log.info("[샘플 추천 완료] userId={}, 추천동물={}", userId, top1PetName);
//
//        } catch (Exception e) {
//            log.error("[샘플 추천 실패] 자동 추천 중 오류 발생", e);
//        }
//        
//    }
//
//    
//    // RecommendationScheduler 전용 샘플 계정 등록 메서드
//    private void registerSampleUser(String userId) {
//        Optional<Users> existing = usersRepository.findByUsersId(userId);
//        if (existing.isPresent()) return;
//
//        Users user = new Users();
//        user.setUsersId(userId);
//        user.setUsersNm("GPT샘플");
//        user.setPasswd("dummy_pw");
//        user.setSecurityQuestion("샘플질문");
//        user.setSecurityAnswer("샘플답변");
//        user.setJoinAt(LocalDateTime.now());
//        user.setActiveYn("Y");
//        usersRepository.save(user);
//
//        log.info("➕ 샘플 유저 등록 완료: {}", userId);
//    }
//    
//}
