package com.marketsentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NewsSentimentAnalyzer {

    private static final String URL =
            "jdbc:mysql://localhost:3306/market_sentiment";

    private static final String USER = "root";

    private static final String PASSWORD =
            "tanisha@1808";

    public static void main(String[] args) {

        String selectQuery =
                "SELECT id, title, content, company FROM news";

        String updateQuery =
                "UPDATE news SET sentiment = ?, sentiment_score = ? WHERE id = ?";

        try (
            Connection connection =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(selectQuery);

            PreparedStatement updateStatement =
                    connection.prepareStatement(updateQuery)
        ) {

            System.out.println("======================================");
            System.out.println("       MARKET SENTIMENT ANALYZER");
            System.out.println("======================================");

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                String company = resultSet.getString("company");

                // CoreNLP sentiment analysis
                String sentiment =
                        CoreNLPSentimentAnalyzer.analyzeSentiment(content);

                // Calculate 0-100 sentiment score
                double score =
                        CoreNLPSentimentAnalyzer.calculateScore(content);

                // Save results to MySQL
                updateStatement.setString(1, sentiment);
                updateStatement.setDouble(2, score);
                updateStatement.setInt(3, id);

                updateStatement.executeUpdate();

                System.out.println("\nID: " + id);
                System.out.println("Company: " + company);
                System.out.println("News: " + title);
                System.out.println("Sentiment: " + sentiment);
                System.out.printf("Score: %.2f/100%n", score);
                System.out.println("✓ Saved to database");
                System.out.println("--------------------------------------");
            }

            System.out.println("\n✅ Sentiment analysis completed!");

        } catch (SQLException e) {

            System.out.println("❌ Database error!");
            e.printStackTrace();
        }
    }
}