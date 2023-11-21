package com.anadolstudio.adelaide.feature.gallery.presetnation.behavior

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import com.anadolstudio.adelaide.R
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class AppBarBehavior() : AppBarLayout.Behavior() {

    constructor(context: Context, attrs: AttributeSet) : this()

    private companion object {
        const val DEFAULT_VALUE = 1F
        const val RATIO_OFFSET = 0.75F
    }

    private var _topInfoView: View? = null
    private val topInfoView: View get() = requireNotNull(_topInfoView)

    private var _toolbar: View? = null
    private val toolbar: View get() = requireNotNull(_toolbar)

    init {
        setDragCallback(
                object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean = false
                }
        )
    }

    override fun onLayoutChild(
            parent: CoordinatorLayout,
            appBar: AppBarLayout,
            layoutDirection: Int
    ): Boolean {
        if (_topInfoView == null) initView(appBar)

        return super.onLayoutChild(parent, appBar, layoutDirection)
    }

    private fun initView(appBar: AppBarLayout) {
        _topInfoView = appBar.findViewById(R.id.collapsing_toolbar_container)
        _toolbar = appBar.findViewById(R.id.toolbar)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, appBar: AppBarLayout, state: Parcelable) {
        super.onRestoreInstanceState(parent, appBar, (state as? SavedState)?.superState ?: state)

        if (state is SavedState) {
            if (_topInfoView == null) initView(appBar)

            changeView(
                    alphaTopView = state.restoreData().alphaTopView,
                    alphaToolbar = state.restoreData().alphaToolbar,
            )
        }
    }

    override fun onSaveInstanceState(
            parent: CoordinatorLayout, abl: AppBarLayout
    ): Parcelable? = super.onSaveInstanceState(parent, abl)?.let { supperState ->
        val data = SavedState.Data(
                alphaTopView = topInfoView.alpha,
                alphaToolbar = toolbar.alpha,
        )
        SavedState(data, supperState)
    }

    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            appBar: AppBarLayout,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
    ): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            appBar: AppBarLayout,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int
    ) {
        if (appBar.isCollapsed() && !isAbsDyBiggerInnerScroll(dy, target)) {
            return super.onNestedPreScroll(coordinatorLayout, appBar, target, dx, dy, consumed, type)
        }

        val ratio = abs(topAndBottomOffset) / appBar.totalScrollRange.toFloat()

        val offsetRatio = MathUtils.clamp(ratio, RATIO_OFFSET, DEFAULT_VALUE)
        val scaleRatioDiapason = DEFAULT_VALUE - RATIO_OFFSET
        val scaleRatio = MathUtils.clamp(offsetRatio - RATIO_OFFSET, 0F, scaleRatioDiapason)
        val alphaToolbarAlpha = scaleRatio / scaleRatioDiapason

        changeView(alphaTopView = DEFAULT_VALUE - ratio, alphaToolbar = alphaToolbarAlpha)

        super.onNestedPreScroll(coordinatorLayout, appBar, target, dx, dy, consumed, type)
    }

    private fun changeView(alphaTopView: Float, alphaToolbar: Float) {
        topInfoView.alpha = alphaTopView
        toolbar.isInvisible = alphaToolbar == 0F
        toolbar.alpha = alphaToolbar
    }

    private fun isAbsDyBiggerInnerScroll(dy: Int, target: View): Boolean = abs(dy) > target.scrollY

    private fun AppBarLayout.isCollapsed(): Boolean = abs(topAndBottomOffset) == totalScrollRange

    private class SavedState : View.BaseSavedState {

        constructor(superState: Parcelable) : super(superState)

        constructor(data: Data, superState: Parcelable) : super(superState) {
            saveData(data)
        }

        private var savedState = Data()

        fun saveData(newState: Data) {
            savedState = newState
        }

        fun restoreData(): Data = savedState

        constructor(parcel: Parcel) : super(parcel) {
            savedState = savedState.copy(
                    alphaTopView = parcel.readFloat(),
                    alphaToolbar = parcel.readFloat(),
            )
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeFloat(savedState.alphaTopView)
            parcel.writeFloat(savedState.alphaToolbar)
        }

        companion object {

            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }

        data class Data(
                val alphaTopView: Float = 0F,
                val alphaToolbar: Float = 0F,
        )
    }
}
