package com.example.parth.siamapp;

import java.util.List;

/**
 * Created by Priyanshu on 08-01-2017.
 */

public class QuestionObject {
    private String date;
    private String question;
    private String answerType;
    private String answeer;
    private String imageUrl;
    private String winnerEmail;
    private List<AttemptedByUserObject> attemptedByUserObject;
    private List<String> options;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWinnerEmail() {
        return winnerEmail;
    }

    public void setWinnerEmail(String winnerEmail) {
        this.winnerEmail = winnerEmail;
    }

    public List<AttemptedByUserObject> getAttemptedByUserObject() {
        return attemptedByUserObject;
    }

    public void setAttemptedByUserObject(List<AttemptedByUserObject> attemptedByUserObject) {
        this.attemptedByUserObject = attemptedByUserObject;
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

    public String getAnsweer() {
        return answeer;
    }

    public void setAnsweer(String answeer) {
        this.answeer = answeer;
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

    public int getNumberOfAttempts(){
        if(attemptedByUserObject==null){
            return 0;
        }else{
            return attemptedByUserObject.size();
        }
    }
}
