package com.anadolstudio.compose.ui.drawable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.anadolstudio.compose.ui.R

/**
 * Licard illustrations, sorted alphabetically.
 *
 * Illustrations should be named in PascalCase.
 */
@Suppress("ForbiddenMethodCall")
object LicardIllustration {
    val Map: Painter @Composable get() = painterResource(R.drawable.image_onboarding_map)
    val Documents: Painter @Composable get() = painterResource(R.drawable.image_onboarding_documents)
    val Limits: Painter @Composable get() = painterResource(R.drawable.image_onboarding_limits)
    val Balance: Painter @Composable get() = painterResource(R.drawable.image_onboarding_balance)
    val PartnerBusiness: Painter @Composable get() = painterResource(R.drawable.image_onboarding_partner_business)
    val PartnerEmployees: Painter @Composable get() = painterResource(R.drawable.image_onboarding_partner_employees)
    val Payment: Painter @Composable get() = painterResource(R.drawable.image_onboarding_payments)
    val VirtualCard: Painter @Composable get() = painterResource(R.drawable.image_onboarding_virtual_card)
    val LukoilApp: Painter @Composable get() = painterResource(R.drawable.image_onboarding_lukoil_app)
    val Empty: Painter @Composable get() = painterResource(R.drawable.icon_empty)
    val SearchEmpty: Painter @Composable get() = painterResource(R.drawable.icon_search_empty)
    val EmptyTransaction: Painter @Composable get() = painterResource(R.drawable.icon_empty_transaction)
    val Error: Painter @Composable get() = painterResource(R.drawable.icon_error)
    val LukoilLogo: Painter @Composable get() = painterResource(R.drawable.icon_lukoil)
    val Placeholder: Painter @Composable get() = painterResource(R.drawable.image_placeholder)
    val ReportSuccess: Painter @Composable get() = painterResource(R.drawable.icon_report_success)
    val Contacts: Painter @Composable get() = painterResource(R.drawable.image_contacts)
    val AddContact: Painter @Composable get() = painterResource(R.drawable.image_add_contact)
    val Contracts: Painter @Composable get() = painterResource(R.drawable.image_contracts)
    val EmptyAccounts: Painter @Composable get() = painterResource(R.drawable.image_empty_accounts)
    val File: Painter @Composable get() = painterResource(R.drawable.image_file)
}
