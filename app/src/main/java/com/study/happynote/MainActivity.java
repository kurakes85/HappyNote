package com.study.happynote;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
                //getPakageInfo 의 예외처리
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }else{

        }
    }
}