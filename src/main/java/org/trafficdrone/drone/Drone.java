package org.trafficdrone.drone;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trafficdrone.Position;
import org.trafficdrone.data.Station;
import org.trafficdrone.exchange.postion.PositionRequest;
import org.trafficdrone.exchange.report.TrafficConditions;
import org.trafficdrone.exchange.report.TrafficReport;
import org.trafficdrone.exchange.report.TrafficReportChannel;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.javadocmd.simplelatlng.window.CircularWindow;

public class Drone {
	
	private static final Logger logger = LoggerFactory.getLogger(Drone.class);
	
	private final long id;
	
	/**
	 * Current drone position;
	 */
	private Position position;
	
	/**
	 * Position move to;
	 */
	private Position nextPosition;
	
	private Thread thread = null;

	private volatile boolean threadDone = false;
	
	private final BlockingQueue<PositionRequest> positionsQueue = new ArrayBlockingQueue<>(10);
	
	private TrafficReportChannel reportChannel;
	
	/**
	 *  List of all CircularWindow of tube stations. 
	 */
	private List<CircularWindow> stationsCircularWindowList;
	
	/**
	 * Drone cruising speed
	 */
	private final double cruisingSpeed;  
	
	/**
	 * Distance threshold
	 */
	private final double stationDistanceThreshold; 
	
	public Drone(long id, double cruisingSpeed, double stationDistanceThreshold) {
		this.id = id;
		this.cruisingSpeed = cruisingSpeed;
		this.stationDistanceThreshold = stationDistanceThreshold;
	}
	
	/**
	 * Start drone background thread.
	 */
	public void start() {
		if (thread != null) {
            return;
		}
      
		threadDone = false;
	  
        thread = new Thread(new DroneBackgroundProcess(), "Drone [" + id + "] controlling thread");
        thread.setDaemon(true);
        thread.start();
        logger.info("Started " + this);
	}
	
	public void stop() {
		threadDone = true;		
	}
	
	public long getId() {
		return id;
	}

	public void receivePosition(PositionRequest positionRequest) {
		try {
			positionsQueue.put(positionRequest);
		} catch (InterruptedException e) {
			// Do not throw exception here. Just skipping.
			logger.error("Drone " + id + ": error inserting position request into queue", e);
		}
	}

	@Autowired
	public void setReportChannel(TrafficReportChannel reportChannel) {
		this.reportChannel = reportChannel;
	}
	
	@Autowired
	public void setStationLocations(List<Station> stationLocations) {
		stationsCircularWindowList = stationLocations.stream().map(station -> 
			new CircularWindow(
					new LatLng(station.getPosition().getLatitude(), station.getPosition().getLongitude()), stationDistanceThreshold, LengthUnit.METER)
		).collect(Collectors.toList());
	}
	
	protected class DroneBackgroundProcess implements Runnable {

        @Override
        public void run() {
        	
        	Navigation navigation = new Navigation(getId());
        	
        	Throwable t = null;
          
            try {
                while (!threadDone) {
                    try {
                    	Thread.sleep(1L * 100L); //TODO externalize
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                    if (!threadDone) {
                    	
                    	// Drone just started. Getting initial position.
                    	if (position == null) {
                    		PositionRequest positionRequest = positionsQueue.poll();
                    		if (positionRequest == null) {
                    			continue;
                    		}
                    		if (positionRequest.isShutdown()) {
                    			threadDone = true;
                    			continue;
                    		}
                			position = Position.of(positionRequest.getPosition().getLatitude(), positionRequest.getPosition().getLongitude());
                			// Check if there are stations nearby for just set position
                			checkNearbyStationsAndReport();
                    	}
                    	
                    	// position is not null at this point. Drone is not moving and waiting for the next position
                    	if (nextPosition == null) {
                    		PositionRequest positionRequest = positionsQueue.poll();
                    		if (positionRequest == null) {
                    			continue;
                    		}
                    		if (positionRequest.isShutdown()) {
                    			threadDone = true;
                    			continue;
                    		}
                    		
                    		nextPosition = Position.of(positionRequest.getPosition().getLatitude(), positionRequest.getPosition().getLongitude());
                        	// Start moving to the next position
                    		navigation.moveTo(position, nextPosition, cruisingSpeed);
                    	} else {
                    		// Current position and next position are not null. 
                    		// It means drone is moving to the next position. Lets check where it is.
                    		if (navigation.checkIfPositionReached()) {
                    			// If target position is reached, set current position to previous target position, reset target (next) and send report if required
                    			position = nextPosition;
                    			nextPosition = null;
                    			checkNearbyStationsAndReport();
                    		}
                    	}
                    }
                }
            } catch (RuntimeException | Error e) {
                t = e;
                throw e;
            } finally {
                if (!threadDone) {
                    logger.error("Drone " + getId() + ": process interrupted with error " + t, t);
                }
                // Sending shutdown report to dispatcher.
                reportChannel.sendReport(TrafficReport.shutdownReport(id));
                navigation.scheduler.shutdown();
                logger.info("Stopped " + Drone.this);
            }
        }
        
        private final Random random = new Random();
    	
    	/**
    	 * Method checks if drone is passing any tube station and send report if so
    	 */
    	private void checkNearbyStationsAndReport() {
    		if (stationsCircularWindowList.stream()
    				.anyMatch(w -> w.contains(new LatLng(position.getLatitude(), position.getLongitude())))) {
    			TrafficReport report = new TrafficReport();
    			report.setDroneId(getId());
    			report.setSpeed(cruisingSpeed);
    			report.setTimestamp(LocalDateTime.now());
    			report.setConditions(TrafficConditions.values()[random.nextInt(TrafficConditions.values().length)]);
    			logger.debug("Drone " + getId() + " found station nearby");
    			reportChannel.sendReport(report);
    		}
    	}
	}

	
	/**
	 * I externalized logic of drone's position calculation here
	 * I don't want to have all those state variable inside Drone class. They are very specific to a way how we simulate drone movement.  
	 */
	protected static class Navigation {
		
		private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		
		private boolean positionReached;
		
		private final long dronId;
		
		Navigation(long dronId) {
			this.dronId = dronId; 
		}
		
		public void moveTo(Position currentPosition, Position newPosition, double speed) {
			positionReached = false;
			LatLng current = new LatLng(currentPosition.getLatitude(), currentPosition.getLongitude());
			LatLng moveTo = new LatLng(newPosition.getLatitude(), newPosition.getLongitude());
			double l = LatLngTool.distance(current, moveTo, LengthUnit.METER);
			double t = l / speed;
			scheduler.schedule(this::reachPosition, (long) (t * 1000L), TimeUnit.MILLISECONDS);
			logger.debug("Drone " + dronId + " moving to " + newPosition + ". Estimated in " + t + " seconds.");
		}
		
		public boolean checkIfPositionReached() {
			return positionReached; 
		}
		
		private void reachPosition() {
			positionReached = true;
		}
	}

	@Override
	public String toString() {
		return "Drone [id=" + id + ", cruisingSpeed=" + cruisingSpeed + ", stationDistanceThreshold=" + stationDistanceThreshold + "]";
	}

}