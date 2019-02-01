package com.rhodonite.json_sample;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    JSONArray JArray;
    JSONObject jsonObj;
    byte[] JasonKey = {(byte)0x00}; //input your JASON key
    String payload = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jsonObj = new JSONObject();
        JArray = new JSONArray();
        String test1 = "test1";
        String test2 = "test2";
        try {
            jsonObj.put("Test1", test1);
            jsonObj.put("Test2", test2);
            JArray.put(jsonObj);
            String test = JArray.toString().substring(1, JArray.toString().length() - 1);
            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                    new Payload(test));
            jwsObject.sign(new MACSigner(JasonKey));
            payload = jwsObject.serialize();
            JASONTask task = new JASONTask(jwsObject.serialize());
            task.execute();
        } catch (JSONException | JOSEException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("StaticFieldLeak")
    public class JASONTask extends AsyncTask {
        String urlString = "http://"; //input your http server path
        String urlParameter = "";
        JASONTask(String cryptPayload) {
            urlParameter = "payload=" + cryptPayload;
            urlString = urlString + "?" + urlParameter;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object[] params) {
            String backResult = "";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                backResult = response.toString();
            } catch (Exception ex) {
                backResult = ex.getMessage();
            }
            return backResult;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                Log.d("backResult", o.toString());
                JSONObject jsonObj = new JSONObject(o.toString());
                Log.d("result", jsonObj.getString("result"));   //開始切分回傳內容
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}