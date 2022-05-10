package com.anadolstudio.adelaide.domain.touchlisteners;

import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.annotation.NonNull;

/**
 * Created on 18/01/2017.
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * <p></p>
 */
public class ImageTouchListener implements OnTouchListener {
    public static final String TAG = ImageTouchListener.class.getName();

    private static final int INVALID_POINTER_ID = -1;

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    private static void move(View view, TransformInfo info, IViewMove listener) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);

        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);

        view.setRotation(adjustAngle(view.getRotation() + info.deltaAngle));
        if (listener != null) {
            listener.viewMove();
        }
    }

    private static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }
    private final GestureDetector mGestureListener;
    private final float defaultX;
    private final float defaultY;
    private final float defaultScaleX;
    private final float defaultScaleY;
    private final float defaultRotation;
    private boolean isRotateEnabled = true;
    private final boolean isTranslateEnabled = true;
    private final boolean isScaleEnabled = true;
    private float minimumScale = 0.25f;
    private float maximumScale = 10.0f;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPrevX, mPrevY, mPrevRawX, mPrevRawY;
    private final ScaleGestureDetector mScaleGestureDetector;
    private final int[] location = new int[2];
    private Rect outRect;
    private IViewMove listener = null;
    private OnMultiTouchListener onMultiTouchListener;
    private OnGestureControl mOnGestureControl;
    private final boolean mIsPinchScalable;

    public ImageTouchListener(@NonNull View view,
                              boolean isPinchScalable,
                              IViewMove listener
    ) {
        this(view, isPinchScalable);
        this.listener = listener;
    }

    public ImageTouchListener(@NonNull View view,
                              boolean isPinchScalable
    ) {
        defaultX = view.getTranslationX();
        defaultY = view.getTranslationY();
        defaultScaleX = view.getScaleX();
        defaultScaleY = view.getScaleY();
        defaultRotation = view.getRotation();
        mIsPinchScalable = isPinchScalable;

        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        mGestureListener = new GestureDetector(new GestureListener());

    }

    public ImageTouchListener(@NonNull View view,
                              boolean isPinchScalable,
                              boolean onlyScale
    ) {
        this(view, isPinchScalable);
        if (onlyScale) {
            this.minimumScale = 1;
            this.maximumScale = 10;
//            isTranslateEnabled = false;
            isRotateEnabled = false;
        }
    }

    private boolean isViewInBounds(View view, int x, int y) {
        /*Rect outRect = new Rect();
        int[] location = new int[2];*/
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    public float getDefaultX() {
        return defaultX;
    }

    public float getDefaultScaleX() {
        return defaultScaleX;
    }

    public float getDefaultScaleY() {
        return defaultScaleY;
    }

    public float getDefaultRotation() {
        return defaultRotation;
    }

    public float getDefaultY() {
        return defaultY;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(view, event);
        mGestureListener.onTouchEvent(event);
        int action = event.getAction();

        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouch: ACTION_DOWN");
                mPrevX = event.getX();
                mPrevY = event.getY();
                mPrevRawX = event.getRawX();
                mPrevRawY = event.getRawY();
                mActivePointerId = event.getPointerId(0);
                view.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                // Only enable dragging on focused stickers.
                int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                if (pointerIndexMove != -1) {
                    float currX = event.getX(pointerIndexMove);
                    float currY = event.getY(pointerIndexMove);
                    if (!mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, currX - mPrevX, currY - mPrevY);
                        if (listener != null) {
                            listener.viewMove();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                /*if (!isViewInBounds(view, x, y)) {
                    view.animate().translationY(0).translationY(0);
                }*/
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndexPointerUp = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndexPointerUp);
                if (pointerId == mActivePointerId) {
                    int newPointerIndex = pointerIndexPointerUp == 0 ? 1 : 0;
                    mPrevX = event.getX(newPointerIndex);
                    mPrevY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
        }
        return true;
    }

    void setOnMultiTouchListener(OnMultiTouchListener onMultiTouchListener) {
        this.onMultiTouchListener = onMultiTouchListener;
    }

    void setOnGestureControl(OnGestureControl onGestureControl) {
        mOnGestureControl = onGestureControl;
    }

    public void getDefaultState(View view) {
        getDefaultState(view, false);
    }

    public void getDefaultState(View view, boolean withAnim) {
        if (withAnim) {
            view.animate().translationX(getDefaultX()).translationY(getDefaultY());
            view.animate().scaleX(getDefaultScaleX()).scaleY(getDefaultScaleY());
            view.animate().rotation(getDefaultRotation());
            view.clearAnimation();
        } else {
            view.setTranslationX(getDefaultX());
            view.setTranslationY(getDefaultY());
            view.setScaleX(getDefaultScaleX());
            view.setScaleY(getDefaultScaleY());
            view.setRotation(getDefaultRotation());
        }
    }

    public interface IViewMove {
        void viewMove();
    }

    interface OnMultiTouchListener {
        void onEditTextClickListener(String text, int colorCode);

        void onRemoveViewListener(View removedView);
    }

    interface OnGestureControl {
        void onClick();

        void onLongClick();
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mPivotX;
        private float mPivotY;
        private final Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            Log.d(TAG, "onTouch: onScaleBegin");
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return mIsPinchScalable;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            Log.d(TAG, "onTouch: onScale");
            TransformInfo info = new TransformInfo();
            info.deltaScale = isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            info.deltaX = isTranslateEnabled ? detector.getFocusX() - mPivotX : 0.0f;
            info.deltaY = isTranslateEnabled ? detector.getFocusY() - mPivotY : 0.0f;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = minimumScale;
            info.maximumScale = maximumScale;
            move(view, info, listener);
            return !mIsPinchScalable;
        }
    }

    private class TransformInfo {
        float deltaX;
        float deltaY;
        float deltaScale;
        float deltaAngle;
        float pivotX;
        float pivotY;
        float minimumScale;
        float maximumScale;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mOnGestureControl != null) {
                mOnGestureControl.onClick();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (mOnGestureControl != null) {
                mOnGestureControl.onLongClick();
            }
        }
    }
}