package com.gyanexpert.kafka.customer;

import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

	final static String STORE_NAME = "customer-current-balance";
	@Bean
	public KeyValueBytesStoreSupplier storeSupplier(){
		 return Stores.persistentKeyValueStore(STORE_NAME);
	}

}
