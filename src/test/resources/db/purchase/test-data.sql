insert into shopping_cart (id, version) values (11, 1);

insert into shopping_cart_set (
branch_id,
shopping_cart_id,
amount,
price,
commodity_name,
commodity_image_uri
)
values(
5,
11,
2,
45.0,
'T-SHIRT 01',
'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png'
);

insert into shopping_cart (id, version) values (13, 1);

--insert into shopping_cart_set (id, amount, branch_id, shopping_cart_id) values(14, 6, 5, 13);
--
insert into customer_order (
        id,
        date_of_creation,
        paymentid,
        payment_provider,
        status,
        version,
        customer_id)
        values (
        16,
        '2019-08-25 18:46:23.918',
         null,
         null,
         'AWAITING_PAYMENT',
         1,
         10);
insert into PURCHASE (
branch_id, order_id,
amount,
price,
commodity_name,
commodity_image_uri
) values (
5,
16,
2,
45.0,
'T-SHIRT 01',
'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png'
);

--
---- shopping cart without customer, founded by id in cookie
--insert into shopping_cart (id, version) values (17, 1);
--insert into shopping_cart_set (id, amount, branch_id, shopping_cart_id) values(18, 1, 5, 17);
--
---- shopping cart with empty branch
--insert into shopping_cart (id, version) values (22, 1);
--insert into shopping_cart_set (id, amount, branch_id, shopping_cart_id) values(23, 2, 20, 22);
--
---- payment approved
insert into customer_order (
        id,
        date_of_creation,
        paymentid,
        payment_provider,
        status,
        version,
        customer_id)
        values (
        25,
        '2019-08-23 18:46:25.918',
         'PAYPALZX1293',
         'Paypal',
         'PAYMENT_APPROVED',
         1,
         10);
--insert into PURCHASE (branch_id, order_id, amount) values (5,25,1);
insert into PURCHASE (
branch_id, order_id,
amount,
price,
commodity_name,
commodity_image_uri
) values (
5,
25,
1,
45.0,
'T-SHIRT 02',
'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png'
);
--insert into PURCHASE (branch_id, order_id, amount) values (20,25,1);
insert into PURCHASE (
branch_id, order_id,
amount,
price,
commodity_name,
commodity_image_uri
) values (
20,
25,
1,
45.0,
'T-SHIRT 20',
'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png'
);