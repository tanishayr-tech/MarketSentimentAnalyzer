package com.marketsentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/market_sentiment";
    private static final String USER = "root";
    private static final String PASSWORD = "tanisha@1808";

    public static void main(String[] args) {

        String query = "SELECT id, title, content, source, company FROM news";

        try (
            Connection connection =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query)
        ) {

            System.out.println("✅ Database connected!");
            System.out.println("\n===== MARKET NEWS =====");

            while (resultSet.next()) {

                System.out.println("\nID: " +
                        resultSet.getInt("id"));

                System.out.println("Title: " +
                        resultSet.getString("title"));

                System.out.println("Content: " +
                        resultSet.getString("content"));

                System.out.println("Source: " +
                        resultSet.getString("source"));

                System.out.println("Company: " +
              
                        resultSet.getString("company"));

                System.out.println("----------------------------");
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error!");
            e.printStackTrace();
        }
    }
}