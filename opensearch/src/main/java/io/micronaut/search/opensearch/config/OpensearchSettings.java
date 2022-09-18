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

import org.apache.http.HttpHost;

/** Opensearch settings to be shared across the module. */
public interface OpensearchSettings {

    /**
     * The prefix to use for all Opensearch settings.
     */
    String PREFIX = "opensearch";

    /**
     * Default Opensearch host.
     */
    HttpHost DEFAULT_HOST = new HttpHost("127.0.0.1", 9200, "http");

}
