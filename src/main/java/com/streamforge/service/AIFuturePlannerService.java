package com.streamforge.service;

import com.streamforge.dto.response.AIFuturePlannerResponse;

public interface AIFuturePlannerService {

    AIFuturePlannerResponse generateForecast(
            Long showId
    );
}