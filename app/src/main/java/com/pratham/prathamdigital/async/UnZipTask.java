package com.pratham.prathamdigital.async;

import android.os.AsyncTask;

import com.pratham.prathamdigital.util.UnzipUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by HP on 02-08-2017.
 */

class UnZipTask extends AsyncTask<String, Void, Boolean> {
    @SuppressWarnings("rawtypes")
    @Override
    protected Boolean doInBackground(String... params) {
        String filePath = params[0];
        String destinationPath = params[1];

        File archive = new File(filePath);
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, destinationPath);
            }
            UnzipUtil d = new UnzipUtil(/*StorezipFileLocation*/"", /*DirectoryName*/"");
            d.unzip();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
//        mProgressDialog.dismiss();
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


