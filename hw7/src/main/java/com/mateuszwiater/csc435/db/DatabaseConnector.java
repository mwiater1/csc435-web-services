package com.mateuszwiater.csc435.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

@SuppressWarnings("ALL")
public class DatabaseConnector {
    private static Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
    private static DatabaseConnector instance;
    private Connection connection;

    private static void bootstrap(final Connection con) {
        try {
            ResultSet rs = con.getMetaData().getTables(null, null, null, new String[]{"TABLE"});

            // Check if DB is empty.
            rs.last();
            if (rs.getRow() == 0) {
                // Create the Users table.
                logger.info("Creating Tables...");
                Statement st = con.createStatement();
                st.addBatch("CREATE TABLE USERS (" +
                        "USERNAME VARCHAR(255) NOT NULL," +
                        "PASSWORD VARCHAR(255) NOT NULL," +
                        "APIKEY UUID NOT NULL," +
                        "PRIMARY KEY (USERNAME));");

                // Create the Sources table
                st.addBatch("CREATE TABLE SOURCES (" +
                        "ID VARCHAR(255) NOT NULL," +
                        "NAME VARCHAR(1024) NOT NULL," +
                        "DESCRIPTION VARCHAR(1024) NOT NULL," +
                        "URL VARCHAR(1024) NOT NULL," +
                        "CATEGORY VARCHAR(255) NOT NULL," +
                        "LANGUAGE VARCHAR(255) NOT NULL," +
                        "COUNTRY VARCHAR(255) NOT NULL," +
                        "SORTBYS VARCHAR(255) NOT NULL," +
                        "PRIMARY KEY (ID));");

                // Create the Articles table
                st.addBatch("CREATE TABLE ARTICLES (" +
                        "ID int NOT NULL AUTO_INCREMENT," +
                        "AUTHOR VARCHAR(1024) NOT NULL," +
                        "TITLE VARCHAR(1024) NOT NULL," +
                        "DESCRIPTION VARCHAR(1024) NOT NULL," +
                        "URL VARCHAR(1024) NOT NULL," +
                        "URLTOIMAGE VARCHAR(1024) NOT NULL," +
                        "SOURCE VARCHAR(255) NOT NULL," +
                        "SORTBY VARCHAR(255) NOT NULL," +
                        "PUBLISHEDAT VARCHAR(255) NOT NULL," +
                        "PRIMARY KEY (ID));");

                st.addBatch("CREATE TABLE PREFERENCES (" +
                        "ID int NOT NULL AUTO_INCREMENT," +
                        "APIKEY UUID NOT NULL," +
                        "ARTICLEID VARCHAR(255) NOT NULL," +
                        "FAVORITE VARCHAR(255) NOT NULL," +
                        "READ VARCHAR(255) NOT NULL," +
                        "PRIMARY KEY (ID));");

                st.executeBatch();
                logger.info("Tables Created!");

                SourcesLoader.loadSources();
                ArticlesLoader.loadArticles("c923e97e84ff46e6a7657a6b48995b6e");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private DatabaseConnector() {
        try {
            final Gson g = new Gson();
            final Reader config = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config.json"));
            final DatabaseConfig db = g.fromJson(config, new TypeToken<DatabaseConfig>(){}.getType());
            Class.forName(db.getDriver());
            connection = DriverManager.getConnection(db.getUrl(), db.getUser(), db.getPassword());
        } catch (ClassNotFoundException e) {
            logger.error("H2 Driver Class Not Found!", e);
            System.exit(-1);
        } catch (SQLException e) {
            logger.error("Error Establishing Connection To Database!", e);
        }
    }

    private static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
            bootstrap(instance.getConnection());
        }

        return instance;
    }

    private Connection getConnection() {
        return connection;
    }

    public static Optional<List<List<String>>> runQuery(final String query) throws SQLException {
        List<List<String>> table = new ArrayList<>();

        try (final Statement stmt = getInstance().getConnection().createStatement()) {
            // Check if there is a ResultSet
            if (stmt.execute(query)) {
                // Convert the ResultSet to List<List<String>>
                try (final ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) {
                        final List<String> row = new ArrayList<>();
                        for(int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                            row.add(rs.getString(i));
                        }
                        table.add(row);
                    }

                    return table.size() == 0 ? Optional.empty() : Optional.of(table);
                }
            } else {
                return Optional.empty();
            }
        }
    }

    private static class DatabaseConfig {
        private String url, user, driver, password;

        public DatabaseConfig(final String driver, final String url, final String user, final String password) {
            this.url = url;
            this.user = user;
            this.driver = driver;
            this.password = password;
        }

        String getUrl() {
            return url;
        }

        String getUser() {
            return user;
        }

        String getDriver() {
            return driver;
        }

        String getPassword() {
            return password;
        }
    }
}
