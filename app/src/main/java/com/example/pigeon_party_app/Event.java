package com.example.pigeon_party_app;

public class Event {
    private String eventTitle;
    private boolean chosen; // Just a placeholder for testing notification sending.
                            // There will have to be a better way to see if a User is chosen by lottery.

    public Event(String eventTitle, boolean chosen) {
        this.eventTitle = eventTitle;
        this.chosen = chosen; // not final
    }

    public String getEventTitle() {
        return eventTitle;
    }

    // not final
    public boolean isChosen() {
        return chosen;
    }

    // not final
    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

}
