CREATE DATABASE gym_db;
USE gym_db;
CREATE TABLE Account (
    account_id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_info VARCHAR(100),

    require_password_reset BOOLEAN DEFAULT FALSE,

    PRIMARY KEY (account_id)
);

CREATE TABLE Role (
    role_id INT NOT NULL AUTO_INCREMENT,

    role_name VARCHAR(50) NOT NULL UNIQUE,

    PRIMARY KEY (role_id)
);

CREATE TABLE Permission (
    permission_id INT NOT NULL AUTO_INCREMENT,
    permission_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (permission_id)
);

CREATE TABLE Account_Role (
    account_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (account_id, role_id),
    FOREIGN KEY (account_id) REFERENCES Account(account_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Role(role_id) ON DELETE CASCADE
);

CREATE TABLE Role_Permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES Role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permission(permission_id) ON DELETE CASCADE
);

CREATE TABLE Membership (
    membership_id INT NOT NULL AUTO_INCREMENT,
    account_id INT NOT NULL,
    billing_cycle ENUM('monthly', 'yearly') NOT NULL,
    start_date DATE NOT NULL,
    status ENUM('active', 'cancelled') NOT NULL,
    PRIMARY KEY (membership_id),
    FOREIGN KEY (account_id) REFERENCES Account(account_id) ON DELETE CASCADE
);

CREATE TABLE Fitness_Class (
    class_id INT NOT NULL AUTO_INCREMENT,
    coach_id INT NOT NULL,

    class_type VARCHAR(100) NOT NULL,

    class_time DATETIME NOT NULL,
    status ENUM('scheduled', 'cancelled', 'completed') NOT NULL,
    PRIMARY KEY (class_id),
    FOREIGN KEY (coach_id) REFERENCES Account(account_id) ON DELETE CASCADE
);

CREATE TABLE Class_Booking (
    booking_id INT NOT NULL AUTO_INCREMENT,
    account_id INT NOT NULL,
    class_id INT NOT NULL,
    booking_time DATETIME NOT NULL,
    PRIMARY KEY (booking_id),
    FOREIGN KEY (account_id) REFERENCES Account(account_id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES Fitness_Class(class_id) ON DELETE CASCADE
);

CREATE TABLE Trainer_Appointment (
    appointment_id INT NOT NULL AUTO_INCREMENT,
    customer_id INT NOT NULL,
    trainer_id INT NOT NULL,
    appointment_time DATETIME NOT NULL,
    status ENUM('pending', 'accepted', 'declined', 'cancelled', 'completed') NOT NULL,
    PRIMARY KEY (appointment_id),
    FOREIGN KEY (customer_id) REFERENCES Account(account_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES Account(account_id) ON DELETE CASCADE
);

CREATE USER 'customer'@'%' IDENTIFIED BY 'pass123';
CREATE USER 'employee'@'%' IDENTIFIED BY 'pass123';
CREATE USER 'admin'@'%' IDENTIFIED BY 'pass123';

GRANT SELECT, INSERT ON gym_db.* TO 'customer'@'%';
GRANT SELECT, INSERT, UPDATE ON gym_db.* TO 'employee'@'%';
GRANT ALL PRIVILEGES ON gym_db.* TO 'admin'@'%';

INSERT INTO Account (username, password, full_name, employee_type)
VALUES ('admin1', 'admin123', 'Admin User', NULL);


