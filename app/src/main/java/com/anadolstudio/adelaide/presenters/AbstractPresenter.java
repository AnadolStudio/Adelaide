package com.anadolstudio.adelaide.presenters;


import com.anadolstudio.adelaide.interfaces.MainContract;

abstract class AbstractPresenter<T, V extends MainContract.SimpleView<T>> implements MainContract.Presenter<T, V> {

    protected V view;

    public AbstractPresenter(V view) {
        init(view);
    }

    @Override
    public void init(V view) {
        this.view = view;
    }

    @Override
    public void onDestroy() {

    }
}
