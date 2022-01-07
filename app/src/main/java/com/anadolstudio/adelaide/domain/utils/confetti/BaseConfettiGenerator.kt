package com.anadolstudio.adelaide.domain.utils.confetti

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiManager.INFINITE_DURATION
import com.github.jinatonic.confetti.ConfettiSource
import com.github.jinatonic.confetti.ConfettoGenerator
import com.github.jinatonic.confetti.confetto.BitmapConfetto

abstract class BaseConfettiGenerator(confettiContainer: ViewGroup) {

    protected val confettiManager: ConfettiManager

    init {
        val allPossibleConfetti = getAllPossibleConfettiParticles(confettiContainer.context)
        val confettiGenerator = getConfettiGenerator(allPossibleConfetti)
        confettiManager = getConfiguredConfettiManager(confettiContainer, confettiGenerator)
    }

    fun launchConfetti() {
        confettiManager.animate()
    }

    private fun getAllPossibleConfettiParticles(context: Context): List<Bitmap> {
        val allPossibleConfetti = mutableListOf<Bitmap>()

        getAllConfettiParticlesResId().forEach { drawableId ->
            val drawable = ContextCompat.getDrawable(context, drawableId)

            if (drawable != null) {
                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                allPossibleConfetti.add(bitmap)
            }
        }
        return allPossibleConfetti
    }

    protected abstract fun getAllConfettiParticlesResId(): List<Int>

    private fun getConfettiGenerator(allPossibleParticles: List<Bitmap>): ConfettoGenerator = ConfettoGenerator { random ->
        val randomIndex = random.nextInt(allPossibleParticles.size)
        val randomBitmap = allPossibleParticles[randomIndex]

        BitmapConfetto(randomBitmap)
    }

    protected open fun getConfiguredConfettiManager(
            confettiContainer: ViewGroup,
            confettiGenerator: ConfettoGenerator
    ): ConfettiManager {
        // Отрицательные значения задаются для создания расширенных границ рисования частиц
        val confettiXStartCoord = -100
        val confettiXEndCoord = confettiContainer.width - confettiXStartCoord
        val confettiYStartCoord = -200
        val confettiYEndCoord = confettiYStartCoord
        val confettiSource = ConfettiSource(confettiXStartCoord, confettiYStartCoord, confettiXEndCoord, confettiYEndCoord)

        return ConfettiManager(confettiContainer.context, confettiGenerator, confettiSource, confettiContainer)
    }
}
