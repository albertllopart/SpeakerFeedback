package edu.upc.citm.android.speakerfeedback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Poll {
    private String question;
    private List<String> options;
    private boolean open;
    private Date start, end;
    private List<Integer> results;
    private String poll_id;


    Poll() {

    }


    public String getPoll_id() {
        return poll_id;
    }

    public void setPoll_id(String poll_id) {
        this.poll_id = poll_id;
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

        for (int i = 0; i < options.size(); i++) {
            b.append(options.get(i));
            if (results != null) {
                b.append(" ");
                if (results.get(i) == null) {
                    b.append("0");
                } else {
                    b.append(results.get(i));
                }
            }
            if (i < options.size() - 1) {
                b.append("\n");
            }
        }

        return b.toString();
    }
}
