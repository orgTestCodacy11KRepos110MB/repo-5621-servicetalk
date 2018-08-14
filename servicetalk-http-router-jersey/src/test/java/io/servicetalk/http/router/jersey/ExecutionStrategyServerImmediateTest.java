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
package io.servicetalk.http.router.jersey;

import io.servicetalk.concurrent.api.Executor;
import io.servicetalk.concurrent.api.Executors;
import io.servicetalk.http.netty.DefaultHttpConnectionBuilder;
import io.servicetalk.http.netty.DefaultHttpServerStarter;
import io.servicetalk.transport.netty.IoThreadFactory;
import io.servicetalk.transport.netty.internal.ExecutionContextRule;

import org.junit.Before;
import org.junit.ClassRule;

import java.net.InetSocketAddress;

import static io.servicetalk.buffer.netty.BufferAllocators.DEFAULT_ALLOCATOR;
import static io.servicetalk.concurrent.internal.Await.awaitIndefinitelyNonNull;
import static io.servicetalk.transport.netty.NettyIoExecutors.createIoExecutor;

public class ExecutionStrategyServerImmediateTest extends AbstractExecutionStrategyTest {
    @ClassRule
    public static final ExecutionContextRule IMMEDIATE_SERVER_CTX = new ExecutionContextRule(() -> DEFAULT_ALLOCATOR,
            () -> createIoExecutor(new IoThreadFactory("st-server-io")),
            Executors::immediate);

    public ExecutionStrategyServerImmediateTest(final String path,
                                                final ExpectedExecutor expectedExecutor,
                                                final TestMode testMode) {
        super(path, expectedExecutor, testMode);
    }

    @Override
    @Before
    public void initServerAndClient() throws Exception {
        serverContext = awaitIndefinitelyNonNull(
                new DefaultHttpServerStarter()
                        .start(IMMEDIATE_SERVER_CTX, new InetSocketAddress(0),
                                configureBuilder(new HttpJerseyRouterBuilder()).build(getApplication())));

        clientConnection = awaitIndefinitelyNonNull(
                new DefaultHttpConnectionBuilder<InetSocketAddress>()
                        .build(CLIENT_CTX, (InetSocketAddress) serverContext.getListenAddress()));
    }

    @Override
    protected Executor getServerExecutor() {
        return IMMEDIATE_SERVER_CTX.getExecutor();
    }
}
