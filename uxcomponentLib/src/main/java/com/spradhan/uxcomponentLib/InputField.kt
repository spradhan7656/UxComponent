package com.spradhan.uxcomponentLib

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.InputFilter
import android.text.method.KeyListener
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

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

    init {
        inflate(context, R.layout.input_field, this)

        labelTV = findViewById(R.id.tvLabel)
        requiredTV = findViewById(R.id.tvRequired)
        editText = findViewById(R.id.etInput)
        endIconIV = findViewById(R.id.ivEndIcon)
        startIconIV = findViewById(R.id.ivStartIcon)
        linearLayout = findViewById(R.id.inputContainer)

        originalKeyListener = editText.keyListener

        linearLayout.background =
            CommonDrawables.getGreyCurvedWhiteBg(editText.context)

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

            } finally {
                recycle()
            }
        }
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

}
