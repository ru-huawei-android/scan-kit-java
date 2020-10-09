package com.huawei.scankit.java.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzer;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.scankit.java.Config;
import com.huawei.scankit.java.R;
import com.huawei.scankit.java.custom.BardCodeView;
import com.huawei.scankit.java.custom.CameraController;
import com.huawei.scankit.java.custom.ScanUtils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class BitmapFragment extends Fragment implements SurfaceHolder.Callback {

    public static String TAG = "BitmapFragment";

    private BardCodeView bcvResult;
    private TextView tvResult;
    private RadioGroup rgFunction;

    private CameraController cameraController;

    private Size scanViewSize;

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_bitmap, container, false);

        tvResult = view.findViewById(R.id.tvBitmapResult);
        SurfaceView surfaceView = view.findViewById(R.id.svBitmapResult);
        bcvResult = view.findViewById(R.id.bcvBitmapResult);

        rgFunction = view.findViewById(R.id.rgBitmapFunctions);
        rgFunction.setOnCheckedChangeListener((radioGroup, id) -> {
            bcvResult.clear();
            tvResult.setText("");
        });

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        Activity activity = Objects.requireNonNull(getActivity());
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        cameraController = new CameraController(cameraManager, surfaceView);

        bcvResult.post(() -> {
            int width = bcvResult.getMeasuredWidth();
            int height = bcvResult.getMeasuredHeight();
            scanViewSize = new Size(width, height);
        });

        return view;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated()");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i(TAG, "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceDestroyed()");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cameraController != null) {
            cameraController.startCameraPreview(onImageAvailableListener);
        } else{
            Log.e(TAG, "CameraController not available, can not start Camera Preview");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cameraController.stopCameraPreview();
    }

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = reader -> {
        try (Image image = reader.acquireLatestImage()) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);

            YuvImage yuv = new YuvImage(bytes, ImageFormat.NV21, image.getWidth(),
                    image.getHeight(), null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
            yuv.compressToJpeg(rect, 100, stream);

            int length = stream.toByteArray().length;
            Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, length);

            switch (rgFunction.getCheckedRadioButtonId()) {
                case R.id.rbFunctionBitmap:
                    scanBitmap(bitmap);
                    break;
                case R.id.rbFunctionMultiProcessor:
                    scanMultiprocessor(bitmap);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }
    };


    private void scanBitmap(Bitmap bitmap) {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
                .setPhotoMode(false)
                .create();

        HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(getActivity(), bitmap, options);
        // Process the decoding result when the scanning is successful.
        if (hmsScans != null && hmsScans.length > 0 && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
            Size size = new Size(bitmap.getWidth(), bitmap.getHeight());
            // Display the scanning result.
            showResult(hmsScans, size);
        }
    }


    private void scanMultiprocessor(Bitmap bitmap) {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
                .create();

        HmsScanAnalyzer barcodeDetector = new HmsScanAnalyzer(options);
        MLFrame image = MLFrame.fromBitmap(bitmap);

        SparseArray<HmsScan> result = barcodeDetector.analyseFrame(image);
        // Process the decoding result when the scanning is successful.
        if (result != null && result.size() > 0) {
            Size size = new Size(bitmap.getWidth(), bitmap.getHeight());

            HmsScan[] hmsScans = new HmsScan[result.size()];
            for (int i = 0; i < result.size(); i++) {
                int key = result.keyAt(i);
                hmsScans[i] = result.get(key);
            }

            // Display the scanning result.
            showResult(hmsScans, size);
        }
    }

    private void showResult(HmsScan[] result, Size bitmapSize) {
        // Obtain the scanning result object HmsScan.

        Rect[] rectangles = new Rect[result.length];
        int position = 0;

        StringBuilder sb = new StringBuilder();
        for (HmsScan hmsScan: result) {

            sb.append(ScanUtils.convertHmsScanToString(hmsScan));

            if (position < result.length - 1) {
                sb.append(Config.DOUBLE_LINE_TRANSLATION);
            }

            Rect borderRect = ScanUtils.convertCameraRect(
                hmsScan.getBorderRect(), bitmapSize, scanViewSize
            );

            rectangles[position++] = borderRect;
        }

        Log.i(TAG, sb.toString());

        tvResult.setText(sb.toString());
        Linkify.addLinks(tvResult, Linkify.ALL);

        bcvResult.setBorderRectangles(rectangles);
    }
}
