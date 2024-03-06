package com.pos.demo.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Calculations {

	// logger
	private static Logger logger = LoggerFactory.getLogger(Calculations.class);

	private static final DecimalFormat decfor = new DecimalFormat("0.00");

	// Calculated from checkout date and rental days.
	public static Date calculateDueDate(Date checkoutDate, Integer rentalDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(checkoutDate);
		cal.add(Calendar.DATE, rentalDays);
		return cal.getTime();
	}

	// Count of chargeable days, from day after checkout through and including
	// duedate, excluding “no charge” days as specified by the tool type.
	// Independence Day, July 4th - If falls on weekend, it is observed on the
	// closest weekday
	// (if Sat, then Friday before, if Sunday, then Monday after)
	// Labor Day - First Monday in September
	public static Integer calculateChargeDays(Date checkoutDate, Date dueDate, Integer rentalDays, String weekendCharge,
			String holidayCharge) {

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(checkoutDate);
		int year = calendar.get(Calendar.YEAR);

		String july4th = "04-06-" + year;
		Date laborDay = firstMondayinSeptember(year);
		logger.info(july4th);
		logger.info(laborDay.toString());
		// Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

		try {
			if (between(formatter.parse(july4th), checkoutDate, dueDate)) {
				logger.info("July 4th");
				if ("No".equalsIgnoreCase(holidayCharge))
					rentalDays--;
			}
			if (between(laborDay, checkoutDate, dueDate)) {
				logger.info("Labor Day");
				if ("No".equalsIgnoreCase(holidayCharge))
					rentalDays--;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int weekends = rentalDays - getWorkingDaysBetweenTwoDates(checkoutDate, dueDate);
		logger.info("weekends :" + weekends);

		if ("No".equalsIgnoreCase(weekendCharge))
			rentalDays = rentalDays - weekends;

		return rentalDays;
	}

	// Calculated as charge days X daily charge. Resulting total rounded half upto
	// cents.
	public static Double calculatePreDiscountCharge(Integer chargeDays, Double dailyRentalCharge) {
		return Double.valueOf(decfor.format(chargeDays * dailyRentalCharge));
	}

	// Calculated from discount % and pre-discount charge. Resulting amount rounded
	// half up to cents.
	public static Double calculateDiscountAmount(Double preDiscountCharge, Double discountPercent) {
		return Double.valueOf(decfor.format(preDiscountCharge * discountPercent / 100));
	}

	// Calculated as pre-discount charge - discount amount.
	public static Double calculateFinalCharge(Double preDiscountCharge, Double discountAmount) {
		return Double.valueOf(decfor.format(preDiscountCharge - discountAmount));
	}

	public static boolean between(Date date, Date dateStart, Date dateEnd) {
		if (date != null && dateStart != null && dateEnd != null) {
			if (date.after(dateStart) && date.before(dateEnd)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static Date firstMondayinSeptember(int year) {
		ZoneId defaultZoneId = ZoneId.systemDefault();

		LocalDate curr = LocalDate.of(year, Month.SEPTEMBER, 1);
		// return curr.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
		Date date = Date.from(
				curr.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).atStartOfDay(defaultZoneId).toInstant());
		return date;
	}

	public static int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);

		int workDays = 0;

		// Return 0 if start and end are the same
		if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
			return 0;
		}

		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
			startCal.setTime(endDate);
			endCal.setTime(startDate);
		}

		do {
			// excluding start date
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				++workDays;
			}
		} while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); // excluding end date

		return workDays;
	}
}
