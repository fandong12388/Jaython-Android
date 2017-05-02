package com.jaython.cc.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * time: 2016/3/17
 * description: Thank for boredream ,
 * I made a Util before, but it is not good .
 * Yesterday I find some classes from boredream.
 * So I pick some useful to make this class.
 * <p>
 * PS:为了避免三星手机屏幕旋转造成的bug，需在 Manifest中添加
 * android:configChanges="orientation|screenSize|keyboardHidden"
 *
 * @author bigflower
 */
public class PictureUtil {
    public static final String CROP_CACHE_FOLDER = "Jaython/photoCache";
    public static final String REQUEST_DATA = "data";

    public static final String TAG = PictureUtil.class.getSimpleName();

    // 4 modes
    public static final int REQUEST_CODE_FROM_CAMERA = 901;
    public static final int REQUEST_CODE_FROM_ALBUM = 902;
    public static final int REQUEST_CODE_FROM_CROP = 903;
    public static final int REQUEST_CODE_ALBUM_CROP = 904;

    public static Uri imgUri = null;

    /**
     * creat a uri for takePhoto
     * <p>
     * I don't know the name , what's the meaning of "GetWorld"?
     *
     * @param context
     * @return
     */
    public static Uri createImageUri(Context context) {
        // 根据机型来判断，这样真的好吗？
        if ("Lenovo".equals(Build.MODEL.split(" ")[0])) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            String path = Environment.getExternalStorageDirectory()
                    + "/zjb/photoCache" + System.currentTimeMillis() + ".jpg";
            imgUri = Uri.parse("file:///" + path);
        } else {
            String name = "GetWorld" + System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, name);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            imgUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        return imgUri;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
