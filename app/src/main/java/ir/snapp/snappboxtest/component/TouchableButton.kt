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

    private var levelIncrement = Constants.LEVEL_INCREMENT
    private var mAnimator: TimeAnimator? = null
    private var mCurrentLevel = 0
    private var mClipDrawable: ClipDrawable? = null

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

    private fun fillFromCenter() {
        background =
            ContextCompat.getDrawable(context, R.drawable.background_button_centeric)
        val layerDrawable = background as LayerDrawable
        mClipDrawable =
            layerDrawable.findDrawableByLayerId(R.id.clip_drawable_centric) as ClipDrawable

        levelIncrement = Constants.LEVEL_INCREMENT_CLICK
        animateButton()
    }

    private fun fillFromRight() {
        background = ContextCompat.getDrawable(context, R.drawable.background_button)
        val layerDrawable = background as LayerDrawable
        mClipDrawable =
            layerDrawable.findDrawableByLayerId(R.id.clip_drawable) as ClipDrawable

        levelIncrement = Constants.LEVEL_INCREMENT
        animateButton()
    }

    override fun onTimeUpdate(animation: TimeAnimator?, totalTime: Long, deltaTime: Long) {
        mClipDrawable?.level = mCurrentLevel
        if (mCurrentLevel >= Constants.MAX_LEVEL)
            mAnimator?.cancel()
        else mCurrentLevel =
            Constants.MAX_LEVEL.coerceAtMost(mCurrentLevel + levelIncrement)
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
}