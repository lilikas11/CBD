package ua.deti;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import redis.clients.jedis.Jedis;

public class ex5b {
    private Jedis jedis;
    private int productLimit;
    private int timeLimit;
    private Map<String, Map<String, Integer>> userProductQuantities;

    public ex5b(Jedis jedis, int productLimit, int timeLimit) {
        this.jedis = jedis;
        this.productLimit = productLimit;
        this.timeLimit = timeLimit;
        this.userProductQuantities = new HashMap<>();
    }

    public boolean isAllowed(String username, String product, int quantity) {
        String userKey = String.format("user:%s", username);
        String productKey = String.format("user:%s:product:%s", username, product);
        long currentTime = System.currentTimeMillis() / 1000;

        userProductQuantities.putIfAbsent(username, new HashMap<>());
        Map<String, Integer> userProducts = userProductQuantities.get(username);
        userProducts.putIfAbsent(product, 0);

        int currentQuantity = userProducts.get(product);
        int totalQuantity = currentQuantity + quantity;

        if (totalQuantity > productLimit) {
            System.out.println("Erro: Limite de pedidos de produtos excedido");
            return false;
        }

        userProducts.put(product, totalQuantity);
        jedis.set(productKey, currentTime + ":" + totalQuantity);

        String value = jedis.get(userKey);
        int totalUserQuantity = 0;

        if (value != null) {
            String[] parts = value.split(":");
            long lastTime = Long.parseLong(parts[0]);
            totalUserQuantity = Integer.parseInt(parts[1]);

            if (currentTime - lastTime < timeLimit && totalUserQuantity + quantity > productLimit) {
                System.out.println("Erro: Limite de quantidade excedido para o produto");
                return false;
            }
        }

        jedis.set(userKey, currentTime + ":" + (totalUserQuantity + quantity));
        return true;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();
        ex5b rateLimiter = new ex5b(jedis, 3, 3600);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Insira o nome de utilizador:");
        String username = scanner.nextLine();

        long lastRequestTime = System.currentTimeMillis() / 1000;

        while (true) {
            long currentTime = System.currentTimeMillis() / 1000;

            if (currentTime - lastRequestTime >= 3600) {
                System.out.println("Limite de tempo atingido. Não é possível pedir mais produtos neste momento.");
                break;
            } else {
                System.out.println("Insira o produto:");
                String product = scanner.nextLine();

                try {
                    System.out.println("Insira a quantidade:");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();

                    if (!rateLimiter.isAllowed(username, product, quantity)) {
                        System.out.println("Não é possível pedir mais produtos neste momento.");
                        break;
                    } else {
                        System.out.println("Pedido atendido para o produto: " + product);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Erro: Quantidade inválida. Por favor, insira um número inteiro.");
                    scanner.nextLine();
                }
            }
        }
        scanner.close();
    }
}
