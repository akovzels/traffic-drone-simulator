package org.trafficdrone.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.trafficdrone.Position;
import org.trafficdrone.exchange.postion.PositionRequest;

public class PositionRequests {

	private final CSVResourceReader reader = new CSVResourceReader();
	
	public Stream<PositionRequest> getAllByDroneId(Long droneId) {
		return reader.readFromResource("/" + droneId + ".csv", 
				line -> {
					PositionRequest positionRequest = new PositionRequest();
					positionRequest.setDroneId(Long.valueOf(line[0]));
					positionRequest.setPosition(Position.of(Double.parseDouble(line[1]), Double.parseDouble(line[2])));
					positionRequest.setTimestamp(LocalDateTime.parse(line[3], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
					return positionRequest;
				});
	}

}
