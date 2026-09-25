package com.spradhan.uxcomponentLib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HorizontalCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tvHeader: TextView
    private val btnPrev: ImageView
    private val btnNext: ImageView
    private val rvCalendar: RecyclerView

    private var currentWeekStartCalendar = Calendar.getInstance()
    private val eventDotsMap = mutableMapOf<String, List<Int>>()
    private var dateSelectedListener: ((Date) -> Unit)? = null

    private var adapter: CalendarAdapter
    private var config: CalendarAdapter.CalendarConfig

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.horizontal_calendar_view, this, true)

        tvHeader = findViewById(R.id.tvCalendarHeader)
        btnPrev = findViewById(R.id.btnPrevWeek)
        btnNext = findViewById(R.id.btnNextWeek)
        rvCalendar = findViewById(R.id.rvCalendarDays)

        // Read XML configurations
        var headerColor = Color.parseColor("#333333")
        var cardBg = Color.parseColor("#F5F5F5")
        var selectedCardBg = Color.parseColor("#EAEAEA")
        var textColor = Color.parseColor("#4A4A4A")
        var selectedTextColor = Color.parseColor("#222222")
        var radiusPx = 16f

        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.HorizontalCalendarView)
            try {
                headerColor = ta.getColor(R.styleable.HorizontalCalendarView_hcv_headerTextColor, headerColor)
                cardBg = ta.getColor(R.styleable.HorizontalCalendarView_hcv_cardBgColor, cardBg)
                selectedCardBg = ta.getColor(R.styleable.HorizontalCalendarView_hcv_selectedCardBgColor, selectedCardBg)
                textColor = ta.getColor(R.styleable.HorizontalCalendarView_hcv_textColor, textColor)
                selectedTextColor = ta.getColor(R.styleable.HorizontalCalendarView_hcv_selectedTextColor, selectedTextColor)
                radiusPx = ta.getDimension(R.styleable.HorizontalCalendarView_hcv_cardCornerRadius, radiusPx)
            } finally {
                ta.recycle()
            }
        }

        tvHeader.setTextColor(headerColor)

        config = CalendarAdapter.CalendarConfig(
            cardBgColor = cardBg,
            selectedCardBgColor = selectedCardBg,
            textColor = textColor,
            selectedTextColor = selectedTextColor,
            cornerRadiusPx = radiusPx
        )

        // Lock to current week structure starting on Sunday or Monday
        currentWeekStartCalendar.set(Calendar.DAY_OF_WEEK, currentWeekStartCalendar.firstDayOfWeek)

        val weekDates = generateWeekDates(currentWeekStartCalendar.time)
        adapter = CalendarAdapter(weekDates, eventDotsMap, config) { selectedDate ->
            updateHeaderText(selectedDate)
            dateSelectedListener?.invoke(selectedDate)
        }

        rvCalendar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvCalendar.adapter = adapter

        updateHeaderText(Date())

        setupArrowTriggers()
    }

    private fun setupArrowTriggers() {
        btnPrev.setOnClickListener {
            currentWeekStartCalendar.add(Calendar.WEEK_OF_YEAR, -1)
            val newDates = generateWeekDates(currentWeekStartCalendar.time)
            adapter.updateDates(newDates)
            // auto-select first day of the new week to refresh view text
            adapter.selectedDate = newDates[0]
            updateHeaderText(newDates[0])
            dateSelectedListener?.invoke(newDates[0])
        }

        btnNext.setOnClickListener {
            currentWeekStartCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            val newDates = generateWeekDates(currentWeekStartCalendar.time)
            adapter.updateDates(newDates)
            adapter.selectedDate = newDates[0]
            updateHeaderText(newDates[0])
            dateSelectedListener?.invoke(newDates[0])
        }
    }

    private fun generateWeekDates(weekStartDate: Date): List<Date> {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance().apply { time = weekStartDate }
        for (i in 0 until 7) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return list
    }

    private fun updateHeaderText(date: Date) {
        val formatter = SimpleDateFormat("MMMM, d EEEE", Locale.US)
        tvHeader.text = formatter.format(date)
    }

    fun setOnDateSelectedListener(listener: (Date) -> Unit) {
        this.dateSelectedListener = listener
    }

    fun addEventDots(date: Date, colors: List<Int>) {
        val cal = Calendar.getInstance().apply { time = date }
        val dateKey = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
        eventDotsMap[dateKey] = colors
        adapter.notifyDataSetChanged()
    }
}