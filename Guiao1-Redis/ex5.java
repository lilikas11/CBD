package ua.deti;

import java.util.Scanner;
import redis.clients.jedis.Jedis;

public class ex5 {
    private Jedis jedis;
    private int productLimit;
    private int timeLimit;

    public ex5(Jedis jedis, int productLimit, int timeLimit) {
        this.jedis = jedis;
        this.productLimit = productLimit;
        this.timeLimit = timeLimit;
    }

    public boolean isAllowed(String username, String product) {
        String userKey = String.format("user:%s", username);
        String productKey = String.format("user:%s:product:%s", username, product);
        long currentTime = System.currentTimeMillis() / 1000;

        if (jedis.scard(userKey) >= productLimit) {
            System.out.println("Erro: Limite de pedidos de produtos excedido");
            return false;
        }

        if (jedis.sismember(userKey, product)) {
            System.out.println("Erro: Produto já solicitado anteriormente");
            return false;
        }

        String value = jedis.get(productKey);
        if (value != null) {
            String[] parts = value.split(":");
            long lastTime = Long.parseLong(parts[0]);

            if (currentTime - lastTime < timeLimit) {
                System.out.println("Erro: Limite de tempo excedido para o produto");
                return false;
            }
        }

        jedis.set(productKey, currentTime + ":1");
        jedis.sadd(userKey, product);

        return true;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll(); // Descarta os dados da última utilização
        ex5 rateLimiter = new ex5(jedis, 3, 3600); // Altere os valores conforme necessário

        Scanner scanner = new Scanner(System.in);

        System.out.println("Insira o nome de utilizador:");
        String username = scanner.nextLine();
        long lastRequestTime = System.currentTimeMillis() / 1000;

        while (true) {
            long currentTime = System.currentTimeMillis() / 1000;

            if (currentTime - lastRequestTime >= 3600) { // Altere para o mesmo número de segundos definido acima
                System.out.println("Limite de tempo atingido. Não é possível pedir mais produtos neste momento.");
                break;
            } else {
                System.out.println("Insira o produto:");
                String product = scanner.nextLine();

                if (!rateLimiter.isAllowed(username, product)) {
                    System.out.println("Limite de produtos atingido ou produto já solicitado anteriormente. Não é possível pedir mais produtos neste momento.");
                    break;
                } else {
                    System.out.println("Pedido atendido para o produto: " + product);
                }
            }
        }
        scanner.close();
    }
}
