CREATE KEYSPACE IF NOT EXISTS ex4 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE ex4;

CREATE TABLE IF NOT EXISTS Players (
    name text PRIMARY KEY,
    age int
);

CREATE TABLE IF NOT EXISTS Itens (
    id int,
    player_name text,
    item_name text,
    category text,
    price int,
    ts timestamp,
    PRIMARY KEY (id, ts)
);

CREATE TABLE IF NOT EXISTS Blocks (
    id int PRIMARY KEY,
    bloco_name text,
    bloco_type text,
    hardness double
);

CREATE TABLE IF NOT EXISTS Words (
    id int,
    players set<text>,
    blocks_ids list<int>,
    word_name text,
    word_description text,
    ts timestamp,
    PRIMARY KEY (id, ts)
);

CREATE TABLE IF NOT EXISTS Events (
    id int PRIMARY KEY,
    event_name text,
    event_description text
);

CREATE TABLE IF NOT EXISTS Players_Events (
    player_name text PRIMARY KEY,
    events map<timestamp, int>
);


CREATE INDEX IF NOT EXISTS ON Players (age);
CREATE INDEX IF NOT EXISTS ON Itens (player_name);
CREATE INDEX IF NOT EXISTS ON Itens (item_name);
CREATE INDEX IF NOT EXISTS ON Blocks (bloco_type);
CREATE INDEX IF NOT EXISTS ON Words (players);
CREATE INDEX IF NOT EXISTS ON Words (blocks_ids);