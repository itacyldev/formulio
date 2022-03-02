package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.Map;

public class RawRequest extends Request<HttpEntity> {

    private final Response.Listener listener;
    private HttpEntity entity;


    public RawRequest(int method, String url, HttpEntity entity,
                      Response.Listener listener, Response.ErrorListener errorListener) {
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
    protected Response<HttpEntity> parseNetworkResponse(NetworkResponse response) {
        HttpEntity entity = new HttpEntity(response.data, response.headers);
        entity.setHttpStatus(response.statusCode);
        return Response.success(entity, null);
    }

    @Override
    protected void deliverResponse(HttpEntity response) {
        listener.onResponse(response);
    }

}
