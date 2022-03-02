package com.android.volley.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class VolleyMocks {

    public static RequestQueue createMockRequestQueue(){
        Answer<NetworkResponse> delayAnswer =
                new Answer<NetworkResponse>() {
                    @Override
                    public NetworkResponse answer(InvocationOnMock invocationOnMock)
                            throws Throwable {
                        Thread.sleep(20);
                        return mock(NetworkResponse.class);
                    }
                };
        Network mMockNetwork = mock(Network.class);

        ResponseDelivery mDelivery = new ImmediateResponseDelivery();

        RequestQueue queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()), 1, mDelivery);
//        queue.addRequestFinishedListener(mMockListener);
        queue.start();
        return queue;
    }
}
