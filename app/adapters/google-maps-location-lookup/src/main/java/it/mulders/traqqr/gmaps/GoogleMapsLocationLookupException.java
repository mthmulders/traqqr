package it.mulders.traqqr.gmaps;

public class GoogleMapsLocationLookupException extends RuntimeException {
    public GoogleMapsLocationLookupException(String message) {
        super(message);
    }

    public GoogleMapsLocationLookupException(Throwable cause) {
        super(cause);
    }
}
