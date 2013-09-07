package com.phyous.trackpad;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TrackpadActivity extends Activity {
    private final String DEBUG_TAG = this.getClass().getName();

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
                int action = MotionEventCompat.getActionMasked(event);

                switch(action) {
                    case (MotionEvent.ACTION_MOVE) :
                        float x = event.getX();
                        float y = event.getY();

                        String eventStr = String.format("%f:X %f:Y", x, y);
                        Log.d(DEBUG_TAG, eventStr);

                        mXpos.setText(String.format("%.2f", x));
                        mYpos.setText(String.format("%.2f", y));
                        return true;
                    default :
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
}
