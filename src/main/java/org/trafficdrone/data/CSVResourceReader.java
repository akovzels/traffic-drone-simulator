package org.trafficdrone.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

public class CSVResourceReader {

	private static final Logger logger = LoggerFactory.getLogger(CSVResourceReader.class);
	
	public <T> Stream<T> readFromResource(String resourse, Function<? super String[], ? extends T> lineMapper) {
		try {
			try (CSVReader reader = new CSVReader(
					new InputStreamReader(getClass().getResourceAsStream(resourse)))) {
				return reader.readAll().stream().map(lineMapper); 
			}
		} catch (IOException e) {
			logger.error("Error reading resource " + resourse);
			throw new RuntimeException(e);
		}
	}
}
