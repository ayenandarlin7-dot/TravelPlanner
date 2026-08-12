# Database Contract

Your teammate's MySQL database must use these table and column names.

## users
- user_id INT PRIMARY KEY AUTO_INCREMENT
- full_name VARCHAR(100) NOT NULL
- email VARCHAR(100) NOT NULL UNIQUE
- password_hash VARCHAR(64) NOT NULL
- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

## cities
- city_id INT PRIMARY KEY AUTO_INCREMENT
- city_name VARCHAR(100) NOT NULL UNIQUE

## transportations
- transportation_id INT PRIMARY KEY AUTO_INCREMENT
- transport_name VARCHAR(30) NOT NULL UNIQUE

Expected sample values: Bus, Train, Flight.

## routes
- route_id INT PRIMARY KEY AUTO_INCREMENT
- starting_city_id INT NOT NULL
- destination_city_id INT NOT NULL
- transportation_id INT NOT NULL
- distance_km DECIMAL(10,2) NOT NULL
- travel_time_hours DECIMAL(10,2) NOT NULL
- estimated_cost DECIMAL(12,2) NOT NULL

Foreign keys:
- starting_city_id -> cities.city_id
- destination_city_id -> cities.city_id
- transportation_id -> transportations.transportation_id

## trips
- trip_id INT PRIMARY KEY AUTO_INCREMENT
- user_id INT NOT NULL
- route_id INT NOT NULL
- travel_date DATE NOT NULL
- budget DECIMAL(12,2) NOT NULL
- preference VARCHAR(20) NOT NULL
- recommended_cost DECIMAL(12,2) NOT NULL
- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

Foreign keys:
- user_id -> users.user_id
- route_id -> routes.route_id
