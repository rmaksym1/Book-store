INSERT INTO users (id, email, password, first_name, last_name)
VALUES (7, 'rudycooper@gmail.com', '$2a$08$xitEMEXybmfbqq9jb7RA7.vwQBM5M1E.05jo8EtFOqBZLou2c0Pli', 'Rudy', 'Cooper');

INSERT INTO users_roles (user_id, role_id)
VALUES (7, 2);