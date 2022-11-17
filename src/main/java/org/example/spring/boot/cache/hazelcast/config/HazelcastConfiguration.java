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
package org.example.spring.boot.cache.hazelcast.config;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.AutoDetectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.HazelcastInstance;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

/**
 * {@link SpringBootConfiguration} used to configure the {@link HazelcastInstance}.
 *
 * @author John Blum
 * @see com.hazelcast.client.config.ClientConfig
 * @see com.hazelcast.config.Config
 * @see com.hazelcast.core.HazelcastInstance
 * @see org.springframework.boot.SpringBootConfiguration
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Conditional
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.core.env.Environment
 * @see org.testcontainers.containers.GenericContainer
 * @since 0.1.0
 */
@SpringBootConfiguration
@SuppressWarnings("unused")
public class HazelcastConfiguration {

	@SpringBootConfiguration
	@Profile("hazelcast-client-server")
	@Conditional(HazelcastClientServerConditions.class)
	static class ClientServerHazelcastConfiguration {

		static final int HAZELCAST_PORT = 5701;

		static final String HAZELCAST_SERVER_PORT_PROPERTY = "example.app.hazelcast.server.port";

		static final String HAZELCAST_DOCKER_IMAGE_NAME = "hazelcast/hazelcast:%s";
		static final String HAZELCAST_DOCKER_IMAGE_NAME_PROPERTY = "example.app.hazelcast.docker.image.name";
		static final String HAZELCAST_DOCKER_IMAGE_VERSION = "latest";
		static final String HAZELCAST_DOCKER_IMAGE_VERSION_PROPERTY = "example.app.hazelcast.docker.image.version";

		// See: https://hub.docker.com/r/hazelcast/hazelcast
		@Bean("HazelcastServer")
		@SuppressWarnings("all")
		GenericContainer<?> hazelcastServerContainer(Environment environment) {

			String configuredHazelcastDockerImageName = getConfiguredHazelcastDockerImageName(environment);
			String configuredHazelcastDockerImageVersion = getConfiguredHazelcastDockerImageVersion(environment);

			String resolvedHazelcastDockerImageName =
				resolveHazelcastDockerImageName(configuredHazelcastDockerImageName, configuredHazelcastDockerImageVersion);

			Integer hazelcastServerPort =
				getConfiguredHazelcastServerPort(environment);

			GenericContainer<?> hazelcastContainer = new GenericContainer<>(resolvedHazelcastDockerImageName)
				.withExposedPorts(hazelcastServerPort)
				.withCopyFileToContainer(MountableFile.forClasspathResource("/hazelcast-server.xml"),
					"/opt/hazelcast/config/hazelcast.xml")
				.withEnv("HAZELCAST_CONFIG", "/opt/hazelcast/config/hazelcast.xml");

			hazelcastContainer.start();

			return hazelcastContainer;
		}

		private @NotNull String getConfiguredHazelcastDockerImageName(@NotNull Environment environment) {
			return environment.getProperty(HAZELCAST_DOCKER_IMAGE_NAME_PROPERTY, HAZELCAST_DOCKER_IMAGE_NAME);
		}

		private @NotNull String getConfiguredHazelcastDockerImageVersion(@NotNull Environment environment) {
			return environment.getProperty(HAZELCAST_DOCKER_IMAGE_VERSION_PROPERTY, HAZELCAST_DOCKER_IMAGE_VERSION);
		}

		private @NotNull Integer getConfiguredHazelcastServerPort(@NotNull Environment environment) {
			return environment.getProperty(HAZELCAST_SERVER_PORT_PROPERTY, Integer.class, HAZELCAST_PORT);
		}

		private @NotNull String resolveHazelcastDockerImageName(@NotNull String configuredHazelcastDockerImageName,
			@NotNull String configuredHazelcastDockerImageVersion) {

			return String.format(configuredHazelcastDockerImageName, configuredHazelcastDockerImageVersion);
		}

		@Bean
		ClientConfig hazelcastClientServerConfiguration(
				@Qualifier("HazelcastServer") GenericContainer<?> hazelcastContainer) {

			ClientConfig clientConfig = new ClientConfig();

			clientConfig.getNetworkConfig().addAddress(String.format("%s:%d",
				hazelcastContainer.getHost(), hazelcastContainer.getFirstMappedPort()));

			return clientConfig;
		}
	}

	static class HazelcastClientServerConditions extends AnyNestedCondition {

		HazelcastClientServerConditions() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		@ConditionalOnProperty(name = "spring.cache.type", havingValue = "hazelcast", matchIfMissing = true)
		static class SpringCacheTypeIsHazelcastCondition { }

		@ConditionalOnProperty(name = "spring.cache.jcache.provider",
			havingValue = "com.hazelcast.client.cache.HazelcastClientCachingProvider",
			matchIfMissing = true
		)
		static class JCacheCachingProviderIsHazelcastClient { }

	}

	@SpringBootConfiguration
	@Profile("hazelcast-embedded")
	static class EmbeddedHazelcastConfiguration {

		@Bean
		Config hazelcastEmbeddedConfiguration() {

			Config config = new Config();

			config.getNetworkConfig()
				.setJoin(new JoinConfig().setAutoDetectionConfig(new AutoDetectionConfig().setEnabled(false)));

			return config;
		}
	}
}
