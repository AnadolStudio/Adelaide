package com.anadolstudio.core.interfaces;

public interface StateListener {

    boolean backClick(); // Возвращает false, если перехватывается, иначе(стандартное поведение выполняется после метода) - true

    boolean isReadyToBackClick();

    boolean isReadyToApply();

    boolean apply();
}
