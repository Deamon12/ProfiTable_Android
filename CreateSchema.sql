/**
 * Database schema for ProfiTable mobile application
 * Author: Eric Gremban
 * Last update: 20160413
 */

/* TODO: identify type for secret id and password */
CREATE TABLE Employee (
   emp_id         INTEGER,
   emp_type       VARCHAR(20)    NOT NULL,
   first_name     VARCHAR(50)    NOT NULL,
   last_name      VARCHAR(50)    NOT NULL,
   PRIMARY KEY (emp_id)
);

CREATE TABLE Customer (
   cust_id        INTEGER       IDENTITY(1,1),
   PRIMARY KEY (cust_id)
);

CREATE TABLE Location (
   loc_id         INTEGER,
   loc_status     VARCHAR(20)    NOT NULL,
   name           VARCHAR(50)    NOT NULL,
   PRIMARY KEY (loc_id)
);

CREATE TABLE Order (
   order_id       INTEGER        IDENTITY(1,1),
   order_status   VARCHAR(20)    NOT NULL,
   time_in        TIME,
   time_out       TIME,
   PRIMARY KEY (order_id)
);

CREATE TABLE Discount (
   disc_id        INTEGER,
   disc_type      VARCHAR(50)    NOT NULL,
   disc_percent   FLOAT          NOT NULL,
   Available      BIT            NOT NULL,
   PRIMARY KEY (disc_id)
);

CREATE TABLE Menu_item (
   menu_id        INTEGER,
   menu_name      VARCHAR(50)    NOT NULL,
   description    VARCHAR(100)   NOT NULL,
   price          FLOAT          NOT NULL,
   Available      BIT            NOT NULL,
   PRIMARY KEY (menu_id)
);

CREATE TABLE Item (
   item_id        INTEGER        IDENTITY(1,1),
   notes          VARCHAR(100)   NOT NULL,
   item_status    VARCHAR(20)    NOT NULL,
   PRIMARY KEY (item_id)
);

CREATE TABLE Category (
   cat_id         INTEGER,
   cat_name       VARCHAR(20)    NOT NULL,
   PRIMARY KEY (cat_id)
);

CREATE TABLE Food_attribute (
   attr_id        INTEGER,
   attribute      VARCHAR(50)    NOT NULL,
   price_mod      FLOAT          NOT NULL,
   Available      BIT            NOT NULL,
   PRIMARY KEY (attr_id)
);

 /*
  Relational Tables:
  Has_order
  Has_disc
  Has_cust
  Ordered_item
  Ordered_with
  Has_attr
  Has_cat
  */

CREATE TABLE Has_order (
   loc_id         INTEGER,
   order_id       INTEGER,
   emp_id         INTEGER,
   PRIMARY KEY (order_id),
   FOREIGN KEY (loc_id)         REFERENCES Location(loc_id),
   FOREIGN KEY (order_id)       REFERENCES Order(order_id),
   FOREIGN KEY (emp_id)         REFERENCES Employee(emp_id)
);

CREATE TABLE Has_disc (
   loc            INTEGER,
   ord            INTEGER,
   PRIMARY KEY (ord),
   FOREIGN KEY (loc)            REFERENCES Location(loc_id),
   FOREIGN KEY (ord)            REFERENCES Order(order_id)
);



/* 
Below examples of rel tables 
*/

CREATE TABLE On_waitlist (
   stud_ssn    INTEGER,
   sect_id     INTEGER,
   join_num    INTEGER     NOT NULL,
   position    INTEGER     NOT NULL,
   PRIMARY KEY (stud_ssn, sect_id),
   FOREIGN KEY (stud_ssn) REFERENCES Student(ssn)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
   FOREIGN KEY (sect_id) REFERENCES Section(section_id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);