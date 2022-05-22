package com.anadolstudio.adelaide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anadolstudio.adelaide.view.ViewModelFactory
import com.anadolstudio.adelaide.view.screens.gallery.GalleryListViewModel
import com.anadolstudio.core.tasks.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryViewModelTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val activity = InstrumentationRegistry.getInstrumentation()
            .newActivity(
                this.javaClass.classLoader,
                "TestActivity",
                Intent(appContext, TestActivity::class.java)
            )
        assertEquals("com.anadolstudio.adelaide", appContext.packageName)

        val viewModel = ViewModelFactory().create(GalleryListViewModel::class.java)

        viewModel.loadImages(appContext)

        viewModel.images.observe(activity as AppCompatActivity){ result ->
            if (result is Result.Success) {
                assertTrue(result.data.isNotEmpty())
            }
        }

    }
}