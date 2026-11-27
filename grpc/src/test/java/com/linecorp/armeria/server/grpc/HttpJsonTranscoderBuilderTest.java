/*
 * Copyright 2026 LY Corporation
 *
 * LY Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.armeria.server.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import com.linecorp.armeria.server.Route;

import testing.grpc.jsonname.JsonNameTestServiceGrpc;

class HttpJsonTranscoderBuilderTest {

    @Test
    void jsonMarshallerIsBuiltOnlyForProtoSerialization() {
        for (boolean protoSerialization : new boolean[] { true, false }) {
            final HttpJsonTranscoder transcoder =
                    new HttpJsonTranscoderBuilder()
                            .protoSerialization(protoSerialization)
                            .serviceDefinitions(ImmutableList.of(
                                    new JsonNameTestServiceGrpc.JsonNameTestServiceImplBase() {}
                                            .bindService()))
                            .build();
            assertThat(transcoder).isNotNull();
            assertThat(transcoder.routes()).isNotEmpty();
            for (Route route : transcoder.routes()) {
                final HttpJsonTranscoder.TranscodingSpec spec = transcoder.findSpec(route);
                assertThat(spec).isNotNull();
                assertThat(spec.method().jsonMarshaller != null).isEqualTo(protoSerialization);
            }
        }
    }
}
