package com.example.aniting.petseed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class PetSeeder implements CommandLineRunner {

    private final PetSeedService petSeedService;

    @Override
    public void run(String... args) throws Exception {
        petSeedService.generateAndSavePets();  // 애플리케이션 시작 시 호출됨
    }
}
