package com.example;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import redis.clients.jedis.Jedis;


public class Autocomplete {
    public static String USERS_KEY = "users";
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Jedis jedis = new Jedis();
        jedis.flushDB();
        Scanner fileScanner = null;

        // colocar informações no redis
        try {
            fileScanner = new Scanner(new File("./src/names.txt"));
        } catch (FileNotFoundException e){
            System.out.println("File not found");
            System.exit(1);
        }

        while(fileScanner.hasNextLine()) {
            String s = fileScanner.nextLine();
            jedis.sadd(USERS_KEY, s);
        }

        fileScanner.close();

        String user = "x";
        while (true){

            System.out.println("Search for ('Enter' for quit): ");
            user = scan.nextLine().toLowerCase();
            if(user.equals("")) break;
            
            Set<String> names = jedis.smembers(USERS_KEY);
            List<String> matches = new ArrayList<>();
            for(String name : names) 
                if(name.toLowerCase().startsWith(user.toLowerCase())) matches.add(name);

            Collections.sort(matches);
            for(String name : matches)
                System.out.println(name);
            
        }
        jedis.close();
        scan.close();
    }
    
}
