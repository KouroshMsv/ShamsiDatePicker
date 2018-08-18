package mousavi.kourosh.datepicker

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import mousavi.kourosh.datepicker.util.PersianCalendar
import mousavi.kourosh.datepicker.util.PersianCalendarConstants
import mousavi.kourosh.datepicker.util.PersianCalendarUtils
import mousavi.kourosh.datepicker.util.PersianHelper
import java.util.*


internal class PersianDatePicker(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(mContext, attrs, defStyleAttr) {
    constructor(mContext: Context, attrs: AttributeSet? = null) : this(mContext, attrs, 0)

    private val pCalendar: PersianCalendar
    private var selectedMonth: Int = 0
    private var selectedYear: Int = 0
    private var selectedDay: Int = 0
    private var displayMonthNames: Boolean = false
    private var mListener: OnDateChangedListener? = null
    private val yearNumberPicker: NumberPicker
    private val monthNumberPicker: NumberPicker
    private val dayNumberPicker: NumberPicker

    private var minYear: Int = 0
    private var maxYear: Int = 0

    private var typeFace: Typeface? = null
    private var dividerColor: Int = 0
    private var yearRange: Int = 0

    private var dateChangeListener: NumberPicker.OnValueChangeListener

    var displayDate: Date
        get() {
            val displayPersianDate = PersianCalendar()
            displayPersianDate.setPersianDate(yearNumberPicker.value, monthNumberPicker.value, dayNumberPicker.value)
            return displayPersianDate.time
        }
        set(displayDate) {
            displayPersianDate = PersianCalendar(displayDate.time)
        }

    // if you pass selected year before min year, then we need to push min year to before that
    // if you pass selected year after max year, then we need to push max year to after that
    var displayPersianDate: PersianCalendar
        get() {
            val displayPersianDate = PersianCalendar()
            displayPersianDate.setPersianDate(yearNumberPicker.value, monthNumberPicker.value, dayNumberPicker.value)
            return displayPersianDate
        }
        set(displayPersianDate) {
            val year = displayPersianDate.persianYear
            val month = displayPersianDate.persianMonth
            var day = displayPersianDate.persianDay
            if (month in 7..11 && day == 31) {
                day = 30
            } else {
                val isLeapYear = PersianCalendarUtils.isPersianLeapYear(year)
                if (isLeapYear && day == 31) {
                    day = 30
                } else if (day > 29) {
                    day = 29
                }
            }


            selectedYear = year
            selectedMonth = month
            selectedDay = day
            if (minYear > selectedYear) {
                minYear = selectedYear - yearRange
                yearNumberPicker.minValue = minYear
            }
            if (maxYear < selectedYear) {
                maxYear = selectedYear + yearRange
                yearNumberPicker.maxValue = maxYear
            }

            yearNumberPicker.value = year
            monthNumberPicker.value = month
            dayNumberPicker.value = day
        }

    private fun updateVariablesFromXml(context: Context, attrs: AttributeSet?) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.PersianDatePicker, 0, 0)
        yearRange = a.getInteger(R.styleable.PersianDatePicker_yearRange, 10)
        /*
         * Initializing yearNumberPicker min and max values If minYear and
         * maxYear attributes are not set, use (current year - 10) as min and
         * (current year + 10) as max.
         */
        minYear = a.getInt(R.styleable.PersianDatePicker_minYear, pCalendar.persianYear - yearRange)
        maxYear = a.getInt(R.styleable.PersianDatePicker_maxYear, pCalendar.persianYear + yearRange)
        displayMonthNames = a.getBoolean(R.styleable.PersianDatePicker_displayMonthNames, false)
        /*
         * displayDescription
         */
        selectedDay = a.getInteger(R.styleable.PersianDatePicker_selectedDay, pCalendar.persianDay)
        selectedYear = a.getInt(R.styleable.PersianDatePicker_selectedYear, pCalendar.persianYear)
        selectedMonth = a.getInteger(R.styleable.PersianDatePicker_selectedMonth, pCalendar.persianMonth)

        // if you pass selected year before min year, then we need to push min year to before that
        if (minYear > selectedYear) {
            minYear = selectedYear - yearRange
        }

        if (maxYear < selectedYear) {
            maxYear = selectedYear + yearRange
        }

        a.recycle()
    }

    init {

        // inflate views
        val view = LayoutInflater.from(context).inflate(R.layout.sl_persian_date_picker, this)

        // get views
        yearNumberPicker = view.findViewById(R.id.yearNumberPicker)
        monthNumberPicker = view.findViewById(R.id.monthNumberPicker)
        dayNumberPicker = view.findViewById(R.id.dayNumberPicker)

        dateChangeListener = NumberPicker.OnValueChangeListener { _, _, _ ->
            val year = yearNumberPicker.value
            val isLeapYear = PersianCalendarUtils.isPersianLeapYear(year)

            val month = monthNumberPicker.value
            val day = dayNumberPicker.value

            if (month < 7) {
                dayNumberPicker.minValue = 1
                dayNumberPicker.maxValue = 31
            } else if (month < 12) {
                if (day == 31) {
                    dayNumberPicker.value = 30
                }
                dayNumberPicker.minValue = 1
                dayNumberPicker.maxValue = 30
            } else if (month == 12) {
                if (isLeapYear) {
                    if (day == 31) {
                        dayNumberPicker.value = 30
                    }
                    dayNumberPicker.minValue = 1
                    dayNumberPicker.maxValue = 30
                } else {
                    if (day > 29) {
                        dayNumberPicker.value = 29
                    }
                    dayNumberPicker.minValue = 1
                    dayNumberPicker.maxValue = 29
                }
            }
            if (mListener != null) {
                mListener!!.onDateChanged(yearNumberPicker.value, monthNumberPicker.value,
                        dayNumberPicker.value)
            }
        }

        yearNumberPicker.setFormatter { i -> PersianHelper.toPersianNumber(i.toString()) }

        monthNumberPicker.setFormatter { i -> PersianHelper.toPersianNumber(i.toString()) }

        dayNumberPicker.setFormatter { i -> PersianHelper.toPersianNumber(i.toString()) }

        // init calendar
        pCalendar = PersianCalendar()

        // update variables from xml
        updateVariablesFromXml(context, attrs)

        // update view
        updateViewData()
    }

    fun setMaxYear(maxYear: Int) {
        this.maxYear = maxYear
        updateViewData()
    }

    fun setMinYear(minYear: Int) {
        this.minYear = minYear
        updateViewData()
    }

    fun setDividerColor(@ColorInt color: Int) {
        this.dividerColor = color
        updateViewData()
    }

    private fun setDividerColor(picker: NumberPicker, color: Int) {

        val pickerFields = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    val colorDrawable = ColorDrawable(color)
                    pf.set(picker, colorDrawable)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                break
            }
        }
    }

    private fun updateViewData() {

        dividerColor = R.color.primary

        if (dividerColor > 0) {
            setDividerColor(yearNumberPicker, dividerColor)
            setDividerColor(monthNumberPicker, dividerColor)
            setDividerColor(dayNumberPicker, dividerColor)
        }

        yearNumberPicker.minValue = minYear
        yearNumberPicker.maxValue = maxYear


        if (selectedYear > maxYear) {
            selectedYear = maxYear
        }
        if (selectedYear < minYear) {
            selectedYear = minYear
        }

        yearNumberPicker.value = selectedYear
        yearNumberPicker.setOnValueChangedListener(dateChangeListener)

        /*
         * initialing monthNumberPicker
         */

        monthNumberPicker.minValue = 1
        monthNumberPicker.maxValue = 12
        if (displayMonthNames) {
            monthNumberPicker.displayedValues = PersianCalendarConstants.persianMonthNames
        }

        if (selectedMonth < 1 || selectedMonth > 12) {
            throw IllegalArgumentException(String.format("Selected month (%d) must be between 1 and 12", selectedMonth))
        }
        monthNumberPicker.value = selectedMonth
        monthNumberPicker.setOnValueChangedListener(dateChangeListener)

        /*
         * initializing dayNumberPicker
         */
        dayNumberPicker.minValue = 1
        dayNumberPicker.maxValue = 31
        if (selectedDay > 31 || selectedDay < 1) {
            throw IllegalArgumentException(String.format("Selected day (%d) must be between 1 and 31", selectedDay))
        }
        if (selectedMonth in 7..11 && selectedDay == 31) {
            selectedDay = 30
        } else {
            val isLeapYear = PersianCalendarUtils.isPersianLeapYear(selectedYear)
            if (isLeapYear && selectedDay == 31) {
                selectedDay = 30
            } else if (selectedDay > 29) {
                selectedDay = 29
            }
        }
        dayNumberPicker.value = selectedDay
        dayNumberPicker.setOnValueChangedListener(dateChangeListener)
    }

    fun setOnDateChangedListener(onDateChangedListener: OnDateChangedListener) {
        mListener = onDateChangedListener
    }

    interface OnDateChangedListener {

        fun onDateChanged(newYear: Int, newMonth: Int, newDay: Int)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)

        ss.datetime = this.displayDate.time
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)

        displayDate = Date(state.datetime)
    }

    internal class SavedState : View.BaseSavedState {
        var datetime: Long = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            this.datetime = `in`.readLong()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeLong(this.datetime)
        }

        companion object {

            // required field that makes Parcelables from a Parcel
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState> {
                    return arrayOf(*newArray(size))
                }
            }
        }
    }

}
