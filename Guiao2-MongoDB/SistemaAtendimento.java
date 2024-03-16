package ua.deti;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Arrays;
import java.util.Scanner;

public class SistemaAtendimento {
    private static final int LIMIT = 6;
    private static final int TIMESLOT = 100;

    public boolean pedido(MongoCollection<Document> collection, String username, String product) {
        long currentTime = System.currentTimeMillis() / 1000;
        Document userDoc = collection.find(new Document("username", username)).first();
        if (userDoc != null) {
            return updateUserOrder(collection, userDoc, product, currentTime);
        } else {
            return createNewUserOrder(collection, username, product, currentTime);
        }
    }

    private boolean updateUserOrder(MongoCollection<Document> collection, Document userDoc, String product, long currentTime) {
        long lastTime = userDoc.getLong("lastTime");
        long elapsedTime = currentTime - lastTime;
        if (userDoc.getList("products", String.class).size() >= LIMIT || elapsedTime > TIMESLOT) {
            System.out.println("Erro: Limite de pedidos excedido ou limite de tempo excedeu.");
            return false;
        }
        collection.updateOne(new Document("username", userDoc.getString("username")), 
                             new Document("$push", new Document("products", product)));
        return true;
    }

    private boolean createNewUserOrder(MongoCollection<Document> collection, String username, String product, long currentTime) {
        collection.insertOne(new Document("username", username)
                .append("products", Arrays.asList(product))
                .append("lastTime", currentTime));
        return true;
    }

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost");
             Scanner scanner = new Scanner(System.in)) {
            MongoDatabase database = mongoClient.getDatabase("cbd2");
            MongoCollection<Document> collection = database.getCollection("users");
            database.drop();

            SistemaAtendimento sistema = new SistemaAtendimento();

            System.out.println("Digite o nome do utilizador:");
            String username = scanner.nextLine();
            handleUserRequests(scanner, sistema, collection, username);
        }
    }

    private static void handleUserRequests(Scanner scanner, SistemaAtendimento sistema, MongoCollection<Document> collection, String username) {
        while (true) {
            System.out.println("Digite o nome do produto:");
            String product = scanner.nextLine();

            if (!sistema.pedido(collection, username, product)) {
                System.out.println("Erro: Pedido não registrado");
                return;
            }

            System.out.println("Pedido registrado com sucesso!");
            if (checkOrderLimit(collection, username)) return;

            System.out.println("Deseja fazer outro pedido? (s/n)");
            if (!"s".equalsIgnoreCase(scanner.nextLine())) {
                break;
            }
        }
    }

    private static boolean checkOrderLimit(MongoCollection<Document> collection, String username) {
        Document userDoc = collection.find(new Document("username", username)).first();
        if (userDoc != null && userDoc.getList("products", String.class).size() >= LIMIT) {
            System.out.println("Limite de pedidos excedeu");
            return true;
        }
        return false;
    }
}