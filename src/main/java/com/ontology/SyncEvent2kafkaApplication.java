package com.ontology;

import com.ontology.utils.Constant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.ontology.mapper")
public class SyncEvent2kafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncEvent2kafkaApplication.class, args);
	}

	@Bean
	public NewTopic adviceTopic() {
		return new NewTopic(Constant.KAFKA_TOPIC, 2, (short) 1);
	}

}
