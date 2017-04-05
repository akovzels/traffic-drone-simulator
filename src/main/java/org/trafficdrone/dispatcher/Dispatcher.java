package org.trafficdrone.dispatcher;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.trafficdrone.data.DronePositionsLoader;
import org.trafficdrone.data.model.DronePosition;
import org.trafficdrone.exchange.PositionChannel;
import org.trafficdrone.report.TrafficReport;


public class Dispatcher implements Lifecycle {
	
	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	
	private final List<TrafficReport> reports = new LinkedList<>();
	
	private Map<Long, PositionChannel> droneChannels = new HashMap<>();
		
	private final DronePositionsLoader dronePositionsLoader;
	
	private final LocalTime stopTime;
	
	private CountDownLatch droneShutdownLatch;
	
	private volatile boolean running = false;
	
	private Object lifecycleMonitor = new Object();
	
	public Dispatcher(DronePositionsLoader dronePositionsLoader, LocalTime stopTime) {
		this.dronePositionsLoader = dronePositionsLoader;
		this.stopTime = stopTime;
	}
	
	public void addDroneChannel(Long droneId, PositionChannel droneChannel) {
		droneChannels.put(droneId, droneChannel);
	}
	
	public void receiveReport(TrafficReport report) {
		if (report.isShutdown()) {
			droneShutdownLatch.countDown();
			logger.info("Shutdown reported from drone " + report.getDroneId());
		} else {
			reports.add(report);
			logger.info("Report received " + report);
		}
	}

	@Override
	public void start() {
		synchronized (this.lifecycleMonitor) {
			if (running) {
				return;
			}
			startDispatching();
			running = true;
			logger.info("Started " + this);
		}
	}

	@Override
	public void stop() {
		synchronized (this.lifecycleMonitor) {
			running = false;
			logger.info("Stopped " + this);
		}
	}

	@Override
	public boolean isRunning() {
		synchronized (this.lifecycleMonitor) {
			return running;
		}
	}

	private void startDispatching() {
		
		droneShutdownLatch = new CountDownLatch(droneChannels.size());
		
		droneChannels.forEach((droneId, channel) -> {
			CompletableFuture.runAsync(() -> {
				try {
					dronePositionsLoader.getAllByDroneId(droneId)
						.filter(position -> position.getTimestamp().toLocalTime().isBefore(stopTime))
						.forEach(position -> channel.sendPosition(position));
					channel.sendPosition(DronePosition.shutdownRequest(droneId));
				} catch (Exception e) {
					logger.error("Error sending position data", e);
					droneShutdownLatch.countDown();
				}
			});
			
		});
		
		CompletableFuture.runAsync(() -> {
			try {
				droneShutdownLatch.await();
			} catch (InterruptedException e) {
				logger.error("Dispatcher thread was unexpectedly interrupted", e);
			}
		})
		.thenRun(this::stop);
		
	}

	@Override
	public String toString() {
		return "Dispatcher [droneChannels=" + droneChannels + ", stopTime=" + stopTime + "]";
	}

	public List<TrafficReport> getReports() {
		return reports;
	}
}
