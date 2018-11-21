package id.co.reich.mockupsouthscape.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EventList {

    @SerializedName("events")
    private ArrayList<Event> eventArrayList;

    public ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

    public void setEventArrayList(ArrayList<Event> eventArrayList) {
        this.eventArrayList = eventArrayList;
    }
}
