package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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

        RequestQueue rq = (requestQueue !=null)? requestQueue: RQProvider.getRQ();
        request.setShouldCache(false);
        future.setRequest(rq.add(request));

        LOGGER.info("Executing request " + rq.toString());
        rq.add(request);
        // wait for the response
        HttpEntity entity = null;
        try {
            entity = future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            this.onErrorResponse(new VolleyError(e));
            LOGGER.error(e);
        }
        LOGGER.debug(String.format("Http request to %s completed", url));
        LOGGER.debug(new String(entity.getContent()));
    }

    private void configureOutputFile() {
        if (StringUtils.isBlank(outputFile)) {
            Context ctx = getGlobalContext();
            outputFile = String.format("%s_%s_%s_httprequest.out", System.currentTimeMillis(),
                    ctx.get("app.id"), ctx.get("job.id"));
            LOGGER.info(String.format(
                    "The 'outputFile' attribute is not set, random name generated: [%s].",
                    outputFile));
        }
        outputFile = TaskResourceAccessor.getWorkingFile(getGlobalContext(), outputFile);
    }

    private RawRequest createRequest(RequestFuture future) {
        byte[] content = null;
        if(body != null){
            content = body.getBytes(this.charset);
        }
        HttpEntity entity = new HttpEntity(content, this.headers);
        return new RawRequest(Request.Method.GET, url, entity, future, future);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        LOGGER.error(error.getMessage());
        LOGGER.error("Http status: " + error.networkResponse);
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
}
