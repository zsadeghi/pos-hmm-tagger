package me.theyinspire.projects.poshmm.model.impl;

import me.theyinspire.projects.poshmm.model.Entry;
import me.theyinspire.projects.poshmm.model.ViterbiMatrix;

import java.util.ArrayList;
import java.util.List;

public class DefaultViterbiMatrix implements ViterbiMatrix {

    private final List<List<Entry>> data;
    private Entry finalEntry;

    public DefaultViterbiMatrix() {
        data = new ArrayList<>();
    }

    @Override
    public List<Entry> getEntries(final int index) {
        return data.get(index);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Entry getFinalEntry() {
        return finalEntry;
    }

    @Override
    public void setFinalEntry(final Entry finalEntry) {
        this.finalEntry = finalEntry;
    }

    @Override
    public void addToken(final List<Entry> entries) {
        data.add(entries);
    }

}
