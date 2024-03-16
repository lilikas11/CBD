Use ex2;

// 7. Permitir a pesquisa de todos os videos de determinado autor;
SELECT * FROM videos WHERE author = 'John';

// 8. Permitir a pesquisa de comentarios por utilizador, ordenado inversamente pela data
SELECT * FROM comments_user WHERE author = 'User1' ORDER BY ts DESC;


// 9. Permitir a pesquisa de comentarios por videos, ordenado inversamente pela data;
SELECT * FROM comments_video WHERE id_video = 1 ORDER BY ts DESC;

// 10. Permitir a pesquisa do rating medio de um video e quantas vezes foi votado;
SELECT AVG(rate) AS avg_rating, COUNT(*) AS vote_count FROM ratings WHERE id_video = 1;


// 1. Os ultimos 3 comentarios introduzidos para um video;
SELECT * FROM comments_video WHERE id_video = 1 LIMIT 3;

// 2. Lista das tags de determinado video;
SELECT tags FROM videos WHERE id = 1;


// 3. Todos os videos com a tag Aveiro;
SELECT * FROM videos WHERE tags CONTAINS 'Aveiro';


// 4. Os ultimos 5 eventos de determinado video realizados por um utilizador;
SELECT * FROM events WHERE username = 'User1' AND id_video = 1 LIMIT 5;


// 5. Videos partilhados por determinado utilizador (maria1987, por exemplo) num determinado periodo de tempo (Agosto de 2017, por exemplo);
SELECT * FROM videos WHERE author = 'Eva' AND ts >= '2017-08-01' AND ts <= '2017-08-31';


// 6. Os ultimos 10 videos, ordenado inversamente pela data da partilhada;
// Não é possivel ordenar inversamente pela data da partilha, pois a chave primaria é o id do video. Mas os ultimos 10 videos seria assim:
SELECT * FROM videos LIMIT 10;

// 7. Todos os seguidores (followers) de determinado video;
SELECT users FROM followers WHERE id_video = 1;

// 8. Todos os comentarios (dos videos) que determinado utilizador esta a seguir (following);
// Não é possivel fazer esta query em cassandra, pois não é possivel fazer joins entre tabelas.
// Esta seria uma operação muito complexa, pois teriamos de percorrer a tabela de followers e para cada utilizador


// 9. Os 5 videos com maior rating;
// Não é possivel porque não conseguimos ordenar pelo rate, já que a chave primaria é id_video, e este tinha de ser mencionado.
SELECT * FROM ratings LIMIT 5;


// 10. Uma query que retorne todos os videos e que mostre claramente a forma pela qual estao ordenados;
// Estão ordenados pela ordem de inserção
SELECT * FROM videos;


// 11. Lista com as Tags existentes e o numero de videos catalogados com cada uma delas;
// seria necessário criar uma tabela com as tags como primary key

// 12.
// Todos os eventos de um utilizador, ordenados inversamente pela data;
SELECT * FROM events WHERE username = 'User1' ORDER BY ts DESC;

// 13.
// Número total de comentarios feitos por um utilizador;
SELECT COUNT(*) FROM comments_user WHERE author = 'User1';