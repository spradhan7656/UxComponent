package com.spradhan.uxcomponentLib

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.spradhan.uxcomponentLib.databinding.AnimatedButtonBinding


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

    private var buttonText: String = ""
    private var loadingText: String = "Please wait..."

    private var isLoadingState = false

    private var normalTextColor: Int = Color.WHITE
    private var loadingTextColor: Int = Color.WHITE

    private var normalBackground: GradientConfig? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomButton,
            0,
            0
        ).apply {

            try {

                buttonText = getString(
                    R.styleable.CustomButton_cb_text
                ).orEmpty()

                loadingText = getString(
                    R.styleable.CustomButton_cb_loadingText
                ) ?: "Please wait..."

                normalTextColor = getColor(
                    R.styleable.CustomButton_cb_textColor,
                    Color.WHITE
                )

                loadingTextColor = getColor(
                    R.styleable.CustomButton_cb_loadingTextColor,
                    normalTextColor
                )

                val enabled =
                    getBoolean(
                        R.styleable.CustomButton_cb_enabled,
                        true
                    )

                binding.root.isEnabled = enabled

            } finally {
                recycle()
            }
        }

        setupViews()
        updateUI()
    }

    private fun setupViews() {

        binding.root.setOnClickListener {

            if (!isLoadingState && isEnabled) {
                performClick()
            }
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    // ---------------------------------------------------------
    // TEXT
    // ---------------------------------------------------------

    fun setText(text: String) {
        buttonText = text

        if (!isLoadingState) {
            binding.tvButton.text = text
        }
    }

    fun getText(): String {
        return buttonText
    }

    fun setLoadingText(text: String) {
        loadingText = text

        if (isLoadingState) {
            binding.tvButton.text = text
        }
    }

    // ---------------------------------------------------------
    // LOADING
    // ---------------------------------------------------------

    fun setLoading(loading: Boolean) {

        if (isLoadingState == loading) {
            return
        }

        isLoadingState = loading

        updateUI()
    }

    fun isLoading(): Boolean {
        return isLoadingState
    }

    private fun updateUI() {

        if (isLoadingState) {

            isEnabled = false
            binding.root.isEnabled = false

            binding.progressBar.visibility = VISIBLE

            binding.tvButton.text = loadingText
            binding.tvButton.setTextColor(loadingTextColor)

            // Usually hide icons while loading
            binding.btnStartIcon.visibility = INVISIBLE
            binding.btnEndIcon.visibility = INVISIBLE

        } else {

            isEnabled = true
            binding.root.isEnabled = true

            binding.progressBar.visibility = GONE

            binding.tvButton.text = buttonText
            binding.tvButton.setTextColor(normalTextColor)

            binding.btnStartIcon.visibility = VISIBLE
            binding.btnEndIcon.visibility = VISIBLE
        }
    }

    // ---------------------------------------------------------
    // ENABLE / DISABLE
    // ---------------------------------------------------------

    override fun setEnabled(enabled: Boolean) {

        super.setEnabled(enabled)

        if (!isLoadingState) {
            binding.root.isEnabled = enabled

            binding.tvButton.alpha =
                if (enabled) 1f else 0.5f
        }
    }

    // ---------------------------------------------------------
    // ICONS
    // ---------------------------------------------------------

    fun setStartIcon(resId: Int) {

        binding.btnStartIcon.setImageResource(resId)
        binding.btnStartIcon.visibility = VISIBLE
    }

    fun setEndIcon(resId: Int) {

        binding.btnEndIcon.setImageResource(resId)
        binding.btnEndIcon.visibility = VISIBLE
    }

    fun hideStartIcon() {
        binding.btnStartIcon.visibility = GONE
    }

    fun hideEndIcon() {
        binding.btnEndIcon.visibility = GONE
    }

    // ---------------------------------------------------------
    // TEXT COLOR
    // ---------------------------------------------------------

    fun setButtonTextColor(@ColorInt color: Int) {

        normalTextColor = color

        if (!isLoadingState) {
            binding.tvButton.setTextColor(color)
        }
    }

    // ---------------------------------------------------------
    // BACKGROUND
    // ---------------------------------------------------------

    fun setGradient(config: GradientConfig) {

        normalBackground = config

        binding.clButton.background =
            CommonUtils.createDynamicGradient(
                context,
                config
            )
    }

    // ---------------------------------------------------------
    // SIMPLE SOLID BACKGROUND
    // ---------------------------------------------------------

    fun setSolidBackground(
        @ColorInt color: Int,
        radiusDp: Float = 0f
    ) {

        val config = GradientConfig(
            shape = GradientDrawable.RECTANGLE,
            solidColor = color,
            cornerRadiusDp = radiusDp
        )

        setGradient(config)
    }
}