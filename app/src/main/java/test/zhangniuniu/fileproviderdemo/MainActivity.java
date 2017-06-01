package test.zhangniuniu.fileproviderdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import test.zhangniuniu.fileproviderdemo.download.ApkUpdateCallback;
import test.zhangniuniu.fileproviderdemo.download.DownloadTask;

public class MainActivity extends AppCompatActivity {

    private String downLoadUrl = "https://pro-app-qn.fir.im/92baa810493c135105c497b125f03c0f5e660efe.apk?attname=app-haijieqian-debug.apk_1.0.7.apk&e=1496286473&token=LOvmia8oXF4xnLh0IdH05XMYpH6ENHNpARlmPc-T:Xd2jov4hArj7sKY3hQDTtOrZzwM=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadTask(MainActivity.this,
                        new ApkUpdateCallback(MainActivity.this))
                        .execute(downLoadUrl, ApkUpdateCallback.APK_FILE_NAME);
            }
        });

    }

}
