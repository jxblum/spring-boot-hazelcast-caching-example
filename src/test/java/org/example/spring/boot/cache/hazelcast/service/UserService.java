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
package org.example.spring.boot.cache.hazelcast.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.example.spring.boot.cache.hazelcast.model.User;
import org.example.spring.boot.cache.hazelcast.repo.UserRepository;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring {@link Service} class used to process and service {@link User Users}.
 *
 * @author John Blum
 * @see org.example.spring.boot.cache.hazelcast.model.User
 * @since 0.1.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

	private final AtomicBoolean cacheMiss = new AtomicBoolean(false);

	@lombok.NonNull
	@Getter(AccessLevel.PROTECTED)
	private final UserRepository userRepository;

	public boolean isCacheMiss() {
		return this.cacheMiss.getAndSet(false);
	}

	@Cacheable("Users")
	public @Nullable User findBy(@NonNull String name) {
		this.cacheMiss.set(true);
		return getUserRepository().findByName(name);
	}

	@CachePut(cacheNames = "Users", key="#user.name")
	public @NonNull User save(@NonNull User user) {
		Assert.notNull(user, "User is required");
		return getUserRepository().save(user);
	}
}
