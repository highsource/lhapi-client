package org.hisrc.lhapi.client;

import org.apache.commons.lang3.Validate;
import org.hisrc.lhapi.client.api.DefaultApi;
import org.hisrc.lhapi.client.invoker.ApiClient;
import org.hisrc.lhapi.client.invoker.ApiException;
import org.hisrc.lhapi.client.model.AccessToken;
import org.hisrc.lhapi.client.model.FlightStatusResponse;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OAuth2LhApiClient implements LhApiClient {

	// Expiration buffer - 15 minutes
	private static final int EXPIRATION_BUFFER = 15 * 60 * 1000;
	private final static String GRANT_TYPE = "client_credentials";
	private final static String API_KEY_PREFIX_BEARER = "Bearer";
	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
	private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");

	private final ApiClient apiClient = new ApiClient();
	private final DefaultApi api = new DefaultApi(this.apiClient);
	private final String clientId;
	private final String clientSecret;

	private long accessTokenExpirationTimestamp = Long.MIN_VALUE;

	public OAuth2LhApiClient(final String clientId, final String clientSecret) {
		Validate.notNull(clientId);
		Validate.notNull(clientSecret);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	private void refreshAccessToken() throws ApiException {
		final long currentTimestamp = System.currentTimeMillis();
		final AccessToken accessToken = api.oauthTokenPost(this.clientId, this.clientSecret, GRANT_TYPE);
		if (accessToken == null) {
			throw new ApiException("Authentication returned null access token.");
		}
		final String token = accessToken.getAccessToken();
		if (token == null) {
			throw new ApiException("Authentication returned null token.");
		}
		final Integer expiresIn = accessToken.getExpiresIn();
		if (expiresIn == null) {
			throw new ApiException("Authentication returned null expiration date.");
		}
		this.apiClient.setApiKey(API_KEY_PREFIX_BEARER + " " + token);
		this.accessTokenExpirationTimestamp = currentTimestamp + (expiresIn.intValue() * 1000) - EXPIRATION_BUFFER;
	}

	@Override
	public FlightStatusResponse flightStatus(String flightNumber, LocalDate date) throws ApiException {
		return executeAuthentified(() -> operationsFlightstatusFlightNumberDateGet(flightNumber, date));
	}

	@Override
	public FlightStatusResponse arrivalsStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws ApiException {
		return executeAuthentified(
				() -> operationsFlightstatusArrivalsAirportCodeFromUntilGet(airportCode, from, until));
	}

	@Override
	public FlightStatusResponse departuresStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws ApiException {
		return executeAuthentified(
				() -> operationsFlightstatusDeparturesAirportCodeFromUntilGet(airportCode, from, until));
	}

	private <T> T executeAuthentified(ApiOperation<T> operation) throws ApiException {
		final long currentTimestamp = System.currentTimeMillis();
		// if (this.accessTokenExpirationTimestamp < currentTimestamp) {
		// refreshAccessToken();
		// }
		try {
			// Try executing the operation
			return operation.execute();
		} catch (ApiException apiex) {
			// If an exception occurs first assume it is because of the expired
			// token
			// So refresh it and try again
			refreshAccessToken();
			return operation.execute();
		}
	}

	private FlightStatusResponse operationsFlightstatusFlightNumberDateGet(String flightNumber, LocalDate date)
			throws ApiException {

		final String dateAsString = DATE_FORMATTER.print(date);
		return this.api.operationsFlightstatusFlightNumberDateGet(flightNumber, dateAsString, null);
	}

	private FlightStatusResponse operationsFlightstatusArrivalsAirportCodeFromUntilGet(String airportCode,
			LocalDateTime from, LocalDateTime until) throws ApiException {

		final String fromAsString = DATE_TIME_FORMATTER.print(from);
		final String untilAsString = DATE_TIME_FORMATTER.print(until);
		return this.api.operationsFlightstatusArrivalsAirportCodeFromUntilGet(airportCode, fromAsString, untilAsString,
				null);
	}

	private FlightStatusResponse operationsFlightstatusDeparturesAirportCodeFromUntilGet(String airportCode,
			LocalDateTime from, LocalDateTime until) throws ApiException {

		final String fromAsString = DATE_TIME_FORMATTER.print(from);
		final String untilAsString = DATE_TIME_FORMATTER.print(until);
		return this.api.operationsFlightstatusDeparturesAirportCodeFromUntilGet(airportCode, fromAsString,
				untilAsString, null);
	}

	@FunctionalInterface
	private interface ApiOperation<T> {
		public T execute() throws ApiException;
	}
}
