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
package org.example.spring.boot.cache.hazelcast.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract Data Type (ADT) used to model a user.
 *
 * @author John Blum
 * @see java.io.Serializable
 * @see jakarta.persistence.Entity
 * @since 0.1.0
 */
@Entity
@Getter
@Table(name= "Users")
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "as")
@SuppressWarnings("unused")
public class User implements Serializable {

	@jakarta.persistence.Id
	@org.springframework.data.annotation.Id
	@Setter(AccessLevel.PROTECTED)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@lombok.NonNull
	private String name;

	public @NonNull User identifiedBy(@Nullable Long id) {
		setId(id);
		return this;
	}
}
