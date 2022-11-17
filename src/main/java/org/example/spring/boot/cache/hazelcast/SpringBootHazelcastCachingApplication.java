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

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

/**
 * {@link SpringBootApplication} configuring and using Hazelcast as a JCache and Spring Cache Abstraction
 * caching provider.
 *
 * @author John Blum
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.context.annotation.Profile
 * @since 0.1.0
 */
@SpringBootApplication
@Profile("application")
public class SpringBootHazelcastCachingApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(SpringBootHazelcastCachingApplication.class)
			.profiles("application", "hazelcast")
			.web(WebApplicationType.NONE)
			.build()
			.run(args);
	}
}
