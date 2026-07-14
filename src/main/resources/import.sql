-- Players
INSERT INTO players(name) VALUES ('Novak Djokovic');
INSERT INTO players(name) VALUES ('Rafael Nadal');
INSERT INTO players(name) VALUES ('Roger Federer');
INSERT INTO players(name) VALUES ('Carlos Alcaraz');
INSERT INTO players(name) VALUES ('Jannik Sinner');
INSERT INTO players(name) VALUES ('Daniil Medvedev');
INSERT INTO players(name) VALUES ('Alexander Zverev');
INSERT INTO players(name) VALUES ('Stefanos Tsitsipas');
INSERT INTO players(name) VALUES ('Andrey Rublev');
INSERT INTO players(name) VALUES ('Casper Ruud');

-- match
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (1, 2, 1);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (3, 4, 4);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (5, 6, 5);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (7, 8, 7);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (9, 10, 10);

INSERT INTO match(player1_id, player2_id, winner_id) VALUES (1, 3, 1);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (2, 4, 2);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (5, 7, 5);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (6, 8, 8);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (9, 1, 1);

INSERT INTO match(player1_id, player2_id, winner_id) VALUES (10, 2, 2);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (3, 5, 5);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (4, 6, 4);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (7, 9, 7);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (8, 10, 8);

INSERT INTO match(player1_id, player2_id, winner_id) VALUES (1, 5, 1);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (2, 6, 6);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (3, 7, 7);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (4, 8, 4);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (9, 5, 5);

INSERT INTO match(player1_id, player2_id, winner_id) VALUES (10, 1, 1);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (2, 7, 7);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (3, 8, 8);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (4, 9, 4);
INSERT INTO match(player1_id, player2_id, winner_id) VALUES (6, 10, 6);
