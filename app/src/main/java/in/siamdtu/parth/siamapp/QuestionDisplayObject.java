package in.siamdtu.parth.siamapp;

/**
 * Created by Parth on 14-03-2017.
 */

public class QuestionDisplayObject {
    String answer;
    String date;
    String imageUrl;
    String question;
    Long credits;

    public QuestionDisplayObject() {
    }

    public QuestionDisplayObject(String answer, String date, String imageUrl, String question, Long credits) {
        this.answer = answer;
        this.date = date;
        this.imageUrl = imageUrl;
        this.question = question;
        this.credits = credits;
    }

    public String getAnswer() {
        return answer;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getQuestion() {
        return question;
    }

    public Long getCredits() {
        return credits;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setCredits(Long credits) {
        this.credits = credits;
    }
}
