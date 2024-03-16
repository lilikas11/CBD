package ua.deti;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Ex3 {

    private static MongoCollection<Document> collection;

    public static void main(String[] args) {
        String uri = "mongodb://localhost";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("cbd2");
            collection = database.getCollection("restaurants");

            basicOperations();
            createIndexesForCollection();
            timeCheckWithAndWithoutIndexes();
            aggregationAndComplexQueries();
            writeResultsToFile();
        }
    }

    private static void basicOperations() {
        // insert, update, find
        Document newRestaurant = new Document("localidade", "Aveiro")
                .append("nome", "DuFogo")
                .append("restaurant_id", "7254971");
        collection.insertOne(newRestaurant);

        collection.updateOne(eq("localidade", "Aveiro"), Updates.set("nome", "Churrasqueira"));

        Document found = collection.find(eq("localidade", "Aveiro")).first();
        if (found != null) {
            System.out.println(found.toJson());
        }
    }

    private static void createIndexesForCollection() {
        collection.createIndex(Indexes.ascending("gastronomia", "localidade"));
        collection.createIndex(Indexes.text("nome"));
    }

    private static void timeCheckWithAndWithoutIndexes() {
        long start = System.currentTimeMillis();
        collection.find(eq("gastronomia", "American")).forEach(document -> {});
        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Time taken: " + timeTaken + "ms");
    }

    private static void aggregationAndComplexQueries() {
        AggregateIterable<Document> countByLocalidade = collection.aggregate(
                Collections.singletonList(
                        Aggregates.group("$localidade", Accumulators.sum("count", 1))
                )
        );

        countByLocalidade.forEach(document -> System.out.println(document.toJson()));
    }

    private static void writeResultsToFile() {
        Path filePath = Paths.get("CBD_L203_103415.txt");
        try {
            List<String> lines = new ArrayList<>();
            lines.add("Number of distinct localities: " + collection.distinct("localidade", String.class).into(new ArrayList<>()).size());
            
            Map<String, Long> restaurantCountByLocality = countRestByLocality();
            lines.add("\nNumber of restaurants by locality:");
            restaurantCountByLocality.forEach((locality, count) ->
                    lines.add(" -> " + locality + " - " + count));

            List<String> restaurantsWithPark = getRestWithNameCloserTo("Park");
            System.out.println("Name of restaurants containing 'Park' in the name:");
            restaurantsWithPark.forEach(name -> System.out.println(" -> " + name));

            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Results successfully added to " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }
    }

    private static Map<String, Long> countRestByLocality() {
        return StreamSupport.stream(collection.aggregate(
                Collections.singletonList(
                        Aggregates.group("$localidade", Accumulators.sum("count", 1))
                )).spliterator(), false)
                .collect(Collectors.toMap(
                        doc -> doc.getString("_id"),
                        doc -> doc.getLong("count")));
    }

    public static List<String> getRestWithNameCloserTo(String name) {
        List<String> restaurantNames = new ArrayList<>();
        // Using the text index created on the "nome" field for a text search
        collection.find(Filters.text(name)).forEach(document -> {
            String restaurantName = document.getString("nome");
            if(restaurantName != null && restaurantName.contains(name)) {
                restaurantNames.add(restaurantName);
            }
        });
        return restaurantNames;
    }
}
