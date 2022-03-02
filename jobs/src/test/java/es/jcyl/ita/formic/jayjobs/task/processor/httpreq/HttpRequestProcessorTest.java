package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.mock.VolleyMocks;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;

public class HttpRequestProcessorTest {

    @Test
    public void test2() {
        RequestQueue requestQueue;

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));

        // Start the queue
        requestQueue.start();

        String url = "http://www.example.com";

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("llego: .... " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("llego ERROR: .... " + error.getMessage());
                    }
                });

// Add the request to the RequestQueue.
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);

        System.out.println("ASFasfs");
    }


    @Test
    public void test1() throws Exception {
        String url = "http://www.example.com";

        runInputStreamRequest(url, RQProvider.getRQ(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("Error: " + error.getMessage());
                System.err.println("Error: " + error.getMessage());
                System.err.println("Error: " + error.getMessage());
                System.err.println("Error: " + error.getMessage());
                System.err.println("Error: " + error.getMessage());
                System.err.println("Error: " + error.getMessage());
            }
        });
        System.out.println("asssssssssss");

    }

    public JSONObject runInputStreamRequest(String url, RequestQueue queue, Response.ErrorListener errorListener) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, errorListener);
        request.setShouldCache(false);
        queue.add(request);
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e));
        }
        return null;
    }

    @Test
    public void testSetSimpleValue() throws TaskException {
        Context taskContext = new BasicContext("t1");
        // create task mock and set the task context
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);

        RequestQueue queue = VolleyMocks.createMockRequestQueue();

        HttpRequestProcessor processor = new HttpRequestProcessor();
        processor.setRequestQueue(queue);
        String url = "https://my-json-server.typicode.com/typicode/demo/db";
        processor.setUrl(url);
        processor.process();

    }


}
