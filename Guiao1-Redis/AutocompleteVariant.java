package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class AutocompleteVariant {
    public static String USERS_KEY = "users";
    public static String DATE_KEY = "date";

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Jedis jedis = new Jedis();
        jedis.flushDB();
        Scanner fileScanner = null;

        // colocar informações no redis
        try {
            fileScanner = new Scanner(new File("./src/nomes-pt-2021.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }

        while (fileScanner.hasNextLine()) {
            String s = fileScanner.nextLine();
            String[] dados = s.split(";");

            jedis.zadd(DATE_KEY, Double.parseDouble(dados[1]), dados[0]);
            jedis.sadd(USERS_KEY, dados[0]);
        }

        fileScanner.close();

        String user = "x";
        while (true) {

            System.out.println("Search for: ('Enter' for quit) ");
            user = scan.nextLine().toLowerCase().toString();
            if (user.equals(""))
                break;

            Set<String> names = jedis.smembers(USERS_KEY);
            List<String> matches = new ArrayList<>();
            for (String name : names) {
                if (name.toLowerCase().toString().startsWith(user)) {
                    System.out.println(name);
                    matches.add(name);
                }
            }

            Collections.sort(matches, (name1, name2) -> {
                Double rating1 = jedis.zscore(DATE_KEY, name1);
                Double rating2 = jedis.zscore(DATE_KEY, name2);
                System.out.println("maria");
                return rating2.compareTo(rating1);
            });
            
            for (String name : matches)
                System.out.println(name);
        }
        jedis.close();
        scan.close();
    }

}
