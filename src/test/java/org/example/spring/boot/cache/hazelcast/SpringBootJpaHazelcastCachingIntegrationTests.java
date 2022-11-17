/*
 *  Copyright 2022-present the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.spring.boot.cache.hazelcast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import org.example.spring.boot.cache.hazelcast.model.User;
import org.example.spring.boot.cache.hazelcast.repo.UserRepository;
import org.example.spring.boot.cache.hazelcast.service.UserService;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Integration Testing using Spring Boot to auto-configure JPA and Hazelcast as a caching provider
 * in Spring's Cache Abstraction.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.cache.annotation.EnableCaching
 * @since 0.1.0
 */
@SpringBootTest
//@ActiveProfiles({ "hazelcast", "hazelcast-test" })
//@ActiveProfiles({ "hazelcast-test", "mock-configuration" })
@ActiveProfiles({ "hazelcast", "hazelcast-test", "mock-configuration"})
@SuppressWarnings("unused")
public class SpringBootJpaHazelcastCachingIntegrationTests {

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private UserService userService;

	@Test
	public void cachingWithHazelcastWorks() {

		User jonDoe = getUserService().findBy("JonDoe");

		assertThat(jonDoe).isNull();
		assertThat(getUserService().isCacheMiss()).isTrue();

		jonDoe = getUserService().save(User.as("JonDoe"));

		assertThat(jonDoe).isNotNull();
		assertThat(jonDoe.getId()).isNotNull();
		assertThat(jonDoe.getName()).isEqualTo("JonDoe");

		User cachedJonDoe = getUserService().findBy(jonDoe.getName());

		assertThat(cachedJonDoe).isNotNull();
		assertThat(cachedJonDoe).isNotSameAs(jonDoe);
		assertThat(cachedJonDoe).isEqualTo(jonDoe);
		assertThat(getUserService().isCacheMiss()).isFalse();
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@Profile("hazelcast-test")
	@EnableCaching
	@ComponentScan(basePackageClasses = NonManagedBeanType.class)
	static class TestConfiguration { }

	@SpringBootConfiguration
	@Profile("mock-configuration")
	@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
	static class MockConfiguration {

		@Bean
		UserRepository mockUserRepository() {

			UserRepository mockUserRepository = mock(UserRepository.class);

			doAnswer(invocation -> invocation.getArgument(0, User.class).identifiedBy(System.currentTimeMillis()))
				.when(mockUserRepository).save(any());

			doReturn(null).when(mockUserRepository).findByName(any());

			return mockUserRepository;
		}
	}
}
