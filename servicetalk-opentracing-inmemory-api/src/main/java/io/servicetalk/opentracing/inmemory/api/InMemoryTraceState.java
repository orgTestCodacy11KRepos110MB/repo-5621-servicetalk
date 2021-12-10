/*
 * Copyright © 2018, 2021 Apple Inc. and the ServiceTalk project authors
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
package io.servicetalk.opentracing.inmemory.api;

import javax.annotation.Nullable;

/**
 * Utility for representing a Ziplin-like trace state.
 * @deprecated This class will be removed in a follow up release. Alternative accessors will be available through
 * the {@link InMemorySpanContext} API.
 */
@Deprecated
public interface InMemoryTraceState {
    /**
     * The hex representation of the traceId.
     * @return hex representation of the traceId.
     */
    String traceIdHex();

    /**
     * The hex representation of the traceId.
     * @return hex representation of the traceId.
     */
    String spanIdHex();

    /**
     * The hex representation of the parent's spanId.
     * @return hex representation of the parent's spanId, or {@code null} if there is no parent.
     */
    @Nullable
    String parentSpanIdHex();

    /**
     * Determine if this state is sampled.
     * @return {@code true} if this state is sampled.
     */
    boolean isSampled();
}