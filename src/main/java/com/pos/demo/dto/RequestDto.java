package com.pos.demo.dto;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pos.demo.util.Constants;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * This DTO holds the request data.
 * 
 */
@Data
public class RequestDto {

	// logger
	private Logger logger = LoggerFactory.getLogger(RequestDto.class);

	// input data elements validations
	@NotEmpty(message = Constants.TOOL_CODE_BLANK_ERR_MESSAGE)
	@Size(min = 3, max = 5, message = Constants.TOOL_CODE_ERR_MESSAGE)
	private String toolCode;

	@Digits(fraction = 0, integer = 4, message = Constants.RENTAL_DAYS_ERR_MESSAGE)
	private Integer rentalDays;

	@Digits(fraction = 2, integer = 2, message = Constants.DISCOUNT_PERCENT_ERR_MESSAGE)
	private Double discountPercent;

	@NotNull(message = Constants.CHECKOUT_DATE_BLANK_ERR_MESSAGE)
	@Future(message = Constants.CHECKOUT_DATE_ERR_MESSAGE)
	private Date checkoutDate;

	// Constructor
	public RequestDto(String toolCode, Integer rentalDays, Double discountPercent, Date checkoutDate) {
		this.toolCode = toolCode;
		this.rentalDays = rentalDays;
		this.discountPercent = discountPercent;
		this.checkoutDate = checkoutDate;

		logger.info("&&&&   Input Data   &&&&");
		logger.info("toolCode:" + toolCode);
		logger.info("rentalDays:" + rentalDays.toString());
		logger.info("checkoutDate:" + checkoutDate.toString());
		logger.info("discountPercent:" + discountPercent.toString());
	}

	// setters and getters
	public String getToolCode() {
		return toolCode;
	}

	public Integer getRentalDays() {
		return rentalDays;
	}

	public Double getDiscountPercent() {
		return discountPercent;
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}
}