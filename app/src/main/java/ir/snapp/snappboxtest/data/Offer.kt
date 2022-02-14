package ir.snapp.snappboxtest.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Represents the offer that the user receives.
 */
@Parcelize
data class Offer(
    val origin: Location,
    val destination: Location,
    val price: Int
) : Parcelable
