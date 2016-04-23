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



/* TODO: identify type for secret id and password */
CREATE TABLE Employee (
   emp_id         BIGSERIAL,
   emp_type       VARCHAR(20)    NOT NULL,
   first_name     VARCHAR(50)    NOT NULL,
   last_name      VARCHAR(50)    NOT NULL,
   PRIMARY KEY (emp_id)
);

CREATE TABLE Customer (
   cust_id        BIGSERIAL,
   cust_at_table  BIGINT,
   PRIMARY KEY (cust_id)
);

CREATE TABLE Location (
   loc_id         BIGSERIAL,
   loc_status     VARCHAR(20)    NOT NULL,
   name           VARCHAR(50)    NOT NULL,
   PRIMARY KEY (loc_id)
);

CREATE TABLE Tab (
   tab_id         BIGSERIAL,
   tab_status     VARCHAR(20)    NOT NULL,
   time_in        TIMESTAMP,
   time_out       TIMESTAMP,
   PRIMARY KEY (tab_id)
);

CREATE TABLE Discount (
   disc_id        BIGINT,
   disc_type      VARCHAR(50)    NOT NULL,
   disc_percent   FLOAT          NOT NULL,
   Available      BOOLEAN        NOT NULL,
   PRIMARY KEY (disc_id)
);

CREATE TABLE Menu_item (
   menu_id        BIGSERIAL,
   menu_name      VARCHAR(50)    NOT NULL,
   description    VARCHAR(100)   NOT NULL,
   price          SMALLINT       NOT NULL,
   Available      BOOLEAN        NOT NULL,
   PRIMARY KEY (menu_id)
);

CREATE TABLE Item (
   item_id        BIGSERIAL,
   notes          VARCHAR(100),
   item_status    VARCHAR(20)    NOT NULL,
   PRIMARY KEY (item_id)
);

CREATE TABLE Category (
   cat_id         BIGSERIAL,
   cat_name       VARCHAR(20)    NOT NULL,
   PRIMARY KEY (cat_id)
);

CREATE TABLE Food_attribute (
   attr_id        BIGSERIAL,
   attribute      VARCHAR(50)    NOT NULL,
   price_mod      SMALLINT       NOT NULL,
   Available      BOOLEAN        NOT NULL,
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
INSERT INTO Employee (emp_type, first_name, last_name) 
   VALUES('Manager','Wayne','Static');
/* 2 */
INSERT INTO Employee (emp_type, first_name, last_name) 
   VALUES('Wait','Nicki','Minaj');
/* 3 */
INSERT INTO Employee (emp_type, first_name, last_name) 
   VALUES('Wait','Justin','Bieber');
/* 4 */
INSERT INTO Employee (emp_type, first_name, last_name) 
   VALUES('Food','Enrique','Iglesias');
/* 5 */
INSERT INTO Employee (emp_type, first_name, last_name) 
   VALUES('Food','Gwen','Stefani');
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Customer (cust_at_table) 
   VALUES(1);
/* 2 */
INSERT INTO Customer (cust_at_table) 
   VALUES(2);
/* 3 */
INSERT INTO Customer (cust_at_table) 
   VALUES(3);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Location (loc_status, name) 
   VALUES('available','bar1');
/* 2 */
INSERT INTO Location (loc_status, name) 
   VALUES('occupied','bar2');
/* 3 */
INSERT INTO Location (loc_status, name) 
   VALUES('available','sit1');
/* 4 */
INSERT INTO Location (loc_status, name) 
   VALUES('occupied','sit2');
/* 5 */
INSERT INTO Location (loc_status, name) 
   VALUES('available','takeout1');
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
INSERT INTO Discount (disc_id, disc_type, disc_percent, Available) 
   VALUES(1,'student',0.10,TRUE);
/* 2 */
INSERT INTO Discount (disc_id, disc_type, disc_percent, Available) 
   VALUES(2,'veteran',0.20,TRUE);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO Menu_item (menu_name, description, price, Available) 
   VALUES('cheeseburger','itsaburger',200,TRUE);
INSERT INTO Menu_item (menu_name, description, price, Available) 
   VALUES('burger','itsaburger',150,TRUE);
INSERT INTO Menu_item (menu_name, description, price, Available) 
   VALUES('fries','frenchtype',100,TRUE);
INSERT INTO Menu_item (menu_name, description, price, Available) 
   VALUES('soda','drinkin',110,TRUE);
INSERT INTO Menu_item (menu_name, description, price, Available) 
   VALUES('milkshake','tasty',230,FALSE);
INSERT INTO Menu_item (menu_name, description, price, Available) 
   VALUES('chicken tacos','pollo asada, 3x',275,TRUE);
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Category (cat_name) 
   VALUES('burgers');
/* 2 */
INSERT INTO Category (cat_name) 
   VALUES('popular');
/* 3 */
INSERT INTO Category (cat_name) 
   VALUES('drinks');
/* 4 */
INSERT INTO Category (cat_name) 
   VALUES('sides');
/* 5 */
INSERT INTO Category (cat_name) 
   VALUES('main course');
COMMIT;
END TRANSACTION;

BEGIN TRANSACTION;
/* 1 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('lettuce',0,TRUE);
/* 2 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('onion - fresh',0,TRUE);
/* 3 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('onion - grilled',0,TRUE);
/* 4 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('tomato',0,TRUE);
/* 5 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('guacamole',135,TRUE);
/* 6 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('salsa',0,TRUE);
/* 7 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('salt',0,TRUE);
/* 8 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('pepper',0,TRUE);
/* 9 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('cherry',0,TRUE);
/* 10 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('ice',135,TRUE);
/* 11 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('cheese',0,TRUE);
/* 12 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('mustard',0,TRUE);
/* 13 */
INSERT INTO Food_attribute (attribute, price_mod, Available) 
   VALUES('ketchup',0,TRUE);
COMMIT;
END TRANSACTION;


BEGIN TRANSACTION;
/* 1 */
INSERT INTO Item (item_status) 
   VALUES('complete');
/* 2 */
INSERT INTO Item (item_status) 
   VALUES('delivered');
/* 3 */
INSERT INTO Item (item_status, notes) 
   VALUES('inprogress', 'add in chipotle mayo');
/* 4 */
INSERT INTO Item (item_status) 
   VALUES('inprogress');
/* 5 */
INSERT INTO Item (item_status) 
   VALUES('inprogress');
COMMIT;
END TRANSACTION;