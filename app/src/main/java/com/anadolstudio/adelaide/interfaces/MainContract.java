package com.anadolstudio.adelaide.interfaces;

import androidx.appcompat.app.AppCompatActivity;


import com.anadolstudio.adelaide.model.ImageData;

import java.util.ArrayList;
import java.util.Set;

import io.reactivex.Observable;

public interface MainContract {

    interface SimpleView<T> {
        void showData(T t);
    }

    interface View<T> extends SimpleView<T> {
        boolean onPreShow();

        void onPostShow(boolean onPreShow);
    }

    interface Presenter<T, V extends SimpleView<T>> {

        void init(V view);

        void onDestroy();
    }


    interface Repository {

        Observable<ArrayList<ImageData>> getAllImagesDataObservable(AppCompatActivity activity, long lastItemId, String folder); // Path + Folder

        Observable<Set<String>> getAllImagesFoldersObservable(AppCompatActivity activity);
    }
}
