package ir.snapp.snappboxtest.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize


/**
 * Represents the location of origin and destination in an [Offer] instance.
 */
@Parcelize
data class Location(
    val latLng: LatLng,
    val address: String
) : Parcelable
