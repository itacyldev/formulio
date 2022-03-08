package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpEntity {
    protected byte[] content;
    private Map<String, String> headers;
    private Map<String, String> params;
    private int httpStatus;
    private HashMap<String, String> responseHeaders;

    public HttpEntity(byte[] content){
        this.content = content;
    }

    public HttpEntity(byte[] data, Map<String, String> headers) {
        this.content = data;
        this.headers = headers;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public InputStream getContentStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    public void setHeaders(Map<String, String> headers){
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHttpHeaders(HashMap<String, String> headers) {
        this.responseHeaders = headers;
    }

    public HashMap<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "HttpEntity{" + "content=" + Arrays.toString(content) + ", headers=" + headers + ", httpStatus=" + httpStatus + ", responseHeaders=" + responseHeaders + '}';
    }
}
