package com.phyous.trackpad;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TrackpadActivity extends Activity {
    private final String DEBUG_TAG = this.getClass().getName();
    private final String TRACKPAD_API_URL = "http://%s:9000/updatePosition?dx=%f&dy=%f";
    private final String TRACKPAD_API_IP = "192.168.1.2";
    private final int frameRate = 15;
    private final float mSensitivity = 70.0f;

    private int currentFrame = 0;
    private float lastX = 0.0f;
    private float lastY = 0.0f;

    private TextView mXpos;
    private TextView mYpos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackpad);

        mXpos = (TextView) findViewById(R.id.x_pos_initial);
        mYpos = (TextView) findViewById(R.id.y_pos_initial);

        View myView = findViewById(R.id.trackpad);

        myView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (currentFrame % frameRate != 0) {
                    currentFrame++;
                    return true;
                } else {
                    currentFrame++;
                }

                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_MOVE):
                        float x = event.getX();
                        float y = event.getY();
                        mXpos.setText(String.format("%.2f", x));
                        mYpos.setText(String.format("%.2f", y));

                        float dx = (x - lastX) / mSensitivity;
                        float dy = (y - lastY) / mSensitivity;
                        new UpdatePositionTask().execute(String.format(TRACKPAD_API_URL, TRACKPAD_API_IP, dx, dy));

                        lastX = x;
                        lastY = y;
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trackpad, menu);
        return true;
    }

    /*
    What's going on here?
    See: http://developer.android.com/training/basics/network-ops/connecting.html
   */
    private class UpdatePositionTask extends AsyncTask<String, Void, String> {
        public UpdatePositionTask() {
            super();
        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                String error = "ERROR";
                return error;
            }
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertStreamToString(is);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
