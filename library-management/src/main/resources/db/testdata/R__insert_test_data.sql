DELETE FROM book_loans;
DELETE FROM books;
DELETE FROM readers;

INSERT INTO books (title, author, isbn, publication_year, total_copies, available_copies)
VALUES
    ('Dune', 'Frank Herbert', '9780441172719', 1965, 5, 5),
    ('1984', 'George Orwell', '9780451524935', 1949, 3, 3),
    ('The Hobbit', 'J.R.R. Tolkien', '9780547928227', 1937, 2, 2);

INSERT INTO readers (first_name, last_name, email, phone, registration_date)
VALUES
    ('Иван', 'Иванов', 'ivan@example.com', '+79991234567', CURRENT_DATE),
    ('Анна', 'Смирнова', 'anna@example.com', '+79009876543', CURRENT_DATE);