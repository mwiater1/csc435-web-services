package com.mateuszwiater.csc435.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mateuszwiater.csc435.db.DatabaseConnector;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Source {
    private boolean isNew;
    private List<String> sortBysAvailable;
    private Map<String, String> urlsToLogos;
    private String id, name, description, url, category, language, country;

    public Source(String id, String name, String description, String url, String category, String language,
                   String country, Map<String, String> urlsToLogos, List<String> sortBysAvailable) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.isNew = true;
        this.country = country;
        this.language = language;
        this.category = category;
        this.description = description;
        this.urlsToLogos = urlsToLogos;
        this.sortBysAvailable = sortBysAvailable;
    }

    public void save() throws SQLException {
        if (isNew) {
            final Gson gson = new Gson();
            final String query = String.format("INSERT INTO SOURCES (ID,URL,NAME,COUNTRY,LANGUAGE,CATEGORY,DESCRIPTION,LOGOS,SORTBYS) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                    id, url, name, country, language, category, description, gson.toJson(urlsToLogos), gson.toJson(sortBysAvailable));
            DatabaseConnector.runQuery(query);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public Map<String, String> getUrlsToLogos() {
        return urlsToLogos;
    }

    public List<String> getSortBysAvailable() {
        return sortBysAvailable;
    }

    public static Optional<Source> getSource(final String id) throws SQLException {
        final String query = String.format("SELECT * FROM SOURCES WHERE ID = '%s'", id);

        final Optional<List<List<String>>> res = DatabaseConnector.runQuery(query);

        if(res.isPresent()) {
            return Optional.of(toSource(res.get().get(0)));
        } else {
            return Optional.empty();
        }
    }

    public static List<Source> getSources(final Category category, final Language language, final Country country) throws SQLException {
        String query = "";

        if (category != null) {
            query += String.format(" AND CATEGORY = '%s'", category.getName());
        }

        if (language != null) {
            query += String.format(" AND LANGUAGE = '%s'", language.getCode());
        }

        if (country != null) {
            query += String.format(" AND COUNTRY = '%s'", country.getCode());
        }

        query = "SELECT * FROM SOURCES" + query.replaceFirst("AND","WHERE") + ";";

        final Optional<List<List<String>>> res = DatabaseConnector.runQuery(query);

        if(res.isPresent()) {
            return res.get().stream()
                    .map(Source::toSource)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private static Source toSource(final List<String> l) {
        final Gson gson = new Gson();
        final Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        final Type listType = new TypeToken<List<String>>() {}.getType();
        final Source source = new Source(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5), l.get(6), gson.fromJson(l.get(7), mapType), gson.fromJson(l.get(8), listType));
        // Make sure this record is not being saved.
        source.isNew = false;
        return source;
    }
}