package com.jefeko.apptwoway.http;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class VolleyHelper {

    private RequestQueue mRequestQueue;
    private ProgressDialog progressDialog;
    private Context context = null;
    public VolleyHelper(Context context) {
        this.context = context;
        mRequestQueue = VolleyRequestQueue.getInstance(context).getRequestQueue();
    }

    //get
    public void sendRequest(final String code, String url, Response.Listener<String> response) {
        showProgress();
        StringRequest request = new StringRequest(Request.Method.GET, url, response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorResponse(code, error);
                    }
                });

        mRequestQueue.add(request);
        dissmissProgress();

    }

    //post
    public void sendRequest(final String code, String url, final Map<String, String> dataParams, Response.Listener<String> response) {
        StringRequest request = new StringRequest(Request.Method.POST, url, response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorResponse(code, error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                LogUtils.e("data params = " + dataParams);
                return dataParams;
            }
        };
        mRequestQueue.add(request);
    }

    //multipart (upload image)
    /*public void sendRequest(final String code, String url, final Map<String, String> dataParams, final Map<String, DataPart> imgParams, Response.Listener<NetworkResponse> response, Response.ErrorListener error) {
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url, response, error
                ){
                    @Override
                    protected Map<String, String> getParams() {
                        LogUtils.e("multipart data params = " + dataParams);
                        return dataParams;
                    }

                    @Override
                    protected Map<String, DataPart> getByteData() {
                        LogUtils.e("multipart DataPart = " + imgParams.toString());
                        return imgParams;
                    }
        };

        mRequestQueue.add(request);
    }*/

    public void errorResponse(String code, VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;

        String errorMessage = "Unknown error";

        if (networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                errorMessage = "Request timeout";
            } else if (error.getClass().equals(NoConnectionError.class)) {
                errorMessage = "Failed to connect server";
            }

        } else {
            String result = new String(networkResponse.data);
            try {

                JSONObject response = new JSONObject(result);
                String status = response.getString("status");
                String message = response.getString("message");

                LogUtils.e("Error Status", status);
                LogUtils.e("Error Message", message);

                if (networkResponse.statusCode == 404) {
                    errorMessage = "Resource not found";
                } else if (networkResponse.statusCode == 401) {
                    errorMessage = message+" Please login again";
                } else if (networkResponse.statusCode == 400) {
                    errorMessage = message+ " Check your inputs";
                } else if (networkResponse.statusCode == 500) {
                    errorMessage = message+" Something is getting wrong";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CommonUtil.showAlertDialog(context, errorMessage);

        dissmissProgress();

        LogUtils.i("Error", "[" + code + "]" + errorMessage);
        error.printStackTrace();
    }

    public void showProgress(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("로딩중입니다...");
        progressDialog.show();
    }

    public void dissmissProgress(){
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
