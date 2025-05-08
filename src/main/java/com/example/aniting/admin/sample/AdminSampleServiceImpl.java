package com.example.aniting.admin.sample;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.ai.OpenAiClient;
import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.entity.Users;
import com.example.aniting.recommendation.RecommendationPrompt;
import com.example.aniting.recommendation.RecommendationResponsePrompt;
import com.example.aniting.recommendation.RecommendationService;
import com.example.aniting.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminSampleServiceImpl implements AdminSampleService {

	@Autowired
	private RecommendationService recommendationService;
	
	@Autowired
    private OpenAiClient openAiClient;
    
	@Autowired
    private UsersRepository usersRepository;
	
	@Override
    public String generateMultipleSamples(int count) {
		
        int success = 0;

        for (int i = 0; i < count; i++) {
            boolean ok = generateOneSample();
            if (ok) success++;
        }

        return success + "개 샘플 데이터 생성 완료";
        
    }
	
	private boolean generateOneSample() {
		
        try {
            String userId = "gpt_user_" + UUID.randomUUID().toString().substring(0, 8);
            registerSampleUser(userId);

            String questionPrompt = RecommendationPrompt.buildQuestionPrompt();
            String rawQuestions = openAiClient.callGPTAPI(questionPrompt);
            List<String> questions = RecommendationResponsePrompt.parseQuestionList(rawQuestions);

            List<AnswerItemDTO> answerItems = new ArrayList<>();
            for (String question : questions) {
                String answerPrompt = RecommendationResponsePrompt.buildAnswerPrompt(question);
                String gptAnswer = openAiClient.callGPTAPI(answerPrompt);
                answerItems.add(new AnswerItemDTO(question, gptAnswer));
            }

            AnswerRequestDTO request = new AnswerRequestDTO();
            request.setAnswers(answerItems);
            RecommendationResultDTO result = recommendationService.getRecommendations(userId, request);

            String top1 = result.getRecommendations().stream()
                    .filter(r -> r.getRank() == 1)
                    .map(RecommendationDTO::getAnimal)
                    .findFirst()
                    .orElse("Unknown");

            log.info("[샘플 생성 완료] userId={}, top1={}", userId, top1);
            return true;

        } catch (Exception e) {
            log.error("[샘플 생성 실패]", e);
            return false;
        }
        
    }

	private void registerSampleUser(String userId) {
		
        Optional<Users> existing = usersRepository.findByUsersId(userId);
        if (existing.isPresent()) return;

        Users user = new Users();
        user.setUsersId(userId);
        user.setUsersNm("GPT샘플");
        user.setPasswd("dummy_pw");
        user.setSecurityQuestion("샘플질문");
        user.setSecurityAnswer("샘플답변");
        user.setJoinAt(LocalDateTime.now());
        user.setActiveYn("Y");
        usersRepository.save(user);

        log.info("➕ 샘플 유저 등록 완료: {}", userId);
        
    }
	
}
