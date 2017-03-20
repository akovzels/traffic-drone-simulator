package org.trafficdrone;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.trafficdrone.data.PositionRequests;
import org.trafficdrone.data.Station;
import org.trafficdrone.data.Stations;
import org.trafficdrone.dispatcher.Dispatcher;
import org.trafficdrone.drone.Drone;
import org.trafficdrone.exchange.postion.PositionChannel;
import org.trafficdrone.exchange.report.TrafficReportChannel;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class Application {
	
	/**
	 * Drone cruising speed in m/s
	 */
	@Value("${dron.cruising.speed}")
	private double droneCruisingSpeed = 5.0; 
	
	/**
	 * Maximum distance in meters
	 */
	@Value("${dron.station.distance.threshold}")
	private double stationDistanceThreshold = 350;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public List<Station> stationLocations() {
		return new Stations().getAll();
	}
	
	@Bean
	public Drone drone1() {
		return new Drone(5937L, droneCruisingSpeed, stationDistanceThreshold);
	}
	
	@Bean
	public Drone drone2() {
		return new Drone(6043L, droneCruisingSpeed, stationDistanceThreshold);
	}
	
	@Bean
	public TrafficReportChannel reportChannel() {
		return new TrafficReportChannel(dispatcher());
	}
	
	@Bean
	public PositionChannel drone1Channel() {
		return new PositionChannel(drone1());		
	}
	
	@Bean
	public PositionChannel drone2Channel() {
		return new PositionChannel(drone2());		
	}
	
	@Bean
	public Dispatcher dispatcher() {
		return new Dispatcher(new PositionRequests(), LocalTime.of(8, 10));
	}

}
