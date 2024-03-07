package com.pos.demo.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * This class has logic to load Brand and Tool data.
 * 
 * @version 1.0
 */
@Component
public class LoadCSV {

	private final static Logger logger = LoggerFactory.getLogger(LoadCSV.class);

	@Autowired
	private ResourceLoader resourceLoader;

	// This is to load CSV file. Usually this would be reading from a table in a
	// database.
	//
	public List<List<String>> loadCSV(String fileName) {

		List<List<String>> records = new ArrayList<List<String>>();
		Resource resource = resourceLoader.getResource("classpath:" + fileName);

		logger.info("Loading Sheets");

		try {
			InputStream is = resource.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			try (CSVReader csvReader = new CSVReader(br)) {
				String[] values = null;
				while ((values = csvReader.readNext()) != null) {
					records.add(Arrays.asList(values));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvValidationException e) {
			e.printStackTrace();
		}

		return records;
	}
}