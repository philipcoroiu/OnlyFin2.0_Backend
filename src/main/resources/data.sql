INSERT INTO users (email, enabled, is_analyst, password, roles, username)
VALUES ('example@example.com', true, false, 'password123', 'user', 'private_user');

INSERT INTO users (email, enabled, is_analyst, password, roles, username)
VALUES ('analyst@example.com', true, true, 'password123', 'user', 'analyst_user');