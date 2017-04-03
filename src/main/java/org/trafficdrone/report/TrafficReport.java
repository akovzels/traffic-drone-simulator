package org.trafficdrone.report;

import java.time.LocalDateTime;

public class TrafficReport {

	private long droneId;

	private LocalDateTime timestamp;

	private double speed;

	private TrafficConditions conditions;

	private boolean shutdown;
	
	public static final TrafficReport shutdownReport(Long droneId) {
		TrafficReport report = new TrafficReport();
		report.setDroneId(droneId);
		report.setShutdown(true);
		return report;
	}

	public long getDroneId() {
		return droneId;
	}

	public void setDroneId(long droneId) {
		this.droneId = droneId;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public TrafficConditions getConditions() {
		return conditions;
	}

	public void setConditions(TrafficConditions conditions) {
		this.conditions = conditions;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	@Override
	public String toString() {
		return "TrafficReport [droneId=" + droneId + ", timestamp=" + timestamp + ", speed=" + speed + ", conditions="
				+ conditions + ", shutdown=" + shutdown + "]";
	}
}
