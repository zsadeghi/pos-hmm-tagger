package me.theyinspire.projects.poshmm.model;

import java.util.List;

public interface ViterbiMatrix {

    List<Entry> getEntries(int index);

    int size();

    Entry getFinalEntry();

    void setFinalEntry(Entry finalEntry);

    void addToken(List<Entry> entries);

}
