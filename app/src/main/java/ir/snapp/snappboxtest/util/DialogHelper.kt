package ir.snapp.snappboxtest.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import ir.snapp.snappboxtest.R

object DialogHelper {

    inline fun showInfoMessage(context: Context, crossinline action: () -> Unit) {
        val builder = AlertDialog.Builder(context/*, R.style.AlertDialogCustom*/).apply {
            setMessage(R.string.alert_dialog_overlay_permission_text)
            setPositiveButton(R.string.alert_dialog_positive_button_text) { _, _ -> action() }
            setCancelable(false)
        }

        val dialog = builder.create()
        dialog.show()

//        val msgTxt = dialog.findViewById<TextView>(android.R.id.message)
//        msgTxt?.typeface =
//            Typeface.createFromAsset(context.assets, Constants.IRAN_SANS_REGULAR_FONT)
    }
}