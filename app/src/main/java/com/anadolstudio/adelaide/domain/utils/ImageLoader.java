package com.anadolstudio.adelaide.domain.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageLoader {
    public static void loadImage(ImageView imageView, String path, ScaleType scaleType) {
        loadImage(imageView, path, scaleType, false, null);
    }

    public static void loadImageWithoutCache(ImageView imageView, String path, SimpleRequestListener<Bitmap> listener) {
        loadImage(imageView, path, ScaleType.FIT_CENTER, true, listener);
    }

    public static void loadImage(ImageView imageView, String path, ScaleType scaleType, boolean skipMemoryCache,
                                 SimpleRequestListener<Bitmap> listener) {
        RequestBuilder<Bitmap> builder = getBaseRequestBuilder(imageView.getContext(), path, scaleType, skipMemoryCache);

        if (listener != null) {
            builder = builder.addListener(getBitmapRequestListener(listener));
        }

        builder.into(imageView);
    }

    public static void loadImageWithoutCache(Context context, Target<Bitmap> target, String path, RequestListener<Bitmap> requestListener) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true)
                .addListener(requestListener)
                .into(target);
    }

    // При загрузке большоего BM виснет
    public static void loadImageWithoutCache(Context context, IResourceReady<Bitmap> listener, String path) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true)
                .into(createBitmapTarget(listener));
    }

    public static void loadImageWithoutCache(Context context, String path, int width, int height, IResourceReady<Bitmap> listener) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true)
                .override(width, height)
                .into(createBitmapTarget(listener));
    }

    public static void loadImageWithoutCache(Context context, IResourceReady<Bitmap> listener, String path, int widthResize, int heightResize) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true)
                .apply(new RequestOptions().override(widthResize, heightResize))
                .into(createBitmapTarget(listener));
    }

    @NonNull
    private static RequestBuilder<Bitmap> getBaseRequestBuilder(Context context, String path, ScaleType scaleType, boolean skipMemoryCache) {
        RequestBuilder<Bitmap> builder = Glide.with(context)
                .asBitmap()
                .load(path);

        if (scaleType == ScaleType.FIT_CENTER) {
            builder = builder.fitCenter();
        } else {
            builder = builder.centerCrop();
        }

        if (skipMemoryCache) {
            builder = builder.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        return builder;
    }

    @NonNull
    private static RequestListener<Bitmap> getBitmapRequestListener(SimpleRequestListener<Bitmap> listener) {
        return new RequestListener<>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                listener.onLoadFailed(e);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                listener.onResourceReady(resource);
                return false;
            }
        };
    }

    @NonNull
    private static Target<Bitmap> createBitmapTarget(IResourceReady<Bitmap> listener) {
        return new CustomTarget<>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                listener.resourceReady(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        };
    }

    private ImageLoader() {
    }

    public enum ScaleType {
        FIT_CENTER,
        CENTER_CROP
    }

    public interface IResourceReady<T> {
        void resourceReady(T t);
    }

    public interface SimpleRequestListener<T> {
        void onResourceReady(T t);

        void onLoadFailed(Exception e);
    }
}
