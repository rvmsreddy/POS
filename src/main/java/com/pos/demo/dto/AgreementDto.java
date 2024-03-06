package com.pos.demo.dto;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.pos.demo.exception.RecordNotFoundException;
import com.pos.demo.util.Calculations;
import com.pos.demo.util.Constants;
import com.pos.demo.util.LoadCSV;

import lombok.Data;

/**
 * This DTO holds the processed data.
 * 
 */
@Data
public class AgreementDto {

	// logger
	private Logger logger = LoggerFactory.getLogger(AgreementDto.class);

	// application context
	private ApplicationContext context;

	// Tool Details
	private String toolCode;
	private String toolType;
	private String toolBrand;

	// Brand Details
	private Double dailyRentalCharge;
	private String weekdayCharge;
	private String weekendCharge;
	private String holidayCharge;

	// Input Details
	private Integer rentalDays;
	private Date checkoutDate;
	private Double discountPercent;

	// Calculated Variables
	private Date dueDate;
	private Integer chargeDays;
	private Double preDiscountCharge;
	private Double discountAmount;
	private Double finalCharge;

	public AgreementDto(ApplicationContext context, RequestDto checkoutRequestDto) {
		this.context = context;
		this.toolCode = checkoutRequestDto.getToolCode();
		this.rentalDays = checkoutRequestDto.getRentalDays();
		this.checkoutDate = checkoutRequestDto.getCheckoutDate();
		this.discountPercent = checkoutRequestDto.getDiscountPercent();
	}

	public void calculate() {

		dueDate = Calculations.calculateDueDate(checkoutDate, rentalDays);
		logger.info("dueDate:" + dueDate.toString());

		chargeDays = Calculations.calculateChargeDays(checkoutDate, dueDate, rentalDays, weekendCharge, holidayCharge);
		logger.info("chargeDays:" + chargeDays.toString());

		preDiscountCharge = Calculations.calculatePreDiscountCharge(chargeDays, dailyRentalCharge);
		logger.info("preDiscountCharge:" + preDiscountCharge.toString());

		discountAmount = Calculations.calculateDiscountAmount(preDiscountCharge, discountPercent);
		logger.info("discountAmount:" + discountAmount.toString());

		finalCharge = Calculations.calculateFinalCharge(preDiscountCharge, discountAmount);
		logger.info("finalCharge:" + finalCharge.toString());
	}

	public String rentalAgreement() {

		String newLine = System.getProperty("line.separator");

		String agreement = Constants.TOOL_CODE.concat(toolCode).concat(newLine).concat(Constants.TOOL_TYPE)
				.concat(toolType).concat(newLine).concat(Constants.TOOL_BRAND).concat(toolBrand).concat(newLine)
				.concat(Constants.RENTAL_DAYS).concat(rentalDays.toString()).concat(newLine)
				.concat(Constants.CHECKOUT_DATE).concat(checkoutDate.toString()).concat(newLine)
				.concat(Constants.DAILY_RENTAL_CHARGE).concat(dailyRentalCharge.toString()).concat(newLine)
				.concat(Constants.DISCOUNT_PERCENT).concat(discountPercent.toString()).concat(newLine)
				.concat(Constants.WEEKDAY_CHARGE).concat(weekdayCharge).concat(newLine).concat(Constants.WEEKEND_CHARGE)
				.concat(weekendCharge).concat(newLine).concat(Constants.HOLIDAY_CHARGE).concat(holidayCharge)
				.concat(newLine).concat(Constants.CHARGE_DAYS).concat(chargeDays.toString()).concat(newLine)
				.concat(Constants.PREDISCOUNT_CHARGE).concat(preDiscountCharge.toString()).concat(newLine)
				.concat(Constants.DISCOUNT_AMOUNT).concat(discountAmount.toString()).concat(newLine)
				.concat(Constants.FINAL_CHARGE).concat(finalCharge.toString()).concat(newLine);

		return agreement;
	}

	public void loadFromTables() throws RecordNotFoundException {

		LoadCSV loadCSV = context.getBean("loadCSV", LoadCSV.class);
		List<List<String>> brands = loadCSV.loadCSV(Constants.BRAND_TABLE);
		List<List<String>> tools = loadCSV.loadCSV(Constants.TOOLS_TABLE);

		boolean bool = false;

		for (List<String> tool : tools) {
			int column = 0;
			for (String row : tool) {
				column++;
				// logger.info(row);
				if (row.equalsIgnoreCase(toolCode))
					bool = true;
				if (bool && column == 2)
					this.toolType = row;
				if (bool && column == 3)
					this.toolBrand = row;
			}
			if (bool)
				break;
		}

		logger.info("bool :" + bool);
		if (bool == false)
			throw new RecordNotFoundException("Tool Code '" + toolCode + "' does not exist in table.");

		bool = false;

		for (List<String> brand : brands) {
			int column = 0;
			for (String row : brand) {
				column++;
				// logger.info(row);
				if (row.equalsIgnoreCase(this.toolType))
					bool = true;
				if (column == 2)
					this.dailyRentalCharge = Double.parseDouble(row);
				if (column == 3)
					this.weekdayCharge = row;
				if (column == 4)
					this.weekendCharge = row;
				if (column == 5)
					this.holidayCharge = row;
			}
			if (bool)
				break;
		}

		logger.info("&&&&   After loading tables and search   &&&&");
		logger.info("toolCode:" + toolCode);
		logger.info("toolType:" + toolType);
		logger.info("toolBrand:" + toolBrand);
		logger.info("rentalDays:" + rentalDays.toString());
		logger.info("checkoutDate:" + checkoutDate.toString());
		logger.info("dailyRentalCharge:" + dailyRentalCharge.toString());
		logger.info("discountPercent:" + discountPercent.toString());
		logger.info("weekdayCharge:" + weekdayCharge);
		logger.info("weekendCharge:" + weekendCharge);
		logger.info("holidayCharge:" + holidayCharge);
	}
}