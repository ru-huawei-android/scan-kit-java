package com.huawei.scankit.java.custom;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Size;

import com.huawei.hms.ml.scan.HmsScan;

public class ScanUtils {

    private final static int RIGHT_ANGLE = 90;

    public static String convertHmsScanToString(HmsScan hmsScan) {
        StringBuilder sb = new StringBuilder();
        sb.append("Scan Type: ").append(convertScanTypeToString(hmsScan.getScanType()));
        sb.append("\nValue: ").append(hmsScan.getOriginalValue());
        if (hmsScan.getBookMarkInfo() != null) {
            sb.append("\nBook place (info): ").append(hmsScan.getBookMarkInfo().bookPlaceInfo);
            sb.append("\nBook place (uri): ").append(hmsScan.getBookMarkInfo().bookUri);
            sb.append("\nBook place (num): ").append(hmsScan.getBookMarkInfo().bookNum);
        }
        if (hmsScan.getContactDetail() != null) {
            sb.append("\nContact detail (title): ").append(hmsScan.getContactDetail().title);
            sb.append("\nContact detail (company): ").append(hmsScan.getContactDetail().company);
            sb.append("\nContact detail (note): ").append(hmsScan.getContactDetail().note);
        }
        if (hmsScan.getDriverInfo() != null) {
            sb.append("\nDriver info (city): ").append(hmsScan.getDriverInfo().city);
        }
        if (hmsScan.getEmailContent() != null) {
            sb.append("\nEmail content (address info): ")
                    .append(hmsScan.getEmailContent().addressInfo);
            sb.append("\nEmail content (body info): ")
                    .append(hmsScan.getEmailContent().bodyInfo);
        }
        if (hmsScan.getEventInfo() != null) {
            sb.append("\nEvent info (theme): ").append(hmsScan.getEventInfo().theme);
            sb.append("\nEvent info (abstract info): ")
                    .append(hmsScan.getEventInfo().abstractInfo);
        }
        if (hmsScan.getLocationCoordinate() != null) {
            sb.append("\nLocation coordinate (latitude): ")
                    .append(hmsScan.getLocationCoordinate().latitude);
            sb.append("\nLocation coordinate (longitude): ")
                    .append(hmsScan.getLocationCoordinate().longitude);
        }
        if (hmsScan.getWiFiConnectionInfo() != null) {
            sb.append("\nWiFi connection info (ssidNumber): ")
                    .append(hmsScan.getWiFiConnectionInfo().ssidNumber);
            sb.append("\nWiFi connection info (cipherMode): ")
                    .append(hmsScan.getWiFiConnectionInfo().cipherMode);
            sb.append("\nWiFi connection info (password): ")
                    .append(hmsScan.getWiFiConnectionInfo().password);
        }
        if (hmsScan.getVehicleInfo() != null) {
            sb.append("\nVehicle info (countryCode): ").append(hmsScan.getVehicleInfo().countryCode);
        }
        if (hmsScan.getBorderRect() != null) {
            sb.append("\nBorder Rect (left): ").append(hmsScan.getBorderRect().left);
            sb.append("\nBorder Rect (top): ").append(hmsScan.getBorderRect().top);
            sb.append("\nBorder Rect (right): ").append(hmsScan.getBorderRect().right);
            sb.append("\nBorder Rect (bottom): ").append(hmsScan.getBorderRect().bottom);
        }
        sb.append("\nZoom Value: ").append(hmsScan.zoomValue);
        return sb.toString();
    }

    public static Rect convertCameraRect(Rect rect, Size bitmapSize, Size scanViewSize) {
        RectF rectF = new RectF(rect);
        Matrix mat = new Matrix();
        mat.setRotate(RIGHT_ANGLE, bitmapSize.getWidth() / 2f, bitmapSize.getHeight() / 2f);
        mat.mapRect(rectF);

        rectF.top = rectF.top * scanViewSize.getHeight() / bitmapSize.getHeight();
        rectF.bottom = rectF.bottom * scanViewSize.getHeight() / bitmapSize.getHeight();
        rectF.left = rectF.left * scanViewSize.getWidth() / bitmapSize.getWidth();
        rectF.right = rectF.right * scanViewSize.getWidth() / bitmapSize.getWidth();

        Rect newRect = new Rect();
        rectF.round(newRect);

        return newRect;
    }

    private static String convertScanTypeToString(int type) {
        switch (type) {
            case 0: return "QRCODE_SCAN_TYPE";
            case 1: return "AZTEC_SCAN_TYPE";
            case 2: return "DATAMATRIX_SCAN_TYPE";
            case 3: return "PDF417_SCAN_TYPE";
            case 4: return "CODE39_SCAN_TYPE";
            case 5: return "CODE93_SCAN_TYPE";
            case 6: return "CODE128_SCAN_TYPE";
            case 7: return "EAN13_SCAN_TYPE";
            case 8: return "EAN8_SCAN_TYPE";
            case 9: return "ITF14_SCAN_TYPE";
            case 10: return "UPCCODE_A_SCAN_TYPE";
            case 11: return "UPCCODE_E_SCAN_TYPE";
            case 12: return "CODABAR_SCAN_TYPE";
            default: return "UNKNOWN";
        }
    }
}
