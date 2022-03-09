package com.android.volley.mock;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Based on
 * https://raw.githubusercontent.com/google/volley/master/core/src/test/java/com/android/volley/mock/MockHttpStack.java
 */

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

public class MockHttpStack extends BaseHttpStack {

    private IOException mExceptionToThrow;

    private String mLastUrl;

    private Map<String, String> mLastHeaders;

    private byte[] mLastPostBody;
    private List<HttpResponse> mResponsesToReturn;
    private int mCurrentRespCounter = -1;

    public String getLastUrl() {
        return mLastUrl;
    }

    public Map<String, String> getLastHeaders() {
        return mLastHeaders;
    }

    public byte[] getLastPostBody() {
        return mLastPostBody;
    }

    public void addResponse(HttpResponse response) {
        if (mResponsesToReturn == null) {
            this.mResponsesToReturn = new ArrayList<>();
        }
        mResponsesToReturn.add(response);
    }

    public void addResponse(int status, byte[] content) {
        addResponse(new HttpResponse(status, new ArrayList<>(), content));
    }

    public void addResponse(int status, String content) {
        addResponse(new HttpResponse(status, new ArrayList<>(), content.getBytes(StandardCharsets.UTF_8)));
    }

    public void setResponseToReturn(HttpResponse response) {
        mResponsesToReturn = Collections.singletonList(response);
    }

    public void setResponsesToReturn(List<HttpResponse> responses) {
        this.mResponsesToReturn = responses;
    }

    public void setExceptionToThrow(IOException exception) {
        mExceptionToThrow = exception;
    }

    @Override
    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        if (mExceptionToThrow != null) {
            throw mExceptionToThrow;
        }
        mLastUrl = request.getUrl();
        mLastHeaders = new HashMap<>();
        if (request.getHeaders() != null) {
            mLastHeaders.putAll(request.getHeaders());
        }
        if (additionalHeaders != null) {
            mLastHeaders.putAll(additionalHeaders);
        }
        try {
            mLastPostBody = request.getBody();
        } catch (AuthFailureError e) {
            mLastPostBody = null;
        }
        mCurrentRespCounter++;
        return mResponsesToReturn.get(mCurrentRespCounter);
    }
}