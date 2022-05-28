package com.anadolstudio.adelaide

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.anadolstudio.adelaide.view.screens.gallery.GalleryImageAdapter
import com.anadolstudio.adelaide.view.screens.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ScreenTest {

    @get:Rule
    val activityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun showMainActivity() {
        onView(withId(R.id.photo_card_view)).perform(click()) // Открываю галерею
        onView(withId(R.id.navigation_tb)).check(matches(isDisplayed())) // Подтверждаю, что галерея открыта

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GalleryImageAdapter.GalleryViewHolder>(0, click())
        )// Открываю edit-экран
        onView(withId(R.id.photo_editor_view)).check(matches(isDisplayed())) // Подтверждаю, что edit-экран открыт

        /*onView(withId(R.id.save_btn)).perform(click()) // Открываю save-экран
        onView(withId(R.id.savedImage)).check(matches(isDisplayed())) // Подтверждаю, что save-экран открыт*/
    }
}