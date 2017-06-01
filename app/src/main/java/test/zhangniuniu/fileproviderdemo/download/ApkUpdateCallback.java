package test.zhangniuniu.fileproviderdemo.download;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import test.zhangniuniu.fileproviderdemo.R;

public class ApkUpdateCallback implements DownloadCallback {
    private LayoutInflater mInflater;
    private ProgressBar mProgressView;
    private TextView mProgressTx;
    private AlertDialog mDownloadDialog;    //下载弹出框
    private Context mContext;
    public static final String APK_FILE_NAME = "hjieqian.apk";

    public ApkUpdateCallback(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onDownloadPreare() {
//    	if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
//    		Toast.makeText(mContext, "请检测SD卡状态", Toast.LENGTH_LONG).show();
//    		return;
//    	}

        Builder builder = new Builder(mContext);
//        builder.setTitle("正在更新版本");
        //---------------------------- 设置在对话框中显示进度条 --------------------
        View view = View.inflate(mContext, R.layout.app_update_dialog, null);
        mProgressView = (ProgressBar) view.findViewById(R.id.progressbar);
        mProgressTx = (TextView) view.findViewById(R.id.updateProgress);
        builder.setView(view);

        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        mDownloadDialog.setCancelable(false);
    }

    @Override
    public void onCompleted(boolean success, String errorMsg) {
//		mDownloadDialog.dismiss();
        if (!success)
            Toast.makeText(mContext, "下载失败", Toast.LENGTH_LONG).show();
        else {//安装
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String downloadFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + DownloadTask.DOWNLOAD_FILE_PATH + APK_FILE_NAME;

                File apkFile = new File(downloadFilePath);
                Uri apkUri = FileProvider.getUriForFile(mContext, "com.zhangniuniu.fileprovider", apkFile);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(new File(downloadFilePath)), "application/vnd.android.package-archive");
                }
                mContext.startActivity(intent);
//				android.os.Process.killProcess(android.os.Process.myPid());
            } else
                Toast.makeText(mContext, "请检测SD卡状态", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onChangeProgress(int progress) {
        mProgressView.setProgress(progress);
        mProgressTx.setText(progress + "%");
    }

    @Override
    public boolean onCancel() {
        return false;
    }
}
