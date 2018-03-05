package me.theyinspire.projects.poshmm.model;

import java.util.Set;

public interface LanguageModel {

    double getTransitionProbability(String current, String previous);

    double getEmissionProbability(String token, String partOfSpeech);

    Set<String> knownPartsOfSpeech();

    Set<String> knownTokens();

    void addTransition(String current, String previous, double probability);

    void addEmission(String token, String partOfSpeech, double probability);

}
