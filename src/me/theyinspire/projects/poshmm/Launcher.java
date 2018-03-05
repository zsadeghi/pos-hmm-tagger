package me.theyinspire.projects.poshmm;

import me.theyinspire.projects.poshmm.model.Entry;
import me.theyinspire.projects.poshmm.model.LanguageModel;
import me.theyinspire.projects.poshmm.model.ViterbiMatrix;
import me.theyinspire.projects.poshmm.model.impl.DefaultEntry;
import me.theyinspire.projects.poshmm.model.impl.DefaultLanguageModel;
import me.theyinspire.projects.poshmm.model.impl.DefaultViterbiMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Launcher {

    public static final String VERB = "V";
    public static final String ADVERB = "ADV";
    public static final String NOUN = "N";
    public static final String START = "S";
    public static final String FINISH = "F";

    public static void main(String[] args) {
        final LanguageModel model = new DefaultLanguageModel();
        // Setting up the transitions.
        setUpTransitions(model);
        // Setting up the emissions.
        setUpEmissions(model);
        // Creating input phrase:
        final String[] phrase = createPhrase();
        // Create a Viterbi matrix out of the phrase
        final ViterbiMatrix matrix = createViterbiMatrix(model, phrase);
        // Print the matrix.
        printMatrix(model, phrase, matrix);
        // Print the tagging.
        printTagging(matrix);
    }

    private static void printTagging(final ViterbiMatrix matrix) {
        final StringBuilder builder = new StringBuilder();
        Entry entry = matrix.getFinalEntry().previous();
        while (entry != null) {
            builder.insert(0, "(" + entry.token() + "," + entry.partOfSpeech() + ")  ");
            entry = entry.previous();
        }
        System.out.println("The final tagging:");
        System.out.println(builder.toString().trim());
    }

    private static void printMatrix(final LanguageModel model, final String[] phrase, final ViterbiMatrix matrix) {
        final String headline = "The Viterbi Matrix for phrase: " + Arrays.toString(phrase);
        System.out.println(headline);
        System.out.println(new String(new char[headline.length()]).replaceAll("\0", "="));
        System.out.print(word(START));
        for (String partOfSpeech : model.knownPartsOfSpeech().stream().sorted().collect(Collectors.toList())) {
            System.out.print(word(partOfSpeech));
        }
        System.out.println(word(FINISH));
        for (int i = 0; i < phrase.length; i++) {
            String token = phrase[i];
            System.out.print(word(token));
            final int index = i;
            model.knownPartsOfSpeech()
                 .stream()
                 .sorted()
                 .forEach(partOfSpeech -> {
                     final List<Entry> entries = matrix.getEntries(index);
                     final Entry entry = entries.stream()
                                                 .filter(e -> e.partOfSpeech().equals(partOfSpeech))
                                                 .findFirst().orElse(null);
                     if (entry == null) {
                         System.out.print(word("(0,X)"));
                     } else {
                         System.out.print(word(entry));
                     }
                 });
            System.out.print(word("(0,X)"));
            System.out.println();
        }
        System.out.print(word(FINISH));
        model.knownPartsOfSpeech()
             .stream()
             .sorted()
             .forEach(partOfSpeech -> {
                 System.out.print(word("(0,X)"));
             });
        System.out.println(matrix.getFinalEntry());
        System.out.println();
    }

    private static String word(Object what) {
        return padRight(String.valueOf(what), 15);
    }

    private static String padRight(String text, int length) {
        return text + new String(new char[Math.max(length - text.length(), 0)]).replaceAll("\0", " ");
    }

    private static ViterbiMatrix createViterbiMatrix(final LanguageModel model, final String[] phrase) {
        final ViterbiMatrix matrix = new DefaultViterbiMatrix();
        for (int i = 0; i < phrase.length; i++) {
            String token = phrase[i];
            final int index = i;
            matrix.addToken(model.knownPartsOfSpeech()
                                 .stream()
                                 .map(partOfSpeech ->
                                              createEntryForPartOfSpeech(model, matrix, index, token, partOfSpeech))
                                 .filter(Objects::nonNull)
                                 .collect(Collectors.toList()));
        }
        matrix.setFinalEntry(createFinalEntry(model, phrase, matrix));
        return matrix;
    }

    private static String[] createPhrase() {
        return new String[]{
                "learning",
                "changes",
                "thoroughly"
        };
    }

    private static void setUpEmissions(final LanguageModel model) {
        model.addEmission("learning", VERB, 0.003D);
        model.addEmission("changes", VERB, 0.004D);
        model.addEmission("thoroughly", ADVERB, 0.002D);
        model.addEmission("learning", NOUN, 0.001D);
        model.addEmission("changes", NOUN, 0.003D);
    }

    private static void setUpTransitions(final LanguageModel model) {
        model.addTransition(VERB, START, 0.3D);
        model.addTransition(NOUN, START, 0.2D);
        model.addTransition(VERB, VERB, 0.1D);
        model.addTransition(ADVERB, VERB, 0.4D);
        model.addTransition(NOUN, VERB, 0.4D);
        model.addTransition(NOUN, NOUN, 0.1D);
        model.addTransition(VERB, NOUN, 0.3D);
        model.addTransition(ADVERB, NOUN, 0.1D);
        model.addTransition(FINISH, ADVERB, 0.1D);
    }

    private static Entry createFinalEntry(final LanguageModel model, final String[] phrase,
                                          final ViterbiMatrix matrix) {
        final List<Entry> entries = matrix.getEntries(phrase.length - 1);
        double max = 0D;
        Entry last = null;
        for (Entry entry : entries) {
            final String partOfSpeech = entry.partOfSpeech();
            final double transitionProbability = model.getTransitionProbability(FINISH, partOfSpeech);
            if (transitionProbability == 0) {
                continue;
            }
            double probability = transitionProbability * entry.probability();
            if (probability > max) {
                max = probability;
                last = entry;
            }
        }
        return new DefaultEntry(FINISH, FINISH, last, max);
    }

    private static Entry createEntryForPartOfSpeech(final LanguageModel model, final ViterbiMatrix matrix, final int i,
                                                    final String token,
                                                    final String partOfSpeech) {
        final double emissionProbability = model.getEmissionProbability(token, partOfSpeech);
        if (emissionProbability == 0D) {
            return null;
        }
        double probability = 0D;
        Entry previous = null;
        if (i > 0) {
            final List<Entry> previousEntries = matrix.getEntries(i - 1);
            for (Entry previousEntry : previousEntries) {
                final double transitionProbability
                        = model.getTransitionProbability(partOfSpeech, previousEntry.partOfSpeech());
                if (transitionProbability == 0) {
                    continue;
                }
                double currentProbability =
                        emissionProbability * transitionProbability * previousEntry.probability();
                if (currentProbability > probability) {
                    probability = currentProbability;
                    previous = previousEntry;
                }
            }
        } else {
            final double transitionProbability = model.getTransitionProbability(partOfSpeech, START);
            if (transitionProbability != 0) {
                probability = transitionProbability * emissionProbability;
            }
        }
        if (probability > 0) {
            return new DefaultEntry(partOfSpeech, token, previous, probability);
        }
        return null;
    }

}
