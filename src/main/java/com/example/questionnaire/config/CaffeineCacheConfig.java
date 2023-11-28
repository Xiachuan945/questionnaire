package com.example.questionnaire.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@EnableCaching
@Configuration //配置檔或設置檔，皆是交由 springboot 託管
public class CaffeineCacheConfig {
	
	// 透過 cacheManager 來管理
	@Bean
	public CacheManager cacheManager () {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager () ;
		cacheManager.setCaffeine(Caffeine.newBuilder()
				// 設定過期時間：每次處理(寫或讀)後，固定時間內不再有任何動作，快取資料就會失效
				.expireAfterAccess(600, TimeUnit.SECONDS)
				// 初始的緩存空間大小
				.initialCapacity(100)
				// 緩存的最大筆數
				.maximumSize(500));
		return cacheManager ;
	}
	

	
}
