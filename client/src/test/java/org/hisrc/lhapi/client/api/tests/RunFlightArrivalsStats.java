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

public class RunFlightArrivalsStats {

	private static final String CLIENT_SECRET_PROPERTY_KEY = "client_secret";
	private static final String CLIENT_ID_PROPERTY_KEY = "client_id";
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
		Assert.assertNotNull(clientId);
		Assert.assertNotNull(clientSecret);
		sut = new AuthenticatingLhApiClient(clientId, clientSecret);
	}

	@Test
	public void returnsArrivalsStatus() {
		LocalDateTime start = LocalDateTime.parse("2016-02-16T00:00:00");
		for (int index = 0; index < (6 * 24); index++) {
			LocalDateTime from = start.plusMinutes(index * 10);
			LocalDateTime to = start.plusMinutes((index + 1) * 10);
			try {
				FlightsStatusResponse response = sut.arrivalsStatus("FRA", from, to);
				System.out.println("[" + LocalDateTime.now() +"] From " + from + " to " + to + ": "
						+ response.getFlightStatusResource().getFlights().getFlight().size());
				Thread.sleep(250);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
