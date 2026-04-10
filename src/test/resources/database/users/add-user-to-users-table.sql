INSERT INTO users (id, email, password, first_name, last_name)
VALUES (7, 'rudycooper@gmail.com', '$2a$08$n4TvfZDh6IH6QnJ9wQrAzO6gW3Yq6dt0QIltCOD4FO9pIX9YOP5C2', 'Rudy', 'Cooper');

INSERT INTO users_roles (user_id, role_id)
VALUES (7, 2);