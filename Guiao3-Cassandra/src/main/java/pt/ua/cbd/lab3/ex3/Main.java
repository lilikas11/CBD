package pt.ua.cbd.lab3.ex3;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class Main {
    public static void main(String[] args) {
        try (CqlSession session = CqlSession.builder().build()) {                                    // (1)
            

            // descomentar para criar a base de dados
            // addDataBase(session, "ex3_add/CBD_L302_108713_DDL.cql");
            // addDataBase(session, "ex3_add/CBD_L302_108713_SEEDDATA.cql");

            System.out.println("-------------------------------------------");
            ex3a(session);
            System.out.println("-------------------------------------------");
            ex3b(session);
            System.out.println("-------------------------------------------");

            ResultSet rs = session.execute("select release_version from system.local");              // (2)
            Row row = rs.one();
            System.out.println(row.getString("release_version"));                                    // (3)
        }
    }

    static void addDataBase(CqlSession session, String file)  {
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (line != null) {
                System.out.println(line);
				session.execute(line);
				// read next line
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    static void ex3a(CqlSession session) {
        session.execute("Use ex2");

        // insert
        try{
            System.out.println(">> Inserting...");
            System.out.println(">> Insert lili to users");
            session.execute("INSERT INTO users (username, name, email, ts) VALUES ('user11', 'Lili', 'lili@email.com', '2023-01-01T19:30:00Z');");
            System.out.println(">> Insert video 'How to insert mongo with java driver'");
            session.execute("INSERT INTO videos (id, author, name, description, tags, ts) VALUES (12, 'Lili', 'How to insert mongo with java driver', 'Learn the basics', {'programming', 'education', 'Aveiro'}, '2023-11-17T13:00:00Z');");
            System.out.println(">> Insert followers of video 'How to insert mongo with java driver'");
            session.execute("INSERT INTO followers (id_video, users) VALUES (11, {'User4', 'User9', 'User11'});");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        // edição
        try{
            System.out.println(">> Editing...");
            System.out.println(">> Changing name of user11 to Liliana");
            session.execute("UPDATE users SET name = 'Liliana' WHERE username = 'user11';");
            System.out.println(">> Changing name of video 12 to 'How to insert cql with java driver - edited'");
            session.execute("UPDATE videos SET name = 'How to insert cql with java driver - edited' WHERE id = 12;");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        

        // searching
        // 7. Permitir a pesquisa de todos os videos de determinado autor;
        System.out.println(">> Searching all videos of author 'Lili'");
        ResultSet rs = session.execute("SELECT * FROM videos WHERE author = 'Liliana';");
        for (Row row : rs) {
            System.out.println(row.getString("name"));
        }

        // 8. Permitir a pesquisa de comentarios por utilizador, ordenado inversamente pela data
        System.out.println(">> Searching all comments of user 'User1'");
        rs = session.execute("SELECT * FROM comments_user WHERE author = 'User1' ORDER BY ts DESC;");
        for (Row row : rs) {
            System.out.println(row.getString("comment"));
        }

        // 9. Permitir a pesquisa de comentarios por videos, ordenado inversamente pela data;
        System.out.println(">> Searching all comments of video 1");
        rs = session.execute("SELECT * FROM comments_video WHERE id_video = 1 ORDER BY ts DESC;");
        for (Row row : rs) {
            System.out.println(row.getString("comment"));
        }

        // 10. Permitir a pesquisa do rating medio de um video e quantas vezes foi votado;
        System.out.println(">> Searching average rating of video 1");
        rs = session.execute("SELECT AVG(rate) AS avg_rating, COUNT(*) AS vote_count FROM ratings WHERE id_video = 1;");
        for (Row row : rs) {
            System.out.println(row.getInt("avg_rating"));        }
    }


    static void ex3b(CqlSession session) {
        session.execute("Use ex2");


        // 4. Os ultimos 5 eventos de determinado video realizados por um utilizador;
        try{
            System.out.println(">> Searching last 5 events of video 1 by user 'User1'");
            ResultSet rs = session.execute("SELECT * FROM events WHERE username = 'User1' AND id_video = 1 LIMIT 5;");
            for (Row row : rs) {
                System.out.println(row.getString("type"));
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        // 5. Videos partilhados por determinado utilizador (maria1987, por exemplo) num determinado periodo de tempo (Agosto de 2017, por exemplo);
        try{
            System.out.println(">> Searching videos shared by user 'Eva' in August 2017");
            ResultSet rs = session.execute("SELECT * FROM videos WHERE author = 'Eva' AND ts >= '2017-08-01' AND ts <= '2017-08-31';");
            for (Row row : rs) {
                System.out.println(row.getString("name"));
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        // 7. Todos os seguidores (followers) de determinado video;
        try{
            System.out.println(">> Searching all followers of video 1");
            ResultSet rs = session.execute("SELECT users FROM followers WHERE id_video = 1;");
            for (Row row : rs) {
                System.out.println(row.getSet("users", String.class));
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }


        // 13. Número total de comentarios feitos por um utilizador;
        try{
            System.out.println(">> Searching total number of comments of user 'User1'");
            ResultSet rs = session.execute("SELECT COUNT(*) FROM comments_user WHERE author = 'User1';");
            for (Row row : rs) {
                System.out.println(row.getLong("count"));
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

    }

}