package com.spradhan.uxcomponentLib

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpinnerDialogSearchableWithMultiCheck<T>(
    context: Context
) : Dialog(context) {

    private var title: String = "Select Options"
    private var searchHint: String = "Search..."
    private var selectAllLabel: String = "Select All"
    private var clearAllLabel: String = "Clear All"

    private var items: List<T> = emptyList()
    private val selectedPositions: MutableSet<Int> = mutableSetOf()
    private var itemFormatter: ((T) -> String)? = null

    private var onItemsSelectedListener: ((selectedItems: List<T>, selectedPositions: List<Int>) -> Unit)? = null
    private var onDismissCallback: (() -> Unit)? = null

    @ColorInt
    private var accentColor: Int = ContextCompat.getColor(context, R.color.app_deep_blue)

    @ColorInt
    private var titleColor: Int = Color.BLACK

    private var cornerRadiusDp: Float = 14f

    private lateinit var tvTitle: TextView
    private lateinit var ivClose: ImageView
    private lateinit var tvSelectAll: TextView
    private lateinit var tvClearAll: TextView
    private lateinit var tvSelectionCount: TextView
    private lateinit var etSearch: EditText
    private lateinit var ivClearSearch: ImageView
    private lateinit var rvItems: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var btnCancel: TextView
    private lateinit var btnApply: TextView
    private lateinit var searchContainer: View
    private lateinit var dialogContainer: View

    private lateinit var adapter: MultiSelectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_searchable_multi_spinner)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (context.resources.displayMetrics.widthPixels * 0.90).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        initViews()
        applyConfigurations()
        setupListeners()
        setupRecyclerView()
        updateSelectionCounter()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvDialogTitle)
        ivClose = findViewById(R.id.ivClose)
        tvSelectAll = findViewById(R.id.tvSelectAll)
        tvClearAll = findViewById(R.id.tvClearAll)
        tvSelectionCount = findViewById(R.id.tvSelectionCount)
        etSearch = findViewById(R.id.etSearch)
        ivClearSearch = findViewById(R.id.ivClearSearch)
        rvItems = findViewById(R.id.rvItems)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        btnCancel = findViewById(R.id.btnCancel)
        btnApply = findViewById(R.id.btnApply)
        searchContainer = findViewById(R.id.searchContainer)
        dialogContainer = findViewById(R.id.dialogContainer)
    }

    private fun applyConfigurations() {
        tvTitle.text = title
        tvTitle.setTextColor(titleColor)
        etSearch.hint = searchHint
        tvSelectAll.text = selectAllLabel
        tvClearAll.text = clearAllLabel

        tvSelectAll.setTextColor(accentColor)
        btnApply.setTextColor(accentColor)

        val bgDrawable = CommonUtils.createDynamicGradient(
            context,
            GradientConfig(
                solidColor = Color.WHITE,
                cornerRadiusDp = cornerRadiusDp
            )
        )
        dialogContainer.background = bgDrawable

        val searchBg = CommonUtils.createDynamicGradient(
            context,
            GradientConfig(
                solidColor = ContextCompat.getColor(context, R.color.whit_smoke),
                cornerRadiusDp = 8f,
                strokeWidthDp = 1f,
                strokeColor = ContextCompat.getColor(context, R.color.light_grey)
            )
        )
        searchContainer.background = searchBg
    }

    private fun setupListeners() {
        ivClose.setOnClickListener { dismiss() }
        btnCancel.setOnClickListener { dismiss() }

        btnApply.setOnClickListener {
            val selectedList = items.filterIndexed { index, _ -> index in selectedPositions }
            val positionsList = selectedPositions.toList().sorted()
            onItemsSelectedListener?.invoke(selectedList, positionsList)
            dismiss()
        }

        tvSelectAll.setOnClickListener {
            adapter.selectAllCurrent()
        }

        tvClearAll.setOnClickListener {
            selectedPositions.clear()
            adapter.notifyDataSetChanged()
            updateSelectionCounter()
        }

        ivClearSearch.setOnClickListener {
            etSearch.text.clear()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                ivClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                adapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        setOnDismissListener {
            onDismissCallback?.invoke()
        }
    }

    private fun setupRecyclerView() {
        adapter = MultiSelectAdapter()
        rvItems.layoutManager = LinearLayoutManager(context)
        rvItems.adapter = adapter
    }

    private fun updateSelectionCounter() {
        val total = items.size
        val count = selectedPositions.size
        tvSelectionCount.text = "$count of $total selected"
    }

    fun setTitle(title: String): SpinnerDialogSearchableWithMultiCheck<T> {
        this.title = title
        if (::tvTitle.isInitialized) {
            tvTitle.text = title
        }
        return this
    }

    fun setSearchHint(hint: String): SpinnerDialogSearchableWithMultiCheck<T> {
        this.searchHint = hint
        if (::etSearch.isInitialized) {
            etSearch.hint = hint
        }
        return this
    }

    fun setSelectAllText(text: String): SpinnerDialogSearchableWithMultiCheck<T> {
        this.selectAllLabel = text
        if (::tvSelectAll.isInitialized) {
            tvSelectAll.text = text
        }
        return this
    }

    fun setClearAllText(text: String): SpinnerDialogSearchableWithMultiCheck<T> {
        this.clearAllLabel = text
        if (::tvClearAll.isInitialized) {
            tvClearAll.text = text
        }
        return this
    }

    fun setItems(
        newItems: List<T>,
        formatter: ((T) -> String)? = null
    ): SpinnerDialogSearchableWithMultiCheck<T> {
        this.items = newItems
        this.itemFormatter = formatter
        if (::rvItems.isInitialized) {
            adapter.updateItems(newItems)
            updateSelectionCounter()
        }
        return this
    }

    fun setSelectedPositions(positions: Collection<Int>): SpinnerDialogSearchableWithMultiCheck<T> {
        this.selectedPositions.clear()
        this.selectedPositions.addAll(positions.filter { it in items.indices })
        if (::rvItems.isInitialized) {
            adapter.notifyDataSetChanged()
            updateSelectionCounter()
        }
        return this
    }

    fun setSelectedItems(selectedItems: Collection<T>): SpinnerDialogSearchableWithMultiCheck<T> {
        this.selectedPositions.clear()
        items.forEachIndexed { index, item ->
            if (item in selectedItems) {
                this.selectedPositions.add(index)
            }
        }
        if (::rvItems.isInitialized) {
            adapter.notifyDataSetChanged()
            updateSelectionCounter()
        }
        return this
    }

    fun setOnItemsSelectedListener(
        listener: (selectedItems: List<T>, selectedPositions: List<Int>) -> Unit
    ): SpinnerDialogSearchableWithMultiCheck<T> {
        this.onItemsSelectedListener = listener
        return this
    }

    fun setOnDismissCallback(callback: () -> Unit): SpinnerDialogSearchableWithMultiCheck<T> {
        this.onDismissCallback = callback
        return this
    }

    fun setAccentColor(@ColorInt color: Int): SpinnerDialogSearchableWithMultiCheck<T> {
        this.accentColor = color
        return this
    }

    fun setTitleColor(@ColorInt color: Int): SpinnerDialogSearchableWithMultiCheck<T> {
        this.titleColor = color
        if (::tvTitle.isInitialized) {
            tvTitle.setTextColor(color)
        }
        return this
    }

    fun setCornerRadius(radiusDp: Float): SpinnerDialogSearchableWithMultiCheck<T> {
        this.cornerRadiusDp = radiusDp
        return this
    }

    private inner class MultiSelectAdapter :
        RecyclerView.Adapter<MultiSelectAdapter.ItemViewHolder>() {

        private var filteredItems: MutableList<Pair<T, Int>> =
            items.mapIndexed { index, item -> Pair(item, index) }.toMutableList()

        fun updateItems(newItems: List<T>) {
            items = newItems
            filter(etSearch.text?.toString() ?: "")
        }

        fun filter(query: String) {
            filteredItems.clear()
            if (query.isEmpty()) {
                items.forEachIndexed { index, item ->
                    filteredItems.add(Pair(item, index))
                }
            } else {
                items.forEachIndexed { index, item ->
                    val label = itemFormatter?.invoke(item) ?: item.toString()
                    if (label.contains(query, ignoreCase = true)) {
                        filteredItems.add(Pair(item, index))
                    }
                }
            }

            tvEmptyState.visibility = if (filteredItems.isEmpty()) View.VISIBLE else View.GONE
            rvItems.visibility = if (filteredItems.isEmpty()) View.GONE else View.VISIBLE
            notifyDataSetChanged()
        }

        fun selectAllCurrent() {
            filteredItems.forEach { (_, originalIndex) ->
                selectedPositions.add(originalIndex)
            }
            notifyDataSetChanged()
            updateSelectionCounter()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_searchable_multi_spinner, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val (item, originalIndex) = filteredItems[position]
            val displayText = itemFormatter?.invoke(item) ?: item.toString()
            val isChecked = originalIndex in selectedPositions

            holder.tvTitle.text = displayText
            holder.cbItem.isChecked = isChecked
            holder.cbItem.buttonTintList = ColorStateList.valueOf(accentColor)

            val listener = View.OnClickListener {
                if (isChecked) {
                    selectedPositions.remove(originalIndex)
                } else {
                    selectedPositions.add(originalIndex)
                }
                notifyItemChanged(position)
                updateSelectionCounter()
            }

            holder.itemView.setOnClickListener(listener)
            holder.cbItem.setOnClickListener(listener)
        }

        override fun getItemCount(): Int = filteredItems.size

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cbItem: CheckBox = itemView.findViewById(R.id.cbItem)
            val tvTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
        }
    }
}
