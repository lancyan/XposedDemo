package com.wrbug.xposeddemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.richinfo.dualsim.TelephonyManagement;

public class MainActivity extends AppCompatActivity {
    TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9, textView10,textView11,textView12,textView13;
    Button btnOK,btnReset;
    private static PermissionListener mListener;
    private static Activity activity;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_PHONE_STATE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);


        textView1 = (TextView) findViewById(R.id.textview1);
        textView2 = (TextView) findViewById(R.id.textview2);
        textView3 = (TextView) findViewById(R.id.textview3);
        textView4 = (TextView) findViewById(R.id.textview4);
        textView5 = (TextView) findViewById(R.id.textview5);
        textView6 = (TextView) findViewById(R.id.textview6);
        textView7 = (TextView) findViewById(R.id.textview7);
        textView8 = (TextView) findViewById(R.id.textview8);
        textView9 = (TextView) findViewById(R.id.textview9);
        textView10 = (TextView) findViewById(R.id.textview10);
        textView11 = (TextView) findViewById(R.id.textview11);
        textView12 = (TextView) findViewById(R.id.textview12);
        textView13 = (TextView) findViewById(R.id.textview13);
        btnOK = (Button) findViewById(R.id.btnOK);
        btnReset = (Button) findViewById(R.id.btnReset);
        int idx = ProviderHelper.getIndex(this);
        textView13.setText("Index : " + idx);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValue();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProviderHelper.update(getBaseContext(), 0);
                int idx = ProviderHelper.getIndex(getBaseContext());
                textView13.setText("Index : " + idx);
            }
        });

    }


    private void setValue() {
        try {

            int idx = ProviderHelper.getIndex(this);
            idx++;
            ProviderHelper.update(this, idx);
            int idx2 = ProviderHelper.getIndex(this);
            textView13.setText("Index : " + idx2);
            if (Common.checkNet(this)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    String ip1 = Common.getWifiAddress(this);
                    if (!TextUtils.isEmpty(ip1)) {
                        textView1.setText("IP : " + ip1);
                    }
                } else if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String ip2 = Common.getLocalIpV4Address();
                    if (!TextUtils.isEmpty(ip2)) {
                        textView1.setText("IP : " + ip2);
                    }
                }
            }


            if (Build.VERSION.SDK_INT >= 23) {//判断当前系统是不是Android6.0
                requestRuntimePermissions(PERMISSIONS_STORAGE, new PermissionListener() {
                    @Override
                    public void granted() {
                        //权限申请通过
                        Toast.makeText(activity, "申请权限成功", Toast.LENGTH_SHORT).show();
                        textView2.setText("IMEI : " + Common.getIMEI(activity, 0));

                        textView3.setText("IMEI : " + Common.getIMEI(activity, 1));

                        textView4.setText("IMSI : " + Common.getIMSI(activity));

                        textView5.setText("phone : " + Common.getPhone(activity));
                    }

                    @Override
                    public void denied(List<String> deniedList) {
                        //权限申请未通过
                        for (String denied : deniedList) {
                            if (denied.equals("android.permission.ACCESS_FINE_LOCATION")) {
                                Toast.makeText(activity, "请检查是否打开定位权限！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "没有文件读写权限,请检查是否打开！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }


            textView6.setText("MAC : " + Common.getMac(this));
            textView7.setText("AndroidId : " + Common.getAndroid(this));
            textView8.setText("Width : " + Common.getWidth(this));
            textView9.setText("Height : " + Common.getHeight(this));
            textView10.setText("Model : " + SystemPropertiesProxy.get(this, "ro.product.brand"));
            textView11.setText("Brand : " + SystemPropertiesProxy.get(this, "ro.product.model"));
            textView12.setText("version : " + SystemPropertiesProxy.get(this, "ro.build.version.release"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 申请权限
     */
    public static void requestRuntimePermissions(String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        // 遍历每一个申请的权限，把没有通过的权限放在集合中
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            } else {
                mListener.granted();
            }
        }
        // 申请权限
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), 1);
        }
    }

    /**
     * 申请后的处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            List<String> deniedList = new ArrayList<>();
            // 遍历所有申请的权限，把被拒绝的权限放入集合
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    mListener.granted();
                } else {
                    deniedList.add(permissions[i]);
                }
            }
            if (!deniedList.isEmpty()) {
                mListener.denied(deniedList);
            }
        }
    }
}
