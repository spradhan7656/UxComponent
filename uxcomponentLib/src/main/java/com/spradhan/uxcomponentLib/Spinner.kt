package com.spradhan.uxcomponentLib

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
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
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

class SpinnerField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val labelTV: TextView
    private val requiredTV: TextView
    private val spinner: Spinner
    private val tvSelectedValue: TextView
    private val dropdownIV: ImageView
    private val startIconIV: ImageView
    private val spinnerLayout: LinearLayout

    private var itemSelectedListener: ((Any, Int) -> Unit)? = null
    private var multiItemSelectedListener: ((List<Any>, List<Int>) -> Unit)? = null

    private val items = mutableListOf<Any>()
    private val selectedPositionsMulti = mutableSetOf<Int>()

    private lateinit var adapter: ArrayAdapter<Any>

    private var isRequired = false
    private var errorMessage = "Please select an option"

    private var isSearchable = false
    private var isMultiSelect = false
    private var dialogTitle: String = "Select Option"
    private var searchHint: String = "Search..."
    private var spHintText: String = "Select an option"
    private var selectAllText: String = "Select All"
    private var clearAllText: String = "Clear All"

    private var selectedPosition: Int = -1
    private var selectedItem: Any? = null

    init {
        orientation = VERTICAL

        LayoutInflater.from(context)
            .inflate(R.layout.spinner_field, this, true)

        labelTV = findViewById(R.id.tvLabel)
        requiredTV = findViewById(R.id.tvRequired)
        spinner = findViewById(R.id.spinner)
        tvSelectedValue = findViewById(R.id.tvSelectedValue)
        dropdownIV = findViewById(R.id.ivDropdown)
        startIconIV = findViewById(R.id.ivStartIcon)
        spinnerLayout = findViewById(R.id.spinnerContainer)

        clipChildren = false
        clipToPadding = false
        spinnerLayout.clipChildren = false
        spinnerLayout.clipToPadding = false

        spinnerLayout.background =
            CommonDrawables.getGreyCurvedWhiteBg(spinnerLayout.context)

        attrs?.let {
            applyAttributes(it)
        }
    }

    private fun applyAttributes(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SpinnerField,
            0,
            0
        ).apply {
            try {
                getString(R.styleable.SpinnerField_labelText)?.let {
                    setLabel(it)
                    if (dialogTitle == "Select Option") {
                        dialogTitle = it
                    }
                }

                getString(R.styleable.SpinnerField_spHintText)?.let {
                    setHintText(it)
                }

                val required = getBoolean(
                    R.styleable.SpinnerField_required,
                    false
                )
                setRequired(required)

                getDrawable(R.styleable.SpinnerField_fieldBackground)?.let {
                    setBgdDrawable(it)
                }

                // Start Icon
                getDrawable(R.styleable.SpinnerField_startIcon)?.let {
                    setStartIcon(it)
                }

                val startIconTint = getColor(R.styleable.SpinnerField_startIconTint, Int.MAX_VALUE)
                if (startIconTint != Int.MAX_VALUE) {
                    startIconIV.imageTintList = ColorStateList.valueOf(startIconTint)
                }

                val startIconSize = getDimensionPixelSize(R.styleable.SpinnerField_startIconSize, -1)
                if (startIconSize != -1) {
                    startIconIV.layoutParams = startIconIV.layoutParams.apply {
                        width = startIconSize
                        height = startIconSize
                    }
                }

                // End Icon / Dropdown Icon
                getDrawable(R.styleable.SpinnerField_endIcon)?.let {
                    setDropdownIconDrawable(it)
                }
                getDrawable(R.styleable.SpinnerField_dropdownIcon)?.let {
                    setDropdownIconDrawable(it)
                }

                val endIconTint = getColor(R.styleable.SpinnerField_endIconTint, Int.MAX_VALUE)
                if (endIconTint != Int.MAX_VALUE) {
                    dropdownIV.imageTintList = ColorStateList.valueOf(endIconTint)
                }

                val endIconSize = getDimensionPixelSize(R.styleable.SpinnerField_endIconSize, -1)
                if (endIconSize != -1) {
                    dropdownIV.layoutParams = dropdownIV.layoutParams.apply {
                        width = endIconSize
                        height = endIconSize
                    }
                }

                // Input Margins
                val inputMarginVertical = getDimensionPixelSize(R.styleable.SpinnerField_inputMarginVertical, -1)
                val inputMarginHorizontal = getDimensionPixelSize(R.styleable.SpinnerField_inputMarginHorizontal, -1)
                if (inputMarginVertical != -1 || inputMarginHorizontal != -1) {
                    listOf(spinner, tvSelectedValue, dropdownIV, startIconIV).forEach { view ->
                        val lp = view.layoutParams as MarginLayoutParams
                        if (inputMarginVertical != -1) {
                            lp.topMargin = inputMarginVertical
                            lp.bottomMargin = inputMarginVertical
                        }
                        if (inputMarginHorizontal != -1) {
                            lp.marginStart = inputMarginHorizontal
                            lp.marginEnd = inputMarginHorizontal
                        }
                        view.layoutParams = lp
                    }
                }

                // Start Icon Margins
                val startIconMarginStart = getDimensionPixelSize(R.styleable.SpinnerField_startIconMarginStart, -1)
                val startIconMarginEnd = getDimensionPixelSize(R.styleable.SpinnerField_startIconMarginEnd, -1)
                if (startIconMarginStart != -1 || startIconMarginEnd != -1) {
                    val lp = startIconIV.layoutParams as MarginLayoutParams
                    if (startIconMarginStart != -1) lp.marginStart = startIconMarginStart
                    if (startIconMarginEnd != -1) lp.marginEnd = startIconMarginEnd
                    startIconIV.layoutParams = lp
                }

                // End Icon Margins
                val endIconMarginStart = getDimensionPixelSize(R.styleable.SpinnerField_endIconMarginStart, -1)
                val endIconMarginEnd = getDimensionPixelSize(R.styleable.SpinnerField_endIconMarginEnd, -1)
                if (endIconMarginStart != -1 || endIconMarginEnd != -1) {
                    val lp = dropdownIV.layoutParams as MarginLayoutParams
                    if (endIconMarginStart != -1) lp.marginStart = endIconMarginStart
                    if (endIconMarginEnd != -1) lp.marginEnd = endIconMarginEnd
                    dropdownIV.layoutParams = lp
                }

                // Advanced Styling for Background and Stroke
                val bgColor = getColor(R.styleable.SpinnerField_inputBackgroundColor, Int.MAX_VALUE)
                val cornerRadius = getDimension(R.styleable.SpinnerField_inputCornerRadius, -1f)
                val topLeftRadius = getDimension(R.styleable.SpinnerField_inputTopLeftRadius, -1f)
                val topRightRadius = getDimension(R.styleable.SpinnerField_inputTopRightRadius, -1f)
                val bottomLeftRadius = getDimension(R.styleable.SpinnerField_inputBottomLeftRadius, -1f)
                val bottomRightRadius = getDimension(R.styleable.SpinnerField_inputBottomRightRadius, -1f)

                val strokeWidth = getDimension(R.styleable.SpinnerField_inputStrokeWidth, -1f)
                val strokeColor = getColor(R.styleable.SpinnerField_inputStrokeColor, Int.MAX_VALUE)
                val dashWidth = getDimension(R.styleable.SpinnerField_inputStrokeDashWidth, 0f)
                val dashGap = getDimension(R.styleable.SpinnerField_inputStrokeDashGap, 0f)

                val strokeGradStart = getColor(R.styleable.SpinnerField_inputStrokeGradientStart, Int.MAX_VALUE)
                val strokeGradEnd = getColor(R.styleable.SpinnerField_inputStrokeGradientEnd, Int.MAX_VALUE)

                // Glow Effect
                val glowColor = getColor(R.styleable.SpinnerField_inputGlowColor, Color.TRANSPARENT)
                val glowSize = getDimension(R.styleable.SpinnerField_inputGlowSize, 0f)

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
                        spinnerLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        val radiusPx = if (cornerRadius != -1f) cornerRadius else 5f * density
                        bgDrawable = CommonUtils.createGlowDrawable(bgDrawable, glowColor, glowSize, radiusPx)

                        val pad = glowSize.toInt()
                        spinnerLayout.setPadding(
                            spinnerLayout.paddingLeft + pad,
                            spinnerLayout.paddingTop + pad,
                            spinnerLayout.paddingRight + pad,
                            spinnerLayout.paddingBottom + pad
                        )
                    }

                    spinnerLayout.background = bgDrawable
                }

                // Label & Input Appearance
                val labelColor = getColor(R.styleable.SpinnerField_labelTextColor, Int.MAX_VALUE)
                if (labelColor != Int.MAX_VALUE) {
                    labelTV.setTextColor(labelColor)
                }

                val labelFontResId = getResourceId(R.styleable.SpinnerField_labelTextFontFamily, -1)
                if (labelFontResId != -1) {
                    setLabelFont(labelFontResId)
                } else {
                    val genericFontResId = getResourceId(R.styleable.SpinnerField_fontFamily, -1)
                    if (genericFontResId != -1) {
                        setFont(genericFontResId)
                    }
                }

                val inputFontResId = getResourceId(R.styleable.SpinnerField_inputFontFamily, -1)
                if (inputFontResId != -1) {
                    setInputFont(inputFontResId)
                }

                val labelMarginBottom = getDimensionPixelSize(R.styleable.SpinnerField_labelTextMarginBottom, -1)
                if (labelMarginBottom != -1) {
                    val lp = spinnerLayout.layoutParams as MarginLayoutParams
                    lp.topMargin = labelMarginBottom
                    spinnerLayout.layoutParams = lp
                }

                isSearchable = getBoolean(R.styleable.SpinnerField_isSearchable, false)
                isMultiSelect = getBoolean(R.styleable.SpinnerField_isMultiSelect, false)

                getString(R.styleable.SpinnerField_dialogTitle)?.let {
                    dialogTitle = it
                }

                getString(R.styleable.SpinnerField_searchHint)?.let {
                    searchHint = it
                }

                getString(R.styleable.SpinnerField_selectAllText)?.let {
                    selectAllText = it
                }

                getString(R.styleable.SpinnerField_clearAllText)?.let {
                    clearAllText = it
                }

                updateViewMode()

            } finally {
                recycle()
            }
        }
    }

    private fun updateViewMode() {
        if (isMultiSelect || isSearchable) {
            spinner.visibility = GONE
            tvSelectedValue.visibility = VISIBLE
            updateSelectedDisplay()

            spinnerLayout.setOnClickListener {
                if (isEnabled) {
                    if (isMultiSelect) {
                        showMultiSelectDialog()
                    } else {
                        showSearchableDialog()
                    }
                }
            }
        } else {
            spinner.visibility = VISIBLE
            tvSelectedValue.visibility = GONE
            spinnerLayout.setOnClickListener(null)
        }
    }

    private fun updateSelectedDisplay() {
        if (isMultiSelect) {
            if (selectedPositionsMulti.isNotEmpty()) {
                val labels = selectedPositionsMulti.sorted()
                    .filter { it in items.indices }
                    .map { items[it].toString() }
                tvSelectedValue.text = labels.joinToString(", ")
                tvSelectedValue.setTextColor(Color.BLACK)
            } else {
                tvSelectedValue.text = spHintText
                tvSelectedValue.setTextColor(Color.GRAY)
            }
        } else {
            if (selectedItem != null) {
                tvSelectedValue.text = selectedItem.toString()
                tvSelectedValue.setTextColor(Color.BLACK)
            } else {
                tvSelectedValue.text = spHintText
                tvSelectedValue.setTextColor(Color.GRAY)
            }
        }
    }

    fun showSearchableDialog() {
        SearchableSpinnerDialog<Any>(context)
            .setTitle(dialogTitle)
            .setSearchHint(searchHint)
            .setItems(items)
            .setSelection(selectedPosition)
            .setOnItemSelectedListener { item, position ->
                selectedItem = item
                selectedPosition = position
                updateSelectedDisplay()

                if (!isSearchable) {
                    spinner.setSelection(position)
                }

                itemSelectedListener?.invoke(item, position)
            }
            .show()
    }

    fun showMultiSelectDialog() {
        SpinnerDialogSearchableWithMultiCheck<Any>(context)
            .setTitle(dialogTitle)
            .setSearchHint(searchHint)
            .setSelectAllText(selectAllText)
            .setClearAllText(clearAllText)
            .setItems(items)
            .setSelectedPositions(selectedPositionsMulti)
            .setOnItemsSelectedListener { selectedList, positionsList ->
                selectedPositionsMulti.clear()
                selectedPositionsMulti.addAll(positionsList)
                updateSelectedDisplay()

                multiItemSelectedListener?.invoke(selectedList, positionsList)
            }
            .show()
    }

    fun setLabel(text: String, required: Boolean = false): SpinnerField {
        labelTV.visibility = VISIBLE
        labelTV.text = text
        this.isRequired = required
        requiredTV.visibility = if (required) VISIBLE else GONE

        if (dialogTitle == "Select Option") {
            dialogTitle = text
        }
        return this
    }

    fun setLabel(text: String): SpinnerField {
        labelTV.visibility = VISIBLE
        labelTV.text = text

        if (dialogTitle == "Select Option") {
            dialogTitle = text
        }
        return this
    }

    fun setRequired(required: Boolean): SpinnerField {
        isRequired = required
        requiredTV.visibility = if (required) VISIBLE else GONE
        return this
    }

    fun setSearchable(searchable: Boolean): SpinnerField {
        this.isSearchable = searchable
        updateViewMode()
        return this
    }

    fun setMultiSelect(multiSelect: Boolean): SpinnerField {
        this.isMultiSelect = multiSelect
        updateViewMode()
        return this
    }

    fun setDialogTitle(title: String): SpinnerField {
        this.dialogTitle = title
        return this
    }

    fun setSearchHint(hint: String): SpinnerField {
        this.searchHint = hint
        return this
    }

    fun setHintText(hint: String): SpinnerField {
        this.spHintText = hint
        updateSelectedDisplay()
        return this
    }

    fun setSelectAllText(text: String): SpinnerField {
        this.selectAllText = text
        return this
    }

    fun setClearAllText(text: String): SpinnerField {
        this.clearAllText = text
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> setItems(newItems: List<T>): SpinnerField {
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
                    if (!isSearchable && !isMultiSelect) {
                        selectedPosition = position
                        selectedItem = items[position]
                        itemSelectedListener?.invoke(
                            items[position],
                            position
                        )
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }

        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> updateItems(newItems: List<T>): SpinnerField {
        items.clear()
        items.addAll(newItems as List<Any>)

        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }

        if (selectedPosition >= items.size) {
            selectedPosition = -1
            selectedItem = null
        }

        selectedPositionsMulti.removeAll { it >= items.size }
        updateSelectedDisplay()

        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> onItemSelected(listener: (item: T, position: Int) -> Unit): SpinnerField {
        itemSelectedListener = listener as (Any, Int) -> Unit
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> onMultiItemsSelected(listener: (items: List<T>, positions: List<Int>) -> Unit): SpinnerField {
        multiItemSelectedListener = listener as (List<Any>, List<Int>) -> Unit
        return this
    }

    fun setSelection(position: Int): SpinnerField {
        if (position in items.indices) {
            selectedPosition = position
            selectedItem = items[position]
            if (isSearchable || isMultiSelect) {
                updateSelectedDisplay()
            } else {
                spinner.setSelection(position)
            }
        }
        return this
    }

    fun setSelectedPositions(positions: Collection<Int>): SpinnerField {
        selectedPositionsMulti.clear()
        selectedPositionsMulti.addAll(positions.filter { it in items.indices })
        updateSelectedDisplay()
        return this
    }

    fun getSelectedPositions(): List<Int> {
        return if (isMultiSelect) {
            selectedPositionsMulti.toList().sorted()
        } else if (selectedPosition >= 0) {
            listOf(selectedPosition)
        } else {
            emptyList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getSelectedItems(): List<T> {
        return if (isMultiSelect) {
            selectedPositionsMulti.sorted()
                .filter { it in items.indices }
                .map { items[it] as T }
        } else {
            val item = getSelectedItem<T>()
            if (item != null) listOf(item) else emptyList()
        }
    }

    fun setRequiredMessage(message: String): SpinnerField {
        errorMessage = message
        return this
    }

    fun setFieldEnabled(enabled: Boolean): SpinnerField {
        isEnabled = enabled
        spinner.isEnabled = enabled
        tvSelectedValue.isEnabled = enabled
        alpha = if (enabled) 1f else 0.5f
        return this
    }

    fun setDropdownIcon(@DrawableRes drawable: Int): SpinnerField {
        dropdownIV.setImageResource(drawable)
        return this
    }

    fun setDropdownIconDrawable(drawable: Drawable): SpinnerField {
        dropdownIV.setImageDrawable(drawable)
        return this
    }

    fun setStartIcon(drawable: Drawable): SpinnerField {
        startIconIV.visibility = VISIBLE
        startIconIV.setImageDrawable(drawable)
        return this
    }

    fun setLabelTypeface(typeface: Typeface): SpinnerField {
        labelTV.typeface = typeface
        return this
    }

    fun setInputTypeface(typeface: Typeface): SpinnerField {
        tvSelectedValue.typeface = typeface
        return this
    }

    fun setLabelFont(@FontRes fontResId: Int): SpinnerField {
        ResourcesCompat.getFont(context, fontResId)?.let { setLabelTypeface(it) }
        return this
    }

    fun setInputFont(@FontRes fontResId: Int): SpinnerField {
        ResourcesCompat.getFont(context, fontResId)?.let { setInputTypeface(it) }
        return this
    }

    fun setFont(@FontRes fontResId: Int): SpinnerField {
        setLabelFont(fontResId)
        setInputFont(fontResId)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getSelectedItem(): T? {
        return if (isSearchable || isMultiSelect) {
            selectedItem as? T
        } else {
            spinner.selectedItem as? T
        }
    }

    fun clearItems(): SpinnerField {
        items.clear()
        selectedPosition = -1
        selectedItem = null
        selectedPositionsMulti.clear()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
        updateSelectedDisplay()
        return this
    }

    fun setBgdDrawable(drawable: Drawable): SpinnerField {
        spinnerLayout.background = drawable
        return this
    }

    fun validate(): Boolean {
        val invalid = if (isMultiSelect) {
            isRequired && selectedPositionsMulti.isEmpty()
        } else if (isSearchable) {
            isRequired && (selectedItem == null || selectedPosition < 0)
        } else {
            isRequired && (items.isEmpty() || spinner.selectedItemPosition == 0)
        }

        if (invalid) {
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
