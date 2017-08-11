package com.pratham.prathamdigital.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;

import com.pratham.prathamdigital.custom.progress_indicators.ProgressLayout;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.UnzipUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by User on 16/11/15.
 */
public class ZipDownloader {

    private String url;
    private String filename;
    private String storezipFileLocation;
    private ProgressUpdate progressUpdate;
    private Context context;

    public ZipDownloader(ProgressUpdate progressUpdate, String url, String foldername, String filename) {
        this.url = url;
        PD_Utility.DEBUG_LOG(1, "url:::", url);
        this.filename = filename;
        this.storezipFileLocation = Environment.getExternalStorageDirectory() + "/PrathamContents/" + foldername;
        this.progressUpdate = progressUpdate;
        File testDirectory = new File(storezipFileLocation);
        if (!testDirectory.exists()) {
            testDirectory.mkdir();
        }
        DownloadZipfile mew = new DownloadZipfile();
        mew.execute(url);
    }

    class DownloadZipfile extends AsyncTask<String, String, String> {
        String result = "";

        @Override
        protected String doInBackground(String... aurl) {
            int count, latestPercentDone;
            int percentDone = -1;
            try {
                System.out.println("download");
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.setDoInput(true);
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                progressUpdate.lengthOfTheFile(lenghtOfFile);
                InputStream input = new BufferedInputStream(url.openStream());

                OutputStream output = new FileOutputStream(storezipFileLocation + "/" + filename);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        return null;
                    }
                    output.write(data, 0, count);
                    if (lenghtOfFile > 0) {
                        total += count;
                        latestPercentDone = (int) Math.round(total / lenghtOfFile * 100.0);
                        if (latestPercentDone >= percentDone + 1) {
                            percentDone = latestPercentDone;
                            publishProgress("" + percentDone);
                        }
                    }
//                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                }
                output.close();
                input.close();
                result = "true";
                System.out.println("download done");

            } catch (Exception e) {
                e.printStackTrace();
                result = "false";
            }
            return null;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            progressUpdate.onProgressUpdate(Integer.parseInt(progress[0]));
//            progressLayout.setCurrent6Progress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            if (result.equalsIgnoreCase("true")) {
                try {
                    System.out.println("lucy download unzip");
                    unzip();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
            }
        }
    }

    public void unzip() throws IOException {
        new UnZipTask().execute(storezipFileLocation + "/" + filename, storezipFileLocation);
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean> {
        @SuppressWarnings("rawtypes")
        @Override
        protected Boolean doInBackground(String... params) {
            System.out.println("lucy download UnZipTask");
            String filePath = params[0];
            String destinationPath = params[1];

            File archive = new File(filePath);
            try {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, destinationPath);
                }

                System.out.println("lucy download UnZipTask util");
                UnzipUtil d = new UnzipUtil(storezipFileLocation + "/" + filename, storezipFileLocation);
                d.unzip();
                System.out.println("lucy download UnZipTask util done");

            } catch (Exception e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            try {
                File file = new File(storezipFileLocation + "/" + filename);
                boolean deleted = file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {
            if (entry.isDirectory()) {
                createDir(new File(outputDir, entry.getName()));
                return;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists()) {
                createDir(outputFile.getParentFile());
            }

            // Log.v("", "Extracting: " + entry);
            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

            try {

            } finally {
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        }

        private void createDir(File dir) {
            if (dir.exists()) {
                return;
            }
            if (!dir.mkdirs()) {
                throw new RuntimeException("Can not create dir " + dir);
            }
        }
    }
}





