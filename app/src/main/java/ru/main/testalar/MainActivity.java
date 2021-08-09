package ru.main.testalar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    private String code;
    private int pageNumber = 1;
    private static String GET_URL = "https://www.alarstudios.com/test/data.cgi?code=%s&p=%s";
    private final static Integer PORT = 443;

    RecyclerView recyclerView;
    AdapterClass adapterClass;
    List<Model> modelClass = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        code = Objects.requireNonNull(getIntent().getExtras()).getString(LoginActivity.STRING_CODE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterClass = new AdapterClass(this, recyclerView, modelClass);
        recyclerView.setAdapter(adapterClass);
        sendGETRequest();

        adapterClass.setLoadMore(() -> {
            modelClass.add(null);
            recyclerView.post(() -> adapterClass.notifyItemInserted(modelClass.size() - 1));
            sendGETRequest();
        });
    }

    private void sendGETRequest(){
        new Thread(() -> {
            try {
                URL urlObject = new URL(String.format(GET_URL, code, pageNumber));

                Socket socket = SSLSocketFactory.getDefault().createSocket(urlObject.getHost(), PORT);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println("GET " + urlObject.getFile() + " HTTP/1.0\r\nHost: " + urlObject.getHost()+ "\r\n\r\n");
                printWriter.flush();

                BufferedReader response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String responseStr = response.readLine();
                while (responseStr != null && responseStr != "") {
                    if (responseStr.contains("status")){
                        JSONObject obj = new JSONObject(responseStr);
                        if (obj.get("status").equals("ok")){

                            for (int i=0; i < obj.getJSONArray("data").length(); i++) {
                                JSONObject mapItem = obj.getJSONArray("data").getJSONObject(i);
                                modelClass.add(new Model(
                                        mapItem.getString("id"),
                                        mapItem.getString("name"),
                                        mapItem.getString("country"),
                                        mapItem.getDouble("lat"),
                                        mapItem.getDouble("lon")));
                            }

                            handler.post(() -> {
                                if (modelClass.size() > 10) {
                                    modelClass.remove(modelClass.size() - 11);
                                    adapterClass.notifyItemRemoved(modelClass.size());
                                }
                                adapterClass.notifyDataSetChanged();
                                adapterClass.setLoaded();
                            });
                        }
                        break;
                    }
                    responseStr = response.readLine();
                }
                pageNumber++;
                response.close();
                printWriter.close();
                socket.close();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}