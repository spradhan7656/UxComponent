package com.spradhan.uxcomponentLib

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import kotlin.math.abs

enum class SnackbarPosition {
    TOP,
    BOTTOM
}

enum class SnackbarType{
    ERROR,
    SUCCESS,
    DEFAULT
}


enum class SnackbarDuration(val millis: Long) {
    SHORT(2000L),
    LONG(3500L),
    INDEFINITE(-1L)
}

data class SnackbarConfig(
    // Message
    val message: CharSequence = "",
    @ColorInt val messageColor: Int = Color.WHITE,
    val messageTextSizeSp: Float = 14f,
    val maxLines: Int = 3,

    // Type
    val type: SnackbarType = SnackbarType.DEFAULT,

    // Action button (e.g. "UNDO")
    val actionText: CharSequence? = null,
    @ColorInt val actionTextColor: Int = Color.YELLOW,
    val onActionClick: (() -> Unit)? = null,

    // Leading icon
    @DrawableRes val iconRes: Int? = null,
    @ColorInt val iconTint: Int? = null,
    val iconSizeDp: Float = 20f,

    // Background - reuses your existing GradientConfig + CommonUtils
    val gradientConfig: GradientConfig = GradientConfig(
        colors = intArrayOf(0xFF323232.toInt(), 0xFF323232.toInt())
    ),

    // Optional glow effect around the bar (uses CommonUtils.createGlowDrawable)
    @ColorInt val glowColor: Int? = null,
    val glowSizeDp: Float = 12f,

    // Layout / positioning
    val position: SnackbarPosition = SnackbarPosition.BOTTOM,
    val marginDp: Float = 12f,
    val paddingHorizontalDp: Float = 16f,
    val paddingVerticalDp: Float = 12f,
    val elevationDp: Float = 6f,

    val marginTop: Float = 12f,
    val marginBottom: Float = 12f,
    val marginHorizontal : Float = 12f,

    // Progress
    val progressEnabled: Boolean = false,
    val progressMax: Int = 100,
    val progressInitial: Int = 0,



    // Timing
    val duration: SnackbarDuration = SnackbarDuration.SHORT,
    val customDurationMillis: Long? = null,
    val animationDurationMillis: Long = 250L,

    // Interaction
    val swipeToDismissEnabled: Boolean = true,
    val dismissOnClick: Boolean = false,

    // Lifecycle callbacks
    val onShown: (() -> Unit)? = null,
    val onDismissed: (() -> Unit)? = null
)


class SnackbarBuilder(private val activity: Activity) {

    private var config = SnackbarConfig()

    fun message(text: CharSequence) = apply { config = config.copy(message = text) }

    fun messageColor(@ColorInt color: Int) = apply { config = config.copy(messageColor = color) }

    fun messageTextSize(sp: Float) = apply { config = config.copy(messageTextSizeSp = sp) }

    fun maxLines(lines: Int) = apply { config = config.copy(maxLines = lines) }

    fun type(type: SnackbarType) = apply {
        config = config.copy(type = type)
    }

    fun action(text: CharSequence, @ColorInt color: Int? = null, onClick: () -> Unit) = apply {
        config = config.copy(
            actionText = text,
            actionTextColor = color ?: config.actionTextColor,
            onActionClick = onClick
        )
    }

    fun icon(@DrawableRes resId: Int, @ColorInt tint: Int? = null, sizeDp: Float = config.iconSizeDp) = apply {
        config = config.copy(iconRes = resId, iconTint = tint, iconSizeDp = sizeDp)
    }

    fun gradient(gradientConfig: GradientConfig) = apply {
        config = config.copy(gradientConfig = gradientConfig)
    }

    /** Convenience for a simple flat/solid background without building a full GradientConfig. */
    fun solidColor(@ColorInt color: Int, cornerRadiusDp: Float = 8f) = apply {
        config = config.copy(
            gradientConfig = GradientConfig(
                solidColor = color,
                colors = null,
                cornerRadiusDp = cornerRadiusDp
            )
        )
    }


    fun progress(
        max: Int = 100,
        initial: Int = 0
    ) = apply {
        config = config.copy(
            progressEnabled = true,
            progressMax = max,
            progressInitial = initial.coerceIn(0, max)
        )
    }
    fun glow(@ColorInt color: Int, sizeDp: Float = 12f) = apply {
        config = config.copy(glowColor = color, glowSizeDp = sizeDp)
    }

    fun position(position: SnackbarPosition) = apply { config = config.copy(position = position) }

    fun margin(dp: Float) = apply { config = config.copy(marginDp = dp) }

    fun marginTop(dp: Float) = apply {
        config = config.copy(marginTop = dp)
    }

    fun marginBottom(dp: Float) = apply {
        config = config.copy(marginBottom = dp)
    }

    fun marginHorizontal(dp: Float) = apply {
        config = config.copy(marginHorizontal = dp)
    }



    fun padding(horizontalDp: Float, verticalDp: Float) = apply {
        config = config.copy(paddingHorizontalDp = horizontalDp, paddingVerticalDp = verticalDp)
    }

    fun elevation(dp: Float) = apply { config = config.copy(elevationDp = dp) }

    fun duration(duration: SnackbarDuration) = apply { config = config.copy(duration = duration) }

    fun durationMillis(millis: Long) = apply { config = config.copy(customDurationMillis = millis) }

    fun animationDuration(millis: Long) = apply { config = config.copy(animationDurationMillis = millis) }

    fun swipeToDismiss(enabled: Boolean) = apply { config = config.copy(swipeToDismissEnabled = enabled) }

    fun dismissOnClick(enabled: Boolean) = apply { config = config.copy(dismissOnClick = enabled) }

    fun onShown(callback: () -> Unit) = apply { config = config.copy(onShown = callback) }

    fun onDismissed(callback: () -> Unit) = apply { config = config.copy(onDismissed = callback) }

    /** Builds the config into a [CustomSnackbar] without showing it. */
    fun build(): CustomSnackbar = CustomSnackbar.make(activity, config)

    /** Builds and immediately shows the snackbar. Returns the instance so callers can call [CustomSnackbar.dismiss] manually. */
    fun show(): CustomSnackbar {
        val snackbar = build()
        snackbar.show()
        return snackbar
    }
}

class CustomSnackbar private constructor(
    private val activity: Activity,
    private val config: SnackbarConfig
) {

    private var rootContainer: FrameLayout? = null
    private val handler = Handler(Looper.getMainLooper())
    private var dismissRunnable: Runnable? = null
    private var isDismissed = false
    private var progressBar: ProgressBar? = null

    companion object {
        // Only one instance shown at a time, mirroring native Snackbar behavior.
        @Volatile
        private var currentInstance: CustomSnackbar? = null

        fun make(activity: Activity, config: SnackbarConfig): CustomSnackbar {
            return CustomSnackbar(activity, config)
        }
    }

    private fun dp(value: Float): Int {
        val density = activity.resources.displayMetrics.density
        return (value * density).toInt()
    }

    fun show() {
        // Replace whatever snackbar is currently showing.
        currentInstance?.dismissImmediately()
        currentInstance = this

        val parent = findSuitableParent() ?: return

        val container = FrameLayout(activity)
        rootContainer = container

        val bar = buildBarView()

        container.addView(
            bar,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        container.layoutParams = buildContainerParams()
        parent.addView(container)

        animateIn(container)
        scheduleAutoDismiss()
    }

    fun dismiss() {
        if (isDismissed) return
        isDismissed = true
        dismissRunnable?.let { handler.removeCallbacks(it) }

        val container = rootContainer ?: return
        val translateTarget = exitTranslationY()

        container.animate()
            .alpha(0f)
            .translationY(translateTarget)
            .setDuration(config.animationDurationMillis)
            .withEndAction {
                removeFromParent()
                config.onDismissed?.invoke()
                if (currentInstance === this) currentInstance = null
            }
            .start()
    }

    // ---- View construction -------------------------------------------------



    private fun buildBarView(): LinearLayout {

        // Outer view owns the background
        val outer = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            background = buildBackground()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = dp(config.elevationDp).toFloat()
            }

            // This is the actual snackbar padding
            setPadding(
                dp(config.paddingHorizontalDp),
                dp(config.paddingVerticalDp),
                dp(config.paddingHorizontalDp),
                dp(config.paddingVerticalDp)
            )
        }

        // Content row
        val contentRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Icon
        config.iconRes?.let { res ->
            contentRow.addView(buildIconView(res))
        }

        // Message
        contentRow.addView(
            buildMessageView().apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }
        )

        // Action
        config.actionText?.let { text ->
            contentRow.addView(buildActionView(text))
        }

        outer.addView(contentRow)

        // Progress
        if (config.progressEnabled) {

            progressBar = buildProgressBar()

            outer.addView(
                progressBar,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(5f)
                ).apply {
                    topMargin = dp(10f)
                }
            )
        }

        // Click dismiss
        if (config.dismissOnClick) {
            outer.setOnClickListener {
                dismiss()
            }
        }

        // Swipe dismiss
        if (config.swipeToDismissEnabled) {
            attachSwipeToDismiss(outer)
        }

        return outer
    }







    private fun buildProgressBar(): ProgressBar {
        return ProgressBar(
            activity,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {

            max = config.progressMax
            progress = config.progressInitial
            isIndeterminate = false

            progressDrawable = createProgressDrawable()
        }
    }


    private fun createProgressDrawable(): Drawable {

        val track = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(3f).toFloat()
            setColor(0x33FFFFFF)
        }

        val progress = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(3f).toFloat()
            setColor(0xFF4FC3F7.toInt())
        }

        return LayerDrawable(
            arrayOf(track, progress)
        ).apply {
            setId(0, android.R.id.background)
            setId(1, android.R.id.progress)
        }
    }


    fun updateProgress(progress: Int) {
        val target = progress.coerceIn(0, config.progressMax)

        progressBar?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.setProgress(target, true)
            } else {
                it.progress = target
            }
        }
    }
    private fun buildBackground(): android.graphics.drawable.Drawable {
        val backgroundConfig = when (config.type) {
            SnackbarType.ERROR -> GradientConfig(
                solidColor = Color.rgb(211, 47, 47),
                colors = null,
                cornerRadiusDp = 12f
            )

            SnackbarType.SUCCESS -> GradientConfig(
                solidColor = Color.rgb(46, 125, 50),
                colors = null,
                cornerRadiusDp = 12f
            )

            SnackbarType.DEFAULT -> config.gradientConfig
        }

        val gradientDrawable = CommonUtils.createDynamicGradient(
            activity,
            backgroundConfig
        )

        val glowColor = config.glowColor ?: return gradientDrawable

        return CommonUtils.createGlowDrawable(
            background = gradientDrawable,
            glowColor = glowColor,
            glowSize = dp(config.glowSizeDp).toFloat(),
            cornerRadius = resolveCornerRadiusPx()
        )
    }

    private fun buildIconView(resId: Int): ImageView {
        return ImageView(activity).apply {
            setImageResource(resId)
            config.iconTint?.let { tint -> setColorFilter(tint, PorterDuff.Mode.SRC_IN) }
            layoutParams = LinearLayout.LayoutParams(
                dp(config.iconSizeDp),
                dp(config.iconSizeDp)
            ).apply { marginEnd = dp(8f) }
        }
    }

    private fun buildMessageView(): TextView {
        return TextView(activity).apply {
            text = config.message
            setTextColor(config.messageColor)
            textSize = config.messageTextSizeSp
            maxLines = config.maxLines
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
    }

    private fun buildActionView(text: CharSequence): TextView {
        return TextView(activity).apply {
            this.text = text
            setTextColor(config.actionTextColor)
            textSize = config.messageTextSizeSp
            setPadding(dp(12f), 0, 0, 0)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                config.onActionClick?.invoke()
                dismiss()
            }
        }
    }

    private fun buildContainerParams(): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = if (config.position == SnackbarPosition.BOTTOM) {
                Gravity.BOTTOM
            } else {
                Gravity.TOP
            }

            val horizontalMargin = dp(config.marginHorizontal)

            leftMargin = horizontalMargin
            rightMargin = horizontalMargin

            topMargin = dp(config.marginTop)
            bottomMargin = dp(config.marginBottom)
        }
    }

    // ---- Animation -----------------------------------------------------------

    private fun entryTranslationY(): Float =
        if (config.position == SnackbarPosition.BOTTOM) dp(40f).toFloat() else -dp(40f).toFloat()

    private fun exitTranslationY(): Float = entryTranslationY()

    private fun animateIn(container: FrameLayout) {
        container.alpha = 0f
        container.translationY = entryTranslationY()

        container.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(config.animationDurationMillis)
            .withEndAction { config.onShown?.invoke() }
            .start()
    }

    // ---- Auto dismiss ----------------------------------------------------------

    private fun scheduleAutoDismiss() {
        val durationMillis = config.customDurationMillis ?: config.duration.millis
        if (durationMillis > 0) {
            val runnable = Runnable { dismiss() }
            dismissRunnable = runnable
            handler.postDelayed(runnable, durationMillis)
        }
    }

    private fun restartAutoDismissTimer() {
        dismissRunnable?.let { handler.removeCallbacks(it) }
        scheduleAutoDismiss()
    }

    // ---- Swipe to dismiss --------------------------------------------------

    private fun attachSwipeToDismiss(view: View) {
        var downX = 0f
        var velocityTracker: VelocityTracker? = null

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    velocityTracker = VelocityTracker.obtain().apply { addMovement(event) }
                    dismissRunnable?.let { handler.removeCallbacks(it) }
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    velocityTracker?.addMovement(event)
                    val deltaX = event.rawX - downX
                    v.translationX = deltaX
                    val width = v.width.takeIf { it > 0 } ?: 1
                    v.alpha = 1f - (abs(deltaX) / width).coerceIn(0f, 1f)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val deltaX = event.rawX - downX
                    velocityTracker?.recycle()
                    velocityTracker = null

                    val width = v.width.takeIf { it > 0 } ?: 1
                    if (abs(deltaX) > width / 4f) {
                        v.animate()
                            .translationX(if (deltaX > 0) width.toFloat() else -width.toFloat())
                            .alpha(0f)
                            .setDuration(150L)
                            .withEndAction { dismiss() }
                            .start()
                    } else {
                        v.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(150L)
                            .start()
                        restartAutoDismissTimer()
                    }
                    true
                }
                else -> false
            }
        }
    }





    // ---- Helpers ------------------------------------------------------------

    private fun resolveCornerRadiusPx(): Float {
        val gc = config.gradientConfig
        return when {
            gc.cornerRadiusDp > 0f -> dp(gc.cornerRadiusDp).toFloat()
            gc.cornerRadii != null && gc.cornerRadii.isNotEmpty() -> dp(gc.cornerRadii[0]).toFloat()
            else -> dp(8f).toFloat()
        }
    }

    private fun findSuitableParent(): ViewGroup? {
        val content = activity.findViewById<View>(android.R.id.content)
        return content as? ViewGroup
    }

    private fun dismissImmediately() {
        isDismissed = true
        dismissRunnable?.let { handler.removeCallbacks(it) }
        removeFromParent()
    }

    private fun removeFromParent() {
        val container = rootContainer ?: return
        (container.parent as? ViewGroup)?.removeView(container)
        rootContainer = null
    }
}
