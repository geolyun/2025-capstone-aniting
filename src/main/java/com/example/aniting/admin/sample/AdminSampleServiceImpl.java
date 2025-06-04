package com.example.aniting.admin.sample;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

import com.example.aniting.petseed.PetSeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
@RequiredArgsConstructor
public class AdminSampleServiceImpl implements AdminSampleService {

    private final PetSeedService petSeedService;
    private final RecommendationService recommendationService;
    private final OpenAiClient openAiClient;
    private final UsersRepository usersRepository;
    private static final Semaphore semaphore = new Semaphore(5);

    @Async
    public CompletableFuture<Boolean> generateOneSampleAsync() {
        try {
            semaphore.acquire();
            boolean result = callWithRetryAndDelay();
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("비동기 샘플 생성 중 예외 발생", e);
            return CompletableFuture.completedFuture(false);
        } finally {
            semaphore.release();
        }
    }

    // ▶ NullPointerException을 막으려면 절대 null을 반환하지 말고, 반드시 completedFuture를 리턴해야 합니다.
    @Async
    public CompletableFuture<Boolean> generateOnePetAsync() {
        try {
            // petSeedService.generateAndSavePets() 내부에서 이미 중복 체크 후 저장합니다.
            petSeedService.generateAndSavePets();
            log.info("✅ generateOnePetAsync(): 반려동물 데이터 생성 성공");
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("❌ generateOnePetAsync(): 반려동물 생성 실패", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public String generateMultipleSamples(int count) {
        int success = 0;
        for (int i = 0; i < count; i++) {
            boolean ok = generateOneSample(); // 기존 generateOneSample() 메서드
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
            List<AnswerItemDTO> questionItems = RecommendationPrompt.parseQuestionItems(rawQuestions); // 🔥 변경 포인트

            List<AnswerItemDTO> answerItems = new ArrayList<>();
            for (AnswerItemDTO qItem : questionItems) {
                String question = qItem.getQuestion();
                String category = qItem.getCategory();

                String answerPrompt = RecommendationResponsePrompt.buildAnswerPrompt(question);
                String gptAnswer = openAiClient.callGPTAPI(answerPrompt);

                answerItems.add(new AnswerItemDTO(question, gptAnswer, category));
            }

            AnswerRequestDTO request = new AnswerRequestDTO();
            request.setAnswers(answerItems);
            RecommendationResultDTO result = recommendationService.getRecommendations(userId, request.getAnswers());

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

    private boolean callWithRetryAndDelay() {

        int retry = 0;
        while (retry < 3) {
            try {
                boolean result = generateOneSample();
                Thread.sleep(500);

                return result;

            } catch (RuntimeException e) {
                if (e.getMessage().contains("429")) {
                    retry++;
                    log.warn("GPT 429 오류 발생, {}초 후 재시도 (시도: {})", (retry + 1), retry);
                    try {
                        Thread.sleep(1000L * (retry + 1));
                    } catch (InterruptedException ignored) {}
                }
                else {
                    throw e; // 429 외 오류는 즉시 터뜨림
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("스레드 인터럽트됨", e);
            }
        }

        throw new RuntimeException("GPT 요청 실패: 429 재시도 초과");

    }

}
