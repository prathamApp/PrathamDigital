package com.pratham.prathamdigital.dbclasses;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

// Code to Reflect data immidiately in Database

public class BackupDatabase {

    public static void backup(Context c) {
        try {

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            //if (sd.canWrite()) {
                String currentDBPath = "//data//com.pratham.prathamdigital//databases//"+"PraDiGi.db";
                String backupDBPath = "PraDiGi.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                else {
                }

            //}
        } catch (Exception e) {
            Toast.makeText( c ,e.getMessage(), Toast.LENGTH_LONG).show();
            throw new Error("Copying Failed");
        }
    }

}
