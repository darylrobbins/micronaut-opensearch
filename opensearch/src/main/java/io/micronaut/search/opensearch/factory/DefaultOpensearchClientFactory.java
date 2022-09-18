/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.search.opensearch.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.search.opensearch.config.DefaultOpensearchConfigurationProperties;
import jakarta.inject.Singleton;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchAsyncClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;

/**
 * Default factory for constructing opensearch clients.
 */
@Factory
public class DefaultOpensearchClientFactory {

    /**
     * @param opensearchConfiguration The {@link DefaultOpensearchConfigurationProperties} object
     * @return The Opensearch Rest Client
     */
    @Bean(preDestroy = "close")
    RestClient restClient(DefaultOpensearchConfigurationProperties opensearchConfiguration) {
        return restClientBuilder(opensearchConfiguration).build();
    }

    /**
     * @param transport The {@link OpenSearchTransport} object.
     * @return The OpenSearchClient.
     */
    @Singleton
    OpenSearchClient opensearchClient(OpenSearchTransport transport) {
        return new OpenSearchClient(transport);
    }

    /**
     * @param transport The {@link OpenSearchTransport} object.
     * @return The OpensearchAsyncClient.
     * @since 4.2.0
     */
    @Singleton
    OpenSearchAsyncClient opensearchAsyncClient(OpenSearchTransport transport) {
        return new OpenSearchAsyncClient(transport);
    }

    /**
     * @param opensearchConfiguration The {@link DefaultOpensearchConfigurationProperties} object.
     * @param objectMapper The {@link ObjectMapper} object.
     * @return The {@link OpenSearchTransport}.
     */
    @Singleton
    @Bean(preDestroy = "close")
    OpenSearchTransport opensearchTransport(DefaultOpensearchConfigurationProperties opensearchConfiguration, ObjectMapper objectMapper) {
        RestClient restClient = restClientBuilder(opensearchConfiguration).build();

        OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
        return transport;
    }

    /**
     * @param config The {@link DefaultOpensearchConfigurationProperties} object
     * @return The {@link RestClientBuilder}
     */
    protected RestClientBuilder restClientBuilder(DefaultOpensearchConfigurationProperties config) {

        RestClientBuilder builder = RestClient.builder(config.getHttpHosts())
            .setRequestConfigCallback(requestConfigBuilder -> {
                requestConfigBuilder = config.getRequestConfigBuilder();
                return requestConfigBuilder;
            })
            .setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder = config.getHttpAsyncClientBuilder();

                if (StringUtils.isNotEmpty(config.getUsername())) {
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }

                return httpClientBuilder;
            });


        if (ArrayUtils.isNotEmpty(config.getDefaultHeaders())) {
            builder.setDefaultHeaders(config.getDefaultHeaders());
        }

        return builder;
    }

}
