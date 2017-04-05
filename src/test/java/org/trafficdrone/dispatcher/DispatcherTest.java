package org.trafficdrone.dispatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.trafficdrone.Position;
import org.trafficdrone.data.DronePositionsLoader;
import org.trafficdrone.data.model.DronePosition;
import org.trafficdrone.exchange.PositionChannel;
import org.trafficdrone.report.TrafficConditions;
import org.trafficdrone.report.TrafficReport;

public class DispatcherTest {
	
	private static final long DRONEID = 1L;
	private static final int HOUR = 10;
	
	@Mock
	private DronePositionsLoader dronePositionsLoader;
	@Mock
	private PositionChannel droneChannel;
	
	private Dispatcher dispatcher;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		dispatcher = new Dispatcher(dronePositionsLoader, LocalTime.of(HOUR, 55));
		dispatcher.addDroneChannel(DRONEID, droneChannel);
	}
	
	@Test
	public void testDispather() throws InterruptedException {
		
		when(dronePositionsLoader.getAllByDroneId(DRONEID)).thenReturn(getTestDronPositionsStream());
		Mockito.doNothing().when(droneChannel).sendPosition(ArgumentMatchers.any());
		
		dispatcher.start();
		
		Thread.sleep(1000L);
		
		dispatcher.stop();
	
		verify(droneChannel, Mockito.times(4)).sendPosition(ArgumentMatchers.argThat(p -> p.getDroneId() == DRONEID && p.getPosition() != null && !p.isShutdown()));
		verify(droneChannel).sendPosition(ArgumentMatchers.argThat(p -> Position.of(51.485302, -0.138126).equals(p.getPosition())));
		verify(droneChannel).sendPosition(ArgumentMatchers.argThat(p -> p.isShutdown()));
		
	}
	
	@Test
	public void testDispatherReceiveReport() throws InterruptedException {
		
		when(dronePositionsLoader.getAllByDroneId(DRONEID)).thenReturn(getTestDronPositionsStream());
		Mockito.doNothing().when(droneChannel).sendPosition(ArgumentMatchers.any());
		
		dispatcher.start();
		
		Thread.sleep(1000L);
		
		dispatcher.receiveReport(buildTrafficReport(DRONEID, TrafficConditions.HEAVY));
		dispatcher.receiveReport(buildTrafficReport(DRONEID, TrafficConditions.MODERATE));
		dispatcher.receiveReport(buildTrafficReport(DRONEID, TrafficConditions.LIGHT));
		dispatcher.receiveReport(TrafficReport.shutdownReport(DRONEID));
		
		dispatcher.stop();
	
		assertThat(dispatcher.getReports(), Matchers.hasSize(3));
		assertThat(dispatcher.getReports().get(0).getConditions(), is(TrafficConditions.HEAVY));
		
	}
	
	private Stream<DronePosition> getTestDronPositionsStream() {
		
		return Arrays.asList(
				buildDronePosition(51.485218, -0.13851, 0),
				buildDronePosition(51.485245, -0.138384, 10),
				buildDronePosition(51.485271, -0.138257, 15),
				buildDronePosition(51.485302, -0.138126, 20),
				buildDronePosition(51.485329, -0.137995, 55))
			.stream();
	}
	
	private DronePosition buildDronePosition(double latitude, double longtitude, int minute) {
		DronePosition dronePosition = new DronePosition();
		dronePosition.setDroneId(1L);
		dronePosition.setPosition(Position.of(latitude, longtitude));
		dronePosition.setTimestamp(LocalDateTime.now().withHour(HOUR).withMinute(minute));
		return dronePosition;
	}
	
	private TrafficReport buildTrafficReport(Long droneId, TrafficConditions trafficConditions) {
		TrafficReport report = new TrafficReport();
		report.setConditions(trafficConditions);
		report.setDroneId(droneId);
		report.setSpeed(1L);
		report.setTimestamp(LocalDateTime.now());
		return report;
	}
	
}
