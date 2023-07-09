CREATE DATABASE fleet;
GRANT ALL PRIVILEGES ON `fleet`.* TO `test`@`%`;
FLUSH PRIVILEGES;
USE fleet;

CREATE TABLE company (
    id       INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    timezone VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE branch (
    id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    timezone   VARCHAR(100) NOT NULL,
    company_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_branch_company
        FOREIGN KEY (company_id)
        REFERENCES company(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE car (
    id                  INT UNSIGNED NOT NULL AUTO_INCREMENT,
    model               VARCHAR(100) NOT NULL,
    chassis_number      VARCHAR(100) NOT NULL,
    color               VARCHAR(100) NOT NULL,
    registration_number VARCHAR(100) NOT NULL,
    branch_id           INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (chassis_number),
    UNIQUE KEY (registration_number),
    CONSTRAINT fk_car_branch
        FOREIGN KEY (branch_id)
        REFERENCES branch(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE driver (
    id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name           VARCHAR(100) NOT NULL,
    license_number VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE customer (
    id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    birthdate  DATE NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE trip (
    id          INT UNSIGNED NOT NULL AUTO_INCREMENT,
    timezone    VARCHAR(255),
    start_on    TIMESTAMP,
    end_at      TIMESTAMP,
    distance    DOUBLE,
    status      ENUM ('STARTED','IN_PROGRESS','FINISHED','CANCELED') NOT NULL,
    car_id      INT UNSIGNED NOT NULL,
    driver_id   INT UNSIGNED NOT NULL,
    customer_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_trip_car
            FOREIGN KEY (car_id)
            REFERENCES car(id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_trip_driver
                FOREIGN KEY (driver_id)
                REFERENCES driver(id)
                ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_trip_customer
                FOREIGN KEY (customer_id)
                REFERENCES customer(id)
                ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;