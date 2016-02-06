package org.hisrc.lhapi.client.api.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hisrc.lhapi.client.AuthenticatingLhApiClient;
import org.hisrc.lhapi.client.LhApiClient;
import org.hisrc.lhapi.client.invoker.ApiException;
import org.hisrc.lhapi.client.model.FlightStatusResponse;
import org.hisrc.lhapi.client.model.FlightsStatusResponse;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthenticatingLhApiClientTest {

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
	public void returnsArrivalsStatus() throws ApiException {
		FlightsStatusResponse arrivalsStatus = sut.arrivalsStatus("FRA", LocalDateTime.now().minusHours(1),
				LocalDateTime.now().plusHours(1));
		Assert.assertNotNull(arrivalsStatus);
		System.out.println(arrivalsStatus);
	}

	@Test
	public void returnsDeparturesStatus() throws ApiException {
		FlightsStatusResponse departuresStatus = sut.departuresStatus("DME", LocalDateTime.now().minusHours(1),
				LocalDateTime.now().plusHours(1));
		Assert.assertNotNull(departuresStatus);
		System.out.println(departuresStatus);
	}

	@Test
	public void returnsFlightStatus() throws ApiException {
		// "SQ2379"
		FlightStatusResponse departuresStatus = sut.flightStatus("LO379", LocalDate.now());
		Assert.assertNotNull(departuresStatus);
		System.out.println(departuresStatus);
	}
	@Test
	public void returnsAnotherFlightStatus() throws ApiException {
		// "SQ2379"
		FlightStatusResponse departuresStatus = sut.flightStatus("SQ2379", LocalDate.now());
		Assert.assertNotNull(departuresStatus);
		System.out.println(departuresStatus);
	}
}
