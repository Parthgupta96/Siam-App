package in.siamdtu.parth.siamapp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Priyanshu on 08-01-2017.
 */

public class QuestionObject {
    public static final int MCQ = 0;
    private String date;
    private String question;
    private String answerType;
    private String answer;
    private String imageUrl;
    private long credits;
    private List<HashMap<String, String>> winnerEmail;
    private List<AttemptedByUserObject> attemptedBy;
    private List<String> options;

    public boolean equals(QuestionObject questionObject) {
        if (question.equalsIgnoreCase(questionObject.question)) {
            return true;
        } else {
            return false;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<HashMap<String, String>> getWinnerEmail() {
        return winnerEmail;
    }

    public void setWinnerEmail(List<HashMap<String, String>> winnerEmail) {
        this.winnerEmail = winnerEmail;
    }

    public List<AttemptedByUserObject> getAttemptedBy() {
        return attemptedBy;
    }

    public void setAttemptedBy(List<AttemptedByUserObject> attemptedBy) {
        this.attemptedBy = attemptedBy;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getNumberOfAttempts() {
        if (attemptedBy == null) {
            return 0;
        } else {
            return attemptedBy.size();
        }
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

}
