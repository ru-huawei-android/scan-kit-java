package com.huawei.scankit.java.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.scankit.java.R;
import com.huawei.scankit.java.custom.BardCodeView;
import com.huawei.scankit.java.custom.ScanUtils;

public class CustomizedViewFragment extends Fragment {

    public static String TAG = "CustomizedViewFragment";

    private BardCodeView bcvResult;
    private TextView tvResult;
    private RemoteView remoteView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customized_view, container, false);

        FrameLayout flContent = view.findViewById(R.id.flCustomizedContent);
        tvResult = view.findViewById(R.id.tvCustomizedResult);

        bcvResult = view.findViewById(R.id.bcvCustomizedResult);

        // Set the scanning area. Set the parameters as required.
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        // Set the width and height of the barcode scanning box to 300 dp.
        final int SCAN_FRAME_SIZE = 300;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = screenWidth / 2 - scanFrameSize / 2;
        rect.right = screenWidth / 2 + scanFrameSize / 2;
        rect.top = screenHeight / 2 - scanFrameSize / 2;
        rect.bottom = screenHeight / 2 + scanFrameSize / 2;

        remoteView = new RemoteView.Builder()
                .setContext(getActivity())
                .setBoundingBox(rect)
                .setFormat(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
                .build();

        // Load the customized view to the activity.
        remoteView.onCreate(savedInstanceState);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );

        flContent.addView(remoteView, 0, params);

        // Subscribe to the recognition result callback event.
        remoteView.setOnResultCallback(this::showResult);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        remoteView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        remoteView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        remoteView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    private void showResult(HmsScan[] result) {

        Rect[] rectangles = new Rect[result.length];
        int position = 0;

        float fragmentTop = getResources().getDimensionPixelOffset(R.dimen.app_bar_height);

        StringBuilder sb = new StringBuilder();
        for (HmsScan hmsScan: result) {
            sb.append(ScanUtils.convertHmsScanToString(hmsScan)).append("\n");

            if (position < result.length - 1) {
                sb.append("\n\n");
            }

            Rect rectangle = hmsScan.getBorderRect();
            rectangle.top += fragmentTop;
            rectangle.bottom += fragmentTop;
            rectangles[position++] = rectangle;
        }

        Log.i(TAG, sb.toString());

        tvResult.setText(sb.toString());
        Linkify.addLinks(tvResult, Linkify.ALL);

        bcvResult.setBorderRectangles(rectangles);
    }
}
