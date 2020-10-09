package com.huawei.scankit.java;

public class Config {
    public static int REQUEST_CODE_SCAN_ONE = 10;

    public static int POSITION_DEFAULT_VIEW = 0;

    public static String DOUBLE_LINE_TRANSLATION = "\n\n";

    public static Boolean isVersionP() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P;
    }

}
