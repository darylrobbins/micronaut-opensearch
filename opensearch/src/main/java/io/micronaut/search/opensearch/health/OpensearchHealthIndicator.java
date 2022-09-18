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
package io.micronaut.search.opensearch.health;

import io.micronaut.context.annotation.Requires;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.endpoint.health.HealthEndpoint;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import org.opensearch.client.opensearch.OpenSearchAsyncClient;
import org.reactivestreams.Publisher;

import jakarta.inject.Singleton;
import java.io.IOException;

import static io.micronaut.health.HealthStatus.DOWN;
import static io.micronaut.health.HealthStatus.UP;

/**
 * A {@link HealthIndicator} for Opensearch that uses an automatically-configured high-level REST
 * client, injected as a dependency, to communicate with Opensearch.
 *
 * @author Daryl Robbins
 * @since 1.0.0
 */
@Requires(beans = HealthEndpoint.class)
@Requires(property = HealthEndpoint.PREFIX + ".opensearch.enabled", notEquals = "false")
@Singleton
public class OpensearchHealthIndicator implements HealthIndicator {

    private static final String NAME = "opensearch";

    private final OpenSearchAsyncClient client;

    /**
     * Constructor.
     *
     * @param client The Opensearch high level REST client.
     */
    public OpensearchHealthIndicator(OpenSearchAsyncClient client) {
        this.client = client;
    }

    /**
     * Tries to call the cluster info API on Opensearch to obtain information about the cluster. If the call succeeds, the Opensearch cluster
     * health status (GREEN / YELLOW / RED) will be included in the health indicator details.
     *
     * @return A positive health result UP if the cluster can be communicated with and is in either GREEN or YELLOW status. A negative health result
     * DOWN if the cluster cannot be communicated with or is in RED status.
     */
    @Override
    public Publisher<HealthResult> getResult() {
        return (subscriber -> {
            final HealthResult.Builder resultBuilder = HealthResult.builder(NAME);
            try {
                client.cluster().health().handle((health, exception) -> {
                    if (exception != null) {
                        subscriber.onNext(resultBuilder.status(DOWN).exception(exception).build());
                        subscriber.onComplete();
                    } else {
                        HealthStatus status = health.status() == org.opensearch.client.opensearch._types.HealthStatus.Red ? DOWN : UP;
                        subscriber.onNext(resultBuilder.status(status).details(health).build());
                        subscriber.onComplete();
                    }
                    return health;
                });
            } catch (IOException e) {
                subscriber.onNext(resultBuilder.status(DOWN).exception(e).build());
                subscriber.onComplete();
            }
        });
    }
}
