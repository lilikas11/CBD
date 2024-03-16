package ua.deti;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SistemaAtendimentoB {
    private static final int LIMIT = 4;
    private static final int TIMESLOT = 100;

    public boolean placeOrder(MongoCollection<Document> collection, String username, String product, int quantity) {
        long currentTime = System.currentTimeMillis() / 1000;
        Document userDoc = collection.find(new Document("username", username)).first();

        if (userDoc != null) {
            return updateUserOrder(collection, userDoc, product, quantity, currentTime);
        } else {
            return createNewUserOrder(collection, username, product, quantity, currentTime);
        }
    }

    private boolean updateUserOrder(MongoCollection<Document> collection, Document userDoc, String product, int quantity, long currentTime) {
        long lastTime = userDoc.getLong("lastTime");
        long elapsedTime = currentTime - lastTime;
        if (elapsedTime > TIMESLOT) {
            System.out.println("Erro: Limite de tempo excedeu.");
            return false;
        }

        List<Document> products = userDoc.getList("products", Document.class);
        int productCount = calculateProductQuantity(products, product);

        if (productCount + quantity > LIMIT) {
            System.out.println("Erro: Limite total de unidades do produto " + product + " excedido");
            return false;
        }

        Bson filter = new Document("username", username);
        Bson updateOperation = new Document("$push", new Document("products", new Document("name", product).append("quantity", quantity)));
        collection.updateOne(filter, updateOperation);

        return true;
    }

    private int calculateProductQuantity(List<Document> products, String product) {
        return products.stream()
                       .filter(p -> p.getString("name").equals(product))
                       .mapToInt(p -> p.getInteger("quantity"))
                       .sum();
    }

    private boolean createNewUserOrder(MongoCollection<Document> collection, String username, String product, int quantity, long currentTime) {
        if (quantity > LIMIT) {
            System.out.println("Erro: Limite total de unidades do produto " + product + " excedido");
            return false;
        }

        Document newUserOrder = new Document("username", username)
            .append("products", List.of(new Document("name", product).append("quantity", quantity)))
            .append("lastTime", currentTime);

        collection.insertOne(newUserOrder);
        return true;
    }

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost");
             Scanner scanner = new Scanner(System.in)) {
            MongoDatabase database = mongoClient.getDatabase("cbd2");
            MongoCollection<Document> collection = database.getCollection("users");
            database.drop();

            SistemaAtendimentoB sistema = new SistemaAtendimentoB();

            System.out.println("Digite o nome do utilizador:");
            String username = scanner.nextLine();

            while (true) {
                System.out.println("Digite o nome do produto:");
                String product = scanner.nextLine();
                
                System.out.println("Digite a quantidade desejada:");
                int quantity = scanner.nextInt();
                scanner.nextLine();  // Clear buffer

                boolean success = sistema.placeOrder(collection, username, product, quantity);
                if (!success) {
                    System.out.println("Erro: Pedido não registrado");
                    return;
                }

                System.out.println("Pedido registrado com sucesso!");
                if (isOrderLimitReached(collection, username, product)) return;

                System.out.println("Deseja fazer outro pedido? (s/n)");
                if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                    break;
                }
            }
        }
    }

    private static boolean isOrderLimitReached(MongoCollection<Document> collection, String username, String product) {
        Document userDoc = collection.find(new Document("username", username)).first();
        if (userDoc != null) {
            List<Document> products = userDoc.getList("products", Document.class);
            int productCount = calculateProductQuantity(products, product);

            if (productCount >= LIMIT) {
                System.out.println("Limite total de unidades do produto " + product + " excedido");
                return true;
            }
        }
        return false;
    }

    private static int calculateProductQuantity(List<Document> products, String product) {
        return products.stream()
                       .filter(p -> p.getString("name").equals(product))
                       .mapToInt(p -> p.getInteger("quantity"))
                       .sum();
    }\
}