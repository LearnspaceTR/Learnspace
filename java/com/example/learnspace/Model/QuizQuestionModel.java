package com.example.learnspace.Model;

public class QuizQuestionModel {


    private String term, definition;

    private String key;

    private int setNum;

    public QuizQuestionModel(String term, String definition, String key, int setNum) {

        this.term = term;
        this.definition = definition;
        this.key = key;
        this.setNum = setNum;
    }


    public QuizQuestionModel() {
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }
}
