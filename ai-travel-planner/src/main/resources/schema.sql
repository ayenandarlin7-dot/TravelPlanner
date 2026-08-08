-- ============================================
-- AI TRAVEL PLANNER DATABASE
-- MySQL
-- ============================================

DROP DATABASE IF EXISTS travel_planner;

CREATE DATABASE travel_planner
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE travel_planner;


-- ============================================
-- 1. USERS
-- ============================================

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ============================================
-- 2. CITIES
-- Used by City.java / CityDAO.java
-- ============================================

CREATE TABLE cities (
    city_id INT PRIMARY KEY AUTO_INCREMENT,

    city_name VARCHAR(100) NOT NULL UNIQUE,

    region VARCHAR(100) NOT NULL,

    popular_attraction VARCHAR(200),

    best_season VARCHAR(100),

    average_hotel_cost DECIMAL(10,2) DEFAULT 0.00,

    average_food_cost DECIMAL(10,2) DEFAULT 0.00,

    recommended_days INT DEFAULT 3,

    weather_type VARCHAR(100),

    city_description TEXT,

    tourism_rating DECIMAL(2,1) DEFAULT 4.0,

    is_beach BOOLEAN DEFAULT FALSE,

    is_mountain BOOLEAN DEFAULT FALSE,

    is_historical BOOLEAN DEFAULT FALSE,

    family_friendly BOOLEAN DEFAULT TRUE,

    adventure_level VARCHAR(20),

    activities VARCHAR(500),

    food_types VARCHAR(500),

    relaxation_types VARCHAR(500)
);


-- ============================================
-- 3. TRANSPORTATIONS
-- ============================================

CREATE TABLE transportations (
    transportation_id INT PRIMARY KEY AUTO_INCREMENT,

    transport_name VARCHAR(30) NOT NULL UNIQUE
);


-- ============================================
-- 4. ROUTES
-- Used by RouteDAO.java
-- ============================================

CREATE TABLE routes (
    route_id INT PRIMARY KEY AUTO_INCREMENT,

    starting_city_id INT NOT NULL,

    destination_city_id INT NOT NULL,

    transportation_id INT NOT NULL,

    distance_km DECIMAL(10,2) NOT NULL,

    travel_time_hours DECIMAL(10,2) NOT NULL,

    estimated_cost DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_route_start_city
        FOREIGN KEY (starting_city_id)
        REFERENCES cities(city_id),

    CONSTRAINT fk_route_destination_city
        FOREIGN KEY (destination_city_id)
        REFERENCES cities(city_id),

    CONSTRAINT fk_route_transportation
        FOREIGN KEY (transportation_id)
        REFERENCES transportations(transportation_id)
);


-- ============================================
-- 5. TRIPS
-- Used by TripDAO.java
-- ============================================

CREATE TABLE trips (
    trip_id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT NOT NULL,

    route_id INT NOT NULL,

    travel_date DATE NOT NULL,

    budget DECIMAL(12,2) NOT NULL,

    preference VARCHAR(30) NOT NULL,

    recommended_cost DECIMAL(12,2) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trip_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_trip_route
        FOREIGN KEY (route_id)
        REFERENCES routes(route_id)
        ON DELETE CASCADE
);


-- ============================================
-- CITY DATA
-- ============================================

INSERT INTO cities (
    city_id,
    city_name,
    region,
    popular_attraction,
    best_season,
    average_hotel_cost,
    average_food_cost,
    recommended_days,
    weather_type,
    city_description,
    tourism_rating,
    is_beach,
    is_mountain,
    is_historical,
    family_friendly,
    adventure_level,
    activities,
    food_types,
    relaxation_types
)
VALUES

(
    1,
    'Yangon',
    'Yangon Region',
    'Shwedagon Pagoda, Kandawgyi Lake',
    'Nov - Feb',
    45000,
    12000,
    3,
    'warm',
    'A lively city with heritage buildings, pagodas, shopping and local food.',
    4.3,
    FALSE,
    FALSE,
    TRUE,
    TRUE,
    'medium',
    'sightseeing,culture,shopping,food,photography',
    'local,spicy,vegetarian',
    'shopping,photography,relaxing,nightlife'
),

(
    2,
    'Mandalay',
    'Mandalay Region',
    'Mandalay Hill, Royal Palace, U Bein Bridge',
    'Nov - Feb',
    40000,
    11000,
    3,
    'warm',
    'A cultural destination with temples, traditional crafts and famous local cuisine.',
    4.4,
    FALSE,
    FALSE,
    TRUE,
    TRUE,
    'medium',
    'sightseeing,culture,photography,food',
    'local,spicy,vegetarian',
    'photography,relaxing,shopping'
),

(
    3,
    'Nay Pyi Taw',
    'Nay Pyi Taw Union Territory',
    'National Landmark Garden, Zoological Garden',
    'Nov - Feb',
    35000,
    10000,
    2,
    'warm',
    'A spacious destination suited to relaxed sightseeing and family trips.',
    4.0,
    FALSE,
    FALSE,
    FALSE,
    TRUE,
    'low',
    'sightseeing,nature,family,photography',
    'local,vegetarian',
    'relaxing,photography,family'
),

(
    4,
    'Bagan',
    'Mandalay Region',
    'Bagan Archaeological Zone, Ancient Temples',
    'Nov - Feb',
    50000,
    13000,
    3,
    'warm',
    'An iconic historical destination famous for ancient temples and sunrise views.',
    4.8,
    FALSE,
    FALSE,
    TRUE,
    TRUE,
    'medium',
    'sightseeing,culture,history,photography,hotairballoon',
    'local,spicy,vegetarian',
    'photography,relaxing,culture'
),

(
    5,
    'Inle Lake',
    'Shan State',
    'Inle Lake, Floating Gardens, Phaung Daw Oo Pagoda',
    'Oct - Feb',
    55000,
    14000,
    3,
    'cool',
    'A peaceful lake destination with mountain scenery, boat trips and local culture.',
    4.7,
    FALSE,
    TRUE,
    TRUE,
    TRUE,
    'medium',
    'nature,sightseeing,photography,culture,boating',
    'local,vegetarian,spicy',
    'relaxing,photography,nature'
),

(
    6,
    'Myeik',
    'Tanintharyi Region',
    'Myeik Archipelago, Island Beaches',
    'Nov - Apr',
    60000,
    18000,
    4,
    'warm',
    'A tropical coastal destination for islands, seafood and outdoor adventures.',
    4.5,
    TRUE,
    FALSE,
    FALSE,
    TRUE,
    'high',
    'beach,nature,adventure,diving,photography,boating',
    'seafood,local,spicy',
    'beach,relaxing,photography'
),

(
    7,
    'Sittwe',
    'Rakhine State',
    'Sittwe Viewpoint, Rakhine Cultural Museum',
    'Nov - Feb',
    40000,
    16000,
    3,
    'warm',
    'A coastal city with Rakhine culture, seafood and peaceful seaside views.',
    4.1,
    TRUE,
    FALSE,
    TRUE,
    TRUE,
    'medium',
    'beach,culture,sightseeing,photography',
    'seafood,local,spicy',
    'beach,relaxing,photography'
),

(
    8,
    'Mawlamyine',
    'Mon State',
    'Kyaikthanlan Pagoda, Strand Road, Win Sein Taw Ya',
    'Nov - Feb',
    35000,
    12000,
    3,
    'warm',
    'A riverside cultural city with pagodas, local food and relaxed scenery.',
    4.2,
    FALSE,
    FALSE,
    TRUE,
    TRUE,
    'low',
    'culture,sightseeing,food,photography,nature',
    'local,seafood,spicy',
    'relaxing,photography,food'
),

(
    9,
    'Taunggyi',
    'Shan State',
    'Taunggyi Viewpoints, Hot Air Balloon Festival Area',
    'Oct - Feb',
    45000,
    13000,
    3,
    'cool',
    'A cool highland destination with mountain views, markets and nearby nature attractions.',
    4.5,
    FALSE,
    TRUE,
    FALSE,
    TRUE,
    'medium',
    'nature,adventure,photography,sightseeing,shopping',
    'local,spicy,vegetarian',
    'photography,shopping,relaxing'
),

(
    10,
    'Pyay',
    'Bago Region',
    'Sri Ksetra Ancient City, Shwesandaw Pagoda',
    'Nov - Feb',
    30000,
    10000,
    2,
    'warm',
    'A quieter historical destination with ancient heritage and local culture.',
    4.0,
    FALSE,
    FALSE,
    TRUE,
    TRUE,
    'low',
    'history,culture,sightseeing,photography',
    'local,spicy,vegetarian',
    'relaxing,photography,culture'
);


-- ============================================
-- TRANSPORTATION DATA
-- ============================================

INSERT INTO transportations (
    transportation_id,
    transport_name
)
VALUES
(1, 'Bus'),
(2, 'Train'),
(3, 'Flight');


-- ============================================
-- ROUTE DATA
-- ============================================

-- ============================================
-- ROUTE DATA
-- ============================================

INSERT INTO routes (
    starting_city_id,
    destination_city_id,
    transportation_id,
    distance_km,
    travel_time_hours,
    estimated_cost
)
VALUES

-- Yangon -> Mandalay
(1, 2, 1, 620, 9, 25000),
(1, 2, 2, 620, 8, 20000),
(1, 2, 3, 620, 1.5, 80000),

-- Mandalay -> Yangon
(2, 1, 1, 620, 9, 25000),
(2, 1, 2, 620, 8, 20000),
(2, 1, 3, 620, 1.5, 80000),

-- Yangon -> Nay Pyi Taw
(1, 3, 1, 320, 5, 15000),
(1, 3, 2, 320, 5, 12000),
(1, 3, 3, 320, 1, 60000),

-- Nay Pyi Taw -> Yangon
(3, 1, 1, 320, 5, 15000),
(3, 1, 2, 320, 5, 12000),
(3, 1, 3, 320, 1, 60000),

-- Yangon -> Bagan
(1, 4, 1, 600, 9, 20000),
(1, 4, 3, 600, 1.5, 75000),

-- Bagan -> Yangon
(4, 1, 1, 600, 9, 20000),
(4, 1, 3, 600, 1.5, 75000),

-- Yangon -> Inle Lake
(1, 5, 1, 580, 11, 30000),
(1, 5, 3, 580, 1, 70000),

-- Inle Lake -> Yangon
(5, 1, 1, 580, 11, 30000),
(5, 1, 3, 580, 1, 70000),

-- Yangon -> Myeik
(1, 6, 3, 700, 1.5, 90000),

-- Myeik -> Yangon
(6, 1, 3, 700, 1.5, 90000),

-- Yangon -> Sittwe
(1, 7, 3, 500, 1, 80000),

-- Sittwe -> Yangon
(7, 1, 3, 500, 1, 80000),

-- Yangon -> Mawlamyine
(1, 8, 1, 300, 6, 15000),
(1, 8, 2, 300, 6, 12000),

-- Mawlamyine -> Yangon
(8, 1, 1, 300, 6, 15000),
(8, 1, 2, 300, 6, 12000),

-- Yangon -> Taunggyi
(1, 9, 1, 500, 10, 25000),

-- Taunggyi -> Yangon
(9, 1, 1, 500, 10, 25000),

-- Yangon -> Pyay
(1, 10, 1, 260, 5, 12000),

-- Pyay -> Yangon
(10, 1, 1, 260, 5, 12000),

-- Mandalay -> Bagan
(2, 4, 1, 180, 4, 10000),
(2, 4, 2, 180, 5, 8000),

-- Bagan -> Mandalay (ပြင်ဆင်ပြီး - travel_time နဲ့ estimated_cost အပြည့်အစုံထည့်ထားသည်)
(4, 2, 1, 180, 4, 10000),
(4, 2, 2, 180, 5, 8000),

-- Mandalay -> Inle Lake
(2, 5, 1, 260, 7, 15000),
(2, 5, 3, 260, 0.8, 65000),

-- Inle Lake -> Mandalay
(5, 2, 1, 260, 7, 15000),
(5, 2, 3, 260, 0.8, 65000),

-- Bagan -> Inle Lake
(4, 5, 1, 300, 8, 18000),
(4, 5, 3, 300, 0.9, 70000),

-- Inle Lake -> Bagan
(5, 4, 1, 300, 8, 18000),
(5, 4, 3, 300, 0.9, 70000),

-- Mandalay -> Nay Pyi Taw
(2, 3, 1, 300, 5, 15000),
(2, 3, 2, 300, 5, 13000),
(2, 3, 3, 300, 1, 60000),

-- Nay Pyi Taw -> Mandalay
(3, 2, 1, 300, 5, 15000),
(3, 2, 2, 300, 5, 13000),
(3, 2, 3, 300, 1, 60000),

-- Bagan -> Nay Pyi Taw
(4, 3, 1, 180, 4, 10000),

-- Nay Pyi Taw -> Bagan
(3, 4, 1, 180, 4, 10000),

-- Mandalay -> Taunggyi
(2, 9, 1, 200, 5, 12000),

-- Taunggyi -> Mandalay
(9, 2, 1, 200, 5, 12000),

-- Mandalay -> Mawlamyine
(2, 8, 1, 430, 8, 18000),

-- Mawlamyine -> Mandalay
(8, 2, 1, 430, 8, 18000),

-- Mandalay -> Pyay
(2, 10, 1, 400, 7, 16000),

-- Pyay -> Mandalay
(10, 2, 1, 400, 7, 16000),

-- Bagan -> Taunggyi
(4, 9, 1, 250, 6, 15000),

-- Taunggyi -> Bagan
(9, 4, 1, 250, 6, 15000),

-- Inle Lake -> Taunggyi
(5, 9, 1, 40, 1.5, 5000),

-- Taunggyi -> Inle Lake
(9, 5, 1, 40, 1.5, 5000),

-- Bagan -> Mawlamyine
(4, 8, 1, 550, 10, 22000),

-- Mawlamyine -> Bagan
(8, 4, 1, 550, 10, 22000),

-- Inle Lake -> Mawlamyine
(5, 8, 1, 500, 9, 22000),

-- Mawlamyine -> Inle Lake
(8, 5, 1, 500, 9, 22000);


-- ============================================
-- CHECK DATABASE
-- ============================================

SELECT 'Users' AS table_name, COUNT(*) AS total FROM users
UNION ALL
SELECT 'Cities', COUNT(*) FROM cities
UNION ALL
SELECT 'Transportations', COUNT(*) FROM transportations
UNION ALL
SELECT 'Routes', COUNT(*) FROM routes
UNION ALL
SELECT 'Trips', COUNT(*) FROM trips;


-- Check cities
SELECT
    city_id,
    city_name,
    region,
    weather_type,
    tourism_rating,
    activities,
    food_types,
    relaxation_types
FROM cities
ORDER BY city_id;