package id.co.reich.mockupsouthscape.pojo;

import com.google.gson.annotations.SerializedName;

public class Event{

    @SerializedName("event_id")
    private int eventID;
    @SerializedName("event_name")
    private String eventName;
    @SerializedName("event_place")
    private String eventPlace;
    @SerializedName("event_start")
    private String eventStart;
    @SerializedName("event_finish")
    private String eventFinish;
    @SerializedName("event_type_id")
    private int eventTypeID;
    @SerializedName("user_id")
    private int userID;
    @SerializedName("type_event_name")
    private String type_event_name;
    @SerializedName("name")
    private String name;

    public Event(int eventID, String eventName, String eventPlace, String eventStart, String eventFinish, int eventTypeID, int userID, String type_event_name, String name)
    {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventPlace = eventPlace;
        this.eventStart = eventStart;
        this.eventFinish = eventFinish;
        this.eventTypeID = eventTypeID;
        this.userID = userID;
        this.type_event_name = type_event_name;
        this.name = name;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventFinish() {
        return eventFinish;
    }

    public void setEventFinish(String eventFinish) {
        this.eventFinish = eventFinish;
    }

    public int getEventTypeID() {
        return eventTypeID;
    }

    public void setEventTypeID(int eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getType_event_name() {
        return type_event_name;
    }

    public void setType_event_name(String type_event_name) {
        this.type_event_name = type_event_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
