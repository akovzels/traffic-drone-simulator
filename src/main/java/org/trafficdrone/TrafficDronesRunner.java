package org.trafficdrone;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.trafficdrone.dispatcher.Dispatcher;
import org.trafficdrone.drone.Drone;

@Component
public class TrafficDronesRunner implements CommandLineRunner {
	
	@Autowired
	private Dispatcher dispatcher;
	@Autowired
	private List<Drone> drones;
	 
	@Override
	public void run(String... args) throws Exception {
		
		drones.forEach(Drone::start);
		
		dispatcher.start();
		
		while (dispatcher.isRunning()) {
			Thread.sleep(100L);
		}
	}

}
