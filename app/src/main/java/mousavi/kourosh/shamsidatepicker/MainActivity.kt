package mousavi.kourosh.shamsidatepicker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import mousavi.kourosh.datepicker.PersianDatePickerDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PersianDatePickerDialog(this).show()
    }
}
