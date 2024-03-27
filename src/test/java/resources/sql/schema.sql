DROP TABLE IF EXISTS positions CASCADE;
DROP TABLE IF EXISTS subdivisions CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS employees_subdivisions CASCADE;
DROP TABLE IF EXISTS phone_numbers CASCADE;

CREATE TABLE IF NOT EXISTS positions
(
    position_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    position_name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS subdivisions
(
    subdivision_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    subdivision_name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS employees
(
    employee_id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_firstName VARCHAR(255) NOT NULL,
    employee_lastName  VARCHAR(255) NOT NULL,
    position_id        BIGINT REFERENCES positions (position_id)
    );

CREATE TABLE IF NOT EXISTS employees_subdivisions
(
    employees_subdivisions_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id              BIGINT REFERENCES employees (employee_id),
    subdivision_id        BIGINT REFERENCES subdivisions (subdivision_id),
    CONSTRAINT unique_link UNIQUE (employee_id, subdivision_id)
    );

CREATE TABLE IF NOT EXISTS phone_numbers
(
    phonenumber_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    phonenumber_number VARCHAR(255) NOT NULL UNIQUE,
    employee_id            BIGINT REFERENCES employees (employee_id)
    );

INSERT INTO positions (position_name)
VALUES ('Администратор'),        -- 1
       ('Технический директор'), -- 2
       ('Программист Java'),     -- 3
       ('Программист React'),    -- 4
       ('HR'); -- 5

INSERT INTO subdivisions (subdivision_name)
VALUES ('Администрация'),       -- 1
       ('BackEnd разработка'),  -- 2
       ('Frontend разработка'), -- 3
       ('HR менеджмент'); -- 4

INSERT INTO employees (employee_firstName, employee_lastName, position_id)
VALUES ('Иван', 'Субботин', 1),      -- 1
       ('Петр', 'Понедельников', 2), -- 2
       ('Игнат', 'Вторников', 3),    -- 3
       ('Иван', 'Середец', 3),       -- 4
       ('Максим', 'Четверкин', 3),   -- 5
       ('Вера', 'Пятницкая', 4),     -- 6
       ('Ольга', 'Воскресенская', 5); -- 7

INSERT INTO employees_subdivisions (employee_id, subdivision_id)
VALUES (1, 1), -- 1
       (2, 1), -- 2
       (3, 2), -- 3
       (4, 2), -- 4
       (5, 2), -- 5
       (6, 1), -- 6
       (6, 3), -- 6
       (7, 4); -- 7

INSERT INTO phone_numbers (phonenumber_number, employee_id)
VALUES ('+1(123)123 1111', 1), -- 1
       ('+1(123)123 2222', 1), -- 2
       ('+1(123)123 3333', 2), -- 3
       ('+1(123)123 4444', 2), -- 4
       ('+1(123)123 5555', 3), -- 5
       ('+1(123)123 6666', 4), -- 6
       ('+1(123)123 7777', 5), -- 7
       ('+1(123)123 8888', 6), -- 8
       ('+1(123)123 9995', 7); -- 9

