package com.anadolstudio.adelaide.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anadolstudio.adelaide.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public class GlideLoader {
    public interface IResourceReady<T> {
        void resourceReady(T t);
    }
    public static void loadImage(ImageView imageView, String path) {
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .fitCenter()
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, String path, IResourceReady<Bitmap> listener) {
        RequestListener<Bitmap> requestListener = getBitmapRequestListener(listener);
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .addListener(requestListener)
                .fitCenter()
                .into(imageView);
    }

    @NonNull
    private static RequestListener<Bitmap> getBitmapRequestListener(IResourceReady<Bitmap> listener) {
        return new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                listener.resourceReady(resource);
                return false;
            }
        };
    }

    public static void loadImageCenterCrop(ImageView imageView, String path) {
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .centerCrop()
                .placeholder(R.drawable.ic_image)
                .into(imageView);
    }

    public static void loadImageCenterCrop(ImageView imageView, String path, Drawable placeholder) {
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .centerCrop()
                .placeholder(placeholder)
                .into(imageView);
    }

    public static void loadImageWithoutCache(ImageView imageView, String path, RequestListener<Bitmap> requestListener) {
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .fitCenter()
                .addListener(requestListener)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    public static void loadImageWithoutCache(ImageView imageView, String path, IResourceReady<Bitmap> listener) {
        RequestListener<Bitmap> requestListener = getBitmapRequestListener(listener);

        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .fitCenter()
                .addListener(requestListener)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    public static void loadImageWithoutCache(Context context, Target<Bitmap> target, String path, RequestListener<Bitmap> requestListener) {
        Glide.with(context)
                .asBitmap()
                .load(path)
                .fitCenter()
                .addListener(requestListener)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }

    public static void loadImageWithoutCache(Context context, Target<Bitmap> target, String path) {

        Glide.with(context)
                .asBitmap()
                .load(path)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }

    // При загрузке большоего BM виснет
    public static void loadImageWithoutCache(Context context, IResourceReady<Bitmap> listener, String path) {
        Target<Bitmap> target = createBitmapTarget(listener);
        Glide.with(context)
                .asBitmap()
                .load(path)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }

    public static void loadImageWithoutCache(Context context, String path, int width, int height, IResourceReady<Bitmap> listener) {

        Target<Bitmap> target = createBitmapTarget(listener);
        Glide.with(context)
                .asBitmap()
                .override(width, height)
                .load(path)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }

    public static void loadImageWithoutCache(Context context, IResourceReady<Bitmap> listener, String path, int widthResize, int heightResize) {
        Target<Bitmap> target = createBitmapTarget(listener);

        Glide.with(context)
                .asBitmap()
                .load(path)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(new RequestOptions().override(widthResize, heightResize))
                .into(target);
    }

    public static void loadImage(Context context, IResourceReady<Bitmap> listener, String path, int widthResize, int heightResize) {
        Target<Bitmap> target = createBitmapTarget(listener);

        Glide.with(context)
                .asBitmap()
                .load(path)
                .fitCenter()
                .apply(new RequestOptions().override(widthResize, heightResize))
                .into(target);
    }

    @NonNull
    private static Target<Bitmap> createBitmapTarget(IResourceReady<Bitmap> listener) {
        return new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                listener.resourceReady(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        };
    }
}

