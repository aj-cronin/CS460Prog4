CREATE TABLE Order_Item (
        order_id        integer,
        item_id         integer,
        quantity        integer,
        unit_price      float,
        primary key (order_id)
);

CREATE TABLE Menu_Item (
        item_id         integer,
        name            varchar2(32),
        category        varchar2(32),
        base_price      float,
        is_available    integer,
        primary key (item_id)
);

CREATE TABLE Customer_Order (
    order_id        integer,
    member_id       integer,
    reservation_id  integer,
    order_date      date,
    order_time      timestamp,
    total_price     float,
    payment_status  varchar2(32),
    primary key (order_id)
);

CREATE TABLE Room (
        room_id integer,
        room_name       varchar2(32),
        room_type       varchar2(32),
        max_capacity    integer,
        is_adoption_area        integer,
        primary key (room_id)
);

CREATE TABLE Reservation (
        reservation_id  integer,
        member_id       integer,
        room_id integer,
        reservation_date        date,
        start_time              timestamp,
        duration_minutes        integer,
        status  varchar2(32),
        check_in_time   timestamp,
        check_out_time  timestamp,
        tier_id         integer,
        primary key (reservation_id)
);

CREATE TABLE Event (
        event_id        integer,
        title           varchar2(40),
        description     varchar2(40),
        room_id         integer,
        event_date      date,
        start_time      timestamp,
        end_time        timestamp,
        max_attendees   integer,
        event_type      varchar2(32),
        staff_id        integer,
        primary key (event_id)
);

CREATE TABLE Event_Registration (
        member_id       integer,
        event_id        integer,
        registration_date       date,
        attendance_status       varchar2(32)
);

CREATE TABLE Health_Record (
    record_id    integer,
    pet_id       integer,
    staff_id     integer,
    record_date  date,
    record_type  varchar2(32),
    notes        varchar2(128),
    primary key (record_id)
);

CREATE TABLE Member (
    member_id          integer,
    name               varchar2(32),
    phone              integer,
    email              varchar2(50),
    date_of_birth      date,
    emergency_contact  varchar2(32),
    tier_id            integer,
    primary key (member_id)
);

CREATE TABLE Membership_Tier (
    tier_id       integer,
    tier_name     varchar2(32),
    description   varchar2(140),
    discount_rate integer,
    visit_limit   integer,
    primary key (tier_id)
);

CREATE TABLE Staff (
    staff_id    integer,
    name        varchar2(32),
    phone       varchar2(32),
    email       varchar2(50),
    hire_date   date,
    is_active   varchar2(32),
    role        varchar2(32),
    primary key (staff_id)
);

CREATE TABLE Pet (
    pet_id           integer,
    name             varchar2(32),
    species          varchar2(32),
    breed            varchar2(32),
    age              integer,
    date_of_arrival  date,
    temperament      varchar2(32),
    special_needs    varchar2(128),
    status           varchar2(32),
    current_room_id  integer,
    primary key (pet_id)
);

CREATE TABLE Adoption_Application (
    application_id  integer,
    member_id       integer,
    pet_id          integer,
    submitted_date  date,
    status          varchar2(32),
    reviewed_by     integer,
    review_date     date,
    notes           varchar2(128),
    primary key (application_id)
);

CREATE TABLE Adoption (
    adoption_id          integer,
    application_id       integer,
    pet_id               integer,
    member_id            integer,
    adoption_date        date,
    adoption_fee         float,
    follow_up_schedule   date,
    primary key (adoption_id)
);
