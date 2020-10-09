package com.huawei.scankit.java.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.scankit.java.R;
import com.huawei.scankit.java.Config;
import com.huawei.scankit.java.custom.ScanUtils;

public class DefaultViewFragment extends Fragment {

    public static String TAG = "DefaultViewFragment";

    private TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_view, container, false);

        view.findViewById(R.id.btnDefaultStartScan).setOnClickListener(button -> {

            // Set scanning parameters (Optional)
            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE , HmsScan.DATAMATRIX_SCAN_TYPE)
                .create();

            ScanUtil.startScan(getActivity(), Config.REQUEST_CODE_SCAN_ONE, options);
        });

        tvResult = view.findViewById(R.id.tvDefaultResult);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        if (requestCode == Config.REQUEST_CODE_SCAN_ONE) {
            HmsScan hmsScan = data.getParcelableExtra(ScanUtil.RESULT);
            if (hmsScan != null) {
                String text = ScanUtils.convertHmsScanToString(hmsScan);
                Log.i(TAG, text);
                tvResult.setText(text);
            }
        }
    }
}
