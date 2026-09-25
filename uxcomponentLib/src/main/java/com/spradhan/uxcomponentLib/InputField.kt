package com.spradhan.uxcomponentLib

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.KeyListener
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class InputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val labelTV: TextView
    private val requiredTV: TextView
    private val editText: EditText
    private val linearLayout: LinearLayout
    private val endIconIV: ImageView
    private val startIconIV: ImageView
    private var originalKeyListener: KeyListener? = null

    private val validators = mutableListOf<ValidatorConfig>()

    private var isPasswordVisible = false
    private var eyeOpenDrawable: Drawable? = null
    private var eyeCloseDrawable: Drawable? = null

    init {
        inflate(context, R.layout.input_field, this)

        labelTV = findViewById(R.id.tvLabel)
        requiredTV = findViewById(R.id.tvRequired)
        editText = findViewById(R.id.etInput)
        endIconIV = findViewById(R.id.ivEndIcon)
        startIconIV = findViewById(R.id.ivStartIcon)
        linearLayout = findViewById(R.id.inputContainer)

        originalKeyListener = editText.keyListener

        clipChildren = false
        clipToPadding = false
        linearLayout.clipChildren = false
        linearLayout.clipToPadding = false

        linearLayout.background =
            CommonDrawables.getGreyCurvedWhiteBg(editText.context)

        eyeOpenDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_open)
        eyeCloseDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_close)

        attrs?.let {
            applyAttributes(it)
        }
    }

    private fun applyAttributes(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InputField,
            0,
            0
        ).apply {
            try {
                getString(R.styleable.InputField_labelText)?.let {
                    setLabel(it)
                }

                getString(R.styleable.InputField_hintText)?.let {
                    editText.hint = it
                }

                val required = getBoolean(
                    R.styleable.InputField_required,
                    false
                )
                setRequired(required)

                getDrawable(R.styleable.InputField_fieldBackground)?.let {
                    editText.background = it
                }

                // Start Icon
                getDrawable(R.styleable.InputField_startIcon)?.let {
                    setStartIcon(it)
                }

                val startIconTint = getColor(R.styleable.InputField_startIconTint, Int.MAX_VALUE)
                if (startIconTint != Int.MAX_VALUE) {
                    startIconIV.imageTintList = ColorStateList.valueOf(startIconTint)
                }

                val startIconSize = getDimensionPixelSize(R.styleable.InputField_startIconSize, -1)
                if (startIconSize != -1) {
                    startIconIV.layoutParams = startIconIV.layoutParams.apply {
                        width = startIconSize
                        height = startIconSize
                    }
                }

                // End Icon
                getDrawable(R.styleable.InputField_endIcon)?.let {
                    setEndIcon(it)
                }

                val endIconTint = getColor(R.styleable.InputField_endIconTint, Int.MAX_VALUE)
                if (endIconTint != Int.MAX_VALUE) {
                    endIconIV.imageTintList = ColorStateList.valueOf(endIconTint)
                }

                val endIconSize = getDimensionPixelSize(R.styleable.InputField_endIconSize, -1)
                if (endIconSize != -1) {
                    endIconIV.layoutParams = endIconIV.layoutParams.apply {
                        width = endIconSize
                        height = endIconSize
                    }
                }

                getDrawable(R.styleable.InputField_eyeOpenIcon)?.let {
                    eyeOpenDrawable = it
                }
                getDrawable(R.styleable.InputField_eyeCloseIcon)?.let {
                    eyeCloseDrawable = it
                }

                // Password Toggle
                val passwordToggleEnabled = getBoolean(R.styleable.InputField_passwordToggleEnabled, false)
                if (passwordToggleEnabled) {
                    setupPasswordToggle()
                }

                // Input Margins
                val inputMarginVertical = getDimensionPixelSize(R.styleable.InputField_inputMarginVertical, -1)
                val inputMarginHorizontal = getDimensionPixelSize(R.styleable.InputField_inputMarginHorizontal, -1)
                if (inputMarginVertical != -1 || inputMarginHorizontal != -1) {
                    val lp = editText.layoutParams as LayoutParams
                    if (inputMarginVertical != -1) {
                        lp.topMargin = inputMarginVertical
                        lp.bottomMargin = inputMarginVertical
                    }
                    if (inputMarginHorizontal != -1) {
                        lp.marginStart = inputMarginHorizontal
                        lp.marginEnd = inputMarginHorizontal
                    }
                    editText.layoutParams = lp
                }

                // Start Icon Margins
                val startIconMarginStart = getDimensionPixelSize(R.styleable.InputField_startIconMarginStart, -1)
                val startIconMarginEnd = getDimensionPixelSize(R.styleable.InputField_startIconMarginEnd, -1)
                if (startIconMarginStart != -1 || startIconMarginEnd != -1) {
                    val lp = startIconIV.layoutParams as LayoutParams
                    if (startIconMarginStart != -1) lp.marginStart = startIconMarginStart
                    if (startIconMarginEnd != -1) lp.marginEnd = startIconMarginEnd
                    startIconIV.layoutParams = lp
                }

                // End Icon Margins
                val endIconMarginStart = getDimensionPixelSize(R.styleable.InputField_endIconMarginStart, -1)
                val endIconMarginEnd = getDimensionPixelSize(R.styleable.InputField_endIconMarginEnd, -1)
                if (endIconMarginStart != -1 || endIconMarginEnd != -1) {
                    val lp = endIconIV.layoutParams as LayoutParams
                    if (endIconMarginStart != -1) lp.marginStart = endIconMarginStart
                    if (endIconMarginEnd != -1) lp.marginEnd = endIconMarginEnd
                    endIconIV.layoutParams = lp
                }

                // Advanced Styling for Background and Stroke
                val bgColor = getColor(R.styleable.InputField_inputBackgroundColor, Int.MAX_VALUE)
                val cornerRadius = getDimension(R.styleable.InputField_inputCornerRadius, -1f)
                val topLeftRadius = getDimension(R.styleable.InputField_inputTopLeftRadius, -1f)
                val topRightRadius = getDimension(R.styleable.InputField_inputTopRightRadius, -1f)
                val bottomLeftRadius = getDimension(R.styleable.InputField_inputBottomLeftRadius, -1f)
                val bottomRightRadius = getDimension(R.styleable.InputField_inputBottomRightRadius, -1f)

                val strokeWidth = getDimension(R.styleable.InputField_inputStrokeWidth, -1f)
                val strokeColor = getColor(R.styleable.InputField_inputStrokeColor, Int.MAX_VALUE)
                val dashWidth = getDimension(R.styleable.InputField_inputStrokeDashWidth, 0f)
                val dashGap = getDimension(R.styleable.InputField_inputStrokeDashGap, 0f)

                val strokeGradStart = getColor(R.styleable.InputField_inputStrokeGradientStart, Int.MAX_VALUE)
                val strokeGradEnd = getColor(R.styleable.InputField_inputStrokeGradientEnd, Int.MAX_VALUE)

                // Glow Effect
                val glowColor = getColor(R.styleable.InputField_inputGlowColor, Color.TRANSPARENT)
                val glowSize = getDimension(R.styleable.InputField_inputGlowSize, 0f)

                if (bgColor != Int.MAX_VALUE || cornerRadius != -1f || strokeWidth != -1f || strokeColor != Int.MAX_VALUE ||
                    strokeGradStart != Int.MAX_VALUE || strokeGradEnd != Int.MAX_VALUE || dashWidth != 0f || glowSize > 0f) {

                    val density = resources.displayMetrics.density
                    val config = GradientConfig(
                        solidColor = if (bgColor == Int.MAX_VALUE) Color.WHITE else bgColor,
                        cornerRadiusDp = if (cornerRadius != -1f) cornerRadius / density else 5f,
                        topLeftRadiusDp = if (topLeftRadius != -1f) topLeftRadius / density else 0f,
                        topRightRadiusDp = if (topRightRadius != -1f) topRightRadius / density else 0f,
                        bottomLeftRadiusDp = if (bottomLeftRadius != -1f) bottomLeftRadius / density else 0f,
                        bottomRightRadiusDp = if (bottomRightRadius != -1f) bottomRightRadius / density else 0f,
                        strokeWidthDp = if (strokeWidth != -1f) strokeWidth / density else 1f,
                        strokeColor = if (strokeColor != Int.MAX_VALUE) strokeColor else Color.parseColor(
                            "#C0C3C6"
                        ),
                        strokeDashWidthDp = dashWidth / density,
                        strokeDashGapDp = dashGap / density,
                        strokeColors = if (strokeGradStart != Int.MAX_VALUE && strokeGradEnd != Int.MAX_VALUE)
                            intArrayOf(strokeGradStart, strokeGradEnd) else null
                    )

                    var bgDrawable = CommonUtils.createDynamicGradient(context, config)

                    if (glowColor != Color.TRANSPARENT && glowSize > 0f) {
                        linearLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        val radiusPx = if (cornerRadius != -1f) cornerRadius else 5f * density
                        bgDrawable = CommonUtils.createGlowDrawable(bgDrawable, glowColor, glowSize, radiusPx)

                        val pad = glowSize.toInt()
                        linearLayout.setPadding(
                            linearLayout.paddingLeft + pad,
                            linearLayout.paddingTop + pad,
                            linearLayout.paddingRight + pad,
                            linearLayout.paddingBottom + pad
                        )
                    }

                    linearLayout.background = bgDrawable
                }

                // Label & Input Appearance
                val labelColor = getColor(R.styleable.InputField_labelTextColor, Int.MAX_VALUE)
                if (labelColor != Int.MAX_VALUE) {
                    labelTV.setTextColor(labelColor)
                }

                val labelFontResId = getResourceId(R.styleable.InputField_labelTextFontFamily, -1)
                if (labelFontResId != -1) {
                    setLabelFont(labelFontResId)
                } else {
                    val genericFontResId = getResourceId(R.styleable.InputField_fontFamily, -1)
                    if (genericFontResId != -1) {
                        setFont(genericFontResId)
                    }
                }

                val inputFontResId = getResourceId(R.styleable.InputField_inputFontFamily, -1)
                if (inputFontResId != -1) {
                    setInputFont(inputFontResId)
                }

                val inputType = getInt(R.styleable.InputField_android_inputType, -1)
                if (inputType != -1) {
                    setInputType(inputType)
                }

                val labelMarginBottom = getDimensionPixelSize(R.styleable.InputField_labelTextMarginBottom, -1)
                if (labelMarginBottom != -1) {
                    val lp = linearLayout.layoutParams as LayoutParams
                    lp.topMargin = labelMarginBottom
                    linearLayout.layoutParams = lp
                }

            } finally {
                recycle()
            }
        }
    }

    private fun setupPasswordToggle() {
        endIconIV.visibility = VISIBLE
        updatePasswordIcon()

        // Initial state: password hidden
        editText.transformationMethod = PasswordTransformationMethod.getInstance()

        endIconIV.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            val selection = editText.selectionEnd
            if (isPasswordVisible) {
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            updatePasswordIcon()
            editText.setSelection(selection)
        }
    }

    private fun updatePasswordIcon() {
        val drawable = if (isPasswordVisible) eyeOpenDrawable else eyeCloseDrawable
        endIconIV.setImageDrawable(drawable)
    }

    fun setLabel(text: String): InputField {
        labelTV.visibility = VISIBLE
        labelTV.text = text
        return this
    }

    fun setRequired(required: Boolean): InputField {
        requiredTV.visibility =
            if (required) VISIBLE else GONE
        return this
    }

    fun addValidator(
        errorMessage: String,
        validator: (String) -> Boolean
    ): InputField {

        validators.add(
            ValidatorConfig(
                validator = validator,
                errorMessage = errorMessage
            )
        )

        return this
    }

    fun validate(): Boolean {

        val value = editText.text.toString()

        for (validatorConfig in validators) {

            val isValid =
                validatorConfig.validator.invoke(value)

            if (!isValid) {

                editText.requestFocus()
                editText.setText("")
                Toast.makeText(context, validatorConfig.errorMessage, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        editText.error = null

        return true
    }

    fun setStartIcon(
        drawable: Drawable
    ): InputField {

        startIconIV.visibility = VISIBLE
        startIconIV.setImageDrawable(drawable)

        return this
    }

    fun setEndIcon(
        drawable: Drawable
    ): InputField {

        endIconIV.visibility = VISIBLE
        endIconIV.setImageDrawable(drawable)

        return this
    }

    fun setEndIconClickListener(
        listener: View.OnClickListener
    ): InputField {

        endIconIV.setOnClickListener(listener)

        return this
    }

    fun setHint(text: String) : InputField {
        editText.hint = text
        Log.d("InputField", "Hint set to: ${editText.hint}")
        return this
    }

    fun getText(): String = editText.text.toString()

    fun setFilters(
        vararg filters: InputFilter
    ): InputField {

        editText.filters = filters

        return this
    }

    fun setText(text: String): InputField {
        editText.setText(text)
        return this
    }

    fun setBackground(bckgrnd : GradientDrawable): InputField{
        editText.background = bckgrnd
        return this
    }

    fun setLabelTypeface(typeface: Typeface): InputField {
        labelTV.typeface = typeface
        return this
    }

    fun setInputTypeface(typeface: Typeface): InputField {
        editText.typeface = typeface
        return this
    }

    fun setLabelFont(@FontRes fontResId: Int): InputField {
        ResourcesCompat.getFont(context, fontResId)?.let { setLabelTypeface(it) }
        return this
    }

    fun setInputFont(@FontRes fontResId: Int): InputField {
        ResourcesCompat.getFont(context, fontResId)?.let { setInputTypeface(it) }
        return this
    }

    fun setFont(@FontRes fontResId: Int): InputField {
        setLabelFont(fontResId)
        setInputFont(fontResId)
        return this
    }

    fun setMode(
        mode: FieldMode
    ): InputField {

        when (mode) {

            FieldMode.READ_ONLY -> {
                editText.keyListener = null
                editText.isCursorVisible = false
            }

            FieldMode.EDITABLE -> {
                editText.keyListener = originalKeyListener
                editText.isCursorVisible = true
            }
        }

        return this
    }

    fun setInputType(type: Int): InputField {
        editText.inputType = type
        return this
    }

}

