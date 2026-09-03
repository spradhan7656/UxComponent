package com.spradhan.uxcomponentLib

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes

class SpinnerField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val labelTV: TextView
    private val requiredTV: TextView
    private val spinner: Spinner
    private val dropdownIV: ImageView
    private val spinnerLayout: LinearLayout

    private var itemSelectedListener:
            ((Any, Int) -> Unit)? = null

    private val items = mutableListOf<Any>()

    private lateinit var adapter: ArrayAdapter<Any>

    private var isRequired = false

    private var errorMessage =
        "Please select an option"

    init {

        orientation = VERTICAL

        LayoutInflater.from(context)
            .inflate(R.layout.spinner_field, this, true)

        labelTV = findViewById(R.id.tvLabel)
        requiredTV = findViewById(R.id.tvRequired)
        spinner = findViewById(R.id.spinner)
        dropdownIV = findViewById(R.id.ivDropdown)
        spinnerLayout = findViewById(R.id.spinnerContainer)

        spinnerLayout.background =
            CommonDrawables.getGreyCurvedWhiteBg(spinnerLayout.context)

        attrs?.let {
            applyAttributes(it)
        }
    }

    private fun applyAttributes(
        attrs: AttributeSet
    ) {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SpinnerField,
            0,
            0
        ).apply {

            try {

                getString(
                    R.styleable.SpinnerField_labelText
                )?.let {
                    setLabel(it)
                }

                val required = getBoolean(
                    R.styleable.SpinnerField_required,
                    false
                )

                setRequired(required)

                getDrawable(
                    R.styleable.SpinnerField_fieldBackground
                )?.let {
                    setBgdDrawable(it)
                }

                getDrawable(
                    R.styleable.SpinnerField_dropdownIcon
                )?.let {
                    dropdownIV.setImageDrawable(it)
                }

            } finally {
                recycle()
            }
        }
    }

    fun setLabel(
        text: String,
        required: Boolean = false
    ): SpinnerField {

        labelTV.visibility = VISIBLE

        labelTV.text = text

        this.isRequired = required

        requiredTV.visibility =
            if (required) VISIBLE
            else GONE

        return this
    }fun setLabel(
        text: String
    ): SpinnerField {

        labelTV.visibility = VISIBLE
        labelTV.text = text

        return this
    }

    fun setRequired(
        required: Boolean
    ): SpinnerField {

        isRequired = required

        requiredTV.visibility =
            if (required) VISIBLE
            else GONE

        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> setItems(
        newItems: List<T>
    ): SpinnerField {

        items.clear()

        items.addAll(newItems as List<Any>)

        adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            items
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinner.adapter = adapter

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    itemSelectedListener?.invoke(
                        items[position],
                        position
                    )
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) = Unit
            }

        return this
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> updateItems(
        newItems: List<T>
    ): SpinnerField {

        items.clear()

        items.addAll(newItems as List<Any>)

        adapter.notifyDataSetChanged()

        return this
    }

    fun <T> onItemSelected(
        listener: (item: T, position: Int) -> Unit
    ): SpinnerField {

        itemSelectedListener =
            listener as (Any, Int) -> Unit

        return this
    }

    fun setSelection(
        position: Int
    ): SpinnerField {

        spinner.setSelection(position)

        return this
    }

    fun setRequiredMessage(
        message: String
    ): SpinnerField {

        errorMessage = message

        return this
    }

    fun setFieldEnabled(
        enabled: Boolean
    ): SpinnerField {

        spinner.isEnabled = enabled

        alpha = if (enabled) 1f else 0.5f

        return this
    }

    fun setDropdownIcon(
        @DrawableRes drawable: Int
    ): SpinnerField {

        dropdownIV.setImageResource(drawable)

        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getSelectedItem(): T? {

        return spinner.selectedItem as? T
    }

    fun clearItems(): SpinnerField {

        items.clear()

        adapter.notifyDataSetChanged()

        return this
    }

    fun setBgdDrawable(
        drawable: Drawable
    ): SpinnerField {

        spinnerLayout.background = drawable

        return this
    }

    fun validate(): Boolean {

        if (
            isRequired &&
            (
                    items.isEmpty() ||
                            spinner.selectedItemPosition == 0
                    )
        ) {

            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()

            requestFocus()

            return false
        }

        return true
    }
}