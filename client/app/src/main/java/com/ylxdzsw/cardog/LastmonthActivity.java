package com.ylxdzsw.cardog;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LastmonthActivity extends AppCompatActivity {
    private String username;
    private String password;
    private WebView webView;
    private String jsondata;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lastmonth);


        Bundle extras = this.getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");

        webView = (WebView) findViewById(R.id.webView);

        webView.loadUrl("file:///android_asset/www/chart.html");
        webView.getSettings().setJavaScriptEnabled(true);

        new Thread(this::query).start();
    }

    private void query() {
        String host = getString(R.string.url_prefix);

        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            data.put("period", "lastmonth");
            byte[] dataBytes = data.toString().getBytes("UTF-8");

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(host + "/query").openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
            urlConnection.getOutputStream().write(dataBytes);

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = reader.read()) >= 0;)
                sb.append((char)c);

            jsondata = sb.toString();

            runOnUiThread(this::updateView);
        } catch (FileNotFoundException e) {
            runOnUiThread(() -> Toast.makeText(this, "无法连接到服务器", Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show());
        }
    }

    private void updateView() {
        webView.evaluateJavascript("gen_loc("+jsondata+");", val -> {});
    }
}
