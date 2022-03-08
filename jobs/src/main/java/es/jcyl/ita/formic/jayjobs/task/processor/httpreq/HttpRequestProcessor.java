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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.processor.AbstractProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.ContextPopulateProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;
import es.jcyl.ita.formic.jayjobs.utils.JsonUtils;
import es.jcyl.ita.formic.jayjobs.utils.VolleyUtils;

/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class HttpRequestProcessor extends AbstractProcessor implements NonIterProcessor, Response.ErrorListener {
    protected static final Log LOGGER = LogFactory.getLog(ContextPopulateProcessor.class);

    private String url;
    private Map<String, Object> jsonObject;
    private String method;
    private String store;
    private String body;

    // output variable name to set the object
    private String outputFile;
    private String contentType = "text/plain";
    private String contentCharset;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Integer timeout = 15; // seconds

    // internal objects
    private RequestQueue requestQueue;

    private int httpMethod = Request.Method.GET;
    private Charset charset = Charset.defaultCharset();

    private enum STORE_TYPE {CONTEXT, FILE, BOTH}

    private STORE_TYPE storeType = STORE_TYPE.BOTH;

    @Override
    public void process() throws TaskException {
        setup();
        // create and execute request
        RequestFuture<HttpEntity> future = RequestFuture.newFuture();
        RawRequest request = createRequest(future);

        RequestQueue rq = (requestQueue != null) ? requestQueue : RQProvider.getRQ();
        request.setShouldCache(false);
        future.setRequest(rq.add(request));

        LOGGER.info("Executing request " + request);
        // wait for the response
        HttpEntity entity;
        try {
            entity = future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            this.onErrorResponse(new VolleyError(e));
            throw new TaskException("There was an error while trying to execute request: " + request, e);
        }
        // treat response
        if (storeType == STORE_TYPE.FILE || storeType == STORE_TYPE.BOTH) {
            storeResponseInFile(entity);
        }
        if (storeType == STORE_TYPE.CONTEXT || storeType == STORE_TYPE.BOTH) {
            storeResponseInContext(entity);
        }
        storeResponseInfo(entity);
        LOGGER.debug(String.format("Http request to %s completed", url));
    }

    /**
     * Read processor configuration and setup internal variables
     */
    private void setup() throws TaskException {
        // configure store type
        if (StringUtils.isNotBlank(store)) {
            try {
                storeType = STORE_TYPE.valueOf(store.toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                throw new TaskException("Invalid 'store' attribute value " + Arrays.asList(STORE_TYPE.values()));
            }
        }
        // set http method
        if (StringUtils.isNotBlank(this.method)) {
            this.httpMethod = VolleyUtils.getMethodFromString(this.method);
        }
        // set charset
        if (StringUtils.isNotBlank(this.contentCharset)) {
            this.charset = Charset.forName(this.contentCharset);
        }
    }

    /**
     * Stores response in file and publishes the absolute file name in the task context.
     *
     * @param entity
     * @throws TaskException
     */
    private void storeResponseInFile(HttpEntity entity) throws TaskException {
        // prepare output file and write entity content
        File f = configureOutputFile();
        try {
            FileUtils.writeByteArrayToFile(f, entity.getContent());
        } catch (IOException e) {
            throw new TaskException("There was an error while trying to store the response content of request: " + url, e);
        }
        // publish outputFile name in task context
        LOGGER.info("Response content stored in file: " + f.getAbsolutePath());
        this.getTaskContext().put("outputFile", this.outputFile);
    }

    /**
     * Stores response as string or json object in current task context.
     *
     * @param entity
     * @throws TaskException
     */
    private void storeResponseInContext(HttpEntity entity) throws TaskException {
        // parse content
        Object responseObj;
        String strContent;
        String cs = null;
        String json = null;
        try {
            cs = HttpHeaderParser.parseCharset(entity.getResponseHeaders(), this.charset.toString());
            strContent = new String(entity.content, cs);
        } catch (UnsupportedEncodingException e) {
            throw new TaskException("Invalid charset received from server: " + cs, e);
        }
        // publish response as string
        this.getTaskContext().put("output", strContent);
        if (contentType.startsWith("application/json")) {
            // parse to json object
            try {
                responseObj = JsonUtils.mapper().readValue(strContent, new TypeReference<Map<String, Object>>() {
                });
            } catch (JsonProcessingException e) {
                throw new TaskException("An error occurred while trying to parse server response: " + json, e);
            }
            // publish response as json object
            this.getTaskContext().put("outputJson", responseObj);
        }
    }

    /**
     * Store in contexto response info
     * @param entity
     */
    private void storeResponseInfo(HttpEntity entity) {
        this.getTaskContext().put("responseHeaders", entity.getResponseHeaders());
    }


    private File configureOutputFile() {
        if (StringUtils.isBlank(this.outputFile)) {
            Context ctx = this.getGlobalContext();
            this.outputFile = String.format("%s_%s_%s_httprequest.out", System.currentTimeMillis(),
                    ctx.get("app.id"), ctx.get("job.id"));
            LOGGER.info(String.format("The 'outputFile' attribute is not set, random name generated: [%s].", outputFile));
        }
        this.outputFile = TaskResourceAccessor.getWorkingFile(getGlobalContext(), outputFile);
        return new File(this.outputFile);
    }

    private RawRequest createRequest(RequestFuture future) {
        byte[] content = null;
        if (body != null) {
            content = body.getBytes(this.charset);
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (headers.containsKey("contentType") && StringUtils.isNotBlank(contentType)) {
            headers.put("contentType", contentType);
        }
        HttpEntity entity = new HttpEntity(content, this.headers);
        entity.setParams(this.params);
        return new RawRequest(httpMethod, url, entity, future, future);
    }

    public void onErrorResponse(VolleyError error) {
        LOGGER.error(error.getMessage());
        LOGGER.error(error.networkResponse);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(Map<String, Object> jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getContentCharset() {
        return contentCharset;
    }

    public void setContentCharset(String contentCharset) {
        this.contentCharset = contentCharset;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
