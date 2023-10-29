package com.example.up2date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        Content content = new Content();
        content.execute();

        Intent intent = getIntent();
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null && "up2date".equals(uri.getScheme())) {
                // Perform the desired action for the deep link
                navigateToHomeScreen();
            }
        }

        // Initialize OneSignal
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId("434aa46d-8092-45aa-a2b1-fa75a9d36175");
    }

    private void navigateToHomeScreen() {
        // Add code here to navigate to the home screen
        // For example, you can start a new activity or load a fragment
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        recyclerView.smoothScrollToPosition(0);
        finish(); // Optional: Close the current activity if needed
    }

    private void refreshData() {
        parseItems.clear();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

        // Perform any other actions to reset your app to the initial state
        // For example, you may need to reset any flags, counters, or reload initial data
        // ...
    }

    private class Content extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = "https://cusat.ac.in/news-lists.php";
                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("li");
                int size = data.size();

                for (int i = 0; i < size; i++) {
                    String imgUrl = "https://images.pexels.com/photos/518543/pexels-photo-518543.jpeg?auto=compress&cs=tinysrgb&w=600";
                    if (i % 2 == 0) {
                        imgUrl = "https://images.pexels.com/photos/16059908/pexels-photo-16059908/free-photo-of-leaves-camera-map-eyeglasses-and-newspaper.jpeg";
                    }
                    if (i % 3 == 0) {
                        imgUrl = "https://images.pexels.com/photos/3837464/pexels-photo-3837464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1";
                    }
                    String title = data.select("h4")
                            .eq(i)
                            .text();
                    String detailUrl = data.select("h4")
                            .eq(i)
                            .text();

                    parseItems.add(new ParseItem(imgUrl, title, detailUrl, i));
                    Log.d("items", "img:" + imgUrl + " . title" + title);

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
            if (parseItems.size() > 0) {
                ParseItem latestItem = parseItems.get(0);
                String notificationTitle = "New Article: " + latestItem.getTitle();
                String notificationContent = "Tap to read more";

                // Build notification payload
                JSONObject notificationPayload = new JSONObject();
                try {
                    notificationPayload.put("headings", new JSONObject().put("en", notificationTitle));
                    notificationPayload.put("contents", new JSONObject().put("en", notificationContent));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Send notification to OneSignal
                OneSignal.postNotification(notificationPayload, new OneSignal.PostNotificationResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        // Notification sent successfully
                    }

                    @Override
                    public void onFailure(JSONObject response) {
                        // Failed to send notification
                    }
                });
            }
        }
    }
}
