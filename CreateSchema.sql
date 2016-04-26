/**
 * Database schema for ProfiTable mobile application
 * Author: Eric Gremban
 * Last update: 20160422
 */

/**
   TODOs:
   connect restaurants with employees and tables
   add in time seated and time left for a given order
*/

/* Drop any versions of this database which existed previously */
DROP TABLE IF EXISTS Restaurant CASCADE;
DROP TABLE IF EXISTS Loc_Category CASCADE;
DROP TABLE IF EXISTS Employee CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;
DROP TABLE IF EXISTS Location CASCADE;
DROP TABLE IF EXISTS Tab CASCADE;
DROP TABLE IF EXISTS Discount CASCADE;
DROP TABLE IF EXISTS Menu_item CASCADE;
DROP TABLE IF EXISTS Category CASCADE;
DROP TABLE IF EXISTS Item CASCADE;
DROP TABLE IF EXISTS Food_attribute CASCADE;
DROP TABLE IF EXISTS Has_order CASCADE;
DROP TABLE IF EXISTS Has_disc CASCADE;
DROP TABLE IF EXISTS Has_cust CASCADE;
DROP TABLE IF EXISTS Ordered_item CASCADE;
DROP TABLE IF EXISTS Ordered_with CASCADE;
DROP TABLE IF EXISTS Has_attr CASCADE;
DROP TABLE IF EXISTS Has_cat CASCADE;



CREATE TABLE Restaurant (
   rest_id        BIGSERIAL,
   name           VARCHAR(50)    NOT NULL,
   PRIMARY KEY (rest_id)
);

/* TODO: identify type for secret id and password */
CREATE TABLE Employee (
   emp_id         BIGSERIAL,
   emp_type       VARCHAR(20)    NOT NULL,
   first_name     VARCHAR(50)    NOT NULL,
   last_name      VARCHAR(50)    NOT NULL,
   restaurant     BIGINT,
   FOREIGN KEY (restaurant)      REFERENCES Restaurant(rest_id),
   PRIMARY KEY (emp_id)
);

CREATE TABLE Loc_Category (
   loccat_id      BIGSERIAL,
   name           VARCHAR(50)    NOT NULL,
   restaurant     BIGINT,
   FOREIGN KEY (restaurant)      REFERENCES Restaurant(rest_id),
   PRIMARY KEY (loccat_id)
);

CREATE TABLE Location (
   loc_id         BIGSERIAL,
   loc_status     VARCHAR(20)    NOT NULL,
   name           VARCHAR(50)    NOT NULL,
   loc_cat        BIGINT         NOT NULL,
   FOREIGN KEY (loc_cat)         REFERENCES Loc_Category(loccat_id),
   restaurant     BIGINT,
   FOREIGN KEY (restaurant)      REFERENCES Restaurant(rest_id),
   PRIMARY KEY (loc_id)
);

CREATE TABLE Tab (
   tab_id         BIGSERIAL,
   tab_status     VARCHAR(20)    NOT NULL,
   time_in        TIMESTAMP,
   time_out       TIMESTAMP,
   PRIMARY KEY (tab_id)
);

CREATE TABLE Customer (
   cust_id        BIGSERIAL,
   order_id       BIGINT,
   FOREIGN KEY (order_id)      REFERENCES Tab(tab_id),
   PRIMARY KEY (cust_id)
);

CREATE TABLE Discount (
   disc_id        BIGINT,
   disc_type      VARCHAR(50)    NOT NULL,
   disc_percent   FLOAT          NOT NULL,
   Available      BOOLEAN        NOT NULL,
   restaurant     BIGINT,
   FOREIGN KEY (restaurant)      REFERENCES Restaurant(rest_id),
   PRIMARY KEY (disc_id)
);

CREATE TABLE Menu_item (
   menu_id        BIGSERIAL,
   menu_name      VARCHAR(50)    NOT NULL,
   description    VARCHAR(100)   NOT NULL,
   price          SMALLINT       NOT NULL,
   Available      BOOLEAN        NOT NULL,
   restaurant     BIGINT,
   FOREIGN KEY (restaurant)      REFERENCES Restaurant(rest_id),
   PRIMARY KEY (menu_id)
);

CREATE TABLE Item (
   item_id        BIGSERIAL,
   notes          VARCHAR(100),
   item_status    VARCHAR(20)    NOT NULL,
   bring_first    BOOLEAN        NOT NULL,
   PRIMARY KEY (item_id)
);

CREATE TABLE Category (
   cat_id         BIGSERIAL,
   cat_name       VARCHAR(20)    NOT NULL,
   restaurant     BIGINT,
   FOREIGN KEY (restaurant)      REFERENCES Restaurant(rest_id),
   PRIMARY KEY (cat_id)
);

CREATE TABLE Food_attribute (
   attr_id        BIGSERIAL,
   attribute      VARCHAR(50)    NOT NULL,
   price_mod      SMALLINT       NOT NULL,
   Available      BOOLEAN        NOT NULL,
   restaurant     BIGINT,
   FOREIGN KEY (restaurant)      REFERENCES Restaurant(rest_id),
   PRIMARY KEY (attr_id)
);


/* TODO: For relational tables, do we want to cascade updates or deletes? */
CREATE TABLE Has_order (
   loc_id         BIGINT,
   order_id       BIGINT,
   emp_id         BIGINT,
   PRIMARY KEY (order_id),
   FOREIGN KEY (loc_id)         REFERENCES Location(loc_id),
   FOREIGN KEY (order_id)       REFERENCES Tab(tab_id),
   FOREIGN KEY (emp_id)         REFERENCES Employee(emp_id)
);

CREATE TABLE Has_disc (
   disc_id         BIGINT,
   order_id        BIGINT,
   FOREIGN KEY (disc_id)        REFERENCES Discount(disc_id),
   FOREIGN KEY (order_id)       REFERENCES Tab(tab_id)
);

CREATE TABLE Has_cust (
   cust_id         BIGINT,
   order_id        BIGINT,
   FOREIGN KEY (cust_id)        REFERENCES Customer(cust_id),
   FOREIGN KEY (order_id)       REFERENCES Tab(tab_id)
);

CREATE TABLE Ordered_item (
   item_id         BIGINT,
   cust_id         BIGINT,
   menu_id         BIGINT,
   FOREIGN KEY (item_id)        REFERENCES Item(item_id),
   FOREIGN KEY (cust_id)        REFERENCES Customer(cust_id),
   FOREIGN KEY (menu_id)        REFERENCES Menu_item(menu_id)
);

CREATE TABLE Ordered_with (
   item_id         BIGINT,
   attr_id         BIGINT,
   FOREIGN KEY (item_id)        REFERENCES Item(item_id),
   FOREIGN KEY (attr_id)        REFERENCES Food_attribute(attr_id)
);

CREATE TABLE Has_attr (
   menu_id         BIGINT,
   attr_id         BIGINT,
   default_incl    BOOLEAN NOT NULL,
   FOREIGN KEY (menu_id)        REFERENCES Menu_item(menu_id),
   FOREIGN KEY (attr_id)        REFERENCES Food_attribute(attr_id)
);

CREATE TABLE Has_cat (
   menu_id         BIGINT,
   cat_id          BIGINT,
   FOREIGN KEY (menu_id)        REFERENCES Menu_item(menu_id),
   FOREIGN KEY (cat_id)         REFERENCES Category(cat_id)
);

/* 
 * ####################
 * ####################
 * # Insert test data # 
 * ####################
 * ####################
 */
BEGIN TRANSACTION;
/* 1 */
INSERT INTO Restaurant (name) 
   VALUES('The Carlito Diner');
/* 2 */
INSERT INTO Restaurant (name) 
   VALUES('Turkish Pizza Parlour');
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Employee (emp_type, first_name, last_name, restaurant) 
   VALUES('Manager','Wayne','Static',1);
/* 2 */
INSERT INTO Employee (emp_type, first_name, last_name, restaurant) 
   VALUES('Wait','Nicki','Minaj',1);
/* 3 */
INSERT INTO Employee (emp_type, first_name, last_name, restaurant) 
   VALUES('Wait','Justin','Bieber',1);
/* 4 */
INSERT INTO Employee (emp_type, first_name, last_name, restaurant) 
   VALUES('Food','Enrique','Iglesias',1);
/* 5 */
INSERT INTO Employee (emp_type, first_name, last_name, restaurant) 
   VALUES('Food','Gwen','Stefani',1);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Tab (tab_status) 
   VALUES('inprogress');
/* 2 */   
INSERT INTO Tab (tab_status) 
   VALUES('complete');
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Customer (order_id) 
   VALUES(1);
/* 2 */
INSERT INTO Customer (order_id) 
   VALUES(1);
/* 3 */
INSERT INTO Customer (order_id) 
   VALUES(2);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Loc_Category (name, restaurant) 
   VALUES('table',1);
/* 2 */
INSERT INTO Loc_Category (name, restaurant) 
   VALUES('bar',1);
/* 3 */
INSERT INTO Loc_Category (name, restaurant) 
   VALUES('take out',1);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Location (loc_status, name, loc_cat, restaurant) 
   VALUES('available','pair table 1',2,1);
/* 2 */
INSERT INTO Location (loc_status, name, loc_cat, restaurant) 
   VALUES('occupied','barseat 4',2,1);
/* 3 */
INSERT INTO Location (loc_status, name, loc_cat, restaurant) 
   VALUES('available','corner booth',1,1);
/* 4 */
INSERT INTO Location (loc_status, name, loc_cat, restaurant) 
   VALUES('occupied','table for 8',1,1);
/* 5 */
INSERT INTO Location (loc_status, name, loc_cat, restaurant) 
   VALUES('occupied','order184-Shamim',3,1);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Discount (disc_id, disc_type, disc_percent, Available, restaurant) 
   VALUES(1,'student',0.10,TRUE,1);
/* 2 */
INSERT INTO Discount (disc_id, disc_type, disc_percent, Available, restaurant) 
   VALUES(2,'veteran',0.20,TRUE,1);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Menu_item (menu_name, description, price, Available, restaurant) 
   VALUES('cheeseburger','itsaburger',200,TRUE,1);
/* 2 */
INSERT INTO Menu_item (menu_name, description, price, Available, restaurant) 
   VALUES('burger','itsaburger',150,TRUE,1);
/* 3 */
INSERT INTO Menu_item (menu_name, description, price, Available, restaurant) 
   VALUES('fries','frenchtype',100,TRUE,1);
/* 4 */
INSERT INTO Menu_item (menu_name, description, price, Available, restaurant) 
   VALUES('soda','drinkin',110,TRUE,1);
/* 5 */
INSERT INTO Menu_item (menu_name, description, price, Available, restaurant) 
   VALUES('milkshake','tasty',230,FALSE,1);
/* 6 */
INSERT INTO Menu_item (menu_name, description, price, Available, restaurant) 
   VALUES('chicken tacos','pollo asada, 3x',275,TRUE,1);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Category (cat_name, restaurant) 
   VALUES('burgers',1);
/* 2 */
INSERT INTO Category (cat_name, restaurant) 
   VALUES('popular',1);
/* 3 */
INSERT INTO Category (cat_name, restaurant) 
   VALUES('drinks',1);
/* 4 */
INSERT INTO Category (cat_name, restaurant) 
   VALUES('sides',1);
/* 5 */
INSERT INTO Category (cat_name, restaurant) 
   VALUES('main course',1);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('lettuce',0,TRUE,1);
/* 2 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('onion - fresh',0,TRUE,1);
/* 3 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('onion - grilled',0,TRUE,1);
/* 4 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('tomato',0,TRUE,1);
/* 5 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('guacamole',135,TRUE,1);
/* 6 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('salsa',0,TRUE,1);
/* 7 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('salt',0,TRUE,1);
/* 8 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('pepper',0,TRUE,1);
/* 9 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('cherry',0,TRUE,1);
/* 10 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('ice',135,TRUE,1);
/* 11 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('cheese',0,TRUE,1);
/* 12 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('mustard',0,TRUE,1);
/* 13 */
INSERT INTO Food_attribute (attribute, price_mod, Available, restaurant) 
   VALUES('ketchup',0,TRUE,1);
COMMIT;
END TRANSACTION;


BEGIN TRANSACTION;
/* 1 */
INSERT INTO Item (item_status, bring_first) 
   VALUES('complete', FALSE);
/* 2 */
INSERT INTO Item (item_status, bring_first) 
   VALUES('delivered', FALSE);
/* 3 */
INSERT INTO Item (item_status, notes, bring_first) 
   VALUES('inprogress', 'add in chipotle mayo', FALSE);
/* 4 */
INSERT INTO Item (item_status, bring_first) 
   VALUES('inprogress', TRUE);
/* 5 */
INSERT INTO Item (item_status, bring_first) 
   VALUES('inprogress', FALSE);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO Has_order (loc_id, order_id, emp_id) 
   VALUES(2,1,2);
INSERT INTO Has_order (loc_id, order_id, emp_id) 
   VALUES(4,2,3);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO Has_disc (disc_id, order_id) 
   VALUES(2,1);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO Ordered_item (item_id, menu_id, cust_id) 
   VALUES(1,1,1);
INSERT INTO Ordered_item (item_id, menu_id, cust_id) 
   VALUES(2,1,2);
INSERT INTO Ordered_item (item_id, menu_id, cust_id) 
   VALUES(3,2,3);
INSERT INTO Ordered_item (item_id, menu_id, cust_id) 
   VALUES(4,4,1);
INSERT INTO Ordered_item (item_id, menu_id, cust_id) 
   VALUES(5,5,2);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(1,1);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(1,2);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(1,5);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(2,1);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(2,5);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(3,2);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(3,4);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(4,2);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(4,3);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(5,3);
INSERT INTO Has_cat (menu_id, cat_id) 
   VALUES(6,5);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 1, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 2, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 4, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 11, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(2, 1, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(2, 2, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(2, 4, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(3, 7, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(3, 8, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(3, 12, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(3, 13, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(3, 11, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 3, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 5, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 12, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(1, 13, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(2, 3, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(2, 5, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(2, 12, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(2, 13, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(4, 10, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(5, 9, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(6, 1, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(6, 3, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(6, 2, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(6, 5, FALSE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(6, 11, TRUE);
INSERT INTO Has_attr (menu_id, attr_id, default_incl) 
   VALUES(6, 4, TRUE);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(1,1);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(1,3);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(1,4);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(1,5);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(1,11);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(2,1);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(2,12);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(2,13);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(2,11);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(3,1);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(3,4);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(3,12);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(3,13);
INSERT INTO Ordered_with (item_id, attr_id) 
   VALUES(4,10);
COMMIT;
END TRANSACTION;

/* 
 * Example Query:
 *
 * Searching for all ordered items with 
 * their location and status
 */
select 
   l.loc_id, 
   l.name, 
   t.tab_status, 
   c.cust_id, 
   mi.menu_name
from
   location l, 
   tab t, 
   customer c, 
   has_order ho, 
   ordered_item oi, 
   menu_item mi
where
   ho.loc_id=l.loc_id
   AND
   ho.order_id=t.tab_id
   AND
   c.order_id=t.tab_id
   AND
   l.restaurant=1
   AND
   oi.cust_id=c.cust_id
   AND
   mi.menu_id=oi.menu_id
order by c.cust_id ASC;
/* should produce:
2  "barseat 4"    "inprogress"  1  "cheeseburger"
2  "barseat 4"    "inprogress"  1  "soda"
2  "barseat 4"    "inprogress"  2  "cheeseburger"
2  "barseat 4"    "inprogress"  2  "milkshake"
4  "table for 8"  "complete"    3  "burger"
*/