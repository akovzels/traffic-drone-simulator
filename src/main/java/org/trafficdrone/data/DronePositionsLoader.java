package org.trafficdrone.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.trafficdrone.Position;
import org.trafficdrone.data.model.DronePosition;

public class DronePositionsLoader {

	private final CSVResourceReader reader = new CSVResourceReader();
	
	/**
	 * @param droneId Id of drone
	 * @return a Stream positions for drone with provided id.
	 */
	public Stream<DronePosition> getAllByDroneId(Long droneId) {
		return reader.readFromResource("/" + droneId + ".csv", 
				line -> {
					DronePosition dronePosition = new DronePosition();
					dronePosition.setDroneId(Long.valueOf(line[0]));
					dronePosition.setPosition(Position.of(Double.parseDouble(line[1]), Double.parseDouble(line[2])));
					dronePosition.setTimestamp(LocalDateTime.parse(line[3], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
					return dronePosition;
				});
	}

}
