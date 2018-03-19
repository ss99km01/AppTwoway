package com.jefeko.apptwoway.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jefeko.apptwoway.R;

import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class CommonUtil {
    public static final String PREFERENCE_APP_NAME = "apptwoway";
    public static final String LOG_TAG = "apptwoway";
    public static boolean IS_DEBUG_LOG = true;
    public static final int RESULT_CODE_PUSH = 1400;




    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int STATUS = 2;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                STATUS = 0;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                STATUS = 1;
            }
        } else {
            STATUS = 2;
        }
        return STATUS;
    }

    public static void popupNetwork(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("네트워크 오류")
                .setMessage("네트워크 상태를 확인해주세요. 앱이 종료됩니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (context instanceof Activity){
                            ((Activity) context).finish();
                        }
                    }
                }).show();
    }

    /**
     * 내장 스토리지 path를 구해오는 함수
     * @return 내장스토리지 path
     */
    public static String getInternalStoragePath() {
        String storagePath = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            storagePath = Environment.getExternalStorageDirectory().getPath();
        }

        return storagePath;
    }

    /**
     * 폴더를 생성함.
     * @param path 폴더 경로
     * @return true이면 정상적으로 처리됨
     */
    public static boolean makeDirs(String path) {

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            return false;

        File dir = new File(path);
        if (dir.exists() == false) {
            try {
                if(!dir.mkdirs()) {
                    //Log.e(TAG, "mkdirs Failed : " + dir.getAbsolutePath()); // mount/unmounnt 시 stack overflow 발생하여 제거
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public static String getCurrencyString(String currency) {
        return NumberFormat.getCurrencyInstance(Locale.KOREA).format(Integer.valueOf(currency));
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getPathFromURI(Context context, Uri imgUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imgUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    public static void showAlertDialog(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert_title))
                .setMessage(msg)
                .setPositiveButton(context.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showAlertDialog(Context context, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert_title))
                .setMessage(msg)
                .setPositiveButton(context.getResources().getString(R.string.btn_ok), listener)
                .show();
    }

    public static void showAlertDialog(Context context, String msg, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert_title))
                .setMessage(msg)
                .setPositiveButton(context.getResources().getString(R.string.btn_ok), positive)
                .setNegativeButton(context.getResources().getString(R.string.btn_cancel),  negative)
                .show();
    }

    public static void showAlertDialog(Context context, String msg, String p, String n, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert_title))
                .setMessage(msg)
                .setPositiveButton(p, positive)
                .setNegativeButton(n,  negative)
                .show();
    }

    public static void hideKeyboard(Context ctx, EditText edt) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    /**
     * 날짜를 해당 포멧의 문자열로 반환함(YYYY-MM-DD)
     * @return String 문자열 날짜정보
     */
    public static String getCurrentYYYYMMDD() {
        Calendar cal = Calendar.getInstance();
        return String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
    }
}
