package com.anadolstudio.adelaide.domain.utils.confetti

import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiManager.INFINITE_DURATION
import com.github.jinatonic.confetti.ConfettoGenerator

class WinterConfettiGenerator(confettiContainer: ViewGroup, isDarkTheme: Boolean):BaseConfettiGenerator(confettiContainer) {

    private val alpha = if (isDarkTheme) 0.25F else 0.75F

    override fun getAllConfettiParticlesResId(): List<Int> = listOf(
            R.drawable.snowflake_1_16,
            R.drawable.snowflake_1_24,
            R.drawable.snowflake_2_16,
            R.drawable.snowflake_2_24,
    )

    override fun getConfiguredConfettiManager(
        confettiContainer: ViewGroup,
        confettiGenerator: ConfettoGenerator
    ): ConfettiManager = super.getConfiguredConfettiManager(confettiContainer, confettiGenerator)
            .setEmissionDuration(INFINITE_DURATION)
            .setEmissionRate(15f)
            .enableFadeOut { alpha }
            .setVelocityY(250f, 75F)
            .setRotationalVelocity(90f, 60f)
            .animate()

}
