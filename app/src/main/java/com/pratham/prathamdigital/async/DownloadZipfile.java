package com.pratham.prathamdigital.async;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by HP on 02-08-2017.
 */

class DownloadZipfile extends AsyncTask<String, String, String>
{
    String result ="";
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
//        mProgressDialog = new ProgressDialog(MainActivity.this);
//        mProgressDialog.setMessage("Downloading...");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... aurl)
    {
        int count;

        try
        {
            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());

            OutputStream output = new FileOutputStream(/*StorezipFileLocation*/"");

            byte data[] = new byte[1024];
            long total = 0;

            while ((count = input.read(data)) != -1)
            {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }
            output.close();
            input.close();
            result = "true";

        } catch (Exception e) {

            result = "false";
        }
        return null;

    }
    protected void onProgressUpdate(String... progress)
    {
        Log.d("ANDRO_ASYNC",progress[0]);
//        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String unused)
    {
//        mProgressDialog.dismiss();
        if(result.equalsIgnoreCase("true"))
        {
            try
            {
                unzip();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {

        }
    }

    public void unzip() throws IOException
    {
//        mProgressDialog = new ProgressDialog(MainActivity.this);
//        mProgressDialog.setMessage("Please Wait...");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
        new UnZipTask().execute(/*StorezipFileLocation*/"", /*DirectoryName*/"");
    }
}