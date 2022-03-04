package ir.snapp.snappboxtest.component

import android.animation.TimeAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import ir.snapp.snappboxtest.R
import ir.snapp.snappboxtest.util.Constants


/**
 * Implements an AppCompatButton that we can use to get touch events on it.
 */
class TouchableButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr), TimeAnimator.TimeListener {

    // region of properties
    private var levelIncrement = Constants.LEVEL_INCREMENT
    private var mAnimator: TimeAnimator? = null
    private var mCurrentLevel = 0
    private var mClipDrawable: ClipDrawable? = null

    var onLongClickListener: OnLongClickListener? = null

    // END of region of properties

    init {
        // Set up TimeAnimator to fire off
        mAnimator = TimeAnimator()
        mAnimator?.setTimeListener(this)

        fillFromRight()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                fillFromCenter()

                return true
            }

            MotionEvent.ACTION_UP -> {
                fillFromRight()

                return true
            }
        }
        return false
    }

    /**
     * Sets background of the button to fill it from center to sides.
     */
    private fun fillFromCenter() {
        background =
            ContextCompat.getDrawable(context, R.drawable.background_button_centeric)
        val layerDrawable = background as LayerDrawable
        mClipDrawable =
            layerDrawable.findDrawableByLayerId(R.id.clip_drawable_centric) as ClipDrawable

        levelIncrement = Constants.LEVEL_INCREMENT_CLICK
        animateButton()
    }

    /**
     * Sets background of the button to fill it from right to left.
     */
    private fun fillFromRight() {
        background = ContextCompat.getDrawable(context, R.drawable.background_button)
        val layerDrawable = background as LayerDrawable
        mClipDrawable =
            layerDrawable.findDrawableByLayerId(R.id.clip_drawable) as ClipDrawable

        levelIncrement = Constants.LEVEL_INCREMENT
        animateButton()
    }

    /**
     * Animates button timer progress by filling it from left to right or from center to sides.
     */
    private fun animateButton() {
        mCurrentLevel = 0
        mAnimator?.cancel()

        if (mAnimator?.isRunning == false) {
            mCurrentLevel = 0
            mAnimator?.start()
        }
    }

    override fun onTimeUpdate(animation: TimeAnimator?, totalTime: Long, deltaTime: Long) {
        // offer timer
        if (levelIncrement == Constants.LEVEL_INCREMENT) {
            mClipDrawable?.level = mCurrentLevel

            if (mCurrentLevel >= Constants.MAX_LEVEL) {
                mAnimator?.cancel()

            } else mCurrentLevel =
                Constants.MAX_LEVEL.coerceAtMost(mCurrentLevel + levelIncrement)
        } else { // click timer
            // times level increment 2 to fill the button in the half of time the activity should be finished
            mClipDrawable?.level = mCurrentLevel.times(2)

            if (mCurrentLevel >= Constants.MAX_LEVEL) {
                mAnimator?.cancel()
                // call onLongClickListener to finish MainActivity
                onLongClickListener?.onLongClickListener()
            } else mCurrentLevel =
                Constants.MAX_LEVEL.coerceAtMost(mCurrentLevel + levelIncrement)
        }
    }

    interface OnLongClickListener {
        /** calls when user long clicks on the button. */
        fun onLongClickListener() // to accept the offer and finish MainActivity
    }
}