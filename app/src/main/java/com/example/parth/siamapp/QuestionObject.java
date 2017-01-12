package com.example.parth.siamapp;

import java.lang.reflect.Array;

/**
 * Created by Priyanshu on 08-01-2017.
 */

public class QuestionObject {
    private String questionNumber;
    private String question;
    private String answerType;
    private String answeer;

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Array getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(Array answerOptions) {
        this.answerOptions = answerOptions;
    }

    private String image;
    private Array answerOptions;

}
