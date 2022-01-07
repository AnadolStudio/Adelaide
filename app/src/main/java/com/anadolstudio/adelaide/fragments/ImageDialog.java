package com.anadolstudio.adelaide.fragments;

import static com.anadolstudio.adelaide.helpers.BitmapHelper.getDegree;
import static com.anadolstudio.adelaide.helpers.BitmapHelper.rotate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.exifinterface.media.ExifInterface;

import com.anadolstudio.adelaide.R;
import com.anadolstudio.adelaide.databinding.DialogImageBinding;

import java.io.IOException;

public class ImageDialog extends AppCompatDialogFragment {
    public static final String TAG = ImageDialog.class.getName();
    private static final String PHOTO_FILE = "photo_file";
    private DialogImageBinding binding;
    private String path;

    public static ImageDialog newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PHOTO_FILE, path);
        ImageDialog fragment = new ImageDialog();
        fragment.setArguments(args);
        return fragment;
    }


    public static Bitmap decodeSampledBitmapFromResource(Context context, String path,
                                                         int reqWidth, int reqHeight, boolean isHard) {
        Uri contentUri = Uri.parse(path);
        Cursor cursor = null;
        String realPath = "";
        int orientation = 1;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            realPath = cursor.getString(column_index);

            ExifInterface exif = new ExifInterface(realPath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(realPath, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight, isHard);
        Log.d(TAG, "decodeSampledBitmapFromResource: " + options.inSampleSize + " " + options.outWidth + " " + options.outHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        Bitmap result = BitmapFactory.decodeFile(realPath, options);
        int degree = getDegree(orientation);

        if (degree != 0) {
            result = rotate(result, degree);
        }
        return result;
    }

    public static Bitmap decodeSampledBitmapFromRealPath(String path,
                                                         int reqWidth, int reqHeight) {

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight, true);
        Log.d(TAG, "decodeSampledBitmapFromResource: " + options.inSampleSize + " " + options.outWidth + " " + options.outHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight, boolean isHard) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        Log.d(TAG, "calculateInSampleSize: " + reqWidth + " " + reqHeight);

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            if (isHard) {
                while ((halfHeight / inSampleSize) > reqHeight || (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            } else {
                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }

            }
        }

        return inSampleSize;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogImageBinding.inflate(getLayoutInflater());
        path = getArguments().getString(PHOTO_FILE);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int min = Math.min(size.x, size.y);
        Bitmap bitmap = decodeSampledBitmapFromRealPath(path, min, min);
        binding.imagePhoto.getLayoutParams().height = bitmap.getHeight();
        binding.imagePhoto.requestLayout();

        binding.imagePhoto.setImageBitmap(bitmap);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .create();
        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

//        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), fast));
        return dialog;
    }
}
