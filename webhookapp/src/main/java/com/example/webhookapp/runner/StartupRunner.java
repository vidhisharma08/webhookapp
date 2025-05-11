package com.example.webhookapp.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.webhookapp.service.WebhookService;

@Component
public class StartupRunner implements ApplicationRunner {

	 private final WebhookService webhookService;

	    public StartupRunner(WebhookService webhookService) {
	        this.webhookService = webhookService;
	    }

	    @Override
	    public void run(ApplicationArguments args) {
	        webhookService.startProcess();
	    }
}
