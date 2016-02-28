package org.hisrc.lhapi.client;

import org.apache.commons.lang3.Validate;
import org.hisrc.lhapi.client.api.DefaultApi;
import org.hisrc.lhapi.client.invoker.ApiClient;
import org.hisrc.lhapi.client.invoker.ApiException;
import org.hisrc.lhapi.client.model.AccessToken;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.sun.jersey.api.client.ClientHandlerException;

public class AuthenticatingLhApiClient implements LhApiClient {

	// Expiration buffer - 15 minutes
	private static final int EXPIRATION_BUFFER = 15 * 60 * 1000;
	private final static String GRANT_TYPE = "client_credentials";
	private final static String API_KEY_PREFIX_BEARER = "Bearer";
	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
	private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");

	private final ApiClient apiClient;
	private final DefaultApi api;
	private final String clientId;
	private final String clientSecret;

	private long accessTokenExpirationTimestamp = Long.MIN_VALUE;

	public AuthenticatingLhApiClient(final String basePath, final String clientId, final String clientSecret) {
		Validate.notNull(clientId);
		Validate.notNull(clientSecret);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.apiClient = new ApiClient();
		this.apiClient.getObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		if (basePath != null) {
			this.apiClient.setBasePath(basePath);
		}
		this.api = new DefaultApi(this.apiClient);
	}

	public AuthenticatingLhApiClient(final String clientId, final String clientSecret) {
		this(null, clientId, clientSecret);
	}

	private void refreshAccessToken() throws LhApiException {
		try {
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
		} catch (ApiException apiex) {
			throw new LhApiException("Error refreshing the access token.", apiex);

		}
	}

	@Override
	public CountriesResponse countries(String countryCode, String lang) throws LhApiException {
		final String _lang = lang == null ? "EN" : lang;
		if (countryCode == null) {
			return executeAuthentified(() -> referencesCountriesGet(_lang));
		} else {
			return executeAuthentified(() -> referencesCountriesCountryCodeGet(countryCode, _lang));
		}
	}

	@Override
	public CitiesResponse cities(String cityCode, String lang) throws LhApiException {
		final String _lang = lang == null ? "EN" : lang;
		if (cityCode == null) {
			return executeAuthentified(() -> referencesCitiesGet(_lang));
		} else {
			return executeAuthentified(() -> referencesCitiesCityCodeGet(cityCode, _lang));
		}
	}

	@Override
	public AirportsResponse airports(String airportCode, String lang, Boolean lhOperates) throws LhApiException {
		final String _lang = lang == null ? "EN" : lang;
		if (airportCode == null) {
			return executeAuthentified(() -> referencesAirportsGet(_lang, lhOperates));
		} else {
			return executeAuthentified(() -> referencesAirportsAirportCodeGet(airportCode, _lang, lhOperates));
		}
	}

	@Override
	public NearestAirportsResponse nearestAirports(Double latitude, Double longitude, String lang)
			throws LhApiException {
		final String _lang = lang == null ? "EN" : lang;
		return executeAuthentified(() -> referencesAirportsNearestLatitudelongitudeGet(latitude, longitude, _lang));
	}

	@Override
	public AirlinesResponse airlines(String airlineCode) throws LhApiException {
		if (airlineCode == null) {
			return executeAuthentified(() -> referencesAirlinesGet());
		} else {
			return executeAuthentified(() -> referencesAirlinesAirlineCodeGet(airlineCode));
		}
	}

	@Override
	public AircraftSummariesResponse aircraftSummaries(String aircraftCode) throws LhApiException {
		if (aircraftCode == null) {
			return executeAuthentified(() -> referencesAircraftGet());
		} else {
			return executeAuthentified(() -> referencesAircraftAircraftCodeGet(aircraftCode));
		}
	}

	@Override
	public FlightStatusResponse flightStatus(String flightNumber, LocalDate date) throws LhApiException {
		return executeAuthentified(() -> operationsFlightstatusFlightNumberDateGet(flightNumber, date));
	}

	@Override
	public FlightsStatusResponse arrivalsStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws LhApiException {
		return executeAuthentified(
				() -> operationsFlightstatusArrivalsAirportCodeFromUntilGet(airportCode, from, until));
	}

	@Override
	public FlightsStatusResponse departuresStatus(String airportCode, LocalDateTime from, LocalDateTime until)
			throws LhApiException {
		return executeAuthentified(
				() -> operationsFlightstatusDeparturesAirportCodeFromUntilGet(airportCode, from, until));
	}

	private <T> T executeAuthentified(ApiOperation<T> operation) throws LhApiException {
		final long currentTimestamp = System.currentTimeMillis();
		if (this.accessTokenExpirationTimestamp < currentTimestamp) {
			refreshAccessToken();
		}
		try {
			// Try executing the operation
			return operation.execute();
		} catch (ApiException apiex) {
			// If an exception occurs first assume it is because of the expired
			// token
			// So refresh it and try again
			refreshAccessToken();
			try {
				return operation.execute();
			} catch (ApiException apiex1) {
				throw new LhApiException("Could not execute the operation.", apiex1);

			} catch (ClientHandlerException chex1) {
				throw new LhApiException("Could not execute the operation.", new ApiException(chex1));
			}
		} catch (ClientHandlerException chex) {
			throw new LhApiException("Could not execute the operation.", new ApiException(chex));
		}
	}

	private CountriesResponse referencesCountriesGet(String lang) throws ApiException {
		return this.api.referencesCountriesGet(lang);
	}

	private CountriesResponse referencesCountriesCountryCodeGet(String countryCode, String lang) throws ApiException {
		return this.api.referencesCountriesCountryCodeGet(countryCode, lang);
	}

	private CitiesResponse referencesCitiesGet(String lang) throws ApiException {
		return this.api.referencesCitiesGet(lang);
	}

	private CitiesResponse referencesCitiesCityCodeGet(String cityCode, String lang) throws ApiException {
		return this.api.referencesCitiesCityCodeGet(cityCode, lang);
	}

	private AirportsResponse referencesAirportsGet(String lang, Boolean lhOperated) throws ApiException {
		return this.api.referencesAirportsGet(lang, lhOperated == null ? null : lhOperated.toString());
	}

	private AirportsResponse referencesAirportsAirportCodeGet(String airportCode, String lang, Boolean lhOperated)
			throws ApiException {
		return this.api.referencesAirportsAirportCodeGet(airportCode, lang,
				lhOperated == null ? null : lhOperated.toString());
	}

	private NearestAirportsResponse referencesAirportsNearestLatitudelongitudeGet(Double latitude, Double longitude,
			String lang) throws ApiException {
		return this.api.referencesAirportsNearestLatitudelongitudeGet(latitude, longitude, lang);
	}

	private AirlinesResponse referencesAirlinesGet() throws ApiException {
		return this.api.referencesAirlinesGet();
	}

	private AirlinesResponse referencesAirlinesAirlineCodeGet(String airlineCode) throws ApiException {
		return this.api.referencesAirlinesAirlineCodeGet(airlineCode);
	}

	private AircraftSummariesResponse referencesAircraftGet() throws ApiException {
		return this.api.referencesAircraftGet();
	}

	private AircraftSummariesResponse referencesAircraftAircraftCodeGet(String aircraftCode) throws ApiException {
		return this.api.referencesAircraftAircraftCodeGet(aircraftCode);
	}

	private FlightStatusResponse operationsFlightstatusFlightNumberDateGet(String flightNumber, LocalDate date)
			throws ApiException {

		final String dateAsString = DATE_FORMATTER.print(date);
		return this.api.operationsFlightstatusFlightNumberDateGet(flightNumber, dateAsString);
	}

	private FlightsStatusResponse operationsFlightstatusArrivalsAirportCodeFromUntilGet(String airportCode,
			LocalDateTime from, LocalDateTime until) throws ApiException {

		final String fromAsString = DATE_TIME_FORMATTER.print(from);
		final String untilAsString = DATE_TIME_FORMATTER.print(until);
		return this.api.operationsFlightstatusArrivalsAirportCodeFromUntilGet(airportCode, fromAsString, untilAsString);
	}

	private FlightsStatusResponse operationsFlightstatusDeparturesAirportCodeFromUntilGet(String airportCode,
			LocalDateTime from, LocalDateTime until) throws ApiException {

		final String fromAsString = DATE_TIME_FORMATTER.print(from);
		final String untilAsString = DATE_TIME_FORMATTER.print(until);
		return this.api.operationsFlightstatusDeparturesAirportCodeFromUntilGet(airportCode, fromAsString,
				untilAsString);
	}

	@FunctionalInterface
	private interface ApiOperation<T> {
		public T execute() throws ApiException;
	}
}
