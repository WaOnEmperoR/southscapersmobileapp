package id.co.reich.mockupsouthscape.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetail {
    @SerializedName("id")
    @Expose
    private int userid;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("birth_date")
    @Expose
    private String birth_date;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("img_avatar")
    @Expose
    private String img_avatar;

    public UserDetail (int id, String name, String email, String gender, String birth_date, String address, String img_avatar)
    {
        this.userid = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birth_date = birth_date;
        this.address = address;
        this.img_avatar = img_avatar;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImg_avatar() {
        return img_avatar;
    }

    public void setImg_avatar(String img_avatar) {
        this.img_avatar = img_avatar;
    }
}
