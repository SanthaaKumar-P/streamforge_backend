package com.streamforge.service;

import com.streamforge.dto.request.ShowRequest;
import com.streamforge.dto.response.ShowResponse;

import java.util.List;

public interface ShowService {

    ShowResponse createShow(ShowRequest request);

    ShowResponse getShowById(Long showId);

    List<ShowResponse> getAllShows();

    ShowResponse updateShow(Long showId, ShowRequest request);

    void deleteShow(Long showId);

}