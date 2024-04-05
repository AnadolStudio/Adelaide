package com.anadolstudio.compose.ui.drawable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.anadolstudio.compose.ui.R.drawable.ic_find
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_92
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_92_plus
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_95
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_95_plus
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_98
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_98_plus
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_diesel
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_diesel_plus
import com.anadolstudio.compose.ui.R.drawable.ic_fuel_gas
import com.anadolstudio.compose.ui.R.drawable.ic_goods_adblue
import com.anadolstudio.compose.ui.R.drawable.ic_goods_auto_chemical
import com.anadolstudio.compose.ui.R.drawable.ic_goods_auto_goods
import com.anadolstudio.compose.ui.R.drawable.ic_goods_oil
import com.anadolstudio.compose.ui.R.drawable.ic_goods_provisions
import com.anadolstudio.compose.ui.R.drawable.ic_service_auto
import com.anadolstudio.compose.ui.R.drawable.ic_service_bridge
import com.anadolstudio.compose.ui.R.drawable.ic_service_carwash
import com.anadolstudio.compose.ui.R.drawable.ic_service_ferry
import com.anadolstudio.compose.ui.R.drawable.ic_service_food
import com.anadolstudio.compose.ui.R.drawable.ic_service_hotel
import com.anadolstudio.compose.ui.R.drawable.ic_service_parking
import com.anadolstudio.compose.ui.R.drawable.ic_service_tire_fit
import com.anadolstudio.compose.ui.R.drawable.ic_service_toll_road
import com.anadolstudio.compose.ui.R.drawable.ic_service_tunnel
import com.anadolstudio.compose.ui.R.drawable.icon_account
import com.anadolstudio.compose.ui.R.drawable.icon_active_filter
import com.anadolstudio.compose.ui.R.drawable.icon_arrow_down
import com.anadolstudio.compose.ui.R.drawable.icon_arrow_right
import com.anadolstudio.compose.ui.R.drawable.icon_back
import com.anadolstudio.compose.ui.R.drawable.icon_backspace
import com.anadolstudio.compose.ui.R.drawable.icon_basket
import com.anadolstudio.compose.ui.R.drawable.icon_basket_in_circle
import com.anadolstudio.compose.ui.R.drawable.icon_bell
import com.anadolstudio.compose.ui.R.drawable.icon_canister
import com.anadolstudio.compose.ui.R.drawable.icon_card
import com.anadolstudio.compose.ui.R.drawable.icon_card_circle
import com.anadolstudio.compose.ui.R.drawable.icon_case
import com.anadolstudio.compose.ui.R.drawable.icon_cashier
import com.anadolstudio.compose.ui.R.drawable.icon_check
import com.anadolstudio.compose.ui.R.drawable.icon_check_mark
import com.anadolstudio.compose.ui.R.drawable.icon_checkbox_disabled
import com.anadolstudio.compose.ui.R.drawable.icon_checkbox_enabled
import com.anadolstudio.compose.ui.R.drawable.icon_circle_heart
import com.anadolstudio.compose.ui.R.drawable.icon_clock
import com.anadolstudio.compose.ui.R.drawable.icon_close
import com.anadolstudio.compose.ui.R.drawable.icon_contacts
import com.anadolstudio.compose.ui.R.drawable.icon_contract
import com.anadolstudio.compose.ui.R.drawable.icon_crown
import com.anadolstudio.compose.ui.R.drawable.icon_dialog_close
import com.anadolstudio.compose.ui.R.drawable.icon_download
import com.anadolstudio.compose.ui.R.drawable.icon_edit
import com.anadolstudio.compose.ui.R.drawable.icon_empty_ais_list
import com.anadolstudio.compose.ui.R.drawable.icon_empty_support_list
import com.anadolstudio.compose.ui.R.drawable.icon_eye
import com.anadolstudio.compose.ui.R.drawable.icon_eye_close
import com.anadolstudio.compose.ui.R.drawable.icon_filled_heart
import com.anadolstudio.compose.ui.R.drawable.icon_filter
import com.anadolstudio.compose.ui.R.drawable.icon_forward
import com.anadolstudio.compose.ui.R.drawable.icon_fuel_card
import com.anadolstudio.compose.ui.R.drawable.icon_gold_heart
import com.anadolstudio.compose.ui.R.drawable.icon_gold_star
import com.anadolstudio.compose.ui.R.drawable.icon_heart
import com.anadolstudio.compose.ui.R.drawable.icon_horizontal_more
import com.anadolstudio.compose.ui.R.drawable.icon_invoice
import com.anadolstudio.compose.ui.R.drawable.icon_localize
import com.anadolstudio.compose.ui.R.drawable.icon_lock_orange
import com.anadolstudio.compose.ui.R.drawable.icon_lukoil_logo_red
import com.anadolstudio.compose.ui.R.drawable.icon_mail
import com.anadolstudio.compose.ui.R.drawable.icon_minus_square
import com.anadolstudio.compose.ui.R.drawable.icon_mobile
import com.anadolstudio.compose.ui.R.drawable.icon_more
import com.anadolstudio.compose.ui.R.drawable.icon_phone
import com.anadolstudio.compose.ui.R.drawable.icon_pin
import com.anadolstudio.compose.ui.R.drawable.icon_plastic_card
import com.anadolstudio.compose.ui.R.drawable.icon_plus
import com.anadolstudio.compose.ui.R.drawable.icon_profile
import com.anadolstudio.compose.ui.R.drawable.icon_qr
import com.anadolstudio.compose.ui.R.drawable.icon_qr_code
import com.anadolstudio.compose.ui.R.drawable.icon_red_star
import com.anadolstudio.compose.ui.R.drawable.icon_remove
import com.anadolstudio.compose.ui.R.drawable.icon_route
import com.anadolstudio.compose.ui.R.drawable.icon_search
import com.anadolstudio.compose.ui.R.drawable.icon_search_black
import com.anadolstudio.compose.ui.R.drawable.icon_secondary_close
import com.anadolstudio.compose.ui.R.drawable.icon_select_card
import com.anadolstudio.compose.ui.R.drawable.icon_service_default
import com.anadolstudio.compose.ui.R.drawable.icon_square_checkbox_disabled
import com.anadolstudio.compose.ui.R.drawable.icon_square_checkbox_enabled
import com.anadolstudio.compose.ui.R.drawable.icon_star
import com.anadolstudio.compose.ui.R.drawable.icon_station
import com.anadolstudio.compose.ui.R.drawable.icon_statistics
import com.anadolstudio.compose.ui.R.drawable.icon_support
import com.anadolstudio.compose.ui.R.drawable.icon_survey
import com.anadolstudio.compose.ui.R.drawable.icon_update
import com.anadolstudio.compose.ui.R.drawable.icon_vertical_more
import com.anadolstudio.compose.ui.R.drawable.icon_virtual_card
import com.anadolstudio.compose.ui.R.drawable.icon_wallet
import com.anadolstudio.compose.ui.R.drawable.icon_warning

/**
 * Licard icons, sorted alphabetically.
 *
 * Icons should be named in PascalCase.
 */
object LicardIcon {
    val Account: Painter @Composable get() = painterResource(icon_account)
    val ActiveFilter: Painter @Composable get() = painterResource(icon_active_filter)
    val ArrowDown: Painter @Composable get() = painterResource(icon_arrow_down)
    val ArrowRight: Painter @Composable get() = painterResource(icon_arrow_right)
    val Back: Painter @Composable get() = painterResource(icon_back)
    val Backspace: Painter @Composable get() = painterResource(icon_backspace)
    val Basket: Painter @Composable get() = painterResource(icon_basket)
    val BasketInCircle: Painter @Composable get() = painterResource(icon_basket_in_circle)
    val Bell: Painter @Composable get() = painterResource(icon_bell)
    val Canister: Painter @Composable get() = painterResource(icon_canister)
    val Card: Painter @Composable get() = painterResource(icon_card)
    val CardInCircle: Painter @Composable get() = painterResource(icon_card_circle)
    val Case: Painter @Composable get() = painterResource(icon_case)
    val Cashier: Painter @Composable get() = painterResource(icon_cashier)
    val Check: Painter @Composable get() = painterResource(icon_check)
    val CheckBoxDisabled: Painter @Composable get() = painterResource(icon_checkbox_disabled)
    val CheckBoxEnabled: Painter @Composable get() = painterResource(icon_checkbox_enabled)
    val CheckMark: Painter @Composable get() = painterResource(icon_check_mark)
    val CircleHeart: Painter @Composable get() = painterResource(icon_circle_heart)
    val Clock: Painter @Composable get() = painterResource(icon_clock)
    val Close: Painter @Composable get() = painterResource(icon_close)
    val Contacts: Painter @Composable get() = painterResource(icon_contacts)
    val Contract: Painter @Composable get() = painterResource(icon_contract)
    val Crown: Painter @Composable get() = painterResource(icon_crown)
    val DialogClose: Painter @Composable get() = painterResource(icon_dialog_close)
    val Download: Painter @Composable get() = painterResource(icon_download)
    val Edit: Painter @Composable get() = painterResource(icon_edit)
    val EmptyAisList: Painter @Composable get() = painterResource(icon_empty_ais_list)
    val EmptySupportList: Painter @Composable get() = painterResource(icon_empty_support_list)
    val Eye: Painter @Composable get() = painterResource(icon_eye)
    val EyeClosed: Painter @Composable get() = painterResource(icon_eye_close)
    val FilledHeart: Painter @Composable get() = painterResource(icon_filled_heart)
    val Filter: Painter @Composable get() = painterResource(icon_filter)
    val Find: Painter @Composable get() = painterResource(ic_find)
    val Forward: Painter @Composable get() = painterResource(icon_forward)
    val Fuel92: Painter @Composable get() = painterResource(ic_fuel_92)
    val Fuel92Plus: Painter @Composable get() = painterResource(ic_fuel_92_plus)
    val Fuel95: Painter @Composable get() = painterResource(ic_fuel_95)
    val Fuel95Plus: Painter @Composable get() = painterResource(ic_fuel_95_plus)
    val Fuel98: Painter @Composable get() = painterResource(ic_fuel_98)
    val Fuel98Plus: Painter @Composable get() = painterResource(ic_fuel_98_plus)
    val FuelCard: Painter @Composable get() = painterResource(icon_fuel_card)
    val FuelDiesel: Painter @Composable get() = painterResource(ic_fuel_diesel)
    val FuelDieselPlus: Painter @Composable get() = painterResource(ic_fuel_diesel_plus)
    val FuelGas: Painter @Composable get() = painterResource(ic_fuel_gas)
    val GoldHeart: Painter @Composable get() = painterResource(icon_gold_heart)
    val GoldStar: Painter @Composable get() = painterResource(icon_gold_star)
    val GoodsAdBlue: Painter @Composable get() = painterResource(ic_goods_adblue)
    val GoodsAuto: Painter @Composable get() = painterResource(ic_goods_auto_goods)
    val GoodsAutoChemical: Painter @Composable get() = painterResource(ic_goods_auto_chemical)
    val GoodsOil: Painter @Composable get() = painterResource(ic_goods_oil)
    val GoodsProvision: Painter @Composable get() = painterResource(ic_goods_provisions)
    val Heart: Painter @Composable get() = painterResource(icon_heart)
    val HorizontalMore: Painter @Composable get() = painterResource(icon_horizontal_more)
    val VerticalMore: Painter @Composable get() = painterResource(icon_vertical_more)
    val Invoice: Painter @Composable get() = painterResource(icon_invoice)
    val Localize: Painter @Composable get() = painterResource(icon_localize)
    val Lock: Painter @Composable get() = painterResource(icon_lock_orange)
    val LukoilLogo: Painter @Composable get() = painterResource(icon_lukoil_logo_red)
    val Mail: Painter @Composable get() = painterResource(icon_mail)
    val MinusSquare: Painter @Composable get() = painterResource(icon_minus_square)
    val More: Painter @Composable get() = painterResource(icon_more)
    val Phone: Painter @Composable get() = painterResource(icon_phone)
    val Pin: Painter @Composable get() = painterResource(icon_pin)
    val PlasticCard: Painter @Composable get() = painterResource(icon_plastic_card)
    val Plus: Painter @Composable get() = painterResource(icon_plus)
    val Profile: Painter @Composable get() = painterResource(icon_profile)
    val Qr: Painter @Composable get() = painterResource(icon_qr)
    val QrCode: Painter @Composable get() = painterResource(icon_qr_code)
    val RedStar: Painter @Composable get() = painterResource(icon_red_star)
    val Remove: Painter @Composable get() = painterResource(icon_remove)
    val Route: Painter @Composable get() = painterResource(icon_route)
    val Search: Painter @Composable get() = painterResource(icon_search)
    val SearchBlack: Painter @Composable get() = painterResource(icon_search_black)
    val SecondaryClose: Painter @Composable get() = painterResource(icon_secondary_close)
    val SelectCard: Painter @Composable get() = painterResource(icon_select_card)
    val ServiceAuto: Painter @Composable get() = painterResource(ic_service_auto)
    val ServiceBridge: Painter @Composable get() = painterResource(ic_service_bridge)
    val ServiceCarWash: Painter @Composable get() = painterResource(ic_service_carwash)
    val ServiceDefault: Painter @Composable get() = painterResource(icon_service_default)
    val ServiceFerry: Painter @Composable get() = painterResource(ic_service_ferry)
    val ServiceFood: Painter @Composable get() = painterResource(ic_service_food)
    val ServiceHotel: Painter @Composable get() = painterResource(ic_service_hotel)
    val ServiceParking: Painter @Composable get() = painterResource(ic_service_parking)
    val ServiceTire: Painter @Composable get() = painterResource(ic_service_tire_fit)
    val ServiceTollRoad: Painter @Composable get() = painterResource(ic_service_toll_road)
    val ServiceTunnel: Painter @Composable get() = painterResource(ic_service_tunnel)
    val SquareCheckboxDisabled: Painter @Composable get() = painterResource(icon_square_checkbox_disabled)
    val SquareCheckboxEnabled: Painter @Composable get() = painterResource(icon_square_checkbox_enabled)
    val Star: Painter @Composable get() = painterResource(icon_star)
    val Station: Painter @Composable get() = painterResource(icon_station)
    val Statistics: Painter @Composable get() = painterResource(icon_statistics)
    val SupportWasSent: Painter @Composable get() = painterResource(icon_support)
    val Survey: Painter @Composable get() = painterResource(icon_survey)
    val Update: Painter @Composable get() = painterResource(icon_update)
    val VirtualCard: Painter @Composable get() = painterResource(icon_virtual_card)
    val Wallet: Painter @Composable get() = painterResource(icon_wallet)
    val Warning: Painter @Composable get() = painterResource(icon_warning)
    val Mobile: Painter @Composable get() = painterResource(icon_mobile)
}
