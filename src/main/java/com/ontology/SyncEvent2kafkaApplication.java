package com.ontology;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SyncEvent2kafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncEvent2kafkaApplication.class, args);
	}

	@Bean
	public NewTopic adviceTopic() {
		return new NewTopic("topic-block-event", 2, (short) 1);
	}

}
