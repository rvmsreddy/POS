package com.pos.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pos.demo.dto.AgreementDto;
import com.pos.demo.dto.RequestDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/pos")
public class PosController {

	private final static Logger logger = LoggerFactory.getLogger(PosController.class);

	@Autowired
	private ApplicationContext context;

	/**
	 * This is the main operation. Restful endpoint.
	 *
	 * @param checkoutRequestDto - Request Data Transfer Object
	 * @return rental agreement
	 */
	@PostMapping(value = "/checkout", consumes = "application/json")
	public String checkout(@Valid @RequestBody RequestDto requestDto) {

		logger.info("In Rest Controller");
		AgreementDto agreementDto = new AgreementDto(context, requestDto);
		// load related data from tables
		agreementDto.loadFromTables();
		// calculate
		agreementDto.calculate();
		// prepare rental agreement
		return agreementDto.rentalAgreement();
	}
}
