package org.hisrc.lhapi.client;

import org.hisrc.lhapi.client.invoker.ApiException;
import org.hisrc.lhapi.client.model.AircraftSummariesResponse;
import org.hisrc.lhapi.client.model.AirlinesResponse;
import org.hisrc.lhapi.client.model.AirportsResponse;
import org.hisrc.lhapi.client.model.CitiesResponse;
import org.hisrc.lhapi.client.model.CountriesResponse;
import org.hisrc.lhapi.client.model.FlightStatusResponse;
import org.hisrc.lhapi.client.model.FlightsStatusResponse;
import org.hisrc.lhapi.client.model.NearestAirportsResponse;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public interface LhApiClient {

	CountriesResponse countries(String countryCode, String lang) throws ApiException;
	
	CitiesResponse cities(String cityCode, String lang) throws ApiException;

	AirportsResponse airports(String airportCode, String lang, Boolean lhOperates) throws ApiException;
	
	NearestAirportsResponse nearestAirports(Double latitude, Double longitude, String lang) throws ApiException;

	AirlinesResponse airlines(String airlineCode) throws ApiException;

	AircraftSummariesResponse aircraftSummaries(String aircraftCode) throws ApiException;

	FlightStatusResponse flightStatus(String flightNumber, LocalDate date) throws ApiException;

	FlightsStatusResponse arrivalsStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws ApiException;

	FlightsStatusResponse departuresStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws ApiException;


}