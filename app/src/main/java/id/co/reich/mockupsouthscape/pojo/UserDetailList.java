package id.co.reich.mockupsouthscape.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserDetailList {
    @SerializedName("users")
    private ArrayList<UserDetail> userDetailArrayList;

    public ArrayList<UserDetail> getUserDetailArrayList() {
        return userDetailArrayList;
    }

    public void setUserDetailArrayList(ArrayList<UserDetail> userDetailArrayList) {
        this.userDetailArrayList = userDetailArrayList;
    }
}
