package org.hisrc.lhapi.client;

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

	CountriesResponse countries(String countryCode, String lang) throws LhApiException;

	CitiesResponse cities(String cityCode, String lang) throws LhApiException;

	AirportsResponse airports(String airportCode, String lang, Boolean lhOperates) throws LhApiException;

	NearestAirportsResponse nearestAirports(Double latitude, Double longitude, String lang) throws LhApiException;

	AirlinesResponse airlines(String airlineCode) throws LhApiException;

	AircraftSummariesResponse aircraftSummaries(String aircraftCode) throws LhApiException;

	FlightStatusResponse flightStatus(String flightNumber, LocalDate date) throws LhApiException;

	FlightsStatusResponse arrivalsStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws LhApiException;

	FlightsStatusResponse departuresStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws LhApiException;

}