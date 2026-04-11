INSERT INTO users (id, email, password, first_name, last_name)
VALUES (3, 'admin@gmail.com', '$2a$08$xitEMEXybmfbqq9jb7RA7.vwQBM5M1E.05jo8EtFOqBZLou2c0Pli', 'Admin', 'Admin');

INSERT INTO users_roles (user_id, role_id)
VALUES (3, 1)