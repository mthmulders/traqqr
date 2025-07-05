package it.mulders.traqqr.domain.batch;

import java.time.OffsetDateTime;

public record BatchJobMessage(OffsetDateTime timestamp, String text) {}
