package com.study.happynote;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    FirebaseRemoteConfig remoteConfig;
    long newAppVersion = 0;
    long toolbarImgCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //remote config
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                //setMinimumFetchIntervalInSeconds(3600) <-- 디버깅 할때만 쓰라는데?
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {

                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        checkVersion(task.isSuccessful());
                        if (task.isSuccessful()) {
                            // 이전에 썻던 내용은 출력 되었으니 과감하게 버린다 난 용자다.
                            Log.e("new_app_version"," = " + remoteConfig.getLong("new_app_version"));
                            Log.e("toolbar_img_count"," = " + remoteConfig.getLong("toolbar_img_count"));
                    }
                    }
                });
    }
    private void checkVersion(boolean successful) {
        if (successful){
            //전역 변수로 저장
            newAppVersion = remoteConfig.getLong("new_app_version");
            toolbarImgCount = remoteConfig.getLong("toolbar_img_count");

            //내 앱의 버전을 아래로 갖고 오자
            try {
                PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                long appVersion;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    appVersion = pi.getLongVersionCode();
                }else{
                    appVersion = pi.versionCode;
                }
            // 버전 업데이트
                if (newAppVersion > appVersion){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("업데이트 알림.");
                    builder.setMessage("최신버전이 등록 되었습니다 \n 업데이트를 하세요!")
                            .setCancelable(false)
                            .setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //플레이스토어로 이동하는 부분 , 나중에 업데이트 할때 주석을 풀자!
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setData(Uri.parse("market://details?id=com.study.happynote"));
//                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "업데이트 버튼 클릭 됨",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
                    //이제 알람을 띄워줘야지!
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }else{

        }
    }
}