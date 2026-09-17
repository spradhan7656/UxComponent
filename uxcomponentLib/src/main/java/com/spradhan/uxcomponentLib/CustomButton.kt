package com.spradhan.uxcomponentLib

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.spradhan.uxcomponentLib.databinding.AnimatedButtonBinding
import kotlin.math.max

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding =
        AnimatedButtonBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    private var buttonText = ""
    private var loadingText = "Please wait..."

    private var loading = false

    private var textColor = Color.WHITE
    private var loadingTextColor = Color.WHITE

    private var gradientStart = Color.TRANSPARENT
    private var gradientEnd = Color.TRANSPARENT

    private var backgroundColor = Color.TRANSPARENT

    private var cornerRadius = 0f

    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomRightRadius = 0f
    private var bottomLeftRadius = 0f

    private var strokeWidth = 0f
    private var strokeColor = Color.TRANSPARENT

    private var strokeDashWidth = 0f
    private var strokeDashGap = 0f

    private var gradientOrientation =
        GradientDrawable.Orientation.LEFT_RIGHT

    private var progressColor = Color.WHITE

    private var glowColor = Color.TRANSPARENT

    private var unpressedGlowSize = 0f

    private var pressedGlowSize = 0f

    private var isButtonPressed = false
    private var baseBackground: Drawable? = null

    init {

        readAttributes(context, attrs)

        applyBackground()
        setupGlowTouch()
        updateUI()
    }

    private fun readAttributes(
        context: Context,
        attrs: AttributeSet?
    ) {

        context.withStyledAttributes(
            attrs,
            R.styleable.CustomButton
        ) {

            buttonText =
                getString(
                    R.styleable.CustomButton_cb_text
                ).orEmpty()

            loadingText =
                getString(
                    R.styleable.CustomButton_cb_loadingText
                ) ?: "Please wait..."

            textColor =
                getColor(
                    R.styleable.CustomButton_cb_textColor,
                    Color.WHITE
                )



            loadingTextColor =
                getColor(
                    R.styleable.CustomButton_cb_loadingTextColor,
                    textColor
                )

            progressColor =
                getColor(
                    R.styleable.CustomButton_cb_progressColor,
                    Color.WHITE
                )

            binding.progressBar.indeterminateTintList =
                ColorStateList.valueOf(progressColor)

            gradientStart =
                getColor(
                    R.styleable.CustomButton_cb_gradientColorStart,
                    Color.TRANSPARENT
                )

            gradientEnd =
                getColor(
                    R.styleable.CustomButton_cb_gradientColorEnd,
                    Color.TRANSPARENT
                )

            backgroundColor =
                getColor(
                    R.styleable.CustomButton_cb_backgroundColor,
                    Color.TRANSPARENT
                )

            cornerRadius =
                getDimension(
                    R.styleable.CustomButton_cb_cornerRadius,
                    0f
                )

            topLeftRadius =
                getDimension(
                    R.styleable.CustomButton_cb_topLeftRadius,
                    0f
                )

            topRightRadius =
                getDimension(
                    R.styleable.CustomButton_cb_topRightRadius,
                    0f
                )

            bottomRightRadius =
                getDimension(
                    R.styleable.CustomButton_cb_bottomRightRadius,
                    0f
                )

            bottomLeftRadius =
                getDimension(
                    R.styleable.CustomButton_cb_bottomLeftRadius,
                    0f
                )

            strokeWidth =
                getDimension(
                    R.styleable.CustomButton_cb_strokeWidth,
                    0f
                )

            strokeColor =
                getColor(
                    R.styleable.CustomButton_cb_strokeColor,
                    Color.TRANSPARENT
                )

            strokeDashWidth =
                getDimension(
                    R.styleable.CustomButton_cb_strokeDashWidth,
                    0f
                )

            strokeDashGap =
                getDimension(
                    R.styleable.CustomButton_cb_strokeDashGap,
                    0f
                )

            val orientation =
                getInt(
                    R.styleable.CustomButton_cb_gradientOrientation,
                    0
                )

            glowColor =
                getColor(
                    R.styleable.CustomButton_cb_glowColor,
                    Color.TRANSPARENT
                )

            unpressedGlowSize =
                getDimension(
                    R.styleable.CustomButton_cb_unpressedGlowSize,
                    0f
                )

            pressedGlowSize =
                getDimension(
                    R.styleable.CustomButton_cb_pressedGlowSize,
                    0f
                )

            gradientOrientation =
                when (orientation) {

                    0 ->
                        GradientDrawable.Orientation.LEFT_RIGHT

                    1 ->
                        GradientDrawable.Orientation.RIGHT_LEFT

                    2 ->
                        GradientDrawable.Orientation.TOP_BOTTOM

                    3 ->
                        GradientDrawable.Orientation.BOTTOM_TOP

                    else ->
                        GradientDrawable.Orientation.LEFT_RIGHT
                }

            loading =
                getBoolean(
                    R.styleable.CustomButton_cb_loading,
                    false
                )

            // Icons
            val startIcon =
                getResourceId(
                    R.styleable.CustomButton_cb_startIcon,
                    0
                )

            if (startIcon != 0) {
                binding.btnStartIcon.setImageResource(startIcon)
            }

            val endIcon =
                getResourceId(
                    R.styleable.CustomButton_cb_endIcon,
                    0
                )

            if (endIcon != 0) {
                binding.btnEndIcon.setImageResource(endIcon)
            }

            val startIconTint = getColor(R.styleable.CustomButton_cb_startIconTint, Int.MAX_VALUE)
            if (startIconTint != Int.MAX_VALUE) {
                binding.btnStartIcon.imageTintList = ColorStateList.valueOf(startIconTint)
            }

            val endIconTint = getColor(R.styleable.CustomButton_cb_endIconTint, Int.MAX_VALUE)
            if (endIconTint != Int.MAX_VALUE) {
                binding.btnEndIcon.imageTintList = ColorStateList.valueOf(endIconTint)
            }

            val startIconSize = getDimensionPixelSize(R.styleable.CustomButton_cb_startIconSize, -1)
            if (startIconSize != -1) {
                val lp = binding.btnStartIcon.layoutParams
                lp.width = startIconSize
                lp.height = startIconSize
                binding.btnStartIcon.layoutParams = lp
            }

            val endIconSize = getDimensionPixelSize(R.styleable.CustomButton_cb_endIconSize, -1)
            if (endIconSize != -1) {
                val lp = binding.btnEndIcon.layoutParams
                lp.width = endIconSize
                lp.height = endIconSize
                binding.btnEndIcon.layoutParams = lp
            }

            val startIconMargin = getDimensionPixelSize(R.styleable.CustomButton_cb_startIconMargin, -1)
            if (startIconMargin != -1) {
                val lp = binding.btnStartIcon.layoutParams as android.widget.LinearLayout.LayoutParams
                lp.marginEnd = startIconMargin
                binding.btnStartIcon.layoutParams = lp
            }

            val endIconMargin = getDimensionPixelSize(R.styleable.CustomButton_cb_endIconMargin, -1)
            if (endIconMargin != -1) {
                val lp = binding.btnEndIcon.layoutParams as android.widget.LinearLayout.LayoutParams
                lp.marginStart = endIconMargin
                binding.btnEndIcon.layoutParams = lp
            }
        }
    }

    private fun setupGlowTouch() {

        binding.clButton.setOnTouchListener { _, event ->

            when (event.action) {

                android.view.MotionEvent.ACTION_DOWN -> {
                    if (!loading) {
                        isButtonPressed = true
                        applyGlow()
                    }
                }

                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    isButtonPressed = false
                    applyGlow()
                }
            }

            false
        }
    }

    private fun applyBackground() {

        val density =
            resources.displayMetrics.density

        val config =
            GradientConfig(

                shape =
                    GradientDrawable.RECTANGLE,

                colors =
                    if (
                        gradientStart != Color.TRANSPARENT ||
                        gradientEnd != Color.TRANSPARENT
                    ) {
                        intArrayOf(
                            gradientStart,
                            gradientEnd
                        )
                    } else {
                        null
                    },

                solidColor =
                    if (
                        gradientStart == Color.TRANSPARENT &&
                        gradientEnd == Color.TRANSPARENT
                    ) {
                        backgroundColor
                    } else {
                        null
                    },

                orientation =
                    gradientOrientation,

                cornerRadiusDp =
                    cornerRadius / density,

                topLeftRadiusDp =
                    topLeftRadius / density,

                topRightRadiusDp =
                    topRightRadius / density,

                bottomRightRadiusDp =
                    bottomRightRadius / density,

                bottomLeftRadiusDp =
                    bottomLeftRadius / density,

                strokeWidthDp =
                    strokeWidth / density,

                strokeColor =
                    strokeColor,

                strokeDashWidthDp =
                    strokeDashWidth / density,

                strokeDashGapDp =
                    strokeDashGap / density
            )

        baseBackground =
            CommonUtils.createDynamicGradient(
                context,
                config
            )
        binding.clButton.background = baseBackground
        applyGlow()

    }

    private fun applyGlow() {

        val background = baseBackground ?: return

        if (glowColor == Color.TRANSPARENT) {
            binding.clButton.background = background
            return
        }

        binding.clButton.setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            null
        )

        val glowSize =
            if (isButtonPressed) {
                pressedGlowSize
            } else {
                unpressedGlowSize
            }

        val radius =
            if (cornerRadius > 0f) {
                cornerRadius
            } else {
                max(
                    max(
                        topLeftRadius,
                        topRightRadius
                    ),
                    max(
                        bottomRightRadius,
                        bottomLeftRadius
                    )
                )
            }

        binding.clButton.background =
            CommonUtils.createGlowDrawable(
                background = background,
                glowColor = glowColor,
                glowSize = glowSize,
                cornerRadius = radius
            )
    }

    private fun updateUI() {

        if (loading) {

            binding.progressBar.visibility =
                VISIBLE

            binding.tvButton.text =
                loadingText

            binding.tvButton.setTextColor(
                loadingTextColor
            )

            binding.btnStartIcon.visibility =
                INVISIBLE

            binding.btnEndIcon.visibility =
                INVISIBLE

            isEnabled = false

        } else {

            binding.progressBar.visibility =
                GONE

            binding.tvButton.text =
                buttonText

            binding.tvButton.setTextColor(
                textColor
            )

            restoreIcons()

            isEnabled = true
        }
    }

    private fun restoreIcons() {

        binding.btnStartIcon.visibility =
            if (binding.btnStartIcon.drawable != null)
                VISIBLE
            else
                GONE

        binding.btnEndIcon.visibility =
            if (binding.btnEndIcon.drawable != null)
                VISIBLE
            else
                GONE
    }

    fun setLoading(value: Boolean) {

        loading = value

        updateUI()
    }

    fun setButtonText(value: String) {

        buttonText = value

        if (!loading) {
            binding.tvButton.text = value
        }
    }

    fun setLoadingText(value: String) {

        loadingText = value

        if (loading) {
            binding.tvButton.text = value
        }
    }
}