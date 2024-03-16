// UPDATE...........
// John cresceu e agora tem 20 anos
UPDATE Players SET age = 20 WHERE name = 'john';

// O item 1 ficou mais caro e agora custa 100
UPDATE Itens SET price = 100 WHERE id = 1 and ts='2023-11-26T18:30:00Z';

// o bloco 1 teve as suas propriedades alteradas
UPDATE Blocks SET hardness = 10 WHERE id = 1;

// Novos jogadores entraram no mundo
UPDATE Words SET players += {'alice', 'john'} WHERE id = 2 and ts='2023-11-26T12:30:00Z';

// Novos blocos foram adicionados ao mundo
UPDATE Words SET blocks_ids = [1, 2, 3] WHERE id = 1 and ts='2023-11-26T12:00:00Z';

UPDATE Players_Events SET events['2023-11-26T12:30:00Z'] = 5 WHERE player_name = 'bob';

// INSERT..........
// Novos eventos foram adicionados ao jogo
insert into events (id, event_name, event_description ) values (16, 'evento16', 'Evento de Natal' );

// UPDATE...........
// Novos players conseguiram os novos eventos
UPDATE Players_Events SET events += {toTimestamp(now()): 16} WHERE player_name = 'alice';
UPDATE Players_Events SET events += {toTimestamp(now()): 16} WHERE player_name = 'david';

// DELETE...........
// O mundo 15 foi apagado pelo administrador
DELETE FROM Words WHERE id = 15 and ts='2023-11-26T19:00:00Z';

// Passou a epoca natalicia e o evento de natal foi apagado
DELETE FROM Events WHERE id = 16;

// O player david foi banido do jogo
DELETE FROM Players WHERE name = 'david';

// Os items do david foram apagados
DELETE FROM Itens WHERE id=4 and ts='2023-11-26T18:45:00Z';

// O bloco 30 foi apagado pelo administrador
DELETE FROM Blocks WHERE id = 30;






// Queries.........
// 1. Obter o nome e a idade de todos os jogadores
SELECT name, age FROM Players;

// 2. Obter o nome e o preço de todos os items
SELECT item_name, price FROM Itens;

// 3. Obter o nome e o preço de todos os items do jogador alice
SELECT item_name, price FROM Itens WHERE player_name = 'alice';

// 4. Obter o nome do jogador e o preço de todos as pocoes de cura
SELECT player_name, price FROM Itens WHERE item_name = 'pocao de cura';

// 5. Obter os eventos que o jogador alice tem
SELECT events FROM Players_Events WHERE player_name = 'alice';

// 6. Obter todos os jogadores com 20 anos
SELECT * FROM Players WHERE age = 20;

// 7. Obter todos os items da categoria arma
SELECT * FROM Itens WHERE category = 'arma';

// 8. Obter todos os blocos do tipo pedra
SELECT * FROM Blocks WHERE bloco_type = 'pedra';

// 9. Obter os mundos que o jogador alice tem
SELECT * FROM Words WHERE players CONTAINS 'alice';

// 10. Obter os mundos que tem o bloco 1
SELECT * FROM Words WHERE blocks_ids CONTAINS 1;

// 11. Obter todos os players que participaram no evento de natal
SELECT player_name FROM Players_Events WHERE events CONTAINS 1;




