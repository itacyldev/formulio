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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.jayjobs.utils.VolleyUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RawRequest extends Request<HttpEntity> {

    private final Response.Listener listener;
    private HttpEntity entity;

    public RawRequest(int method, String url, HttpEntity entity, Response.Listener listener,
                      Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.entity = entity;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> entityHeaders = entity.getHeaders();
        return entityHeaders != null ? entityHeaders : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return this.entity.getContent();
    }

    @Override
    protected Response<HttpEntity> parseNetworkResponse(NetworkResponse response) {
        HttpEntity entity = new HttpEntity();
        entity.setHttpStatus(response.statusCode);
        entity.setResponseContent(response.data);
        entity.setResponseContentType(response.headers.get("Content-Type"));
        entity.setResponseHeaders(new HashMap<>(response.headers));
        return Response.success(entity, null);
    }

    @Override
    public String getBodyContentType() {
        return this.entity.getContentType();
    }

    @Override
    protected void deliverResponse(HttpEntity response) {
        listener.onResponse(response);
    }

    @Override
    public String toString() {
        return "RawRequest{url=" + this.getUrl() + ", method=" + VolleyUtils.getMethodName(this.getMethod()) +
                ", entity=" + entity + '}';
    }
}
