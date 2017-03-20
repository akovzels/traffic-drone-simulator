package org.trafficdrone.exchange.report;

import org.trafficdrone.dispatcher.Dispatcher;

public class TrafficReportChannel {

	private final Dispatcher dispather;
	
	public TrafficReportChannel(Dispatcher dispather) {
		this.dispather = dispather;
	}

	public void sendReport(TrafficReport report) {
		dispather.receiveReport(report);
	}
}
