package com.anadolstudio.adelaide.feature.common.data

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.anadolstudio.adelaide.feature.common.domain.ResourceRepository
import com.anadolstudio.utils.R

class ResourceRepositoryImpl(private val context: Context) : ResourceRepository {
    override fun getString(id: Int): String = context.getString(id)
    override fun getColor(id: Int): Int = context.getColor(id)
    override fun navigateArg(data: Any): Bundle = bundleOf(getString(R.string.data) to data)
}
