//package com.example.aniting.recommendation;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.aniting.ai.OpenAiClient;
//import com.example.aniting.dto.AnswerItemDTO;
//import com.example.aniting.dto.AnswerRequestDTO;
//import com.example.aniting.dto.RecommendationDTO;
//import com.example.aniting.dto.RecommendationResultDTO;
//import com.example.aniting.entity.Users;
//import com.example.aniting.repository.UsersRepository;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/test")
//public class RecommendationTestController {
//
//	private final RecommendationService recommendationService;
//    private final OpenAiClient openAiClient;
//    private final UsersRepository usersRepository;
//	
//    @GetMapping("/sample-recommendation")
//    public String generateSampleRecommendation() {
//        try {
//            // 1. 샘플 유저 등록
//            String userId = "gpt_user_" + UUID.randomUUID().toString().substring(0, 8);
//            registerSampleUser(userId);
//
//            // 2. GPT 질문 생성
//            String questionPrompt = RecommendationResponsePrompt.buildQuestionPrompt();
//            String rawQuestions = openAiClient.callGPTAPI(questionPrompt);
//            List<String> questions = RecommendationResponsePrompt.parseQuestionList(rawQuestions);
//
//            // 3. 각 질문에 대해 GPT가 답변 생성
//            List<AnswerItemDTO> answerItems = new ArrayList<>();
//            for (String question : questions) {
//                String answerPrompt = RecommendationResponsePrompt.buildAnswerPrompt(question);
//                String gptAnswer = openAiClient.callGPTAPI(answerPrompt);
//                answerItems.add(new AnswerItemDTO(question, gptAnswer));
//            }
//
//            // 4. 추천 요청 + DB 저장
//            AnswerRequestDTO request = new AnswerRequestDTO();
//            request.setAnswers(answerItems);
//            RecommendationResultDTO result = recommendationService.getRecommendations(userId, request);
//
//            // 5. 1순위 추천 동물 이름 가져오기
//            String top1PetName = result.getRecommendations().stream()
//                    .filter(r -> r.getRank() == 1)
//                    .findFirst()
//                    .map(RecommendationDTO::getAnimal)
//                    .orElse("Unknown");
//
//            return "✅ 추천 완료 - userId: " + userId + ", 추천 동물: " + top1PetName;
//
//        } catch (Exception e) {
//            log.error("❌ 샘플 추천 중 오류 발생", e);
//            return "❌ 오류: " + e.getMessage();
//        }
//    }
//
//    private void registerSampleUser(String userId) {
//        if (usersRepository.findByUsersId(userId).isPresent()) return;
//
//        Users user = new Users();
//        user.setUsersId(userId);
//        user.setUsersNm("GPT샘플");
//        user.setPasswd("dummy_pw");
//        user.setSecurityQuestion("샘플 질문");
//        user.setSecurityAnswer("샘플 답변");
//        user.setJoinAt(LocalDateTime.now());
//        user.setActiveYn("Y");
//        usersRepository.save(user);
//    }
//    
//}
