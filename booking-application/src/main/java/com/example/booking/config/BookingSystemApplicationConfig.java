package com.example.booking.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.booking.repo.BaseRepositoryImpl;
import com.example.booking.security.config.BookingSystemSecurityConfig;

@Configuration
@EnableJpaRepositories(
	    basePackages = "com.example.booking.repo",
	    repositoryBaseClass = BaseRepositoryImpl.class
	)
@Import(BookingSystemSecurityConfig.class)
public class BookingSystemApplicationConfig {
	
	@Value("${spring.radis.host}")
	private String redisHost;
	@Value("${spring.radis.port}")
	private String redisPort;
	
	@Bean
     RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("redis://%s:%s", redisHost, redisPort));
        return Redisson.create(config);
    }

}
