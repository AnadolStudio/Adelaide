package com.anadolstudio.adelaide.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ViewStartButtonBinding
import com.anadolstudio.adelaide.view.function.Imageble
import com.anadolstudio.adelaide.view.function.Textable
import com.anadolstudio.core.util.common.RealFormatter

class StartButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), Imageble {


    private val binding: ViewStartButtonBinding = ViewStartButtonBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.view_start_button, this, true)
    )

    init {
        initializeAttributes(attrs)
    }

    private fun initializeAttributes(attrs: AttributeSet?) {
        if (attrs == null) return
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.StartButton)

        binding.apply {
            typeArray.getDrawable(R.styleable.StartButton_src).let(::setDrawable)
        }

        typeArray.recycle()
    }

    override fun setDrawable(drawable: Drawable?) = binding.imageView.setImageDrawable(drawable)

    override fun setDrawable(drawableRes: Int) = setDrawable(ContextCompat.getDrawable(context, drawableRes))

}
