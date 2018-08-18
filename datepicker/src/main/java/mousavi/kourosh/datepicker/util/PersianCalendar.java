package mousavi.kourosh.datepicker.util;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class PersianCalendar extends GregorianCalendar {

	private static final long serialVersionUID = 5541422440580682494L;

	private int persianYear;
	private int persianMonth;
	private int persianDay;
	private String delimiter = "/";

	private long convertToMilis(long julianDate) {
		return PersianCalendarConstants.MILLIS_JULIAN_EPOCH + julianDate * PersianCalendarConstants.MILLIS_OF_A_DAY
				+ PersianCalendarUtils.ceil(getTimeInMillis() - PersianCalendarConstants.MILLIS_JULIAN_EPOCH, PersianCalendarConstants.MILLIS_OF_A_DAY);
	}

	public PersianCalendar(long millis) {
		setTimeInMillis(millis);
	}

	public PersianCalendar() {
		super(TimeZone.getDefault(), Locale.getDefault());
	}

	protected void calculatePersianDate() {
		YearMonthDay persianYearMonthDay = PersianCalendar.gregorianToJalali(new YearMonthDay(this.get(PersianCalendar.YEAR), this.get(PersianCalendar.MONTH), this.get(PersianCalendar.DAY_OF_MONTH)));
		this.persianYear = persianYearMonthDay.year;
		this.persianMonth = persianYearMonthDay.month;
		this.persianDay = persianYearMonthDay.day;
	}

	public boolean isPersianLeapYear() {
		// calculatePersianDate();
		return PersianCalendarUtils.isPersianLeapYear(this.persianYear);
	}

	public void setPersianDate(int persianYear, int persianMonth, int persianDay) {
		this.persianYear = persianYear;
		this.persianMonth = persianMonth;
		this.persianDay = persianDay;
		YearMonthDay gregorianYearMonthDay = persianToGregorian(new YearMonthDay(persianYear, this.persianMonth - 1, persianDay));
		this.set(gregorianYearMonthDay.year, gregorianYearMonthDay.month, gregorianYearMonthDay.day);
	}

	public int getPersianYear() {
		return this.persianYear;
	}
	public int getPersianMonth() {
		// calculatePersianDate();
		return this.persianMonth + 1;
	}
	public String getPersianMonthName() {
		// calculatePersianDate();
		return PersianCalendarConstants.persianMonthNames[this.persianMonth];
	}

	public int getPersianDay() {
		// calculatePersianDate();
		return this.persianDay;
	}

	public String getPersianWeekDayName() {
		switch (get(DAY_OF_WEEK)) {
		case SATURDAY:
			return PersianCalendarConstants.persianWeekDays[0];
		case SUNDAY:
			return PersianCalendarConstants.persianWeekDays[1];
		case MONDAY:
			return PersianCalendarConstants.persianWeekDays[2];
		case TUESDAY:
			return PersianCalendarConstants.persianWeekDays[3];
		case WEDNESDAY:
			return PersianCalendarConstants.persianWeekDays[4];
		case THURSDAY:
			return PersianCalendarConstants.persianWeekDays[5];
		default:
			return PersianCalendarConstants.persianWeekDays[6];
		}

	}

	public String getPersianLongDate() {
		return getPersianWeekDayName() + "  " + this.persianDay + "  " + getPersianMonthName() + "  " + this.persianYear;
	}

	public String getPersianLongDateAndTime() {
		return getPersianLongDate() + " ساعت " + get(HOUR_OF_DAY) + ":" + get(MINUTE) + ":" + get(SECOND);
	}

	public String getPersianShortDate() {
		// calculatePersianDate();
		return "" + formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth()) + delimiter + formatToMilitary(this.persianDay);
	}

	public String getPersianShortDateTime() {
		return "" + formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth()) + delimiter + formatToMilitary(this.persianDay) + " " + formatToMilitary(this.get(HOUR_OF_DAY)) + ":" + formatToMilitary(get(MINUTE))
				+ ":" + formatToMilitary(get(SECOND));
	}

	private String formatToMilitary(int i) {
		return (i < 9) ? "0" + i : String.valueOf(i);
	}

	public void addPersianDate(int field, int amount) {
		if (amount == 0) {
			return; // Do nothing!
		}

		if (field < 0 || field >= ZONE_OFFSET) {
			throw new IllegalArgumentException();
		}

		if (field == YEAR) {
			setPersianDate(this.persianYear + amount, getPersianMonth(), this.persianDay);
			return;
		} else if (field == MONTH) {
			setPersianDate(this.persianYear + ((getPersianMonth() + amount) / 12), (getPersianMonth() + amount) % 12, this.persianDay);
			return;
		}
		add(field, amount);
		calculatePersianDate();
	}

	public void parse(String dateString) {
		PersianCalendar p = new PersianDateParser(dateString, delimiter).getPersianDate();
		setPersianDate(p.getPersianYear(), p.getPersianMonth(), p.getPersianDay());
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String toString() {
		String str = super.toString();
		return str.substring(0, str.length() - 1) + ",PersianDate=" + getPersianShortDate() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);

	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public void set(int field, int value) {
		super.set(field, value);
		calculatePersianDate();
	}

	@Override
	public void setTimeInMillis(long millis) {
		super.setTimeInMillis(millis);
		calculatePersianDate();
	}

	@Override
	public void setTimeZone(TimeZone zone) {
		super.setTimeZone(zone);
		calculatePersianDate();
	}

	private static int gregorianDaysInMonth[] = {31, 28, 31, 30, 31, 30, 31,
		31, 30, 31, 30, 31};
	private static int persianDaysInMonth[] = {31, 31, 31, 31, 31, 31, 30, 30,
		30, 30, 30, 29};

	private static YearMonthDay gregorianToJalali(YearMonthDay gregorian) {

		if (gregorian.getMonth() > 11 || gregorian.getMonth() < -11) {
			throw new IllegalArgumentException();
		}
		int persianYear;
		int persianMonth;
		int persianDay;

		int gregorianDayNo, persianDayNo;
		int persianNP;
		int i;

		gregorian.setYear(gregorian.getYear() - 1600);
		gregorian.setDay(gregorian.getDay() - 1);

		gregorianDayNo = 365 * gregorian.getYear() + (int) Math.floor((gregorian.getYear() + 3) / 4)
			- (int) Math.floor((gregorian.getYear() + 99) / 100)
			+ (int) Math.floor((gregorian.getYear() + 399) / 400);
		for (i = 0; i < gregorian.getMonth(); ++i) {
			gregorianDayNo += gregorianDaysInMonth[i];
		}

		if (gregorian.getMonth() > 1 && ((gregorian.getYear() % 4 == 0 && gregorian.getYear() % 100 != 0)
			|| (gregorian.getYear() % 400 == 0))) {
			++gregorianDayNo;
		}

		gregorianDayNo += gregorian.getDay();

		persianDayNo = gregorianDayNo - 79;

		persianNP = (int) Math.floor(persianDayNo / 12053);
		persianDayNo = persianDayNo % 12053;

		persianYear = 979 + 33 * persianNP + 4 * (int) (persianDayNo / 1461);
		persianDayNo = persianDayNo % 1461;

		if (persianDayNo >= 366) {
			persianYear += (int) Math.floor((persianDayNo - 1) / 365);
			persianDayNo = (persianDayNo - 1) % 365;
		}

		for (i = 0; i < 11 && persianDayNo >= persianDaysInMonth[i]; ++i) {
			persianDayNo -= persianDaysInMonth[i];
		}
		persianMonth = i;
		persianDay = persianDayNo + 1;

		return new YearMonthDay(persianYear, persianMonth, persianDay);
	}


	private static YearMonthDay persianToGregorian(YearMonthDay persian) {
		if (persian.getMonth() > 11 || persian.getMonth() < -11) {
			throw new IllegalArgumentException();
		}

		int gregorianYear;
		int gregorianMonth;
		int gregorianDay;

		int gregorianDayNo, persianDayNo;
		int leap;

		int i;
		persian.setYear(persian.getYear() - 979);
		persian.setDay(persian.getDay() - 1);

		persianDayNo = 365 * persian.getYear() + (int) (persian.getYear() / 33) * 8
			+ (int) Math.floor(((persian.getYear() % 33) + 3) / 4);
		for (i = 0; i < persian.getMonth(); ++i) {
			persianDayNo += persianDaysInMonth[i];
		}

		persianDayNo += persian.getDay();

		gregorianDayNo = persianDayNo + 79;

		gregorianYear = 1600 + 400 * (int) Math.floor(gregorianDayNo / 146097); /* 146097 = 365*400 + 400/4 - 400/100 + 400/400 */
		gregorianDayNo = gregorianDayNo % 146097;

		leap = 1;
		if (gregorianDayNo >= 36525) /* 36525 = 365*100 + 100/4 */ {
			gregorianDayNo--;
			gregorianYear += 100 * (int) Math.floor(gregorianDayNo / 36524); /* 36524 = 365*100 + 100/4 - 100/100 */
			gregorianDayNo = gregorianDayNo % 36524;

			if (gregorianDayNo >= 365) {
				gregorianDayNo++;
			} else {
				leap = 0;
			}
		}

		gregorianYear += 4 * (int) Math.floor(gregorianDayNo / 1461); /* 1461 = 365*4 + 4/4 */
		gregorianDayNo = gregorianDayNo % 1461;

		if (gregorianDayNo >= 366) {
			leap = 0;

			gregorianDayNo--;
			gregorianYear += (int) Math.floor(gregorianDayNo / 365);
			gregorianDayNo = gregorianDayNo % 365;
		}

		for (i = 0; gregorianDayNo >= gregorianDaysInMonth[i] + ((i == 1 && leap == 1) ? i : 0); i++) {
			gregorianDayNo -= gregorianDaysInMonth[i] + ((i == 1 && leap == 1) ? i : 0);
		}
		gregorianMonth = i;
		gregorianDay = gregorianDayNo + 1;

		return new YearMonthDay(gregorianYear, gregorianMonth, gregorianDay);

	}

	static class YearMonthDay {

		YearMonthDay(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}

		private int year;
		private int month;
		private int day;

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getMonth() {
			return month;
		}

		public void setMonth(int month) {
			this.month = month;
		}

		public int getDay() {
			return day;
		}

		public void setDay(int date) {
			this.day = date;
		}

		public String toString() {
			return getYear() + "/" + getMonth() + "/" + getDay();
		}
	}

}
