USE xiantao;

UPDATE sys_user SET password='$2a$10$EqKcp1WFKVQISheBxkV3FeYMmM8sOBfKLOlLGiXfY6C4VhFDqSJB6';

INSERT INTO category (name, icon, sort, status) VALUES
('Digital Products', 'phone', 1, 1),
('Books', 'book', 2, 1),
('Daily Necessities', 'home', 3, 1),
('Clothing', 'shirt', 4, 1),
('Sports', 'ball', 5, 1),
('Others', 'box', 6, 1);

INSERT INTO product (title, description, price, original_price, category_id, seller_id, status, view_count) VALUES
('iPhone 14 Pro 256G Space Black', 'Used iPhone 14 Pro, 99% new, no scratches, battery health 92%', 5999.00, 8999.00, 1, 1, 1, 128),
('MacBook Pro M2 14inch 16G 512G', 'Company gift, brand new unopened', 12999.00, 15999.00, 1, 1, 1, 256),
('AirPods Pro 2nd Gen', 'Used for 6 months, works perfectly', 999.00, 1899.00, 1, 2, 1, 89),
('Advanced Mathematics 7th Edition', 'Required for exams, some notes', 35.00, 89.00, 2, 2, 1, 45),
('CET-4 Vocabulary Book', 'Exam preparation materials', 25.00, 68.00, 2, 3, 1, 67),
('Xiaomi Standing Fan', 'Used for 1 year, quiet and powerful', 129.00, 299.00, 3, 1, 1, 34),
('IKEA Bean Bag Sofa', 'Graduation sale, 80% new', 199.00, 499.00, 3, 2, 1, 56),
('Nike Air Max 270 Size 42', 'Worn a few times, like new', 399.00, 1099.00, 4, 3, 1, 78),
('UNIQLO Down Jacket Size L', 'Bought last winter, barely worn', 299.00, 599.00, 4, 1, 1, 43),
('Decathlon Yoga Mat', 'Brand new, moving sale', 89.00, 159.00, 5, 3, 1, 23);

INSERT INTO orders (order_no, product_id, product_title, product_price, seller_id, buyer_id, status, create_time, pay_time, complete_time) VALUES
('202403160001001', 1, 'iPhone 14 Pro 256G Space Black', 5999.00, 1, 2, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('202403160001002', 4, 'Advanced Mathematics 7th Edition', 35.00, 2, 3, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
('202403160001003', 7, 'IKEA Bean Bag Sofa', 199.00, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL);
