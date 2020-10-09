package com.huawei.scankit.java.custom;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.huawei.scankit.java.Config;

import java.util.Vector;
import java.util.concurrent.Executors;

public class CameraDeviceCallback extends CameraDevice.StateCallback {

    private static String TAG = "CameraDeviceCallback";

    private SurfaceView cameraPreview;
    private ImageReader imageReader;
    private Handler handler;
    private CameraCaptureSession cameraSession;
    private CameraDevice cameraDevice;

    public CameraDeviceCallback(SurfaceView cameraPreview, ImageReader imageReader, Handler handler) {
        this.cameraPreview = cameraPreview;
        this.imageReader = imageReader;
        this.handler = handler;
    }

    public void closeCameraCaptureSession() {
        if (cameraSession != null) {
            try {
                cameraSession.stopRepeating();
            } catch (CameraAccessException e) {
                Log.e(TAG, String.format("%s: %s", "onPause", e.getMessage()));
            }

            if (cameraDevice != null) {
                cameraDevice.close();
            }

            cameraSession.close();
            cameraSession = null;
        }
    }

    @Override
    public void onOpened(@NonNull final CameraDevice cameraDevice) {
        Log.i(TAG, "CameraDevice.StateCallback onOpened()");
        this.cameraDevice = cameraDevice;

        final CameraCaptureSession.StateCallback csc = new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                Log.i(TAG, "CameraCaptureSession.StateCallback onConfigured()");
                cameraSession = cameraCaptureSession;
                try {
                    CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    builder.addTarget(cameraPreview.getHolder().getSurface());
                    builder.addTarget(imageReader.getSurface());
                    cameraSession.setRepeatingRequest(builder.build(), null, null);
                } catch (CameraAccessException e) {
                    Log.e(TAG, "CameraCaptureSession.StateCallback Error", e);
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                Log.e(TAG, "CameraCaptureSession.StateCallback onConfigureFailed()");
            }
        };

        Surface previewSurface = cameraPreview.getHolder().getSurface();
        Surface imageSurface = imageReader.getSurface();

        try {
            if (Config.isVersionP()) {
                Vector<OutputConfiguration> outputConfigurations = new Vector<>();
                outputConfigurations.add(new OutputConfiguration(previewSurface));
                outputConfigurations.add(new OutputConfiguration(imageSurface));
                SessionConfiguration sessionConfiguration = new SessionConfiguration(
                        SessionConfiguration.SESSION_REGULAR, outputConfigurations,
                        Executors.newSingleThreadExecutor(), csc
                );
                cameraDevice.createCaptureSession(sessionConfiguration);
            } else {
                Vector<Surface> v = new Vector<>();
                v.add(previewSurface);
                v.add(imageSurface);
                cameraDevice.createCaptureSession(v, csc, handler);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "CameraDevice.StateCallback Error", e);
        }
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice cameraDevice) {
        Log.i(TAG, "CameraDevice.StateCallback onDisconnected()");
        cameraDevice.close();
    }

    @Override
    public void onError(@NonNull CameraDevice cameraDevice, int i) {
        Log.i(TAG, "CameraDevice.StateCallback onError()");
    }
}