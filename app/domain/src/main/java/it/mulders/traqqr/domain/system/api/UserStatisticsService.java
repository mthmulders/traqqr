package it.mulders.traqqr.domain.system.api;

import it.mulders.traqqr.domain.user.Owner;

public interface UserStatisticsService {
    long countMeasurements(Owner owner);
}
