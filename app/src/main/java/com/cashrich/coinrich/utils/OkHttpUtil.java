package com.cashrich.coinrich.utils;

import android.util.Log;

import com.cashrich.coinrich.vo.ResponseVo;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    private static final String TAG = "OkHttpUtil";

    public static ResponseVo cashRichPostRequest(JSONObject jsonObject, String url, Map<String, String> headerMap) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        headerMap.put("Content-Type", "application/json");
        Request request = new Request.Builder().headers(Headers.of(headerMap)).url(url).post(body).build();

        try {
            // Execute the request synchronously
            Response response = client.newCall(request).execute();
            return handleResponse(response);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return new ResponseVo(0, "Error occurred while connection");
        }
    }

    private static ResponseVo handleResponse(Response response) {
        String responseBody;
        try {
            assert response.body() != null;
            responseBody = response.body().string();
        } catch (IOException e) {
            return new ResponseVo(0, "Internal Error");
        }

        ResponseVo responseVo = new ResponseVo();
        if (response.code() == 200) {
            responseVo.setVal(1);
            responseVo.setResponse(responseBody);
            return responseVo;
        }
        responseVo.setVal(0);
        if (responseBody.startsWith("{")) {
            responseVo.setResponse("Error occurred while connection");
        } else {
            responseVo.setResponse(responseBody);
        }
        return responseVo;
    }
}