package org.hisrc.lhapi.client;


import org.hisrc.lhapi.client.invoker.ApiException;
import org.hisrc.lhapi.client.model.FlightStatusResponse;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public interface LhApiClient {

	FlightStatusResponse flightStatus(String flightNumber, LocalDate date) throws ApiException;

	FlightStatusResponse arrivalsStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws ApiException;

	FlightStatusResponse departuresStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws ApiException;

}