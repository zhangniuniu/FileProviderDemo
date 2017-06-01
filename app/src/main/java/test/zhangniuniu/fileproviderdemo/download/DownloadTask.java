package test.zhangniuniu.fileproviderdemo.download;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件下载
 *
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {
	private DownloadCallback mDownloadCallback;
	private Context mContext;
	private WakeLock mWakeLock;
	private static final String SUCCESS ="success";
	private static final String FAIL ="fail";
	public static final String DOWNLOAD_FILE_PATH = File.separator+"providerdemo"+ File.separator;
	
	public DownloadTask(Context context, DownloadCallback downloadCallback) {
		mContext = context.getApplicationContext();
		this.mDownloadCallback = downloadCallback;
	}
	
	@Override
	protected void onPreExecute() {
		// take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
             getClass().getName());
        mWakeLock.acquire();
        
		mDownloadCallback.onDownloadPreare();
		
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
			return FAIL;
			
		InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(arg0[0]);
            if(!checkURL(arg0[0])) 
               return FAIL;
            
            connection = (HttpURLConnection) url.openConnection();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(getDownloadFile() + arg0[1]);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                	closeRes(input, output, connection);
                    return FAIL;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            return SUCCESS;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            closeRes(input, output, connection);
        }
        return FAIL;
	}

	private String getDownloadFile(){
		String cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + DOWNLOAD_FILE_PATH;
		File cacheFile = new File(cacheDir);
		if(!cacheFile.exists()){
			cacheFile.mkdirs();
		}
		return cacheDir;
	}
	
	private void closeRes(InputStream input, OutputStream output, HttpURLConnection connection) {
		try {
		    if (output != null)
		        output.close();
		    
		    if (input != null)
		        input.close();
		    
		    if (connection != null)
		    	connection.disconnect();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

    /**
     * 检查地址是否正确
     * @param url
     * @return
     */
	private boolean checkURL(String url) {
		return !TextUtils.isEmpty(url) && url.startsWith("http");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		mDownloadCallback.onChangeProgress(values[0]);
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onCancelled() {
		if(mWakeLock != null){
			mWakeLock.release();
			mWakeLock = null;
		}
		
		mDownloadCallback.onCompleted(false, "");
		super.onCancelled();
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(mWakeLock != null){
			mWakeLock.release();
			mWakeLock = null;
		}
		
		mDownloadCallback.onCompleted(SUCCESS.equals(result), result);
		super.onPostExecute(result);
	}
}
