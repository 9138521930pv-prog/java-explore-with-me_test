package ru.practicum.ewm.service;

import ru.practicum.ewm.EndpointRequest;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewsStatsRequest;

import java.util.List;

public interface StatsService {
    void saveHit(EndpointRequest hit);

    List<ViewStats> getViewStatsList(ViewsStatsRequest request);
}