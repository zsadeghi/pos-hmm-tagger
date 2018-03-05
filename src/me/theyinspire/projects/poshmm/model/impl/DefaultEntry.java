package me.theyinspire.projects.poshmm.model.impl;

import me.theyinspire.projects.poshmm.Launcher;
import me.theyinspire.projects.poshmm.model.Entry;

import java.util.Objects;

public class DefaultEntry implements Entry {

    private final String partOfSpeech;
    private final String word;
    private final Entry previous;
    private final double probability;

    public DefaultEntry(final String partOfSpeech, final String word,
                        final Entry previous, final double probability) {
        this.partOfSpeech = partOfSpeech;
        this.word = word;
        this.previous = previous;
        this.probability = probability;
    }

    @Override
    public String partOfSpeech() {
        return partOfSpeech;
    }

    @Override
    public String token() {
        return word;
    }

    @Override
    public Entry previous() {
        return previous;
    }

    @Override
    public double probability() {
        return probability;
    }

    @Override
    public String toString() {
        return "(" + probability() + "," + (previous() == null ? Launcher.START : previous().partOfSpeech()) + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DefaultEntry that = (DefaultEntry) o;
        return Objects.equals(partOfSpeech, that.partOfSpeech) &&
                Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partOfSpeech, word);
    }

}
