package org.trafficdrone.exchange;

import org.trafficdrone.dispatcher.Dispatcher;
import org.trafficdrone.report.TrafficReport;

public class TrafficReportChannel {

	private final Dispatcher dispather;
	
	public TrafficReportChannel(Dispatcher dispather) {
		this.dispather = dispather;
	}

	public void sendReport(TrafficReport report) {
		dispather.receiveReport(report);
	}
}
