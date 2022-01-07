package com.anadolstudio.adelaide.interfaces;

public interface ISelectableItem<T> {

    void setCurrentSelectedItem(T t);

    void updateView(T t, boolean isSelected);

    int saveState(T t);

    int getState();

}
