DELETE
FROM users;
DELETE
FROM roles;
DELETE
FROM restaurants;
DELETE
FROM dishes;
ALTER SEQUENCE global_seq
RESTART WITH 10000;

INSERT INTO USERS (NAME, EMAIL, PASSWORD, REGISTERED, ENABLED)
VALUES ('Sergey', 'admin@yandex.ru', '{noop}pass', '2018-12-05 00:00:00.000000', true),
       ('Ann', 'user@ya.ru', '{noop}pass', '2018-12-06 00:00:00.000000', true),
       ('Ann2', 'user2@ya.ru', '{noop}pass', '2018-12-07 00:00:00.000000', true),
       ('Ann3', 'user3@ya.ru', '{noop}pass', '2018-12-08 00:00:00.000000', true);

INSERT INTO ROLES (USER_ID, ROLE)
VALUES (10000, 'ROLE_ADMIN'),
       (10001, 'ROLE_USER'),
       (10002, 'ROLE_USER'),
       (10003, 'ROLE_USER');

INSERT INTO RESTAURANTS (NAME, CITY, DESCRIPTION)
VALUES ('KFC1', 'Москва', 'Куриные бургеры и картошка'),
       ('KFC2', 'Москва', 'Куриные бургеры и картошка'),
       ('KFC3', 'Москва', 'Куриные бургеры и картошка'),
       ('McDs1', 'Москва', 'Бургеры и картошка'),
       ('McDs2', 'Москва', 'Бургеры и картошка'),
       ('McDs3', 'Москва', 'Бургеры и картошка');

INSERT INTO MENUS (ADDED, RESTAURANT_ID)
VALUES ('2018-12-14', 10006),
       ('2018-12-14', 10007);

INSERT INTO MENUS (RESTAURANT_ID)
VALUES (10006);

INSERT INTO DISHES (NAME, PRICE, MENU_ID)
VALUES ('1Картошка', 70.15, 10010),
       ('2Бургер куриный', 80.55, 10010),
       ('3Салат', 100.35, 10010),
       ('1Картошка', 72.55, 10011),
       ('2Чизбургер', 75.99, 10011),
       ('3Бургер', 79.49, 10011),
       ('1Пицца', 279.49, 10012),
       ('2Пицца', 279.49, 10012);

