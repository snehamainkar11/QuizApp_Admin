package com.example.adminquizapp;

public class QuestionModel {
    private String id,Question,A,B,C,D,answer;
    private int set;

    public QuestionModel(String id, String question, String a, String b, String c, String d, String answer, int set) {
        this.id = id;
        Question = question;
        A = a;
        B = b;
        C = c;
        D = d;
        this.answer = answer;
        this.set = set;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }


}
