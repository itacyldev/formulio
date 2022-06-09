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
import org.mini2Dx.collections.MapUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.processor.AbstractProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.ContextPopulateProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;
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

    private String inputFile;

    // context where additionally the results of the processor will be published
    private String outputContext;
    private String contentType = "text/plain";
    private String responseContentType;
    private String contentCharset;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Integer timeout = 30; // seconds

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
            FileUtils.writeByteArrayToFile(f, entity.getResponseContent());
        } catch (IOException e) {
            throw new TaskException("There was an error while trying to store the response content of request: " + url, e);
        }
        // publish outputFile name in task context
        LOGGER.info("Response content stored in file: " + f.getAbsolutePath());
        this.getTaskContext().put("outputFile", getOutputFile());
        if (StringUtils.isNotBlank(outputContext)) {
            this.getGlobalContext().put(outputContext + ".outputFile", getOutputFile());
        }
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
            strContent = new String(entity.getResponseContent(), cs);
        } catch (UnsupportedEncodingException e) {
            throw new TaskException("Invalid charset received from server: " + cs, e);
        }
        // publish response as string
        this.getTaskContext().put("output", strContent);
        if (StringUtils.isNotBlank(outputContext)) {
            this.getGlobalContext().put(outputContext + ".output", strContent);
        }
        if (isJsonContent(entity)) {
            // parse to json object
            if (StringUtils.isBlank(strContent)) {
                responseObj = MapUtils.EMPTY_MAP;
            } else {
                try {
                    responseObj = JsonUtils.mapper().readValue(strContent, new TypeReference<Map<String, Object>>() {
                    });
                } catch (JsonProcessingException e) {
                    throw new TaskException("An error occurred while trying to parse server response, is it really json?: "
                            + json, e);
                }
            }
            // publish response as json object
            this.getTaskContext().put("outputJson", responseObj);
            if (StringUtils.isNotBlank(outputContext)) {
                this.getGlobalContext().put(outputContext + ".outputJson", responseObj);
            }
        }
    }

    private boolean isJsonContent(HttpEntity entity) {
        // if responseContentType attribute is set use it, otherwise, use the response Content-Type header
        String responseContType = (StringUtils.isNotBlank(responseContentType)) ? responseContentType :
                entity.getResponseContentType();
        return (StringUtils.isBlank(responseContType)) ? false : responseContType.toLowerCase().startsWith("application/json");
    }

    /**
     * Store in contexto response info
     *
     * @param entity
     */
    private void storeResponseInfo(HttpEntity entity) {
        this.getTaskContext().put("responseHeaders", entity.getResponseHeaders());
        if (StringUtils.isNotBlank(outputContext)) {
            this.getGlobalContext().put(outputContext + ".responseHeaders", entity.getResponseHeaders());
        }
    }

    private RawRequest createRequest(RequestFuture future) throws TaskException {
        byte[] content = null;
        if(inputFile != null){
            try{
                content = FileUtils.readFileToByteArray(new File(inputFile));
            } catch (IOException e) {
                throw new TaskException("There was an error while trying to read inputfile: ", e);
            }
        } else {
            if (body != null) {
                content = body.getBytes(this.charset);
            }
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (params == null) {
            params = new HashMap<>();
        }
        if (headers.containsKey("contentType") && StringUtils.isNotBlank(contentType)) {
            headers.put("contentType", contentType);
            //headers.put("Content-Type", "multipart/form-data");
        }
        HttpEntity entity = new HttpEntity(content, this.headers);
        entity.setParams(this.params);
        entity.setContentType(this.contentType);
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

    public String getOutputContext() {
        return outputContext;
    }

    public void setOutputContext(String outputContext) {
        this.outputContext = outputContext;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

}