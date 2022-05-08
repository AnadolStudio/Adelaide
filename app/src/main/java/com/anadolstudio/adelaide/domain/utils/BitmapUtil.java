package com.anadolstudio.adelaide.domain.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.anadolstudio.core.util.BitmapDecoder;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;

public class BitmapUtil {
    public static final String TAG = BitmapUtil.class.getName();

    public static final float TURN_LEFT_90 = -90f;
    public static final float TURN_RIGHT_90 = 90f;

    public static final int COLOR_MASK = Color.parseColor("#EFEFEF");// panelBackground, c ALFA_8 в любом случае будет черным.

    public static final Integer[] ARRAY_OF_COLORS_MASK = new Integer[]{Color.RED, Color.GREEN, Color.BLUE};
    public static final String CONTENT = "content:";

    public static Observable<Bitmap> loadBitmapFromAssets(Context context, final String path) {
        return Observable.create(emitter -> {
            try {
                InputStream inputStream = context.getAssets().open(path);
                emitter.onNext(BitmapFactory.decodeStream(inputStream));
                inputStream.close();
                emitter.onComplete();
            } catch (IOException ex) {
                emitter.onError(ex);
            }
        });
    }

    public static Bitmap centerCrop(Bitmap source) {
        if (source == null) {
            return null; // TODO ?
        }

        int h = source.getHeight();
        int w = source.getWidth();
        int minSide = Math.min(w, h);
        int maxSide = Math.max(w, h);
        int x = (w >= h) ? (maxSide / 2 - minSide / 2) : 0;
        int y = (w >= h) ? 0 : (maxSide / 2 - minSide / 2);

        return Bitmap.createBitmap(source, x, y, minSide, minSide);
    }

    public static int getXSpace(Bitmap bitmap, Bitmap bitmap1) {
        return Math.abs((bitmap1.getWidth() - bitmap.getWidth()) / 2);
    }

    public static int getYSpace(Bitmap bitmap, Bitmap bitmap1) {
        return Math.abs((bitmap1.getHeight() - bitmap.getHeight()) / 2);
    }

    public static Bitmap trim(Bitmap source) {
        int firstX = 0, firstY = 0;
        int lastX = source.getWidth();
        int lastY = source.getHeight();
        int[] pixels = new int[source.getWidth() * source.getHeight()];
        source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());

        loop:
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = firstX; x < source.getWidth(); x++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstY = y;
                    break loop;
                }
            }
        }
        loop:
        for (int x = source.getWidth() - 1; x >= firstX; x--) {
            for (int y = source.getHeight() - 1; y >= firstY; y--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = source.getHeight() - 1; y >= firstY; y--) {
            for (int x = source.getWidth() - 1; x >= firstX; x--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastY = y;
                    break loop;
                }
            }
        }
        return Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY);
    }

    public static int[] getBounds(Bitmap source) {
        int[] pixels = new int[source.getWidth() * source.getHeight()];
        source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());

        return getBounds(pixels, source.getWidth(), source.getHeight());
    }

    public static int[] getBounds(int[] pixels, int width, int height) {
        int firstX = 0, firstY = 0;
        int lastX = width;
        int lastY = height;

        loop:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixels[x + (y * width)] != Color.TRANSPARENT) {
                    firstX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = 0; y < height; y++) {
            for (int x = firstX; x < width; x++) {
                if (pixels[x + (y * width)] != Color.TRANSPARENT) {
                    firstY = y;
                    break loop;
                }
            }
        }
        loop:
        for (int x = width - 1; x >= firstX; x--) {
            for (int y = height - 1; y >= firstY; y--) {
                if (pixels[x + (y * width)] != Color.TRANSPARENT) {
                    lastX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = height - 1; y >= firstY; y--) {
            for (int x = width - 1; x >= firstX; x--) {
                if (pixels[x + (y * width)] != Color.TRANSPARENT) {
                    lastY = y;
                    break loop;
                }
            }
        }
        return new int[]{firstX, firstY, lastX, lastY};
    }

    public static Bitmap cropFromSource(
            int height, int weight,
            int x, int y,
            Bitmap source) {

        return Bitmap.createBitmap(
                source,
                x, y,
                weight, height);
    }

    public static Bitmap scaleBitmap(Bitmap main, Bitmap support) {
        return scaleBitmap(main.getWidth(), main.getHeight(), support, true);
    }

    public static Bitmap scaleBitmap(Bitmap main, Bitmap support, boolean isHard) {
        return scaleBitmap(main.getWidth(), main.getHeight(), support, isHard);
    }

    public static Bitmap scaleBitmap(float mainW, float mainH, Bitmap support) {
        return scaleBitmap(mainW, mainH, support, true);
    }

    public static Bitmap scaleBitmap(float mainW, float mainH, Bitmap support, boolean isHard) {
        float supportW = support.getWidth();
        float supportH = support.getHeight();
        float scaleRatio = getScaleRatio(mainW, mainH, supportW, supportH);

        int scaleW = (int) (isHard ? mainW : supportW * scaleRatio);
        int scaleH = (int) (isHard ? mainH : supportH * scaleRatio);

        return Bitmap.createScaledBitmap(support, scaleW, scaleH, true);
    }

    public static float getScaleRatio(float mainW, float mainH, float supportW, float supportH) {
        return (supportW > mainW && supportH > mainH) ?
                Math.min(mainW / supportW, mainH / supportH) :
                Math.max(mainW / supportW, mainH / supportH);
    }

    public static float getScaleRatio(float current, float defaultInt) {
        return defaultInt / current;
    }

    public static void fitImageToEdge(int widthContainer, int heightContainer, Bitmap source, ImageView scaledImageView) {
        if (source == null) {
            return;
        }

        float ratio = Math.min(
                getScaleRatio(source.getHeight(), heightContainer),
                getScaleRatio(source.getWidth(), widthContainer));

        float realWidthBitmap = source.getWidth() * (ratio >= 1 ? 1 / ratio : ratio);
        float realHeightBitmap = source.getHeight() * (ratio >= 1 ? 1 / ratio : ratio);

        float scale = Math.max(
                widthContainer / realWidthBitmap,
                heightContainer / realHeightBitmap);

        scaledImageView.setScaleX(scale);
        scaledImageView.setScaleY(scale);
    }

    public static Bitmap captureView(View view) {
        return captureView(view.getWidth(), view.getHeight(), view);
    }

    public static Bitmap captureView(int width, int height, View... views) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if (views == null) {
            return bitmap;
        }

        Canvas canvas = new Canvas(bitmap);
        for (View v : views) {
            v.draw(canvas);
        }

        return bitmap;
    }

    public static Bitmap captureViewMask(View view) {
        int width = view.getWidth();
        int height = view.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(COLOR_MASK);
        view.draw(canvas);

        return bitmap;
    }


    private static int getCorrectColor(int color) {
        int r = getColorIfThisMax(Color.red(color));
        int g = getColorIfThisMax(Color.green(color));
        int b = getColorIfThisMax(Color.blue(color));
        return Color.argb(255, r, g, b);
    }

    private static int getColorIfThisMax(int color) {
        return color == 255 ? color : 0;
    }

    @NonNull
    public static DisplayMetrics getDefaultSize(AppCompatActivity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    @NonNull

    public static DisplayMetrics getRealSize(AppCompatActivity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    public static Bitmap getBitmapFromImageView(ImageView source) {
        BitmapDrawable bitmap = ((BitmapDrawable) source.getDrawable());
        return bitmap == null ? null : bitmap.getBitmap();
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void getInfoOfBitmap(Bitmap bitmap) {
        Log.d(TAG, String.format("bitmap size = %sx%s, byteCount = %s, total = %s",
                bitmap.getWidth(), bitmap.getHeight(),
                (bitmap.getByteCount()), bitmap.getHeight() * bitmap.getWidth()));
        logMemory();
    }

    public static String getMimeType(AppCompatActivity activity, Uri uri) {
        String mimeType;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = activity.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static Bitmap decodeBitmapFromPath(Context context, String path,
                                              int reqWidth, int reqHeight) {
        BitmapDecoder decoder;

        if (path.contains(CONTENT)) {
            decoder = new BitmapDecoder.FromContentPath(context);
        } else {//RealPath
            decoder = new BitmapDecoder.FromRealPath();
        }

        return decoder.decode(path, reqWidth, reqHeight);
    }

    private static void logMemory() {
        Log.i(TAG, String.format("Used memory = %s",
                (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)));
        Log.i(TAG, String.format("Total memory = %s",
                (int) ((Runtime.getRuntime().totalMemory()) / 1024)));
    }
}
