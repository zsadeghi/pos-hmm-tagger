package me.theyinspire.projects.poshmm.model;

/**
 * An entry in the language model while calculating the final part of speech tagging for a phrase.
 */
public interface Entry {

    /**
     * @return part of speech for this entry.
     */
    String partOfSpeech();

    /**
     * @return the token corresponding to this entry.
     */
    String token();

    /**
     * @return the previous entry leading to this entry. {@code null} if this is the first entry
     * in the phrase.
     */
    Entry previous();

    /**
     * @return the probability with which this entry has been reached.
     */
    double probability();

}
