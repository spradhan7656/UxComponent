package com.spradhan.uxcomponentLib

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Date

class CalendarAdapter(
    private var dates: List<Date>,
    private val eventDotsMap: Map<String, List<Int>>,
    private val config: CalendarConfig,
    private val onDateSelected: (Date) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    var selectedDate: Date = Date()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    data class CalendarConfig(
        val cardBgColor: Int,
        val selectedCardBgColor: Int,
        val textColor: Int,
        val selectedTextColor: Int,
        val cornerRadiusPx: Float
    )

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.cardContainer)
        val tvDateNumber: TextView = view.findViewById(R.id.tvDateNumber)
        val tvDayName: TextView = view.findViewById(R.id.tvDayName)
        val dotContainer: LinearLayout = view.findViewById(R.id.dotContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = dates[position]
        val cal = Calendar.getInstance().apply { time = date }

        val dateNumber = cal.get(Calendar.DAY_OF_MONTH).toString()
        val dayName = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> ""
        }

        holder.tvDateNumber.text = dateNumber
        holder.tvDayName.text = dayName

        val isSelected = isSameDay(date, selectedDate)

        // Draw curved cards backgrounds dynamically
        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = config.cornerRadiusPx
            setColor(if (isSelected) config.selectedCardBgColor else config.cardBgColor)
        }
        holder.container.background = backgroundDrawable

        val activeTextColor = if (isSelected) config.selectedTextColor else config.textColor
        holder.tvDateNumber.setTextColor(activeTextColor)
        holder.tvDayName.setTextColor(if (isSelected) activeTextColor else Color.parseColor("#7A7A7A"))

        // Bind multi event dots indicators
        holder.dotContainer.removeAllViews()
        val dateKey = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
        eventDotsMap[dateKey]?.let { dotsColors ->
            for (color in dotsColors) {
                val dot = View(holder.itemView.context).apply {
                    val size = (6 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(size, size).apply {
                        setMargins(
                            (2 * resources.displayMetrics.density).toInt(), 0,
                            (2 * resources.displayMetrics.density).toInt(), 0
                        )
                    }
                    val shapeDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(color)
                    }
                    background = shapeDrawable
                }
                holder.dotContainer.addView(dot)
            }
        }

        holder.itemView.setOnClickListener {
            selectedDate = date
            onDateSelected(date)
        }
    }

    override fun getItemCount(): Int = dates.size

    fun updateDates(newDates: List<Date>) {
        dates = newDates
        notifyDataSetChanged()
    }

    private fun isSameDay(d1: Date, d2: Date): Boolean {
        val c1 = Calendar.getInstance().apply { time = d1 }
        val c2 = Calendar.getInstance().apply { time = d2 }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }
}