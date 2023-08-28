package com.anadolstudio.adelaide

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.anadolstudio.adelaide.ui.screens.gallery.GalleryImageAdapter
import com.anadolstudio.adelaide.feature.start.StartFragment
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@LargeTest
class ScreenTest {

    @get:Rule
    val activityTestRule = ActivityScenarioRule(StartFragment::class.java)

    @Test
    fun navigateOnAllScreens() {
        onView(withId(R.id.photo_card_view)).perform(click()) // Открываю галерею
        onView(withId(R.id.navigation_tb)).check(matches(isDisplayed())) // Подтверждаю, что галерея открыта

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GalleryImageAdapter.GalleryViewHolder>(0, click())
        )// Открываю edit-экран
        onView(withId(R.id.photo_editor_view)).check(matches(isDisplayed())) // Подтверждаю, что edit-экран открыт

        onView(withId(R.id.save_btn)).perform(click()) // Открываю save-экран
/*
        onView(isRoot()).perform(waitUntilShow(R.id.savedImagePanel) { view -> view.check(matches(isDisplayed())) })// Жду не более 2 секунд для сохранения фотографии
*/ // Не взлетает(

        onView(isRoot()).perform(waitFor(2000))// 2 секунд обычно хватает для сохранения фотографии
        onView(withId(R.id.savedImagePanel)).check(matches(isDisplayed())) // Подтверждаю, что save-экран открыт
    }

    private fun waitFor(delay: Long): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isRoot()
        override fun getDescription(): String = "wait for $delay milliseconds"
        override fun perform(uiController: UiController, v: View?) = uiController.loopMainThreadForAtLeast(delay)
    }

    private fun waitUntilShow(id: Int, block: (ViewInteraction) -> Unit): ViewAction {

        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for view is show"
            override fun perform(uiController: UiController, v: View?) {
                var isShow = false

                for (i in 0 until 5) {
                    try {
                        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
                        isShow = true
                        break
                    } catch (ex: Exception) {
                        val sleepTime = TimeUnit.SECONDS.toMillis(1)
                        uiController.loopMainThreadForAtLeast(sleepTime)
                        Thread.sleep(sleepTime)
                    }
                }

                if (isShow) {
                    block.invoke(Espresso.onView(ViewMatchers.withId(id)))
                } else {
                    throw NoMatchingViewException.Builder().build()
                }
            }
        }
    }

}
