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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import es.jcyl.ita.formic.jayjobs.utils.VolleyUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RawRequest extends Request<HttpEntity> {

    private final Response.Listener listener;
    private HttpEntity entity;

    //private String boundary = "apiclient-" + System.currentTimeMillis();

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();

    public RawRequest(int method, String url, HttpEntity entity, Response.Listener listener,
                      Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.entity = entity;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        /*Map<String, String> entityHeaders = entity.getHeaders();
        return entityHeaders != null ? entityHeaders : super.getHeaders();*/
        Map<String, String> entityHeaders = entity.getHeaders();
        entityHeaders.put("Content-Type","multipart/form-data; boundary=" + boundary);
        return entityHeaders;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        //Creating parameters
        Map<String,String> params = new Hashtable<String, String>();

        //Adding parameters
        params.put("contentFile", "cabecera.csv");

        //returning parameters
        return params;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = this.entity.getContent();
        if (getHeaders().containsKey("Content-Type") && ((String)getHeaders().get("Content-Type")).contains("multipart")){
            try {
                body = createFileContent(this.entity.getContent(), boundary, getBodyContentType(), "C:\\Desarrollo\\workspaces\\wks-and\\FRMDRD\\jobs\\build\\intermediates\\java_res\\debugUnitTest\\out\\cabecera.csv");
                //body = createFileContent(this.entity.getContent(), boundary, getBodyContentType(), "cabecera.csv");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            body = this.entity.getContent();
        }
        return body;
        /*this.entity.getParams().put("contentFile", "cabecera.csv");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // populate text payload
            //Map<String, String> params = this.entity.getParams();
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(dos, params, getParamsEncoding());
            }

            // populate data byte payload
            Map<String, DataPart> data = getByteData();
            if (data != null && data.size() > 0) {
                dataParse(dos, data);
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            FileUtils.writeByteArrayToFile(new File("C:\\Desarrollo\\workspaces\\wks-and\\FRMDRD\\jobs\\build\\intermediates\\java_res\\debugUnitTest\\out\\prueba2.txt"), bos.toByteArray());

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    /**
     * Custom method handle data payload.
     *
     * @return Map data part label with data byte
     * @throws AuthFailureError
     */
    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        Map<String, DataPart> params = new HashMap<>();

        params.put("contentFile", new DataPart("cabecera.csv" ,this.entity.getContent()));
        return params;
    }

    /**
     * Parse string map into data output stream by key and value.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param params           string inputs collection
     * @param encoding         encode the inputs, default UTF-8
     * @throws IOException
     */
    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    /**
     * Parse data into data output stream.
     *
     * @param dataOutputStream data output stream handle file attachment
     * @param data             loop through data
     * @throws IOException
     */
    private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
        }
    }

    /**
     * Write string data into header and data output stream.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param parameterName    name of input
     * @param parameterValue   value of input
     * @throws IOException
     */
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    /**
     * Write data file into header and data output stream.
     *
     * @param dataOutputStream data output stream handle data parsing
     * @param dataFile         data byte as DataPart from collection
     * @param inputName        name of data input
     * @throws IOException
     */
    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart() {
        }

        DataPart(String name, byte[] data) {
            fileName = name;
            content = data;
        }

        String getFileName() {
            return fileName;
        }

        byte[] getContent() {
            return content;
        }

        String getType() {
            return type;
        }

    }

    private byte[] createFileContent(byte[] data, String boundary, String contentType, String fileName) throws IOException {
        String start = "Content-Type: "+ contentType + "\"--"+  boundary + "\"\r\n\r\n"+
                "--"+  boundary + "\r\n"+
                "Content-Type: application/octet-stream; name="+fileName + "\r\n"+
                "Content-Transfer-Encoding: binary"+"\r\n"+
                "Content-Disposition: form-data; name=\"contentFile\"; filename=\"" + fileName + "\"\r\n\r\n";

        String end = "\r\n--" + boundary + "--";


        byte[] bytesFileContent = ArrayUtils.addAll(start.getBytes(), ArrayUtils.addAll(data, end.getBytes()));

        FileUtils.writeByteArrayToFile(new File("C:\\Desarrollo\\workspaces\\wks-and\\FRMDRD\\jobs\\build\\intermediates\\java_res\\debugUnitTest\\out\\prueba2.txt"), bytesFileContent);
        return bytesFileContent;
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
        //return this.entity.getContentType();
        return "multipart/form-data; boundary=";
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
