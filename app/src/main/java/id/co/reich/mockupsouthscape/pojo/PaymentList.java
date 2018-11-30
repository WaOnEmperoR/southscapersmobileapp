package id.co.reich.mockupsouthscape.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PaymentList {
    @SerializedName("payments")
    private ArrayList<Payment> paymentArrayList;

    public ArrayList<Payment> getPaymentArrayList() {
        return paymentArrayList;
    }

    public void setPaymentArrayList(ArrayList<Payment> paymentArrayList) {
        this.paymentArrayList = paymentArrayList;
    }
}
