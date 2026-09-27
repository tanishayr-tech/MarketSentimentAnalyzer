package com.marketsentiment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class NewsAPI {

    private static final String URL =
            "jdbc:mysql://localhost:3306/market_sentiment";

    private static final String USER =
            "root";

    private static final String PASSWORD =
            "tanisha@1808";


    public static void main(String[] args) throws Exception {

        // Register MySQL driver
        DriverManager.registerDriver(
                new com.mysql.cj.jdbc.Driver()
        );


        // Create server
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );


        // Existing news endpoint
        server.createContext(
                "/api/news",
                NewsAPI::getNews
        );


        // NEW sentiment analysis endpoint
        server.createContext(
                "/api/analyze",
                NewsAPI::analyzeNews
        );


        server.setExecutor(null);


        System.out.println(
                "======================================"
        );

        System.out.println(
                "       MARKET SENTIMENT API"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "News API:"
        );

        System.out.println(
                "http://localhost:8080/api/news"
        );

        System.out.println();

        System.out.println(
                "Sentiment API:"
        );

        System.out.println(
                "http://localhost:8080/api/analyze"
        );


        server.start();
    }



    // =========================================
    // GET NEWS
    // =========================================

    private static void getNews(
            HttpExchange exchange
    ) throws IOException {

        addCorsHeaders(exchange);

        exchange.getResponseHeaders()
                .add(
                        "Content-Type",
                        "application/json"
                );


        String json =
                getNewsFromDatabase();


        sendResponse(
                exchange,
                200,
                json
        );
    }



    // =========================================
    // ANALYZE NEW HEADLINE
    // =========================================

    private static void analyzeNews(
            HttpExchange exchange
    ) throws IOException {

        addCorsHeaders(exchange);


        // Handle OPTIONS request
        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            exchange.close();

            return;
        }


        // Only POST is allowed
        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"Only POST method is allowed\"}"
            );

            return;
        }


        // Read request body
        InputStream input =
                exchange.getRequestBody();


        String requestBody =
                new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );


        System.out.println(
                "Analyze request: "
                + requestBody
        );


        // Extract headline from JSON
        String headline =
                extractHeadline(
                        requestBody
                );


        if (headline == null ||
                headline.trim().isEmpty()) {

            sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"Headline is required\"}"
            );

            return;
        }


        // Analyze sentiment
        String sentiment =
                SentimentAnalyzer
                        .analyzeSentiment(
                                headline
                        );


        // Calculate score
        int score =
                SentimentAnalyzer
                        .calculateScore(
                                headline
                        );


        // Create response
        String response =
                "{"
                + "\"headline\":\""
                + escapeJson(headline)
                + "\","

                + "\"sentiment\":\""
                + sentiment
                + "\","

                + "\"score\":"
                + score

                + "}";


        sendResponse(
                exchange,
                200,
                response
        );
    }



    // =========================================
    // EXTRACT HEADLINE FROM JSON
    // =========================================

    private static String extractHeadline(
            String json
    ) {

        String key =
                "\"headline\"";


        int keyPosition =
                json.indexOf(key);


        if (keyPosition == -1) {

            return null;
        }


        int colonPosition =
                json.indexOf(
                        ":",
                        keyPosition
                );


        if (colonPosition == -1) {

            return null;
        }


        int firstQuote =
                json.indexOf(
                        "\"",
                        colonPosition + 1
                );


        if (firstQuote == -1) {

            return null;
        }


        int secondQuote =
                json.indexOf(
                        "\"",
                        firstQuote + 1
                );


        if (secondQuote == -1) {

            return null;
        }


        return json.substring(
                firstQuote + 1,
                secondQuote
        );
    }



    // =========================================
    // GET NEWS FROM DATABASE
    // =========================================

    private static String getNewsFromDatabase() {

        String query =
                "SELECT id, company, title "
                + "FROM news "
                + "ORDER BY id DESC";


        List<String> newsList =
                new ArrayList<>();


        try (

            Connection connection =
                    DriverManager.getConnection(
                            URL,
                            USER,
                            PASSWORD
                    );

            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(query)

        ) {


            while (resultSet.next()) {


                int id =
                        resultSet.getInt(
                                "id"
                        );


                String company =
                        resultSet.getString(
                                "company"
                        );


                String title =
                        resultSet.getString(
                                "title"
                        );


                // Analyze headline automatically
                String sentiment =
                        SentimentAnalyzer
                                .analyzeSentiment(
                                        title
                                );


                // Calculate score
                int score =
                        SentimentAnalyzer
                                .calculateScore(
                                        title
                                );


                String news =
                        "{"

                        + "\"id\":"
                        + id
                        + ","

                        + "\"company\":\""
                        + escapeJson(company)
                        + "\","

                        + "\"title\":\""
                        + escapeJson(title)
                        + "\","

                        + "\"sentiment\":\""
                        + sentiment
                        + "\","

                        + "\"score\":"
                        + score

                        + "}";


                newsList.add(news);
            }


        } catch (SQLException e) {

            e.printStackTrace();


            return
                    "{\"error\":\"Database connection failed\"}";
        }


        return
                "["
                + String.join(
                        ",",
                        newsList
                )
                + "]";
    }



    // =========================================
    // CORS
    // =========================================

    private static void addCorsHeaders(
            HttpExchange exchange
    ) {

        exchange.getResponseHeaders()
                .add(
                        "Access-Control-Allow-Origin",
                        "*"
                );

        exchange.getResponseHeaders()
                .add(
                        "Access-Control-Allow-Methods",
                        "GET, POST, OPTIONS"
                );

        exchange.getResponseHeaders()
                .add(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );
    }



    // =========================================
    // SEND RESPONSE
    // =========================================

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );


        byte[] data =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.sendResponseHeaders(
                statusCode,
                data.length
        );


        OutputStream output =
                exchange.getResponseBody();


        output.write(data);

        output.close();
    }



    // =========================================
    // ESCAPE JSON
    // =========================================

    private static String escapeJson(
            String text
    ) {

        if (text == null) {

            return "";
        }


        return text
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                );
    }


}