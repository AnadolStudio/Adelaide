package com.anadolstudio.adelaide.interfaces;

public interface ISelectableAdapterItem<T> {

    void setCurrentSelectedItem(T t);

    void updateView(int state);

    int saveState(T t);

    int getState();

}
