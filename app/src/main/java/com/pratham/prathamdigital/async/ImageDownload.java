package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by HP on 12-08-2017.
 */

public class ImageDownload extends AsyncTask<String, String, String> {
    String result = "";
    public String filename = "";
    private final File mydir;
    private Context context;
    private String filepath;
    private File fileWithinMyDir;

    public ImageDownload(Context context, String filename) {
        this.filename = filename;
        this.context = context;
        Log.d("image_filename", filename);
        mydir = context.getDir("PrathamImages", Context.MODE_PRIVATE); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;
        try {
            URL url = new URL(aurl[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            fileWithinMyDir = new File(mydir, filename); //Getting a file within the dir.
            // Output stream to write file
            OutputStream output = new FileOutputStream(fileWithinMyDir);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        Log.d("ANDRO_ASYNC", progress[0]);
//            progressLayout.setCurrent6Progress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String unused) {
        if (result != null) {
            try {
                Log.i("filepath:", " image downloaded");
                Log.i("filepath:", " " + fileWithinMyDir.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("filepath:", " image not downloaded");
        }
    }
}
