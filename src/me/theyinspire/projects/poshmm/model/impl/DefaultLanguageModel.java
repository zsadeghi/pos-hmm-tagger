package me.theyinspire.projects.poshmm.model.impl;

import me.theyinspire.projects.poshmm.model.LanguageModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultLanguageModel implements LanguageModel {

    private final Map<Item, Double> transitions;
    private final Map<Item, Double> emissions;

    public DefaultLanguageModel() {
        transitions = new HashMap<>();
        emissions = new HashMap<>();
    }

    @Override
    public double getTransitionProbability(final String current, final String previous) {
        return transitions.getOrDefault(new Item(current, previous), 0D);
    }

    @Override
    public double getEmissionProbability(final String token, final String partOfSpeech) {
        return emissions.getOrDefault(new Item(token, partOfSpeech), 0D);
    }

    @Override
    public Set<String> knownPartsOfSpeech() {
        return emissions.keySet().stream().map(Item::getSecond).collect(Collectors.toSet());
    }

    @Override
    public Set<String> knownTokens() {
        return emissions.keySet().stream().map(Item::getFirst).collect(Collectors.toSet());
    }

    @Override
    public void addTransition(final String current, final String previous, final double probability) {
        transitions.put(new Item(current, previous), probability);
    }

    @Override
    public void addEmission(final String token, final String partOfSpeech, final double probability) {
        emissions.put(new Item(token, partOfSpeech), probability);
    }

    private final class Item {

        private final String first;
        private final String second;

        Item(final String first, final String second) {
            this.first = first;
            this.second = second;
        }

        String getFirst() {
            return first;
        }

        String getSecond() {
            return second;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Item item = (Item) o;
            return Objects.equals(first, item.first) &&
                    Objects.equals(second, item.second);
        }

        @Override
        public int hashCode() {

            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return "Item{" +
                    "first='" + first + '\'' +
                    ", second='" + second + '\'' +
                    '}';
        }

    }

}
