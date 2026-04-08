INSERT INTO users (id, email, password, first_name, last_name)
VALUES (5, 'user@gmail.com', '9df52223ed4894c4c3b774550e00819375540a579843eea778417d78cebb9f4b', 'John', 'Doe');

INSERT INTO shopping_carts (id, user_id)
VALUES (5, 5);

INSERT INTO books (id, title, author, isbn, price)
VALUES (2, 'name', 'author', '284-1843729473', 25.99);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (3, 5, 2, 10)