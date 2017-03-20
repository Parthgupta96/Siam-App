package in.siamdtu.parth.siamapp;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Priyanshu on 09-01-2017.
 */

public class UserObject implements Serializable {

    private String name;
    private String uid;
    private String email;
    private String photoUrl;
    private String contact;//bekaar
    private long credits;
    public Map<String,AttemptedByUserObject> attemptedQuestions;

    public void setAttemptedQuestions(Map<String, AttemptedByUserObject> attemptedQuestions) {
        this.attemptedQuestions = attemptedQuestions;
    }

    public Map<String, AttemptedByUserObject> getAttemptedQuestions() {
        return attemptedQuestions;
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
