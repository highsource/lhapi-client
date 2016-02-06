package org.hisrc.lhapi.client.api.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.hisrc.lhapi.client.LhApiClient;
import org.hisrc.lhapi.client.OAuth2LhApiClient;
import org.hisrc.lhapi.client.invoker.ApiException;
import org.hisrc.lhapi.client.model.FlightStatusResponse;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LhApiClientTest {

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
		sut = new OAuth2LhApiClient(clientId, clientSecret);
	}

	@Test
	public void returnsArrivalsStatus() throws ApiException {
		FlightStatusResponse arrivalsStatus = sut.arrivalsStatus("FRA", LocalDateTime.now().minusHours(1),
				LocalDateTime.now().plusHours(1));
		Assert.assertNotNull(arrivalsStatus);
		System.out.println(arrivalsStatus);
	}

}
