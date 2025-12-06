BEGIN EXECUTE IMMEDIATE 'DROP TABLE Event_Registration CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Event CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Order_Item CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Customer_Order CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Health_Record CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Adoption CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Adoption_Application CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Pet CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Member CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Staff CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Reservation CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Room CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Menu_Item CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Membership_Tier CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE Membership_Tier (
                                 tier_id        INTEGER PRIMARY KEY,
                                 tier_name      VARCHAR2(50) NOT NULL,
                                 description    VARCHAR2(200),
                                 discount_rate  NUMBER(3) CHECK (discount_rate BETWEEN 0 AND 100),
                                 visit_limit    INTEGER
);

CREATE TABLE Member (
                        member_id        INTEGER PRIMARY KEY,
                        name             VARCHAR2(100) NOT NULL,
                        phone            VARCHAR2(20),
                        email            VARCHAR2(100),
                        date_of_birth    DATE,
                        emergency_contact VARCHAR2(200),
                        tier_id          INTEGER
);

ALTER TABLE Member
    ADD CONSTRAINT fk_member_tier
        FOREIGN KEY (tier_id)
            REFERENCES Membership_Tier(tier_id);

CREATE TABLE Room (
                      room_id          INTEGER PRIMARY KEY,
                      room_name        VARCHAR2(100) NOT NULL,
                      room_type        VARCHAR2(50),
                      max_capacity     INTEGER NOT NULL,
                      is_adoption_area NUMBER(1) CHECK (is_adoption_area IN (0,1))
);

CREATE TABLE Reservation (
                             reservation_id    INTEGER PRIMARY KEY,
                             member_id         INTEGER NOT NULL,
                             room_id           INTEGER NOT NULL,
                             reservation_date  DATE NOT NULL,
                             start_time        TIMESTAMP NOT NULL,
                             duration_minutes  INTEGER NOT NULL,
                             status            VARCHAR2(20),
                             check_in_time     TIMESTAMP,
                             check_out_time    TIMESTAMP,
                             tier_id           INTEGER
);

ALTER TABLE Reservation
    ADD CONSTRAINT fk_reservation_member
        FOREIGN KEY (member_id)
            REFERENCES Member(member_id);

ALTER TABLE Reservation
    ADD CONSTRAINT fk_reservation_room
        FOREIGN KEY (room_id)
            REFERENCES Room(room_id);

ALTER TABLE Reservation
    ADD CONSTRAINT fk_reservation_tier
        FOREIGN KEY (tier_id)
            REFERENCES Membership_Tier(tier_id);

CREATE TABLE Staff (
                       staff_id        INTEGER PRIMARY KEY,
                       name            VARCHAR2(100) NOT NULL,
                       phone           VARCHAR2(20),
                       email           VARCHAR2(100),
                       hire_date       DATE,
                       is_active       NUMBER(1) CHECK (is_active IN (0,1)),
                       role            VARCHAR2(50)
);

CREATE TABLE Pet (
                     pet_id          INTEGER PRIMARY KEY,
                     name            VARCHAR2(100) NOT NULL,
                     species         VARCHAR2(100),
                     breed           VARCHAR2(100),
                     age             INTEGER,
                     date_of_arrival DATE,
                     temperament     VARCHAR2(200),
                     special_needs   VARCHAR2(200),
                     status          VARCHAR2(30),
                     current_room_id INTEGER
);

ALTER TABLE Pet
    ADD CONSTRAINT fk_pet_room
        FOREIGN KEY (current_room_id)
            REFERENCES Room(room_id);

CREATE TABLE Health_Record (
                               record_id   INTEGER PRIMARY KEY,
                               pet_id      INTEGER NOT NULL,
                               staff_id    INTEGER,
                               record_date DATE NOT NULL,
                               record_type VARCHAR2(50),
                               notes       VARCHAR2(500)
);

ALTER TABLE Health_Record
    ADD CONSTRAINT fk_health_pet
        FOREIGN KEY (pet_id)
            REFERENCES Pet(pet_id);

ALTER TABLE Health_Record
    ADD CONSTRAINT fk_health_staff
        FOREIGN KEY (staff_id)
            REFERENCES Staff(staff_id);

CREATE TABLE Adoption_Application (
                                      application_id INTEGER PRIMARY KEY,
                                      member_id      INTEGER NOT NULL,
                                      pet_id         INTEGER NOT NULL,
                                      submitted_date DATE NOT NULL,
                                      status         VARCHAR2(20),
                                      reviewed_by    INTEGER,
                                      review_date    DATE,
                                      notes          VARCHAR2(500)
);

ALTER TABLE Adoption_Application
    ADD CONSTRAINT fk_app_member
        FOREIGN KEY (member_id)
            REFERENCES Member(member_id);

ALTER TABLE Adoption_Application
    ADD CONSTRAINT fk_app_pet
        FOREIGN KEY (pet_id)
            REFERENCES Pet(pet_id);

ALTER TABLE Adoption_Application
    ADD CONSTRAINT fk_app_staff
        FOREIGN KEY (reviewed_by)
            REFERENCES Staff(staff_id);

CREATE TABLE Adoption (
                          adoption_id        INTEGER PRIMARY KEY,
                          application_id     INTEGER NOT NULL,
                          pet_id             INTEGER NOT NULL,
                          member_id          INTEGER NOT NULL,
                          adoption_date      DATE NOT NULL,
                          adoption_fee       NUMBER(7,2),
                          follow_up_schedule DATE
);

ALTER TABLE Adoption
    ADD CONSTRAINT fk_adopt_app
        FOREIGN KEY (application_id)
            REFERENCES Adoption_Application(application_id);

ALTER TABLE Adoption
    ADD CONSTRAINT fk_adopt_pet
        FOREIGN KEY (pet_id)
            REFERENCES Pet(pet_id);

ALTER TABLE Adoption
    ADD CONSTRAINT fk_adopt_member
        FOREIGN KEY (member_id)
            REFERENCES Member(member_id);

CREATE TABLE Menu_Item (
                           item_id      INTEGER PRIMARY KEY,
                           name         VARCHAR2(100) NOT NULL,
                           category     VARCHAR2(50),
                           base_price   NUMBER(6,2) NOT NULL,
                           is_available NUMBER(1) CHECK (is_available IN (0,1))
);

CREATE TABLE Customer_Order (
                                order_id       INTEGER PRIMARY KEY,
                                member_id      INTEGER,
                                reservation_id INTEGER,
                                order_date     DATE,
                                order_time     TIMESTAMP,
                                total_price    NUMBER(8,2),
                                payment_status VARCHAR2(20)
);

ALTER TABLE Customer_Order
    ADD CONSTRAINT fk_order_member
        FOREIGN KEY (member_id)
            REFERENCES Member(member_id);

ALTER TABLE Customer_Order
    ADD CONSTRAINT fk_order_res
        FOREIGN KEY (reservation_id)
            REFERENCES Reservation(reservation_id);

CREATE TABLE Order_Item (
                            order_id   INTEGER,
                            item_id    INTEGER,
                            quantity   INTEGER NOT NULL,
                            unit_price NUMBER(7,2) NOT NULL,
                            PRIMARY KEY (order_id, item_id)
);

ALTER TABLE Order_Item
    ADD CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id)
            REFERENCES Customer_Order(order_id);

ALTER TABLE Order_Item
    ADD CONSTRAINT fk_order_item_item
        FOREIGN KEY (item_id)
            REFERENCES Menu_Item(item_id);

CREATE TABLE Event (
                       event_id      INTEGER PRIMARY KEY,
                       title         VARCHAR2(100),
                       description   VARCHAR2(400),
                       room_id       INTEGER NOT NULL,
                       event_date    DATE NOT NULL,
                       start_time    TIMESTAMP,
                       end_time      TIMESTAMP,
                       max_attendees INTEGER NOT NULL,
                       event_type    VARCHAR2(50),
                       staff_id      INTEGER NOT NULL
);

ALTER TABLE Event
    ADD CONSTRAINT fk_event_room
        FOREIGN KEY (room_id)
            REFERENCES Room(room_id);

ALTER TABLE Event
    ADD CONSTRAINT fk_event_staff
        FOREIGN KEY (staff_id)
            REFERENCES Staff(staff_id);

CREATE TABLE Event_Registration (
                                    member_id         INTEGER,
                                    event_id          INTEGER,
                                    registration_date DATE,
                                    attendance_status VARCHAR2(30),
                                    PRIMARY KEY(member_id, event_id)
);

ALTER TABLE Event_Registration
    ADD CONSTRAINT fk_event_reg_member
        FOREIGN KEY (member_id)
            REFERENCES Member(member_id);

ALTER TABLE Event_Registration
    ADD CONSTRAINT fk_event_reg_event
        FOREIGN KEY (event_id)
            REFERENCES Event(event_id);
