package com.anadolstudio.adelaide.domain.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.anadolstudio.adelaide.R;
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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageLoader {

    private ImageLoader() {
    }

    public static void loadImage(ImageView imageView, @DrawableRes int id, ScaleType scaleType) {
        RequestBuilder<Drawable> builder = Glide
                .with(imageView)
                .load(id);

        if (scaleType == ScaleType.FIT_CENTER) {
            builder = builder.fitCenter();
        } else {
            builder = builder.centerCrop();
        }

        builder.into(imageView);
    }

    public static void loadImage(ImageView imageView, String path, ScaleType scaleType, boolean withPlaceHolder) {
        loadImage(imageView, path, scaleType, false, null, withPlaceHolder);
    }

    public static void loadImageWithoutCache(ImageView imageView, String path, SimpleRequestListener<Bitmap> listener, boolean withPlaceHolder) {
        loadImage(imageView, path, ScaleType.FIT_CENTER, true, listener, withPlaceHolder);
    }

    public static void loadImage(ImageView imageView, String path, ScaleType scaleType, boolean skipMemoryCache,
                                 SimpleRequestListener<Bitmap> listener, boolean withPlaceHolder) {
        RequestBuilder<Bitmap> builder = getBaseRequestBuilder(imageView.getContext(), path, scaleType, skipMemoryCache, withPlaceHolder);

        if (listener != null) {
            builder = builder.addListener(getBitmapRequestListener(listener));
        }

        builder.into(imageView);
    }

    public static void loadImageWithoutCache(Context context, Target<Bitmap> target, String path, RequestListener<Bitmap> requestListener) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true, false)
                .addListener(requestListener)
                .into(target);
    }

    // При загрузке большоего BM виснет
    public static void loadImageWithoutCache(Context context, String path, IResourceReady<Bitmap> listener) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true, false)
                .into(createBitmapTarget(listener));
    }

    public static void loadImageWithoutCache(Context context, String path, int width, int height, IResourceReady<Bitmap> listener) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true, false)
                .override(width, height)
                .into(createBitmapTarget(listener));
    }

    public static void loadImageWithoutCache(Context context, String path, IResourceReady<Bitmap> listener, int widthResize, int heightResize) {
        getBaseRequestBuilder(context, path, ScaleType.FIT_CENTER, true, false)
                .apply(new RequestOptions().override(widthResize, heightResize))
                .into(createBitmapTarget(listener));
    }

    @NonNull
    private static RequestBuilder<Bitmap> getBaseRequestBuilder(Context context, String path, ScaleType scaleType, boolean skipMemoryCache,
                                                                boolean withPlaceHolder) {
        RequestBuilder<Bitmap> builder = Glide.with(context)
                .asBitmap()
                .load(path);

        if (withPlaceHolder) {
            builder = builder.placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_broken_image);
        }

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
