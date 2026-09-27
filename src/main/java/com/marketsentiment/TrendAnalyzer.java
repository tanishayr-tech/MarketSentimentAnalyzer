package com.marketsentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TrendAnalyzer {

    private static final String URL =
            "jdbc:mysql://localhost:3306/market_sentiment";

    private static final String USER = "root";

    private static final String PASSWORD =
            "tanisha@1808";

    public static void main(String[] args) {

        String query =
                "SELECT sentiment, COUNT(*) AS total " +
                "FROM news " +
                "GROUP BY sentiment";

        int positive = 0;
        int negative = 0;
        int neutral = 0;

        try (
            Connection connection =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(query)
        ) {

            while (resultSet.next()) {

                String sentiment =
                        resultSet.getString("sentiment");

                int total =
                        resultSet.getInt("total");

                if (sentiment.contains("POSITIVE")) {
                    positive += total;
                }
                else if (sentiment.contains("NEGATIVE")) {
                    negative += total;
                }
                else {
                    neutral += total;
                }
            }

            System.out.println("======================================");
            System.out.println("          MARKET TREND ANALYZER");
            System.out.println("======================================");

            System.out.println("Positive News : " + positive);
            System.out.println("Negative News : " + negative);
            System.out.println("Neutral News  : " + neutral);

            String trend;

            if (positive > negative) {
                trend = "POSITIVE";
            }
            else if (negative > positive) {
                trend = "NEGATIVE";
            }
            else {
                trend = "NEUTRAL";
            }

            System.out.println("--------------------------------------");
            System.out.println("Overall Market Trend: " + trend);
            System.out.println("======================================");

        } catch (SQLException e) {

            System.out.println("❌ Database error!");
            e.printStackTrace();
        }
    }
}