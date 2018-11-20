package edu.upc.citm.android.speakerfeedback;

import java.util.Date;
import java.util.List;

public class Poll {
    private String question;
    private List<String> options;
    private boolean open;
    private Date start, end;
    private List<Integer> results;

    Poll() {

    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isOpen() {
        return open;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public List<Integer> getResults() {
        return results;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setResults(List<Integer> results) {
        this.results = results;
    }

    public String getOptionsString(){
        //així se sumen molts strings a java
        StringBuilder b = new StringBuilder();
        for (String opt : options)
        {
            b.append(opt);
            b.append("\n");
        }
        return b.toString();
    }
}
