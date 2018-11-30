package id.co.reich.mockupsouthscape.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment {
    @SerializedName("payment_submitted")
    @Expose
    private String paymentSubmitted;

    @SerializedName("payment_verified")
    @Expose
    private String paymentVerified;

    @SerializedName("payment_session_name")
    @Expose
    private String paymentSession;

    @SerializedName("payment_type_name")
    @Expose
    private String paymentType;

    @SerializedName("user_name")
    @Expose
    private String userName;

    @SerializedName("rejection_cause")
    @Expose
    private String rejectionCause;

    @SerializedName("img_file_proof")
    @Expose
    private String imageFile;

    public Payment(String paymentSubmitted, String paymentVerified, String paymentSession, String paymentType, String userName, String rejectionCause, String imageFile)
    {
        this.paymentSubmitted = paymentSubmitted;
        this.paymentVerified = paymentVerified;
        this.paymentSession = paymentSession;
        this.paymentType = paymentType;
        this.userName = userName;
        this.rejectionCause = rejectionCause;
        this.imageFile = imageFile;
    }

    public String getPaymentSubmitted() {
        return paymentSubmitted;
    }

    public void setPaymentSubmitted(String paymentSubmitted) {
        this.paymentSubmitted = paymentSubmitted;
    }

    public String getPaymentVerified() {
        return paymentVerified;
    }

    public void setPaymentVerified(String paymentVerified) {
        this.paymentVerified = paymentVerified;
    }

    public String getPaymentSession() {
        return paymentSession;
    }

    public void setPaymentSession(String paymentSession) {
        this.paymentSession = paymentSession;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRejectionCause() {
        return rejectionCause;
    }

    public void setRejectionCause(String rejectionCause) {
        this.rejectionCause = rejectionCause;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
