package org.trafficdrone.exchange;

import org.trafficdrone.data.model.DronePosition;
import org.trafficdrone.drone.Drone;

public class PositionChannel {

	private final Drone drone;
	
	public PositionChannel(Drone drone) {
		this.drone = drone;
	}
	
	public Long getDroneId() {
		return drone.getId();
	}
	
	public void sendPosition(DronePosition position) {
		drone.receivePosition(position);
	}

	@Override
	public String toString() {
		return "PositionChannel [droneId=" + getDroneId() + "]";
	}

}
