package it.mulders.traqqr.gmaps.dto;

import jakarta.json.bind.annotation.JsonbProperty;

import java.util.List;

/**
 * DTOs for the Google Geocoding API response (reverse geocoding).
 * Matches the fields described in the documentation: https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding
 */
public final class GeocodeResponse {
    @JsonbProperty("results")
    public List<Result> results;

    @JsonbProperty("status")
    public String status;

    @JsonbProperty("error_message")
    public String errorMessage;

    @JsonbProperty("plus_code")
    public PlusCode plusCode;

    public static final class Result {
        @JsonbProperty("address_components")
        public List<AddressComponent> addressComponents;

        @JsonbProperty("formatted_address")
        public String formattedAddress;

        @JsonbProperty("geometry")
        public Geometry geometry;

        @JsonbProperty("place_id")
        public String placeId;

        @JsonbProperty("types")
        public List<String> types;

        @JsonbProperty("partial_match")
        public Boolean partialMatch;

        @Override
        public String toString() {
            return "Result{" +
                    "formattedAddress='" + formattedAddress + '\'' +
                    ", placeId='" + placeId + '\'' +
                    '}';
        }
    }

    public static final class AddressComponent {
        @JsonbProperty("long_name")
        public String longName;

        @JsonbProperty("short_name")
        public String shortName;

        @JsonbProperty("types")
        public List<String> types;

        @Override
        public String toString() {
            return "AddressComponent{" +
                    "longName='" + longName + '\'' +
                    ", shortName='" + shortName + '\'' +
                    ", types=" + types +
                    '}';
        }
    }

    public static final class Geometry {
        @JsonbProperty("location")
        public Location location;

        @JsonbProperty("location_type")
        public String locationType;

        @JsonbProperty("viewport")
        public Viewport viewport;

        @JsonbProperty("bounds")
        public Viewport bounds;

        @Override
        public String toString() {
            return "Geometry{" +
                    "location=" + location +
                    ", locationType='" + locationType + '\'' +
                    '}';
        }
    }

    public static final class Location {
        @JsonbProperty("lat")
        public double lat;

        @JsonbProperty("lng")
        public double lng;

        @Override
        public String toString() {
            return "Location{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
    }

    public static final class Viewport {
        @JsonbProperty("northeast")
        public Location northeast;

        @JsonbProperty("southwest")
        public Location southwest;

        @Override
        public String toString() {
            return "Viewport{" +
                    "northeast=" + northeast +
                    ", southwest=" + southwest +
                    '}';
        }
    }

    public static final class PlusCode {
        @JsonbProperty("compound_code")
        public String compoundCode;

        @JsonbProperty("global_code")
        public String globalCode;

        @Override
        public String toString() {
            return "PlusCode{" +
                    "compoundCode='" + compoundCode + '\'' +
                    ", globalCode='" + globalCode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GeocodeResponse{" +
                "status='" + status + '\'' +
                ", results=" + results +
                '}';
    }
}
