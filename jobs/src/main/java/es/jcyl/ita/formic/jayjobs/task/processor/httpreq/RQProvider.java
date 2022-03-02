package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import java.io.File;

import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;

public class RQProvider {

    private static RequestQueue instance;

    private static void createInstance() {
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        instance = new RequestQueue(new NoCache(), network);

        // Start the queue
        instance.start();
    }

    public static RequestQueue getRQ() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }
}
