package com.prison.service;

import com.prison.dao.LegalEventDAO;
import com.prison.dao.PrisonerDAO;
import com.prison.model.LegalEvent;
import com.prison.model.Prisoner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LegalEventService {
    private LegalEventDAO legalEventDAO;
    private PrisonerDAO prisonerDAO;

    public LegalEventService() {
        legalEventDAO = new LegalEventDAO();
        prisonerDAO = new PrisonerDAO();
    }

    public boolean recordLegalEvent(LegalEvent event) {
        // Verify the prisoner exists
        Prisoner prisoner = prisonerDAO.getPrisoner(event.getPrisonerId());
        if (prisoner == null) {
            return false;
        }

        return legalEventDAO.addLegalEvent(event);
    }

    public LegalEvent getLegalEvent(int eventId) {
        return legalEventDAO.getLegalEvent(eventId);
    }

    public List<LegalEvent> getLegalHistoryForPrisoner(int prisonerId) {
        return legalEventDAO.getLegalEventsForPrisoner(prisonerId);
    }

    public List<LegalEvent> getUpcomingLegalEvents(int daysAhead) {
        // Calculate date range
        long millisecondsInDay = 24 * 60 * 60 * 1000;
        Date startDate = new Date(); // today
        Date endDate = new Date(System.currentTimeMillis() + (daysAhead * millisecondsInDay));

        return legalEventDAO.getLegalEventsBetweenDates(startDate, endDate);
    }

    public Map<String, Integer> getEventTypeDistribution() {
        List<LegalEvent> allEvents = legalEventDAO.getAllLegalEvents();
        Map<String, Integer> distribution = new HashMap<>();

        for (LegalEvent event : allEvents) {
            String eventType = event.getEventType();
            distribution.put(eventType, distribution.getOrDefault(eventType, 0) + 1);
        }

        return distribution;
    }

    public boolean updateLegalEvent(LegalEvent event) {
        return legalEventDAO.updateLegalEvent(event);
    }

    public boolean deleteLegalEvent(int eventId) {
        return legalEventDAO.deleteLegalEvent(eventId);
    }

    public int getTotalLegalEvents() {
        return legalEventDAO.getTotalLegalEvents();
    }

    public int getTotalLegalEventsForPrisoner(int prisonerId) {
        return legalEventDAO.getTotalLegalEventsForPrisoner(prisonerId);
    }

    public boolean scheduleNextLegalEvent(LegalEvent event) {
        // Logic for scheduling follow-up legal events
        // For example, after a hearing, schedule a trial
        // This is a placeholder - real logic would be more complex
        return legalEventDAO.addLegalEvent(event);
    }
}
