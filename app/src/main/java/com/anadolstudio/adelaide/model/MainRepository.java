package com.anadolstudio.adelaide.model;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.anadolstudio.adelaide.interfaces.MainContract;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;

public class MainRepository implements MainContract.Repository {
    public static final String TAG = MainRepository.class.getName();
    public static final int ONE_PORTION = 99;
    public static final long NULL = -1;
    private final ArrayList<String> legalFormat = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png"));

    @Override
    public Observable<ArrayList<ImageData>> getAllImagesDataObservable(AppCompatActivity activity, long lastItemId, String folder) {
        //Вернуть все
        return Observable.create(emitter -> {
            try {
                Uri uri;
                ArrayList<ImageData> listOfAllImages = new ArrayList<>();
                Cursor cursor;

                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                Log.d(TAG, "getAllImagesDataObservable: uri " + uri);

                String[] projection = {
                        MediaStore.MediaColumns._ID,
                        MediaStore.MediaColumns.MIME_TYPE,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
                String selection = null;
                ArrayList<String> selectionArg = null;

                //TODO Можно написать более красиво?
                if (folder != null) {
                    selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?";
                    selectionArg = new ArrayList<>();
                    selectionArg.add(folder);
                }
                if (lastItemId > NULL) {
                    if (selection != null) {
                        selection = selection.concat(" and " + MediaStore.MediaColumns._ID + " < ?");
                    } else {
                        selection = MediaStore.MediaColumns._ID + " < ?";
                    }
                    if (selectionArg == null) {
                        selectionArg = new ArrayList<>();
                    }
                    selectionArg.add(Long.toString(lastItemId));
                }

                String[] selectionArgArray = null;
                if (selectionArg != null) {
                    selectionArgArray = new String[selectionArg.size()];
                    selectionArgArray = selectionArg.toArray(selectionArgArray);
                }

                cursor = activity.getContentResolver().query(uri, projection,
                        selection,
                        selectionArgArray,
                        MediaStore.MediaColumns._ID + " DESC");

                if (cursor != null) {
                    int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                    int indexMT = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);

                    int count = 0;

                    while (cursor.moveToNext() && count < ONE_PORTION) {

                        String pathOfImage = Uri.withAppendedPath(uri, cursor.getString(columnIndexData)).toString();

                        String format = cursor.getString(indexMT).split("/")[1];//image/jpeg
                        if (!legalFormat.contains(format)) continue;

                        Log.d(TAG, "pathOfImage: " + pathOfImage);
                        listOfAllImages.add(new ImageData(pathOfImage));
                        count++;
                    }
                    cursor.close();

                    emitter.onNext(listOfAllImages);
                    emitter.onComplete();
                }
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        });
    }

    public Observable<Set<String>> getAllImagesFoldersObservable(AppCompatActivity activity) {
        return Observable.create(emitter -> {
            try {
                Uri uri;
                Set<String> listOfFolders = new HashSet<>();
                Cursor cursor;

                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Log.d(TAG, "getAllImagesDataObservable: uri " + uri);

                String[] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

                cursor = activity.getContentResolver().query(uri, projection,
                        null,
                        null,
                        MediaStore.MediaColumns._ID + " DESC");

                if (cursor != null) {
                    int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                    while (cursor.moveToNext()) {
                        String folder = Uri.withAppendedPath(uri, cursor.getString(columnIndexData)).toString();
                        listOfFolders.add(new File((Uri.parse(folder)).getPath()).getName());
                    }
                    cursor.close();

                    emitter.onNext(listOfFolders);
                    emitter.onComplete();
                }
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        });
    }
}
