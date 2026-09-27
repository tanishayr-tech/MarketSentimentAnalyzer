package com.marketsentiment;

import java.util.Properties;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class CoreNLPSentimentAnalyzer {

    private static StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();

        props.setProperty(
                "annotators",
                "tokenize,ssplit,pos,lemma,parse,sentiment"
        );

        pipeline = new StanfordCoreNLP(props);
    }

    // Returns sentiment score from 0 to 100
    public static double calculateScore(String text) {

        CoreDocument document = new CoreDocument(text);

        pipeline.annotate(document);

        if (document.sentences().isEmpty()) {
            return 50.0;
        }

        double totalScore = 0;

        for (CoreSentence sentence : document.sentences()) {

            String sentiment = sentence.sentiment();

            totalScore += convertSentimentToScore(sentiment);
        }

        double average =
                totalScore / document.sentences().size();

        return average * 25;
    }

    // Converts CoreNLP sentiment to 0-4
    private static int convertSentimentToScore(String sentiment) {

        switch (sentiment) {

            case "Very negative":
                return 0;

            case "Negative":
                return 1;

            case "Neutral":
                return 2;

            case "Positive":
                return 3;

            case "Very positive":
                return 4;

            default:
                return 2;
        }
    }

    // Returns sentiment category
    public static String analyzeSentiment(String text) {

        double score = calculateScore(text);

        if (score <= 20) {
            return "VERY NEGATIVE";
        } else if (score <= 40) {
            return "NEGATIVE";
        } else if (score <= 60) {
            return "NEUTRAL";
        } else if (score <= 80) {
            return "POSITIVE";
        } else {
            return "VERY POSITIVE";
        }
    }

    public static void main(String[] args) {

        String news =
                "Reliance Industries reported strong revenue and profit growth.";

        double score = calculateScore(news);

        String sentiment = analyzeSentiment(news);

        System.out.println("News: " + news);
        System.out.println("Sentiment: " + sentiment);
        System.out.println("Score: " + score + "/100");
    }
}
