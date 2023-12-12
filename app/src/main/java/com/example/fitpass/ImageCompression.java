package com.example.fitpass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class ImageCompression {

    public static float maxHeight = 1280.0f;
    public static float maxWidth = 1280.0f;

    public static String compressImage(String image, Context context, boolean isGalleryFile) {
        Bitmap scaledBitmap = null;
        String imagePath = image;
        File file = new File(image);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(image, options);

        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Log.e("compressImage","imageWidth===>> " + imageWidth +" imageHeight==>> " +imageHeight );

        maxHeight = imageHeight;
        maxWidth = imageWidth;

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        try {
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        //    options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        // options.inTempStorage = new byte[12 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(image, options);
        } catch (Exception exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        try {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bmp != null) {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(image);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = imagePath;
        if (isGalleryFile) {
            SimpleDateFormat timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            filepath = context.getFilesDir().getPath() + File.separator + "Img_" + timeStamp.format(System.currentTimeMillis()) + ".png";
        }


        File newFile = new File(filepath);
        if (newFile.exists())
            newFile.delete();

        try {
            MediaScannerConnection.scanFile(context, new String[]{newFile.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
            out = new FileOutputStream(filepath);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(newFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

            MediaScannerConnection.scanFile(context, new String[]{newFile.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


        return filepath;
    }


    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
