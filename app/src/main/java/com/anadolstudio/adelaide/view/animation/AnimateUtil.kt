package com.anadolstudio.adelaide.view.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.sqrt

class AnimateUtil {
    companion object {
        const val DURATION_NORMAL: Long = 300
        const val DURATION_LONG: Long = 400
        const val DURATION_EXTRA_LONG: Long = 800

        private fun changeThemeWithAnim(rootView: View, imageView: ImageView, centerView: View) {

            // TODO доделать
            var instant = false

            try {
                val pos = arrayOf(
                    centerView.x.toInt() + centerView.width / 2,
                    centerView.y.toInt() - centerView.height / 2
                )
                val w: Int = rootView.measuredWidth
                val h: Int = rootView.measuredHeight
                val bitmap = Bitmap.createBitmap(
                    rootView.measuredWidth,
                    rootView.measuredHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                rootView.draw(canvas)
//            canvas.drawColor(Color.BLUE)
                imageView.setImageBitmap(bitmap)
                imageView.visibility = View.VISIBLE
                val finalRadius = max(
                    sqrt(((w - pos[0]) * (w - pos[0]) + (h - pos[1]) * (h - pos[1])).toDouble()),
                    sqrt((pos[0] * pos[0] + (h - pos[1]) * (h - pos[1])).toDouble())
                )
                    .toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(
                    rootView,
                    pos[0], pos[1], 0f, finalRadius
                )
                anim.duration = DURATION_LONG
                anim.interpolator = MyInterpolator.EASE_IN_OUT_QUAD
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        imageView.setImageDrawable(null)
                        imageView.visibility = View.GONE


                    }
                })
                anim.start()
/*
            delegate.localNightMode =
                if (delegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    AppCompatDelegate.MODE_NIGHT_YES
                }
*/

                instant = true
            } catch (ignore: Throwable) {
            }
            /* val theme: Theme.ThemeInfo = args.get(0) as Theme.ThemeInfo
         val nigthTheme = args.get(1) as Boolean
         val accentId = args.get(3) as Int
         actionBarLayout.animateThemedValues(theme, accentId, nigthTheme, instant)
         if (AndroidUtilities.isTablet()) {
             layersActionBarLayout.animateThemedValues(theme, accentId, nigthTheme, instant)
             rightActionBarLayout.animateThemedValues(theme, accentId, nigthTheme, instant)
         }*/
        }

        fun addTranslationAnimToList(mRecyclerView: RecyclerView) {
            //Не без багов
            mRecyclerView.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        val parent = mRecyclerView.bottom
                        for (i in 0 until mRecyclerView.childCount) {
                            val v = mRecyclerView.getChildAt(i)
                            v.y = parent.toFloat()
                            v.animate().translationY(1.0f)
                                .setDuration(DURATION_LONG)
                                .setStartDelay(i * 50L)
                                .start()
                            v.animate().startDelay = 0 //возвращаю дефолтное значение
                        }
                        mRecyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                })
        }

        fun showAnimX(view: View, start: Float, end: Float) {
            view.visibility = View.INVISIBLE
            view.translationX = start
            view.animate()
                .translationX(end)
                .setDuration(DURATION_NORMAL)
                .setListener(getSimpleListener(view))
        }

        fun showAnimX(view: View, start: Float, end: Float, listener: AnimatorListenerAdapter) {
            view.visibility = View.INVISIBLE
            view.translationX = start
            view.animate()
                .translationX(end)
                .setDuration(DURATION_NORMAL)
                .setListener(listener)
        }

        //TODO UP, DOWN, LEFT, RIGHT
        fun showAnimY(view: View, start: Float, end: Float) {
            view.visibility = View.INVISIBLE
            view.translationY = start
            view.animate()
                .translationY(end)
                .setDuration(DURATION_NORMAL)
                .setListener(getSimpleListener(view))
        }

        fun showAnimY(view: View, start: Float, end: Float, listener: AnimatorListenerAdapter) {
            view.visibility = View.INVISIBLE
            view.translationY = start
            view.animate()
                .translationY(end)
                .setDuration(DURATION_NORMAL)
                .setListener(listener)
        }


        private fun getSimpleListener(view: View) = object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                view.clearAnimation()
                view.visibility = View.VISIBLE
            }
        }
    }
}