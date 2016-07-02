package com.ylxdzsw.cardog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class Record {
    public String time;
    public String loc;
    public String amount;
    public String balance;
    public String status;
}

public class TodayActivity extends AppCompatActivity {
    private String username;
    private String password;
    private ListView listView;
    private List<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        Bundle extras = this.getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");

        records  = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        new Thread(this::query).start();
    }

    private void query() {
        String host = getString(R.string.url_prefix);

        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            data.put("period", "today");
            byte[] dataBytes = data.toString().getBytes("UTF-8");

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(host + "/query").openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
            urlConnection.getOutputStream().write(dataBytes);

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            parseData(reader);
            runOnUiThread(this::updateView);
        } catch (FileNotFoundException e) {
            runOnUiThread(() -> Toast.makeText(this, "无法连接到服务器", Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show());
        }
    }

    private void parseData(BufferedReader data) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int c; (c = data.read()) >= 0;)
                sb.append((char)c);

            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Record record = new Record();
                record.time    = jsonObject.getString("time");
                record.loc     = jsonObject.getString("loc");
                record.amount  = jsonObject.getString("amount");
                record.balance = jsonObject.getString("balance");
                record.status  = jsonObject.getString("status");
                records.add(record);
            }
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT));
        }
    }

    private void updateView() {
        Toast.makeText(this, "fuck", Toast.LENGTH_SHORT).show();
    }
}
