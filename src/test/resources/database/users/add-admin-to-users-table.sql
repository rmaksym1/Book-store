INSERT INTO users (id, email, password, first_name, last_name)
VALUES (3, 'admin@gmail.com', '9df52223ed4894c4c3b774550e00819375540a579843eea778417d78cebb9f4b', 'Admin', 'Admin');

INSERT INTO users_roles (user_id, role_id)
VALUES (3, 1)