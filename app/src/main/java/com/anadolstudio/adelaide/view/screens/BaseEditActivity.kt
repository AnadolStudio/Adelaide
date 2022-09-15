package com.anadolstudio.adelaide.view.screens

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.activity.BaseActivity
import com.anadolstudio.core.activity.SingleErrorDialog
import com.anadolstudio.core.activity.SingleErrorSnack
import com.anadolstudio.core.activity.SingleMessageDialog
import com.anadolstudio.core.activity.SingleMessageSnack
import com.anadolstudio.core.activity.SingleMessageToast
import com.anadolstudio.core.common_extention.startAppSettingsActivity
import com.anadolstudio.core.livedata.SingleCustomEvent
import com.anadolstudio.core.livedata.SingleError
import com.anadolstudio.core.livedata.SingleEvent
import com.anadolstudio.core.livedata.SingleMessage
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

open class BaseEditActivity : BaseActivity() {

    override fun showError(event: SingleError) {
        val error = event.error

        when (event) {
            is SingleErrorSnack -> showSnack(error.message.orEmpty())
            is SingleErrorDialog -> showErrorDialog(event)
        }
    }

    override fun defaultErrorMessage(): String = getString(R.string.edit_error_failed_save_image)

    override fun showMessage(event: SingleMessage) {
        val text = event.message ?: defaultErrorMessage()

        when (event) {
            is SingleMessageSnack -> showToast(text)
            is SingleMessageToast -> showSnack(text)
        }
    }

    protected fun showToast(@StringRes stringRes: Int) = showToast(getString(stringRes))

    protected fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    protected fun showSnack(text: String) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show()
    }

    override fun showMessageDialog(event: SingleMessageDialog) {
        when (event) {
            is SingleMessageDialog.Information -> showInformationDialog()
            is SingleMessageDialog.Choice -> showChoiceDialog()
        }
    }

    private fun showChoiceDialog() {
        TODO("Not yet implemented")
    }

    private fun showInformationDialog() {
        TODO("Not yet implemented")
    }

    override fun showErrorDialog(event: SingleErrorDialog) {
        TODO("Not yet implemented")
    }

    fun showSettingsSnackbar() = Snackbar
            .make(
                    findViewById(android.R.id.content),
                    getText(R.string.gallery_error_miss_permission),
                    BaseTransientBottomBar.LENGTH_INDEFINITE
            )
            .setAction(R.string.gallery_snack_bar_settings) { startAppSettingsActivity() }
            .show()

    override fun handleEvent(event: SingleEvent) = when (event) {
        is SingleMessage -> showMessage(event)
        is SingleError -> showError(event)
        is SingleCustomEvent -> {}
    }

    // TODO Temp

    fun replaceFragment(fragment: Fragment, containerId: Int) {
        replaceFragment(fragment, containerId, true)
    }

    fun replaceFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean) {

        val transaction = supportFragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)

        if (addToBackStack) transaction.addToBackStack(fragment.javaClass.name)

        transaction.commit()
    }

    private fun addFragment(fragment: Fragment, containerId: Int) {

        supportFragmentManager.beginTransaction()
                .add(containerId, fragment)
                .commit()
    }
}
