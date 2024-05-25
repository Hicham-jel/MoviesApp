package com.example.moviesapp;

import androidx.core.app.ActivityCompat;
import static android.content.ContentValues.TAG;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

    public class MovieDetailActivity extends AppCompatActivity implements
            OnMapReadyCallback{
        private SupportMapFragment mapFragment;
        private TextView descriptionTextView;
        private TextView Name;

        private ImageView img;
        private String trailerKey;
        private RequestQueue requestQueue;
        private Button playButton;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
        private GoogleMap mMap;
        private List<LatLng> cinemaLocations = new ArrayList<>();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_movie_detail);
            descriptionTextView = findViewById(R.id.Details);
            img = findViewById(R.id.imageview);
            Name = findViewById(R.id.textName);
            requestQueue = Volley.newRequestQueue(this);
// Retrieve movie ID from Intent extras
            int movieId = getIntent().getIntExtra("movieId", -1);
            if (movieId != -1) {
                fetchMovieDetails(movieId);
            } else {
                descriptionTextView.setText("No movie ID provided");
            }
            playButton = findViewById(R.id.playButton);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playTrailer();
                }
            });
            cinemaLocations.add(new LatLng(33.596460, -7.615480)); //Example cinema location
            mapFragment = (SupportMapFragment)getSupportFragmentManager().
                    findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        private void fetchMovieDetails(int movieId) {
            String TMDB_API_KEY = "9d566083d952b168585fa6f289c990cf";
            String movieDetailsUrl = "https://api.themoviedb.org/3/movie/" + movieId +
                    "?api_key=" + TMDB_API_KEY;
            String movieVideosUrl = "https://api.themoviedb.org/3/movie/" + movieId +
                    "/videos?api_key=" + TMDB_API_KEY;
            JsonObjectRequest movieDetailsRequest = new
                    JsonObjectRequest(Request.Method.GET, movieDetailsUrl,
                    null, new Response.Listener<JSONObject>() {
                @Override

                public void onResponse(JSONObject response) {

                    try {
// Log the full response for debugging
                        Log.d(TAG, "Movie Details Response: " +

                                response.toString());
//Retrieve movie name, description, and image URL from the JSON response
                        String movieName = response.getString("title");
                        String movieDescription = response.getString("overview");
                        String imageUrl = "https://image.tmdb.org/t/p/w500" +
                                response.getString("poster_path");
// Set the movie name in the appropriate TextView
                        Name.setText(movieName);
// Set the movie description in the appropriate TextView
                        descriptionTextView.setText(movieDescription);
// Load the movie image using Glide or any other image loading library
                        Glide.with(MovieDetailActivity.this).load(imageUrl).into(img);
                    } catch (JSONException e) {
// e.printStackTrace();
// Handle specific exceptions if necessary
                        if (e.getMessage().contains("title")) {
                            Log.e(TAG, "Error: Missing 'title' key in response");
                        } else if (e.getMessage().contains("overview")) {
                            Log.e(TAG, "Error: Missing 'overview' key in response");
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override

                public void onErrorResponse(VolleyError error) {

// Log error message for debugging

                    Log.e(TAG, "Error fetching movie details: " +

                            error.getMessage());
// Set error message in the TextView

                    descriptionTextView.setText("Failed to fetch movie details");

                }
            });
            JsonObjectRequest movieVideosRequest = new
                    JsonObjectRequest(Request.Method.GET, movieVideosUrl, null, new
                    Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("results")) {
                                    Toast.makeText(MovieDetailActivity.this,
                                            "fiiiiiiiiiilm", Toast.LENGTH_SHORT).show();
                                    JSONArray results = response.getJSONArray("results");
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject video = results.getJSONObject(i);
                                        if (video.getString("type").equals("Trailer")) {
                                            trailerKey = video.getString("key");
// Log the trailer key for debugging
                                            Log.d(TAG, "Trailer Key: " + trailerKey);
// Now you can use the trailer key as needed
// For example, you can store it in a member variable and use it later to play the trailer
// trailerKey =
                                            video.getString("key");

                                            break; // Assuming you only need first trailer key
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener() {

                @Override

                public void onErrorResponse(VolleyError error) {

// Log error message for debugging
                    Log.e(TAG, "Error fetching movie videos: " +
                            error.getMessage());
// Show a message that the trailer is not available
                    Toast.makeText(MovieDetailActivity.this, "Trailer not available", Toast.LENGTH_SHORT).show();
                }
            });
// Add both requests to the RequestQueue
            requestQueue.add(movieDetailsRequest);
            requestQueue.add(movieVideosRequest);
        }
        private void playTrailer() {
// Check if trailerKey is available
            if (trailerKey != null && !trailerKey.isEmpty()) {
                Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
// Construct the URL for the trailer
                String trailerUrl = "https://www.youtube.com/embed/" + trailerKey;
// Create an intent to start the VideoPlayerActivity
                Toast.makeText(this, "direction video", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MovieDetailActivity.this, VideoPlayer.class);

                intent.putExtra("videoUrl", trailerUrl);
                startActivity(intent);
            } else {
// Show a message that the trailer is not available
                Toast.makeText(this, "Trailer not available", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
// Check location permission
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
// Enable current location button
                mMap.setMyLocationEnabled(true);
// Move camera to current location
                LatLng cinemaLocation = new LatLng(33.596460, -7.615480);
                addCinemaMarker(cinemaLocation);
                moveToCurrentLocation();
// addCinemaMarkers();
            } else {
// Request location permission
                ActivityCompat.requestPermissions(this, new
                                String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        private void addCinemaMarker(LatLng cinemaLocation) {
            mMap.addMarker(new MarkerOptions().position(cinemaLocation).title("Cinema")
                    .snippet("Location of the cinema"));
        }
        private void moveToCurrentLocation() {
// Check if the app has location permission
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
// Get the location manager
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
// Get the last known location
                Location location = null;
                try {
                    location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException e) {
                    e.printStackTrace();
// Handle the SecurityException
                    Toast.makeText(this, "Location permission denied",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
// Check if the location is not null
                if (location != null) {
// Create a LatLng object representing the current location
                    LatLng currentLocation = new LatLng(location.getLatitude(),
                            location.getLongitude());
// Move the camera to the current location with a zoom level of 15
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                            15));
                } else {
// Handle the case where the last known location is null
                    Toast.makeText(this, "Last known location not available",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
// Request location permission if it has not been granted

                ActivityCompat.requestPermissions(this, new
                                String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[]
                permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
// Permission granted, move camera to current location
                    moveToCurrentLocation();
                } else {
// Permission denied, show a message or handle accordingly
                    Toast.makeText(this, "Location permission denied",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
