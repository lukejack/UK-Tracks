package com.jackson.luke.UKTracks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by luke on 06/12/2016.
 */

public class Bitmap64 {
    static String to64(Bitmap image){
        //http://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    static Bitmap toBitmap(String base){
        //http://stackoverflow.com/questions/3801760/android-code-to-convert-base64-string-to-bitmap
        byte[] imageAsBytes = Base64.decode(base.getBytes(),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
