package org.trafficdrone.data;

import java.util.List;
import java.util.stream.Collectors;

import org.trafficdrone.Position;

public class Stations extends CSVResourceReader {
	private static final String STATIONS_RESOURCE = "/tube.csv";
	
	private final CSVResourceReader reader = new CSVResourceReader();
	
	public List<Station> getAll() {
		return reader.readFromResource(STATIONS_RESOURCE, 
				line -> {
					Station station = new Station();
					station.setName(line[0]);
					station.setPosition(Position.of(Double.parseDouble(line[1]), Double.parseDouble(line[2])));
					return station;
				}).collect(Collectors.toList());
	}
}
