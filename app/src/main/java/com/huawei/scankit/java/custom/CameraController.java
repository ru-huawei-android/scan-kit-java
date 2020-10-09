package com.huawei.scankit.java.custom;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraController {

    private static String THREAD_NAME = "Thread";
    private static String TAG = "CameraController";

    private final static int MAX_IMAGES = 5;

    private CameraManager cameraManager;
    private SurfaceView cameraPreview;
    private ImageReader imageReader;
    private CameraDeviceCallback cameraDeviceCallback;
    private HandlerThread handlerThread;

    public CameraController(CameraManager cameraManager, SurfaceView cameraPreview){
        this.cameraManager = cameraManager;
        this.cameraPreview = cameraPreview;
    }

    @SuppressLint("MissingPermission")
    public void startCameraPreview(ImageReader.OnImageAvailableListener listener) {
        Log.i(TAG, "Starting Camera Preview");

        handlerThread = new HandlerThread(THREAD_NAME);
        handlerThread.start();

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Handler backgroundHandler = new Handler(handlerThread.getLooper());

        // we want to use the backFacing camera
        String backFacingId = getBackFacingCameraId(cameraManager);
        if (backFacingId == null) {
            Log.e(TAG, "Can not open Camera because no backFacing Camera was found");
            return;
        }

        setupStreamReader(cameraManager, backFacingId, listener, backgroundHandler);

        cameraDeviceCallback = new CameraDeviceCallback(cameraPreview, imageReader, mainHandler);

        //we have a backFacing camera, so we can start the preview
        try {
            cameraManager.openCamera(backFacingId, cameraDeviceCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopCameraPreview() {
        cameraDeviceCallback.closeCameraCaptureSession();
        handlerThread.quitSafely();

        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }


    private String getBackFacingCameraId(CameraManager cameraManager){
        try {
            String[] ids = cameraManager.getCameraIdList();
            for (String id : ids) {
                Log.i(TAG, "Found Camera ID: " + id);
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Integer cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (CameraCharacteristics.LENS_FACING_BACK == cameraDirection) {
                    Log.i(TAG, "Found back facing camera");
                    return id;
                }
            }
            return null;
        }
        catch(CameraAccessException ce){
            ce.printStackTrace();
            return null;
        }
    }

    private void setupStreamReader(
        CameraManager cameraManager,
        String backFacingId,
        ImageReader.OnImageAvailableListener listener,
        Handler handler
    ) {
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(backFacingId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (map != null) {
                Size size = map.getOutputSizes(SurfaceHolder.class)[0];
                int width = size.getWidth();
                int height = size.getHeight();
                cameraPreview.getHolder().setFixedSize(width, height);
                imageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, MAX_IMAGES);
                imageReader.setOnImageAvailableListener(listener, handler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}