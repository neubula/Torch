package com.neubula.torch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Switch torchSwitch;
    RelativeLayout torchBody;
    LayoutParams layoutPars;
    Camera camera;
    Camera.Parameters cameraParam;
    Context context;
    PackageManager pm;
    boolean isTorchOn = false;
    boolean hasFlash = false;
    boolean bodyClicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = this;

        pm = context.getPackageManager();


        torchBody = (RelativeLayout) findViewById(R.id.tourch_body);
        torchSwitch = (Switch) findViewById(R.id.tourch_switch);


        //Get the current window attributes
        layoutPars = getWindow().getAttributes();

        if (camera == null)
            camera = Camera.open();

        hasFlash = hasFlash();
        if (hasFlash) {
            cameraParam = camera.getParameters();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Flash
        if (hasFlash) {
            torchBody.setBackgroundColor(Color.GRAY);
            // Starting flash
            cameraParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(cameraParam);
            camera.startPreview();
        } else {
            torchBody.setBackgroundColor(Color.WHITE);
            //Set the brightness of this window
            layoutPars.screenBrightness = 1f;
            //Apply attribute changes to this window
            getWindow().setAttributes(layoutPars);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        isTorchOn = true;
        bodyClicked = true;

        torchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleChange(isChecked);

            }
        });

        torchBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyClicked = !bodyClicked;
                torchSwitch.setChecked(bodyClicked);
                handleChange(bodyClicked);
            }
        });
    }

    private void handleChange(boolean isChecked) {
        if (isChecked) {
            if (!isTorchOn) {
                if (hasFlash) {
                    // Flash
                    torchBody.setBackgroundColor(Color.GRAY);
                    // Starting flash
                    cameraParam.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(cameraParam);
                    camera.startPreview();
                } else {
                    torchBody.setBackgroundColor(Color.WHITE);
                    //Set the brightness of this window
                    layoutPars.screenBrightness = 1.0f;
                    //Apply attribute changes to this window
                    getWindow().setAttributes(layoutPars);

                    getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                isTorchOn = true;
            }
        } else {
            if (isTorchOn) {
                if (hasFlash) {
                    // Flash
                    torchBody.setBackgroundColor(Color.GRAY);
                    // Stop flash
                    cameraParam.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(cameraParam);
                    camera.stopPreview();
                } else {

                    torchBody.setBackgroundColor(Color.GRAY);
                    //Set the brightness of this window
                    layoutPars.screenBrightness = -1.0f;
                    //Apply attribute changes to this window
                    getWindow().setAttributes(layoutPars);

                    getWindow().clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                isTorchOn = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.release();
    }

    public boolean hasFlash() {
        if (camera == null) {
            return false;
        }

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }
}
