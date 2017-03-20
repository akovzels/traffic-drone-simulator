package org.trafficdrone.exchange.postion;

import org.trafficdrone.drone.Drone;

public class PositionChannel {

	private final Drone drone;
	
	public PositionChannel(Drone drone) {
		this.drone = drone;
	}
	
	public Long getDroneId() {
		return drone.getId();
	}
	
	public void sendPosition(PositionRequest position) {
		drone.receivePosition(position);
	}

	@Override
	public String toString() {
		return "PositionChannel [droneId=" + getDroneId() + "]";
	}

}
