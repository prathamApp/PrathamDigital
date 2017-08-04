package com.pratham.prathamdigital.async;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;

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

    private String url = "http://hlearning.openiscool.org/content/games/AwazChitraH.zip";
    private String storezipFileLocation = Environment.getExternalStorageDirectory()+"/PrathamContents/";
    private String directoryName = Environment.getExternalStorageDirectory()+"/unzip/";
    private ProgressBar progressBar;

    public ZipDownloader(ProgressBar progressBar) {
//            this.url = url;
//            this.storezipFileLocation = location;
        this.progressBar = progressBar;
        File testDirectory = new File(storezipFileLocation);
        if(!testDirectory.exists()){
            testDirectory.mkdir();
        }
        File zipDirectory = new File(directoryName);
        if (!zipDirectory.exists()) {
            zipDirectory.mkdir();
        }
        DownloadZipfile mew = new DownloadZipfile();
        mew.execute(url);
    }

    class DownloadZipfile extends AsyncTask<String, String, String> {
        String result = "";

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                System.out.println("lucy download");
                URL url = new URL(aurl[0]);
                System.out.println("lucy download1");
                URLConnection conexion = url.openConnection();
                System.out.println("lucy download2");
                conexion.setDoInput(true);
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                System.out.println("lucy download3");
                InputStream input = new BufferedInputStream(url.openStream());

                OutputStream output = new FileOutputStream(storezipFileLocation+"/file1.zip");
                System.out.println("lucy download4");
                byte data[] = new byte[1024];
                long total = 0;
                System.out.println("lucy download5");
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                System.out.println("lucy download6");
                output.close();
                input.close();
                result = "true";
                System.out.println("lucy download done");

            } catch (Exception e) {
                e.printStackTrace();
                result = "false";
            }
            return null;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            progressBar.setProgress(Integer.parseInt(progress[0]));
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
        new UnZipTask().execute(storezipFileLocation+"/file1.zip", directoryName);
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
                UnzipUtil d = new UnzipUtil(storezipFileLocation+"/file1.zip", directoryName);
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
                File file=new File(storezipFileLocation+"/file1.zip");
                boolean deleted=file.delete();
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





