package com.marketsentiment;

public class SentimentAnalyzer {

    public static String analyzeSentiment(String text) {

        text = text.toLowerCase();

        String[] positiveWords = {

            "strong",
            "growth",
            "profit",
            "increase",
            "increased",
            "stable",
            "gain",
            "gains",
            "positive",
            "demand",
            "good",
            "success",
            "excellent",
            "rise",
            "rising",
            "higher",
            "boost",
            "improved",
            "record"

        };


        String[] negativeWords = {

            "fall",
            "fell",
            "decline",
            "declining",
            "weak",
            "weaker",
            "concerns",
            "concern",
            "negative",
            "loss",
            "losses",
            "drop",
            "dropped",
            "disappointing",
            "risk",
            "risks",
            "poor",
            "lower",
            "crisis"

        };


        int positiveScore = 0;

        int negativeScore = 0;


        for (String word : positiveWords) {

            if (text.contains(word)) {

                positiveScore++;

            }

        }


        for (String word : negativeWords) {

            if (text.contains(word)) {

                negativeScore++;

            }

        }


        if (positiveScore > negativeScore) {

            return "POSITIVE";

        }

        else if (negativeScore > positiveScore) {

            return "NEGATIVE";

        }

        else {

            return "NEUTRAL";

        }

    }


    // =========================================
    // CALCULATE SENTIMENT SCORE
    // =========================================

    public static int calculateScore(String text) {

        text = text.toLowerCase();


        String[] positiveWords = {

            "strong",
            "growth",
            "profit",
            "increase",
            "increased",
            "stable",
            "gain",
            "gains",
            "positive",
            "demand",
            "good",
            "success",
            "excellent",
            "rise",
            "rising",
            "higher",
            "boost",
            "improved",
            "record"

        };


        String[] negativeWords = {

            "fall",
            "fell",
            "decline",
            "declining",
            "weak",
            "weaker",
            "concerns",
            "concern",
            "negative",
            "loss",
            "losses",
            "drop",
            "dropped",
            "disappointing",
            "risk",
            "risks",
            "poor",
            "lower",
            "crisis"

        };


        int positiveScore = 0;

        int negativeScore = 0;


        for (String word : positiveWords) {

            if (text.contains(word)) {

                positiveScore++;

            }

        }


        for (String word : negativeWords) {

            if (text.contains(word)) {

                negativeScore++;

            }

        }


        int total =
                positiveScore + negativeScore;


        // No sentiment words
        if (total == 0) {

            return 50;

        }


        /*
         * Convert positive words into
         * a score from 0 to 100.
         *
         * 100 = completely positive
         * 50  = neutral
         * 0   = completely negative
         */

        int score =
                (int) Math.round(
                        ((double) positiveScore / total)
                                * 100
                );


        return score;

    }


    // =========================================
    // TEST PROGRAM
    // =========================================

    public static void main(String[] args) {


        String[] newsList = {

            "Reliance Industries reported strong revenue and profit growth.",

            "TCS shares fall after disappointing results.",

            "HDFC Bank announces stable quarterly performance.",

            "Infosys gains after strong technology demand.",

            "Tata Motors faces concerns over declining sales."

        };


        for (String news : newsList) {


            String sentiment =
                    analyzeSentiment(news);


            int score =
                    calculateScore(news);


            System.out.println(
                    "News: " + news
            );


            System.out.println(
                    "Sentiment: " + sentiment
            );


            System.out.println(
                    "Score: " + score
            );


            System.out.println(
                    "-----------------------------"
            );

        }

    }

}