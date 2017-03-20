package in.siamdtu.parth.siamapp;

import java.io.Serializable;

/**
 * Created by Priyanshu on 15-01-2017.
 */

public class AttemptedByUserObject implements Serializable {
    private String email;//used in Question Db
    private String answer;
    private String status;
    private Long score;//used in user
    private long submissionTime;

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(long submissionTime) {
        this.submissionTime = submissionTime;
    }
}
