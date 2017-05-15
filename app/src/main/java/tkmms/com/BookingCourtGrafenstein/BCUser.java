package tkmms.com.BookingCourtGrafenstein;

import java.io.Serializable;

/**
 * Created by tkrainz on 12/05/2017.
 */

public class BCUser implements Serializable {

    private long admin = 0;
    private String email = "";
    private String firstname = "";
    private String lastname = "";
    private long payment = 0;
    private String id = "";

    public long getAdmin() {
        return admin;
    }

    public void setAdmin(long admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public long getPayment() {
        return payment;
    }

    public void setPayment(long payment) {
        this.payment = payment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
