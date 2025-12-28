package it.mulders.traqqr.gmaps.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public record Result(
        @JsonbProperty("formatted_address") String formattedAddress,
        @JsonbProperty("place_id") String placeId,
        @JsonbProperty("types") List<String> types,
        @JsonbProperty("partial_match") Boolean partialMatch) {}
