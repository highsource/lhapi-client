package org.hisrc.lhapi.client.api.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hisrc.lhapi.client.AuthenticatingLhApiClient;
import org.hisrc.lhapi.client.LhApiClient;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthenticatingLhApiClientTest {

	private static final String CLIENT_SECRET_PROPERTY_KEY = "client_secret";
	private static final String CLIENT_ID_PROPERTY_KEY = "client_id";
	private static final String BASEPATH_PROPERTY_KEY = "basePath";
	private static final String LHAPI_PROPERTIES_RESOURCE_NAME = "lhapi.properties";
	private LhApiClient sut;

	@Before
	public void createLhApiClient() throws IOException {
		final Properties properties = new Properties();
		final InputStream is = getClass().getClassLoader().getResourceAsStream(LHAPI_PROPERTIES_RESOURCE_NAME);
		Assert.assertNotNull("Could not find the [lhapi.properties] resource. "
				+ "For tests, please create src/test/resources/lhapi.properties with your client_id and client_secret properties.");
		properties.load(is);
		final String clientId = properties.getProperty(CLIENT_ID_PROPERTY_KEY);
		final String clientSecret = properties.getProperty(CLIENT_SECRET_PROPERTY_KEY);
//		final String basePath = properties.getProperty(BASEPATH_PROPERTY_KEY);
		Assert.assertNotNull(clientId);
		Assert.assertNotNull(clientSecret);
		sut = new AuthenticatingLhApiClient(clientId, clientSecret);
	}

	@Test
	public void returnsArrivalsStatus() throws ApiException {
		FlightsStatusResponse arrivalsStatus = sut.arrivalsStatus("FRA", LocalDateTime.now().minusHours(1),
				LocalDateTime.now().plusHours(1));
		Assert.assertNotNull(arrivalsStatus);
	}

	@Test
	public void returnsDeparturesStatus() throws ApiException {
		FlightsStatusResponse departuresStatus = sut.departuresStatus("FRA", LocalDateTime.now().minusHours(1),
				LocalDateTime.now().plusHours(1));
		Assert.assertNotNull(departuresStatus);
	}

	@Test
	public void returnsFlightStatus() throws ApiException {
		// "SQ2379"
		FlightStatusResponse departuresStatus = sut.flightStatus("LO379", LocalDate.now());
		Assert.assertNotNull(departuresStatus);
	}

	@Test
	public void returnsAnotherFlightStatus() throws ApiException {
		// "SQ2379"
		FlightStatusResponse departuresStatus = sut.flightStatus("SQ2379", LocalDate.now());
		Assert.assertNotNull(departuresStatus);
	}

	@Test
	public void returnsCountries() throws ApiException {
		CountriesResponse countries = sut.countries(null, null);
		System.out.println("Countries: " + countries.getCountryResource().getCountries().getCountry().size());
		Assert.assertTrue(countries.getCountryResource().getCountries().getCountry().size() > 100);
	}

	@Test
	public void returnsCities() throws ApiException {
		CitiesResponse cities = sut.cities(null, null);
		System.out.println("Cities: " + cities.getCityResource().getCities().getCity().size());
		Assert.assertTrue(cities.getCityResource().getCities().getCity().size() > 100);
	}

	@Test
	public void returnsAirports() throws ApiException {
		AirportsResponse airports = sut.airports(null, null, null);
		System.out.println("Airports: " + airports.getAirportResource().getAirports().getAirport().size());
		Assert.assertTrue(airports.getAirportResource().getAirports().getAirport().size() > 100);
	}

	@Test
	public void returnsNearestAirports() throws ApiException {
		NearestAirportsResponse airports = sut.nearestAirports(50.110556, 8.682222, null);
		Assert.assertEquals(5, airports.getNearestAirportResource().getAirports().getAirport().size());
		Assert.assertEquals("FRA",
				airports.getNearestAirportResource().getAirports().getAirport().get(0).getAirportCode());
	}

	@Test
	public void returnsAirlines() throws ApiException {
		AirlinesResponse airlines = sut.airlines(null);
		System.out.println("Airlines: " + airlines.getAirlineResource().getAirlines().getAirline().size());
		Assert.assertTrue(airlines.getAirlineResource().getAirlines().getAirline().size() > 100);
	}

	@Test
	public void returnsAircrafts() throws ApiException {
		AircraftSummariesResponse aircrafts = sut.aircraftSummaries(null);
		System.out.println("Aircrafts: " + aircrafts.getAircraftResource().getAircraftSummaries().getAircraftSummary().size());
		Assert.assertTrue(aircrafts.getAircraftResource().getAircraftSummaries().getAircraftSummary().size() > 100);
	}

}
