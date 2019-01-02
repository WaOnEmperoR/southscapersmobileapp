package id.co.reich.mockupsouthscape.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bill {

    @SerializedName("payment_type_id")
    @Expose
    private int paymentTypeID;

    @SerializedName("payment_session_id")
    @Expose
    private int paymentSessionID;

    @SerializedName("payment_name")
    @Expose
    private String paymentName;

    @SerializedName("payment_session_name")
    @Expose
    private String paymentSessionName;

    public Bill(int paymentTypeID, int paymentSessionID, String paymentName, String paymentSessionName)
    {
        this.paymentTypeID = paymentTypeID;
        this.paymentSessionID = paymentSessionID;
        this.paymentName = paymentName;
        this.paymentSessionName = paymentSessionName;
    }

    public int getPaymentTypeID() {
        return paymentTypeID;
    }

    public void setPaymentTypeID(int paymentTypeID) {
        this.paymentTypeID = paymentTypeID;
    }

    public int getPaymentSessionID() {
        return paymentSessionID;
    }

    public void setPaymentSessionID(int paymentSessionID) {
        this.paymentSessionID = paymentSessionID;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentSessionName() {
        return paymentSessionName;
    }

    public void setPaymentSessionName(String paymentSessionName) {
        this.paymentSessionName = paymentSessionName;
    }
}
