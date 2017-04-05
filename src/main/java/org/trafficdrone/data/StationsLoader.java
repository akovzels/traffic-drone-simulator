package org.trafficdrone.data;

import java.util.List;
import java.util.stream.Collectors;

import org.trafficdrone.Position;
import org.trafficdrone.data.model.Station;

public class StationsLoader extends CSVResourceReader {
	private static final String STATIONS_RESOURCE = "/tube.csv";
	
	private final CSVResourceReader reader = new CSVResourceReader();
	
	/**
	 * @return List of tube station locations.
	 */
	public List<Station> getAll() {
		return reader.readFromResource(STATIONS_RESOURCE, 
				line -> new Station(line[0], Position.of(Double.parseDouble(line[1]), Double.parseDouble(line[2])))
			).collect(Collectors.toList());
	}
}
