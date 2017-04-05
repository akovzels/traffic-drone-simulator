package org.trafficdrone.data.model;

import java.time.LocalDateTime;

import org.trafficdrone.Position;

public class DronePosition {

	private long droneId;

	private Position position;

	private LocalDateTime timestamp;

	private boolean shutdown;

	public static final DronePosition shutdownRequest(Long droneId) {
		DronePosition dronePosition = new DronePosition();
		dronePosition.setDroneId(droneId);
		dronePosition.setShutdown(true);
		return dronePosition;
	}
	
	public static final DronePosition at(Long droneId, Position position) {
		DronePosition dronePosition = new DronePosition();
		dronePosition.setDroneId(droneId);
		dronePosition.setPosition(position);
		return dronePosition;
	}

	public long getDroneId() {
		return droneId;
	}

	public void setDroneId(long droneId) {
		this.droneId = droneId;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "DronePosition [droneId=" + droneId + ", position=" + position + ", timestamp=" + timestamp + ", shutdown=" + shutdown + "]";
	}

}
