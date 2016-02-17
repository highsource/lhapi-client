package org.hisrc.lhapi.client;

import org.apache.commons.lang3.Validate;
import org.hisrc.lhapi.client.invoker.ApiException;

public class LhApiException extends RuntimeException {

	private static final long serialVersionUID = 8873660914384201547L;

	public LhApiException(String message, ApiException cause) {
		super(Validate.notNull(message), Validate.notNull(cause));
	}

}
