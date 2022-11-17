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

import static org.cp.elements.lang.LangExtensions.assertThat;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import org.example.spring.boot.cache.hazelcast.model.User;
import org.example.spring.boot.cache.hazelcast.service.UserService;

/**
 * {@link SpringBootApplication} configuring and using Hazelcast as a JCache and Spring Cache Abstraction
 * caching provider.
 *
 * @author John Blum
 * @see org.springframework.boot.ApplicationRunner
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.cache.annotation.EnableCaching
 * @see org.springframework.context.annotation.Profile
 * @since 0.1.0
 */
@SpringBootApplication
@Profile("hazelcast-application")
@EnableCaching
public class SpringBootHazelcastCachingApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(SpringBootHazelcastCachingApplication.class)
			.profiles("hazelcast", "hazelcast-application", "hazelcast-client-server")
			.web(WebApplicationType.NONE)
			.build()
			.run(args);
	}

	@Bean
	@Profile("debug")
	@SuppressWarnings("unused")
	ApplicationRunner runner(UserService userService) {

		return args -> {

			User pieDoe = userService.findBy("PieDoe");

			assertThat(pieDoe).isNull();
			assertThat(userService.isCacheMiss()).isTrue();

			pieDoe = userService.save(User.as("PieDoe"));

			assertThat(pieDoe).isNotNull();
			assertThat(pieDoe.getId()).isNotNull();
			assertThat(pieDoe.getName()).isEqualTo("PieDoe");
			assertThat(userService.isCacheMiss()).isFalse();

			User cachedPieDoe = userService.findBy(pieDoe.getName());

			assertThat(cachedPieDoe).isNotNull();
			assertThat(cachedPieDoe).isEqualTo(pieDoe);
			assertThat(cachedPieDoe).isNotSameAs(pieDoe);
			assertThat(userService.isCacheMiss()).isFalse();

			System.err.println("SUCCESS!");
		};
	}
}
