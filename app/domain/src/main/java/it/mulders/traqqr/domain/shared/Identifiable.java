package it.mulders.traqqr.domain.shared;

import java.util.UUID;

/**
 * Marks an entity as having an identifier.
 */
public interface Identifiable {
    UUID id();
}
