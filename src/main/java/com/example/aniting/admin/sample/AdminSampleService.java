package com.example.aniting.admin.sample;

import java.util.concurrent.CompletableFuture;

public interface AdminSampleService {

	public String generateMultipleSamples(int count);
	public CompletableFuture<Boolean> generateOneSampleAsync();
	public CompletableFuture<Boolean> generateOnePetAsync();

}
