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
package io.micronaut.search.opensearch.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.NodeSelector;

/**
 * Base configuration for Opensearch.
 */
public interface OpensearchConfiguration {
    HttpHost[] getHttpHosts();

    Header[] getDefaultHeaders();

    int getMaxRetryTimeoutMillis();

    NodeSelector getNodeSelector();

    RequestConfig.Builder getRequestConfigBuilder();

    HttpAsyncClientBuilder getHttpAsyncClientBuilder();

    String getUsername();

    String getPassword();
}
