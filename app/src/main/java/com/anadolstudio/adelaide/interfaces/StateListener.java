package com.anadolstudio.adelaide.interfaces;

public interface StateListener {

    boolean onBackClick(); // Возвращает true, если перехватывается, иначе(стандартное поведение выполняется после метода) - false

    boolean isLocalBackClick();

    boolean isLocalApply();

    boolean apply();
}
