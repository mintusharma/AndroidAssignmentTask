package com.mintusharma.webkultask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mintusharma.webkultask.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private EditText editText;
    private AlertDialog dialog;
    private String file_url=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showInfo();
        initView();
    }

    public void showInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.info_dialog, null, false);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        Button btn_ok=view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void initView() {
        Button login_button,track_me_button,download_file_button,create_show_button;
        login_button=findViewById(R.id.facebook_login);
        login_button.setOnClickListener(this);
        track_me_button=findViewById(R.id.track_me_button);
        track_me_button.setOnClickListener(this);
        download_file_button=findViewById(R.id.download_file_button);
        download_file_button.setOnClickListener(this);
        create_show_button=findViewById(R.id.create_show_button);
        create_show_button.setOnClickListener(this);
        TextView github = findViewById(R.id.github);
        github.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.facebook_login:
                startActivity(new Intent(this,LoginActivity.class));
                break;

            case R.id.track_me_button:
                startActivity(new Intent(this,TrackingActivity.class));
                break;

            case R.id.download_file_button:
                //dialogUtils.showDownloadFileDialog(this,new MainActivity());
                showDialogForDownload();

                break;

            case R.id.create_show_button:
                startActivity(new Intent(this,CreateShowActivity.class));
                //showDialogForCreateShow();
                break;

            case R.id.github:
                String url = "https://github.com/mintusharma/AndroidAssignmentTask";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            // checking internet or wifi permission is granted or not ?
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if granted start downloading
                startDownloading();
            } else {
                //if permission denied display message
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // download file code started


    private void showDialogForDownload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.file_download_dialog, null, false);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        Button download_btn=view.findViewById(R.id.download);
        Button cancel_btn=view.findViewById(R.id.cancel);
        editText=view.findViewById(R.id.links);

        TextView clear=view.findViewById(R.id.clear_text);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.getText().toString().isEmpty()){
                    editText.setText("");
                }
            }
        });
        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternet();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void downloadDataFromUrl(String url) {
        file_url=url;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //handling permission denied
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //show popup at runtime to provide permission
                    requestPermissions(permission, PERMISSION_STORAGE_CODE);

                } else {
                    startDownloading();
                }
            } else {
                startDownloading();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startDownloading() {
        boolean isValid = URLUtil.isValidUrl(file_url) && Patterns.WEB_URL.matcher(file_url).matches();
        if(isValid) {
            // if url is valid check internet or wifi permission is enabled
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(file_url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("Downloading...");
            request.setDescription("Downloading file....");

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis());
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
            }
        }else {
            Toast.makeText(this, "URL not valid", Toast.LENGTH_SHORT).show();
        }

    }

    // download file code ended


    private void checkInternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert conMgr != null;
        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                downloadDataFromUrl(editText.getText().toString().trim());
                Toast.makeText(getApplicationContext(),"File downloading..",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                downloadDataFromUrl(editText.getText().toString().trim());
                Toast.makeText(getApplicationContext(),"File downloading..",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }else {
            Toast.makeText(this, "Please Enable You WIFI or Data Network", Toast.LENGTH_SHORT).show();
        }

    }
}
