/*
 * Copyright © 2018 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.data.jackson.jersey;

import io.servicetalk.data.jackson.jersey.resources.PublisherJsonResources;
import io.servicetalk.data.jackson.jersey.resources.SingleJsonResources;
import io.servicetalk.http.router.jersey.AbstractFilterInterceptorTest.UpperCaseInputStream;
import io.servicetalk.http.router.jersey.AbstractJerseyStreamingHttpServiceTest;

import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import static io.servicetalk.data.jackson.jersey.ServiceTalkJacksonSerializerFeature.ST_JSON_FEATURE;
import static io.servicetalk.http.api.HttpHeaderValues.APPLICATION_JSON;
import static io.servicetalk.http.api.HttpResponseStatus.OK;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.Priorities.ENTITY_CODER;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.glassfish.jersey.internal.InternalProperties.JSON_FEATURE;

public class FilterStreamingJsonTest extends AbstractJerseyStreamingHttpServiceTest {
    public FilterStreamingJsonTest(final RouterApi api) {
        super(api);
    }

    @Priority(ENTITY_CODER)
    @Provider
    public static class TestGlobalFilter implements ContainerRequestFilter {
        @Override
        public void filter(final ContainerRequestContext requestCtx) {
            requestCtx.setEntityStream(new UpperCaseInputStream(requestCtx.getEntityStream()));
        }
    }

    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return new HashSet<>(asList(
                    TestGlobalFilter.class,
                    SingleJsonResources.class,
                    PublisherJsonResources.class
            ));
        }

        @Override
        public Map<String, Object> getProperties() {
            return singletonMap(JSON_FEATURE, ST_JSON_FEATURE);
        }
    }

    @Override
    protected Application application() {
        return new TestApplication();
    }

    @Test
    public void filterSingle() {
        runTwiceToEnsureEndpointCache(() -> {
            sendAndAssertResponse(post(SingleJsonResources.PATH + "/map", "{\"foo\":\"bar\"}", APPLICATION_JSON),
                    OK, APPLICATION_JSON, jsonEquals("{\"got\":{\"FOO\":\"BAR\"}}"), __ -> null);
        });
    }

    @Test
    public void filterPublisher() {
        runTwiceToEnsureEndpointCache(() -> {
            sendAndAssertResponse(post(PublisherJsonResources.PATH + "/map", "{\"foo\":\"bar\"}", APPLICATION_JSON),
                    OK, APPLICATION_JSON, jsonEquals("{\"got\":{\"FOO\":\"BAR\"}}"), __ -> null);
        });
    }
}