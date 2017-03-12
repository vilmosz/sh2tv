package tv.shapeshifting.controllers;

import java.io.Serializable;

public class RestErrorMessage implements Serializable {

	private static final long serialVersionUID = -9037243373369327961L;
	private final String message;
	private final String exception;

    public RestErrorMessage(String message, String exception) {
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return this.message;
    }

    public String getException() {
        return this.exception;
    }

}