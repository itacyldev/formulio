package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import com.android.volley.Header;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpEntity {
    protected byte[] content;
    private Map<String, String> headers;
    private int httpStatus;

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
}
