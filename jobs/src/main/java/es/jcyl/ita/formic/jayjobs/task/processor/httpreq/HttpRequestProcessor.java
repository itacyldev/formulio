package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.processor.AbstractProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.ContextPopulateProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;

public class HttpRequestProcessor extends AbstractProcessor implements NonIterProcessor, Response.ErrorListener {


    protected static final Log LOGGER = LogFactory
            .getLog(ContextPopulateProcessor.class);

    private String url;
    private String type = "json";
    private Map<String, Object> jsonObject;
    private String body;
    // output variable name to set the object
    private String outputVar;
    private String outputFile;
    private String contentEncoding = "UTF-8";
    private Map<String, String> headers;
    private Integer timeout; // seconds

    // internal objects
    private RequestQueue requestQueue;
    private Charset charset;

    @Override
    public void process() throws TaskException {
        RequestFuture<HttpEntity> future = RequestFuture.newFuture();

        RawRequest request = createRequest(future);

        RequestQueue rq = (requestQueue != null) ? requestQueue : RQProvider.getRQ();
        request.setShouldCache(false);
        future.setRequest(rq.add(request));

        LOGGER.info("Executing request " + rq.toString());
        rq.add(request);
        // wait for the response
        HttpEntity entity = null;
        try {
            entity = future.get(timeout, TimeUnit.SECONDS);

        } catch (Exception e) {
            this.onErrorResponse(new VolleyError(e));
            throw new TaskException("There was an error while trying to execute request: "
                    + request, e);
        }
        if(StringUtils.isNoneBlank(outputVar)){

        }
        // prepare output file and write entity content
        File f = configureOutputFile();
        try {
            FileUtils.writeByteArrayToFile(f, entity.getContent());
        } catch (IOException e) {
            throw new TaskException("There was an error while trying to store the " +
                    "response content of request: " + request, e);
        }

        LOGGER.debug(String.format("Http request to %s completed", url));
        LOGGER.debug(new String(entity.getContent()));
    }


    private File configureOutputFile() {
        if (StringUtils.isBlank(this.outputFile)) {
            Context ctx = this.getGlobalContext();
            this.outputFile = String.format("%s_%s_%s_httprequest.out", System.currentTimeMillis(),
                    ctx.get("app.id"), ctx.get("job.id"));
            LOGGER.info(String.format(
                    "The 'outputFile' attribute is not set, random name generated: [%s].",
                    outputFile));
        }
        this.outputFile = TaskResourceAccessor.getWorkingFile(getGlobalContext(), outputFile);
        return new File(this.outputFile);
    }

    private RawRequest createRequest(RequestFuture future) {
        byte[] content = null;
        if (body != null) {
            content = body.getBytes(this.charset);
        }
        HttpEntity entity = new HttpEntity(content, this.headers);
        return new RawRequest(Request.Method.GET, url, entity, future, future);
    }

    public void onErrorResponse(VolleyError error) {
        LOGGER.error(error.getMessage());
        LOGGER.error(error.networkResponse);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
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

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
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
}
