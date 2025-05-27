package com.example.aniting.admin.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/sample")
public class AdminSampleController {

	@Autowired
	private AdminSampleService adminSampleService;
	
	@PostMapping("/generate")
    public ResponseEntity<?> generateSamplesAsync(@RequestParam int count) {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            futures.add(adminSampleService.generateOneSampleAsync());
        }

        // 병렬 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long successCount = futures.stream().filter(f -> f.join()).count();
        return ResponseEntity.ok("생성 완료: " + successCount + "개");
    }
	
}
