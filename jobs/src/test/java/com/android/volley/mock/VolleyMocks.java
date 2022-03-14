package com.android.volley.mock;
/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.mockito.Mockito.mock;

import com.android.volley.Header;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import org.codehaus.plexus.util.StringUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to create mocks for Unit testing.
 *
 * @author Gustavo Río (mungarro@itacyl.es)
 */

public class VolleyMocks {

    public static RequestQueue createMockRQRealNetwork() {
        ResponseDelivery mDelivery = new ImmediateResponseDelivery();
        RequestQueue queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()), 1, mDelivery);
        queue.start();
        return queue;
    }

    public static RequestQueue createMockRQ(List<HttpResponse> responses) {
        MockHttpStack mStack = new MockHttpStack();
        mStack.setResponsesToReturn(responses);
        Network network = new BasicNetwork(mStack);
        return createMockRQ(network);
    }

    public static RequestQueue createMockRQ(String responseContent) {
        return createMockRQ(responseContent, null);
    }

    public static RequestQueue createMockRQ(String responseContent, String contentType) {
        // add contentType as header
        List<Header> headers = new ArrayList<>();
        if (StringUtils.isNotBlank(contentType)) {
            headers.add(new Header("Content-Type", contentType));
        }
        HttpResponse response = new HttpResponse(200, headers,
                responseContent.getBytes(StandardCharsets.UTF_8));
        MockHttpStack mStack = new MockHttpStack();
        mStack.setResponseToReturn(response);
        Network network = new BasicNetwork(mStack);
        return createMockRQ(network);
    }

    public static RequestQueue createMockRQ(Network network) {
        ResponseDelivery mDelivery = new ImmediateResponseDelivery();
        RequestQueue queue = new RequestQueue(new NoCache(), network, 1, mDelivery);
        queue.start();
        return queue;
    }


}
