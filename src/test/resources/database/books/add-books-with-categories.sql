INSERT INTO categories (id, name)
VALUES (4, 'Programming');

INSERT INTO books (id, title, author, isbn, price)
VALUES (7, 'Python Basics', 'Sam Sapiol', '978-0143128977', 9.99);

INSERT INTO books_categories (book_id, category_id)
VALUES (7, 4);