package com.anadolstudio.adelaide.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import io.reactivex.Observable;

public class BitmapHelper {
    public static final String TAG = BitmapHelper.class.getName();

    public static final float TURN_LEFT_90 = -90f;
    public static final float TURN_RIGHT_90 = 90f;

    public static final int COLOR_MASK = Color.parseColor("#EFEFEF");// panelBackground, c ALFA_8 в любом случае будет черным.

    public static final Integer[] ARRAY_OF_COLORS_MASK = new Integer[]{Color.RED, Color.GREEN, Color.BLUE};

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
        if (source == null) return null; // TODO ?

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

    public static BitmapWrapper[] trim(Bitmap[] sources) {
        BitmapWrapper[] wrappers = new BitmapWrapper[sources.length];
        for (int i = 0; i < sources.length; i++) {
            wrappers[i] = trim(sources[i]);
        }
        return wrappers;
    }

    public static BitmapWrapper trim(Bitmap source) {
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
        Bitmap bitmap = Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY);
        float[] bounds = new float[]{firstX, firstY, lastX, lastY};

        return new BitmapWrapper(bounds, bitmap);
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
        if (source == null) return;

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
        if (views == null) return bitmap;

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

    public static Bitmap flip(Bitmap source, @NonNull FlipType type) {
        Matrix matrix = new Matrix();
        int xFlip = type.equals(FlipType.VERTICAL) ? -1 : 1;
        int yFlip = type.equals(FlipType.VERTICAL) ? 1 : -1;
        matrix.postScale(xFlip, yFlip, source.getWidth() / 2f, source.getHeight() / 2f);

        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap rotate(Bitmap source, float degree) {
        //Если хочешь кастомный градус, то используй editor().setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(),
                matrix, true);
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

    @NonNull
    public static String getFileName() {
        return "IMG_" + TimeHelper.getTime(TimeHelper.STANDART_FORMAT) + ".jpeg";
    }

    public static Observable<String> saveBitmapAsFile(Context context, Bitmap originalBitmap, File file) {
        return Observable.create(emitter -> {
            try {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    getInfoOfBitmap(originalBitmap);
                    MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, null, (s, uri) -> Log.d(TAG, "onSuccess"));
                    emitter.onNext(file.getPath());
                    emitter.onComplete();
                    fos.flush();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });

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

    private static Bitmap readImage(Context context, String path) {
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
        int degree = getDegree(orientation);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(realPath, options);

        if (degree != 0) {
            bitmap = rotate(bitmap, degree);
        }
        getInfoOfBitmap(bitmap);

        return bitmap;
    }

    private static void logMemory() {
        Log.i(TAG, String.format("Used memory = %s",
                (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024)));
        Log.i(TAG, String.format("Total memory = %s",
                (int) ((Runtime.getRuntime().totalMemory()) / 1024)));
    }

    public static int getDegree(int orientation) {
        int degree = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    public static Bitmap takeScreenShot(AppCompatActivity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, size.x, size.y - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static Bitmap fastBlur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public enum FlipType {HORIZONTAL, VERTICAL}

    public static class BitmapWrapper {
        private float[] bounds;
        private Bitmap bitmap;

        public BitmapWrapper(float[] bounds, Bitmap bitmap) {
            this.bounds = bounds;
            this.bitmap = bitmap;
        }

        public float[] getBounds() {
            return bounds;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }
}
