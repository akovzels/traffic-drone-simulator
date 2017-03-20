package org.trafficdrone.exchange.postion;

import java.time.LocalDateTime;

import org.trafficdrone.Position;

public class PositionRequest {

	private long droneId;

	private Position position;

	private LocalDateTime timestamp;

	private boolean shutdown;

	public static final PositionRequest shutdownRequest(Long droneId) {
		PositionRequest request = new PositionRequest();
		request.setDroneId(droneId);
		request.setShutdown(true);
		return request;
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
		return "PositionRequest [droneId=" + droneId + ", position=" + position + ", timestamp=" + timestamp + ", shutdown=" + shutdown + "]";
	}

}
