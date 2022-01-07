package com.anadolstudio.adelaide.presenters;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anadolstudio.adelaide.interfaces.MainContract;
import com.anadolstudio.adelaide.model.ImageData;
import com.anadolstudio.adelaide.model.MainRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class GalleryPresenter extends AbstractPresenter<GalleryPresenter.GalleryPresenterItem, MainContract.View<GalleryPresenter.GalleryPresenterItem>> {
    public static final String TAG = GalleryPresenter.class.getName();
    private MainRepository repository;
    private CompositeDisposable compositeDisposable;

    public GalleryPresenter(MainContract.View<GalleryPresenterItem> view) {
        super(view);
    }

    @Override
    public void init(MainContract.View<GalleryPresenterItem> view) {
        super.init(view);

        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        if (repository == null) {
            repository = new MainRepository();
        }
    }

    public void loadData(AppCompatActivity activity, long lastItemId, @Nullable String folder) { // Для катологизации (Activity activity, String folder)
        if (!view.onPreShow()) {
            view.onPostShow(false);
            return;
        }

        ArrayList<String> paths = new ArrayList<>();
        compositeDisposable.add(repository.getAllImagesDataObservable(activity, lastItemId, folder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageDataList -> {
                    Log.d(TAG, "loadData: imageDataList " + imageDataList);
                    Log.d(TAG, "loadData: imageDataList.size() " + imageDataList.size());
                    for (ImageData item : imageDataList) {
                        paths.add(item.getPath());
                    }

                    GalleryPresenterItem item = new GalleryPresenterItem(GalleryPresenterItem.TypeData.IMAGES);
                    item.paths.addAll(paths);
                    item.lastItemId = lastItemId;

                    view.showData(item);
                    view.onPostShow(true);
                }, Throwable::printStackTrace));

    }

    public void getAllFolders(AppCompatActivity activity) {
        if (!view.onPreShow()) {
            view.onPostShow(false);
            return;
        }
        compositeDisposable.add(repository.getAllImagesFoldersObservable(activity).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(folders -> {
                    GalleryPresenterItem item = new GalleryPresenterItem(GalleryPresenterItem.TypeData.FOLDERS);
                    item.folders.addAll(folders);
                    view.showData(item);
                    view.onPostShow(false);
                    Log.d(TAG, "loadData: folders " + folders);
                }, Throwable::printStackTrace));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
    }

    public static class GalleryPresenterItem {
        private final ArrayList<String> paths = new ArrayList<>();
        private final Set<String> folders = new HashSet<>();
        private final TypeData typeData;
        private long lastItemId;

        public GalleryPresenterItem(TypeData typeData) {
            this.typeData = typeData;
        }

        public TypeData getTypeData() {
            return typeData;
        }

        public ArrayList<String> getPaths() {
            return paths;
        }

        public Set<String> getFolders() {
            return folders;
        }

        public long getLastItemId() {
            return lastItemId;
        }

        @NotNull
        @Override
        public String toString() {
            // Для проверки в логах
            return "GalleryPresenterItem{" +
                    "paths=" + paths +
                    '}';
        }

        public enum TypeData {IMAGES, FOLDERS}
    }
}
