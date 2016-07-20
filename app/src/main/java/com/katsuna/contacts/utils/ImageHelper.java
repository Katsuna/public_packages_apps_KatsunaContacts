package com.katsuna.contacts.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageHelper {

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
}
