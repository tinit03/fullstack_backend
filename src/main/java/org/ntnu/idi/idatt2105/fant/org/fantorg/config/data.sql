-- ============================================================
-- USERS
-- ============================================================
INSERT INTO user (id, email, first_name, last_name, password, role)
VALUES
    (1, 'test@fant.org', 'Test', 'User', '$2a$10$Si4cD..vn8123bMRWfvjt.fubU7hABUbfTVR6sCZyYYY0aKTirm5m', 'USER'),
    (2, 'JohnDoe@mail.com', 'John', 'Doe', '$2a$10$3xX/aHF.eBFV6j488uV.u./baPJGcrTUfzZXGJHhrPyzqZ9eiTC/a', 'USER'),
    (3, 'alicesmith@mail.com', 'Alice', 'Smith', '$2a$10$CTieeHpIFah9guocqaYJwuupSPxFHE8KUU04sHMJwvrlZI1MW44d.', 'USER');



--------------------------------------------------------------------------------
-- 2. Insert Images for Categories
-- (These images are meant to be used in category rows.)
--------------------------------------------------------------------------------
INSERT INTO images (id, url, public_id, caption)
VALUES
    (101, 'https://res.cloudinary.com/desnhobcx/image/upload/v1743697236/clothes-hanger-svgrepo-com_lnddra.svg', 'clothes-hanger-svgrepo-com_lnddra', 'Clothes image'),
    (102, 'https://res.cloudinary.com/desnhobcx/image/upload/v1743697442/computer-svgrepo-com_gz7m1c.svg', 'computer-svgrepo-com_gz7m1c', 'Electronics image'),
    (103, 'https://res.cloudinary.com/desnhobcx/image/upload/v1743698155/ymlpxut6wpax101shhqw.svg', 'ymlpxut6wpax101shhqw', 'Sports image'),
    (104, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744108933/ctxhoaldixtbr7jlsnwg.svg', 'ctxhoaldixtbr7jlsnwg', 'Books image'),
    (105, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744108930/uhdqgdilcpgrojap0nor.svg', 'uhdqgdilcpgrojap0nor', 'Furniture image'),
    (106, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744109544/uuaa9dnsdsvyoedrncrt.svg', 'uuaa9dnsdsvyoedrncrt', 'Toys Image'),
    (107, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744109547/dnhcpvfffwih5vk1rrg4.svg', 'dnhcpvfffwih5vk1rrg4', 'Home Appliances Image'),
    (108, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744109550/folw0fsqqyqs5pnrkp9y.svg', 'folw0fsqqyqs5pnrkp9y', 'Vehicles Image');
--------------------------------------------------------------------------------
-- 3. Insert Categories
-- (Assuming the Category table has: id, category_name, parent_category_id, image_id)
--------------------------------------------------------------------------------
-- Existing parent categories and subcategories:
INSERT INTO categories (id, category_name, parent_category_id, image_id)
VALUES
    (1, 'Clothes', NULL, 101),
    (2, 'Jackets', 1, NULL),
    (3, 'Shoes', 1, NULL),
    (4, 'Electronics', NULL, 102),
    (5, 'Phones', 4, NULL),
    (6, 'Laptops', 4, NULL),
    (7, 'Sports', NULL, 103),
    (8, 'Bikes', 7, NULL),
    (9, 'Balls', 7, NULL),
    (10, 'Books', NULL, 104),
    (11, 'Fiction', 10, NULL),
    (12, 'Furniture', NULL, 105),
    (13, 'Chairs', 12, NULL),
    (14, 'Toys', NULL, 106),
    (15, 'Action Figures', 14, NULL),
    (16, 'Board Games', 14, NULL),
    (17, 'Home Appliances', NULL, 107),
    (18, 'Refrigerators', 17, NULL),
    (19, 'Washing Machines', 17, NULL),
    (20, 'Vehicles', NULL, 108),
    (21, 'Cars', 20, NULL),
    (22, 'Motorcycles', 20, NULL);


--------------------------------------------------------------------------------
-- 4. Insert Items
-- (Assuming the Item table has:
--   item_id, title, description, price, published_at,
--   location_postal_code, location_county, location_city, location_latitude, location_longitude,
--   seller_id, listing_type, status, condition, for_sale, sub_category_id)
--------------------------------------------------------------------------------
INSERT INTO item (item_id, title, description, price, published_at, location_postal_code, location_county, location_city, location_latitude, location_longitude, seller_id, listing_type, status, condition, for_sale, sub_category_id)
VALUES
    (1001, 'Winter Jacket', 'Insulated winter jacket in great condition', 499.99, '2025-04-08 10:00:00', '7010', 'Trøndelag', 'Trondheim', '63.4305', '10.3951', 1, 'BID', 'ACTIVE', 'ACCEPTABLE', 0, 2),
    (1002, 'Cat', 'Super cool cat', 1500.00, '2025-04-08 10:00:00', '7010', 'Trøndelag', 'Trondheim', '63.4305', '10.3951', 1, 'BID', 'ACTIVE', 'ACCEPTABLE', 0, 9),
    (1003, 'Delicious Dessert', 'Sweet and tasty dessert for food lovers', 99.99, '2025-04-06 10:00:00', '0150', 'Oslo', 'Oslo', '59.9139', '10.7522', 1, 'DIRECT', 'ACTIVE', 'NEW', 1, 6),
    (1004, 'Wooly Sheep', 'Friendly sheep available for petting or wool', 3000.00, '2025-04-07 10:00:00', '5000', 'Vestland', 'Bergen', '60.39299', '5.32415', 3, 'BID', 'ACTIVE', 'GOOD', 1, 9),
    (1005, 'Sporty Shoes', 'Durable and stylish shoes for running and gym', 399.99, '2025-04-08 05:00:00', '6000', 'Møre og Romsdal', 'Ålesund', '62.4722', '6.1499', 2, 'DIRECT', 'ACTIVE', 'NEW', 1, 3),
    (1006, 'Mountain Bike', 'Used mountain bike in great condition', 1200.00, '2025-04-04 10:00:00', '7000', 'Trøndelag', 'Trondheim', '63.4305', '10.3951', 2, 'BID', 'ACTIVE', 'ACCEPTABLE', 1, 8),
    (1007, 'Fantastic Novel', 'A gripping fictional tale', 19.99, '2025-04-08 09:00:00', '0000', 'Oslo', 'Oslo', '59.9139', '10.7522', 1, 'DIRECT', 'ACTIVE', 'NEW', 1, 11),
    (1008, 'Ergonomic Chair', 'Comfortable and stylish chair', 89.99, '2025-04-08 08:00:00', '1111', 'Oslo', 'Oslo', '59.9139', '10.7522', 3, 'DIRECT', 'ACTIVE', 'LIKE_NEW', 1, 13),
    (1009, 'Action Figure Set', 'Collectible action figures for enthusiasts', 59.99, '2025-04-08 07:00:00', '2222', 'Oslo', 'Oslo', '59.9139', '10.7522', 2, 'DIRECT', 'ACTIVE', 'NEW', 1, 15),
    (1010, 'Modern Refrigerator', 'Energy-efficient and spacious refrigerator', 799.99, '2025-04-07 12:00:00', '3333', 'Oslo', 'Oslo', '59.9139', '10.7522', 3, 'DIRECT', 'ACTIVE', 'LIKE_NEW', 1, 18),
    (1011, 'Electric Car', 'Latest model electric car with modern features', 29999.99, '2025-04-06 11:00:00', '4444', 'Oslo', 'Oslo', '59.9139', '10.7522', 1, 'BID', 'ACTIVE', 'NEW', 1, 21);
--------------------------------------------------------------------------------
-- 5. Insert Images for Items
-- (Assuming the Image table for items has columns:
--   id, url, public_id, caption, item_id)
--------------------------------------------------------------------------------
INSERT INTO images (id, url, public_id, caption, item_id)
VALUES
    (2001, 'https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/people/boy-snow-hoodie.jpg', 'samples/people/boy-snow-hoodie', 'Winter Jacket Image', 1001),
    (2002, 'https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/animals/cat.jpg', 'samples/animals/cat', 'cool cat', 1002),
    (2003, 'https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/food/dessert.jpg', 'samples/food/dessert', 'Yummy dessert', 1003),
    (2004, 'https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/sheep.jpg', 'samples/sheep', 'Super sheep', 1004),
    (2005, 'https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/ecommerce/shoes.png', 'samples/ecommerce/shoes', 'Sporty shoes', 1005),
    (2006, 'https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/bike.jpg', 'samples/bike', 'Mountain Bike', 1006),
    (2007, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744111775/llyfzr98jt9os9xjfk9e.jpg', 'llyfzr98jt9os9xjfk9e', 'Catcher in the rye', 1007),
    (2008, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744111895/iexwpih72nkt0j89qvvu.jpg', 'iexwpih72nkt0j89qvvu', 'Black ergonomic Chair', 1008),
    (2009, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744112006/ybrvxvud75ok5z1ped0l.png', 'ybrvxvud75ok5z1ped0l', 'Cool action figure', 1009),
    (2010, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744112148/qfvpj9qafdqtjnffskwf.jpg', 'qfvpj9qafdqtjnffskwf', 'Modern Refrigerator', 1010),
    (2011, 'https://res.cloudinary.com/desnhobcx/image/upload/v1744112263/rmxgjymbt92xbfmq8doq.png', 'rmxgjymbt92xbfmq8doq', 'Electric Car Image', 1011);