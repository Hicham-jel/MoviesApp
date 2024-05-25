package com.example.moviesapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class VideoPlayer extends AppCompatActivity {
    private WebView webView;
    private String videoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);
// Get the video URL from the intent
        videoUrl = getIntent().getStringExtra("videoUrl");
// Initialize the WebView
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
// Load the video URL in the WebView
        webView.loadUrl(videoUrl);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
// Adjust the layout or configuration of your WebView here
        if (webView != null) {
            webView.loadUrl(videoUrl);
        }
    }}