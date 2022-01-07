package com.anadolstudio.adelaide.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ViewSliderEditBinding
import com.anadolstudio.core.common_util.RealFormatter
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import kotlin.math.max
import kotlin.math.min

class EditSliderView(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {

        const val TAG: String = "SliderView"
    }

    enum class State { ALL, ONLY_SLIDER, WITHOUT_SLIDER, WITHOUT_CANCEL, WITHOUT_APPLY }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private val binding: ViewSliderEditBinding = ViewSliderEditBinding.bind(
        LayoutInflater.from(context).inflate(R.layout.view_slider_edit, this, true)
    )

    init {
        initializeAttributes(attrs)
        setWillNotDraw(false)
    }

    private fun initializeAttributes(attrs: AttributeSet?) {

        if (attrs == null) return
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.EditSliderView)

        setState(
            State.values()[typeArray.getInt(R.styleable.EditSliderView_state, State.ALL.ordinal)]
        )

        binding.slider.apply {
            setSliderLabelFormatter(RealFormatter)
            setLabelBehavior()
            setupSlider(
                typeArray.getFloat(R.styleable.EditSliderView_valueFrom, 0F),
                typeArray.getFloat(R.styleable.EditSliderView_valueTo, 100F),
                typeArray.getFloat(R.styleable.EditSliderView_value, 0F)
            )
        }

        typeArray.recycle()
    }

    fun setState(state: State) {
        when (state) {
            State.ALL -> {
                binding.applyButton.visibility = VISIBLE
                binding.cancelButton.visibility = VISIBLE
                binding.slider.visibility = VISIBLE
            }
            State.ONLY_SLIDER -> {
                binding.applyButton.visibility = GONE
                binding.cancelButton.visibility = GONE
            }

            State.WITHOUT_SLIDER -> binding.slider.visibility = GONE
            State.WITHOUT_CANCEL -> binding.cancelButton.visibility = GONE
            State.WITHOUT_APPLY -> binding.applyButton.visibility = GONE
        }
    }

    fun setSliderLabelFormatter(labelFormatter: LabelFormatter) {
        binding.slider.setLabelFormatter(labelFormatter)
    }

    fun setLabelBehavior(labelBehavior: Int = LabelFormatter.LABEL_GONE) {
        binding.slider.labelBehavior = labelBehavior
    }

    fun setSliderListener(listener: Slider.OnChangeListener) {
        binding.slider.addOnChangeListener(listener)
    }

    fun setupSlider(from: Float = 0F, to: Float = 100F, value: Float = (to + from) / 2F) {
        val valueFromHandler = min(from, to)
        val valueToHandler = max(from, to)

        binding.slider.apply {
            valueFrom = valueFromHandler
            valueTo = valueToHandler
            this@EditSliderView.setValue(value)
        }
    }

    fun setValue(value: Float) {
        binding.slider.apply {
            this.value = if (value !in valueFrom..valueTo)
                (valueTo + valueFrom) / 2F
            else
                value
        }
    }

    fun setApplyListener(listener: View.OnClickListener) {
        binding.applyButton.setOnClickListener(listener)
    }

    fun setCancelListener(listener: View.OnClickListener) {
        binding.cancelButton.setOnClickListener(listener)
    }

    fun setApplyIcon(@DrawableRes id: Int) {
        binding.applyButton.setImageResource(id)
    }

    fun setCancelIcon(@DrawableRes id: Int) {
        binding.cancelButton.setImageResource(id)
    }

    fun setApplyIconTint(color: Int) {
        binding.applyButton.setColorFilter(color)
    }

    fun setCancelTint(color: Int) {
        binding.cancelButton.setColorFilter(color)
    }

    fun setSliderVisible(visibility: Int) {
        setVisible(binding.slider, visibility)
    }

    fun setApplyVisible(visibility: Int) {
        setVisible(binding.slider, visibility)
    }

    fun setCancelVisible(visibility: Int) {
        setVisible(binding.slider, visibility)
    }

    private fun setVisible(view: View, visibility: Int) {
        view.visibility = visibility
    }

}
