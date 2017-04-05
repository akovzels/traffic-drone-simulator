package org.trafficdrone.drone;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trafficdrone.Position;
import org.trafficdrone.data.model.DronePosition;
import org.trafficdrone.data.model.Station;
import org.trafficdrone.exchange.PositionChannel;
import org.trafficdrone.exchange.TrafficReportChannel;

public class DroneTest {
	
	private static final Logger logger = LoggerFactory.getLogger(DroneTest.class);
	
	private static final long DRONEID = 1L;
	private static final double CSUISING_SPEED = 100;
	
	private static final List<Station> STATIONS = Arrays.asList(
			new Station("Cradle of History", Position.of(36.15008, -5.3492362)), 
			new Station("St. Paul's Church", Position.of(36.146576, -5.355759)), 
			new Station("American War Memorial", Position.of(36.143337, -5.354429)), 
			new Station("King's Bastion", Position.of(36.139655, -5.354949))); 
	
	private static final List<Position> DRONE_COORDINATES = Arrays.asList(
			Position.of(36.156952, -5.350499), Position.of(36.156952, -5.350499), Position.of(36.148542, -5.349304),
			Position.of(36.144901, -5.353480), Position.of(36.143662, -5.358129), Position.of(36.146261, -5.356456),
			Position.of(36.139187, -5.356112), Position.of(36.138875, -5.351305), Position.of(36.141693, -5.352507),
			Position.of(36.141693, -5.352507), Position.of(36.143539, -5.354747), Position.of(36.148811, -5.356893),
			Position.of(36.156676, -5.352160)); 
	
	private double totalDistance;
	
	private double totalTime;
	
	private Drone drone;
	
	private PositionChannel droneChannel;
	
	@Mock
	private TrafficReportChannel reportChannel;
	
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		drone = new Drone(DRONEID, CSUISING_SPEED, 100);
		drone.setStationLocations(STATIONS);
		drone.setReportChannel(reportChannel);
		
		droneChannel = new PositionChannel(drone);
		
		Mockito.doAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			logger.info("sendReport {}", args);
			return null;
		}).when(reportChannel).sendReport(ArgumentMatchers.any());
		
		// Assume that DRONE_COORDINATES has size > 1
		totalDistance = 0;
		for (int i = 0; i < DRONE_COORDINATES.size() - 1; i++) {
			totalDistance += DRONE_COORDINATES.get(i).distanceTo(DRONE_COORDINATES.get(i + 1));
		}
		totalTime = totalDistance / CSUISING_SPEED;
		//Mockito.doNothing().when(reportChannel).sendReport(Mockito.any());
	}
	
	@Test
	public void testDrone() throws InterruptedException {
		long currentMillis = System.currentTimeMillis();
		
		drone.start();
		
		DRONE_COORDINATES.stream().forEach(position -> droneChannel.sendPosition(DronePosition.at(DRONEID, position)));
		
		droneChannel.sendPosition(DronePosition.shutdownRequest(DRONEID));
		
		while (drone.isRunning()) {
			Thread.sleep(100L);
		}
		
		Mockito.verify(reportChannel, Mockito.times(3)).sendReport(ArgumentMatchers.argThat(report -> report.getDroneId() == DRONEID));
		Mockito.verify(reportChannel, Mockito.times(1)).sendReport(ArgumentMatchers.argThat(report -> report.getDroneId() == DRONEID && report.isShutdown()));
		
		assertThat((double) (System.currentTimeMillis() - currentMillis) / 1000, Matchers.both(Matchers.lessThan(totalTime + 5)).and(Matchers.greaterThan(totalTime - 5)));
		
	}
}
