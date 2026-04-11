INSERT INTO users (id, email, password, first_name, last_name, shipping_address)
    VALUES (10, 'user@gmail.com', '$2a$10$2V9pW1S6G7H/vV9G.V9G.OuY.M.WqXoKkC1XoKkC1XoKkC1XoKkC', 'Bob', 'Developer', 'Moldova, Chisinau');

INSERT INTO users_roles (user_id, role_id)
VALUES (10, 2);

INSERT INTO books (id, title, author, isbn, price, description)
VALUES (10, 'Thinking in Java', 'Bruce Eckel', '978-0131872486', 49.99, 'Classic Java book');

INSERT INTO orders (id, user_id, status, total, order_date_time, shipping_address)
VALUES (10, 10, 'PENDING', 49.99, '2026-04-10 14:00:00', 'Moldova, Chisinau');

INSERT INTO order_items (id, order_id, book_id, quantity, price)
VALUES (10, 10, 10, 1, 49.99);