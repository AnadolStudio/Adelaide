package com.anadolstudio.adelaide.helpers

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.SELECT_ITEM
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class FirebaseHelper private constructor() {
    companion object {
        private const val REPLACE_NAME = "name"
        private var firebaseHelper: FirebaseHelper? = null
        private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

        fun get(): FirebaseHelper {
            return firebaseHelper ?: let {
                firebaseHelper = FirebaseHelper()
                firebaseHelper!!
            }
        }

        private val TAG: String = FirebaseHelper::class.java.name
    }

    private var lastItem = ""


    fun logEvent(event: Event) {
        return // TODO открой в релизе
        val name = event.name.lowercase()
        firebaseAnalytics.logEvent(name) {
            param(name, name)
        }
    }

    fun logEvent(event: Event, item: String) {
        return // TODO открой в релизе
        lastItem = item
        val name = event.name.lowercase().replace(REPLACE_NAME, item)
        firebaseAnalytics.logEvent(SELECT_ITEM) {
            param(name, name)
        }
        if (event.name == "NAME_APPLY") lastItem = ""
    }

    fun getNameFromPath(path: String): String {
        var result = path.substring(path.lastIndexOf("/") + 1)
        result = result.replace("[()[.png]]".toRegex(), "")
        result = result.replace("[ \\-]".toRegex(), "_")
        Log.d(TAG, "getNameFromPath: $result")
        return result
    }

    enum class Mode_EDIT {
        MODE_MAIN, MODE_COLLAGE, MODE_FRAME, MODE_CUT, MODE_EFFECT, MODE_FILTER, MODE_TEXT, MODE_STICKER, MODE_UPGRADE, MODE_BRUSH, MODE_CROP, MODE_TURN
    }

    fun firebaseEvent(currentMode: Mode_EDIT) {
        val item = get().getNameLastItem()
        Log.d(TAG, "firebaseEvent: +$item");

        when (currentMode) {
            Mode_EDIT.MODE_COLLAGE ->
                if (item.isNotEmpty())
                    get().logEvent(Event.COLLAGE_NAME_APPLY, item)
            Mode_EDIT.MODE_FRAME ->
                if (item.isNotEmpty())
                    get().logEvent(Event.FRAME_NAME_APPLY, item);
            Mode_EDIT.MODE_CUT ->
                if (item.isNotEmpty())
                    get().logEvent(Event.BACKGROUND_OPEN)
            Mode_EDIT.MODE_FILTER ->
                if (item.isNotEmpty())
                    get().logEvent(Event.FILTER_NAME_APPLY, item);
            Mode_EDIT.MODE_EFFECT ->
                if (item.isNotEmpty())
                    get().logEvent(Event.EFFECT_NAME_APPLY, item);
            Mode_EDIT.MODE_STICKER ->
                if (item.isNotEmpty())
                    get().logEvent(Event.STICKER_NAME_APPLY, item);
            Mode_EDIT.MODE_TEXT -> get().logEvent(Event.TEXT_APPLY)
            Mode_EDIT.MODE_TURN -> get().logEvent(Event.TURN_APPLY)
            Mode_EDIT.MODE_BRUSH -> get().logEvent(Event.DRAW_APPLY)
            Mode_EDIT.MODE_CROP ->
                if (item.isNotEmpty()) {
                    get().logEvent(Event.CROP_NAME_APPLY, item)
                    get().logEvent(Event.CROP_APPLY)
                }
            else -> Log.d(TAG, "firebaseEvent: NOTHING");

        }
    }

    fun getNameLastItem(): String {
        return lastItem
    }

    enum class Event {
        // TODO Сделай по людски
        PHOTO_EDIT_OPEN,
        COLLAGE_EDIT_OPEN,
        PAYWALL_OPEN,
        FRAMES_OPEN,
        FILTERS_OPEN,
        EFFECTS_OPEN,
        TEXT_OPEN,
        STIKERS_OPEN,
        ENHACE_OPEN,
        DRAW_OPEN,
        CROP_OPEN,
        TURN_OPEN,
        BACKGROUND_OPEN,
        BACKGROUND_CLOSE,
        BACKGROUND_NAME_OPEN,
        BACKGROUND_CUSTOM_OPEN,
        BACKGROUND_COLOR_OPEN,
        FRAME_NAME_OPEN,
        FRAME_NAME_APPLY,
        FRAME_CANCEL,
        FILTER_NAME_OPEN,
        FILTER_NAME_APPLY,
        FILTER_CANCEL,
        TEXT_RIGHT_OPEN,
        TEXT_COLOR_OPEN,
        TEXT_ALIGNMENT_OPEN,
        TEXT_APPLY,
        TEXT_CANCEL,
        STICKER_NAME_OPEN,
        STICKER_NAME_APPLY,
        STICKER_CANCEL,
        BRIGHTNESS_OPEN,
        CONTRAST_OPEN,
        SATURATION_OPEN,
        BLUR_OPEN,
        BRIGHTNESS_APPLY,
        CONTRAST_APPLY,
        SATURATION_APPLY,
        BLUR_APPLY,
        ENHACE_CANCEL,
        ERASER_OPEN,
        DRAW_COLOR_OPEN,
        DRAW_APPLY,
        DRAW_CANCEL,
        CROP_NAME_OPEN,
        CROP_NAME_APPLY,
        CROP_APPLY,
        CROP_CANCEL,
        TURN_VERTICAL_OPEN,
        TURN_HORIZONTAL_OPEN,
        TURN_RIGHT_OPEN,
        TURN_LEFT_OPEN,
        TURN_APPLY,
        TURN_CANCEL,
        EFFECT_NAME_OPEN,
        EFFECT_NAME_APPLY,
        EFFECT_CANCEL,
        COLLAGE_NAME_OPEN,
        COLLAGE_NAME_APPLY,
        COLLAGE_OPEN,
        COLLAGE_CANCEL,
        PAY_SIX_MONTH,
        PAY_MONTH,
        PAY_YEAR,
        PAY_CANCEL,
        SAVE_PHOTO,
        SHARE_OTHERS,
        SHARE_VK,
        SHARE_INSTAGRAM,
        SHARE_FACEBOOK,
        SHARE_MESSENGER,
        SHARE_WHATS_APP,
        SHARE_TWITTER,
        BACK_TO_MAIN,
        BACK_TO_PHOTO,
    }

}

