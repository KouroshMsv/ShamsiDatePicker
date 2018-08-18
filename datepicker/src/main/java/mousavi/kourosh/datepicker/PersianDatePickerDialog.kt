package mousavi.kourosh.datepicker

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import mousavi.kourosh.datepicker.util.PersianCalendar
import mousavi.kourosh.datepicker.util.PersianHelper
import java.util.*

/**
 * Created by aliabdolahi on 1/23/17.
 */

class PersianDatePickerDialog(private val context: Context) {
    private var positiveButtonString = "تایید"
    private var negativeButtonString = "انصراف"
    private var listener: Listener? = null
    private var maxYear = 0
    private var minYear = 0
    private var initDate: PersianCalendar? = null
    private var pCalendar: PersianCalendar? = null
    private var todayButtonString = "امروز"
    private var todayButtonVisibility = false
    private var backgroundColor = Color.WHITE
    private var titleColor = Color.WHITE
    private var cancelable = true
    private var forceMode: Boolean = false
    private var pickerBackgroundColor: Int = 0
    private var pickerBackgroundDrawable: Int = 0
    var typeFace: Typeface? = null

    fun setListener(listener: Listener): PersianDatePickerDialog {
        this.listener = listener
        return this
    }

    fun setMaxYear(maxYear: Int): PersianDatePickerDialog {
        this.maxYear = maxYear
        return this
    }

    fun setTypeFace(typeFace: Typeface): PersianDatePickerDialog {
        this.typeFace = typeFace
        return this
    }

    fun setMinYear(minYear: Int): PersianDatePickerDialog {
        this.minYear = minYear
        return this
    }

    fun setInitDate(initDate: PersianCalendar, force: Boolean = false): PersianDatePickerDialog {
        this.forceMode = force
        this.initDate = initDate
        return this
    }

    fun setPositiveButtonString(positiveButtonString: String): PersianDatePickerDialog {
        this.positiveButtonString = positiveButtonString
        return this
    }

    fun setPositiveButtonResource(@StringRes positiveButton: Int): PersianDatePickerDialog {
        this.positiveButtonString = context.getString(positiveButton)
        return this
    }

    fun setTodayButtonVisible(todayButtonVisiblity: Boolean): PersianDatePickerDialog {
        this.todayButtonVisibility = todayButtonVisiblity
        return this
    }

    fun setTodayButton(todayButton: String): PersianDatePickerDialog {
        this.todayButtonString = todayButton
        return this
    }

    fun setTodayButtonResource(@StringRes todayButton: Int): PersianDatePickerDialog {
        this.todayButtonString = context.getString(todayButton)
        return this
    }

    fun setNegativeButton(negativeButton: String): PersianDatePickerDialog {
        this.negativeButtonString = negativeButton
        return this
    }

    fun setNegativeButtonResource(@StringRes negativeButton: Int): PersianDatePickerDialog {
        this.negativeButtonString = context.getString(negativeButton)
        return this
    }

    fun setActionTextColor(@ColorInt colorInt: Int): PersianDatePickerDialog {
        //    this.actionColor = colorInt;
        return this
    }

    fun setActionTextColorResource(@ColorRes colorInt: Int): PersianDatePickerDialog {
        //    this.actionColor = ContextCompat.getColor(context, colorInt);
        return this
    }

    fun setCancelable(cancelable: Boolean): PersianDatePickerDialog {
        this.cancelable = cancelable
        return this
    }

    fun setBackgroundColor(@ColorInt bgColor: Int): PersianDatePickerDialog {
        this.backgroundColor = bgColor
        return this
    }

    fun setTitleColor(@ColorInt titleColor: Int): PersianDatePickerDialog {
        this.titleColor = titleColor
        return this
    }

    fun setPickerBackgroundColor(@ColorInt color: Int): PersianDatePickerDialog {
        this.pickerBackgroundColor = color
        return this
    }

    fun setPickerBackgroundDrawable(@DrawableRes drawableBg: Int): PersianDatePickerDialog {
        this.pickerBackgroundDrawable = drawableBg
        return this
    }

    fun show() {

        pCalendar = PersianCalendar()

        val v = View.inflate(context, R.layout.dialog_picker, null)
        val datePicker = v.findViewById<PersianDatePicker>(R.id.datePicker)
        val dateText = v.findViewById<TextView>(R.id.dateText)
        val positiveButton = v.findViewById<AppCompatButton>(R.id.positive_button)
        val negativeButton = v.findViewById<AppCompatButton>(R.id.negative_button)
        val todayButton = v.findViewById<AppCompatButton>(R.id.today_button)
        val container = v.findViewById<LinearLayout>(R.id.container)

        container.setBackgroundColor(backgroundColor)
        dateText.setTextColor(titleColor)

        if (pickerBackgroundColor != 0) {
            datePicker.setBackgroundColor(pickerBackgroundColor)
        } else if (pickerBackgroundDrawable != 0) {
            datePicker.background = ContextCompat.getDrawable(context, pickerBackgroundDrawable)
        }

        if (maxYear > 0) {
            datePicker.setMaxYear(maxYear)
        } else if (maxYear == THIS_YEAR) {
            maxYear = pCalendar!!.persianYear
            datePicker.setMaxYear(pCalendar!!.persianYear)
        }

        if (minYear > 0) {
            datePicker.setMinYear(minYear)
        } else if (minYear == THIS_YEAR) {
            minYear = pCalendar!!.persianYear
            datePicker.setMinYear(pCalendar!!.persianYear)
        }

        if (initDate != null) {
            val initYear = initDate!!.persianYear
            if (initYear > maxYear || initYear < minYear) {
                Log.e("PERSIAN CALENDAR", "init year is more/less than minYear/maxYear")
                if (forceMode) {
                    datePicker.displayPersianDate = initDate!!
                }
            } else {
                datePicker.displayPersianDate = initDate!!
            }

        }

        if (typeFace != null) {
            dateText.typeface = typeFace
            positiveButton.typeface = typeFace
            negativeButton.typeface = typeFace
            todayButton.typeface = typeFace
        }

        //    positiveButton.setTextColor(actionColor);
        //    negativeButton.setTextColor(actionColor);
        //    todayButton.setTextColor(actionColor);

        positiveButton.text = positiveButtonString
        negativeButton.text = negativeButtonString
        todayButton.text = todayButtonString

        if (todayButtonVisibility) {
            todayButton.visibility = View.VISIBLE
        }

        pCalendar = datePicker.displayPersianDate
        updateView(dateText)
        datePicker.setOnDateChangedListener(object : PersianDatePicker.OnDateChangedListener {
            override fun onDateChanged(newYear: Int, newMonth: Int, newDay: Int) {
                pCalendar!!.setPersianDate(newYear, newMonth, newDay)
                updateView(dateText)
            }
        })

        val dialog = AlertDialog.Builder(context)
                .setView(v)
                .setCancelable(cancelable)
                .create()

        negativeButton.setOnClickListener { 
            if (listener != null) {
                listener!!.onDismissed()
            }
            dialog.dismiss()
        }

        positiveButton.setOnClickListener {
            if (listener != null) {
                listener!!.onDateSelected(datePicker.displayPersianDate)
            }
            dialog.dismiss()
        }
        todayButton.setOnClickListener {

            datePicker.displayDate = Date()

            if (maxYear > 0) {
                datePicker.setMaxYear(maxYear)
            }

            if (minYear > 0) {
                datePicker.setMinYear(minYear)
            }

            pCalendar = datePicker.displayPersianDate
            updateView(dateText)
        }

        dialog.show()
    }

    private fun updateView(dateText: TextView) {
        val date = pCalendar!!.persianWeekDayName + " " +
                pCalendar!!.persianDay + " " +
                pCalendar!!.persianMonthName + " " +
                pCalendar!!.persianYear
        dateText.text = PersianHelper.toPersianNumber(date)
    }

    interface Listener {

        fun onDateSelected(persianCalendar: PersianCalendar)

        fun onDismissed()
    }

    companion object {
        val THIS_YEAR = -1
    }
}
