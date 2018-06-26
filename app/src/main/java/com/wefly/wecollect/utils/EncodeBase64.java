package com.wefly.wecollect.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class EncodeBase64 {

    public String encode(String filepath) throws IOException {
        FileInputStream fileInputStream = null;
        String val = "";
        try {
            File f = new File(filepath);
            byte[] bytesArray = new byte[(int) f.length()];
            fileInputStream = new FileInputStream(f);
            fileInputStream.read(bytesArray);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            val = Base64.encodeToString(bytesArray, Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return val;
    }

}