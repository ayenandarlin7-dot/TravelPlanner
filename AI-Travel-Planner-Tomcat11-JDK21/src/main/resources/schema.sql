-- ============================================================
-- TravelMate AI - Database Schema (Phase 3)
--
-- Safe to re-run:
--   * Existing users are never overwritten or deleted.
--   * Existing cities / transportations / routes / trips are kept.
--   * Missing columns / keys are added only when they do not exist.
-- ============================================================

CREATE DATABASE IF NOT EXISTS travel_planner
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE travel_planner;

-- ============================================================
-- Helper procedures for idempotent DDL (MySQL 8.0)
-- ============================================================

DROP PROCEDURE IF EXISTS add_column_if_missing;
DELIMITER //
CREATE PROCEDURE add_column_if_missing(IN p_table VARCHAR(64), IN p_column VARCHAR(64), IN p_ddl VARCHAR(2000))
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table
          AND COLUMN_NAME = p_column
    ) THEN
        SET @ddl = p_ddl;
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS add_index_if_missing;
DELIMITER //
CREATE PROCEDURE add_index_if_missing(IN p_table VARCHAR(64), IN p_index VARCHAR(64), IN p_ddl VARCHAR(2000))
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table
          AND INDEX_NAME = p_index
    ) THEN
        SET @ddl = p_ddl;
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS add_foreign_key_if_missing;
DELIMITER //
CREATE PROCEDURE add_foreign_key_if_missing(IN p_table VARCHAR(64), IN p_fk VARCHAR(64), IN p_ddl VARCHAR(2000))
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table
          AND CONSTRAINT_NAME = p_fk
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ) THEN
        SET @ddl = p_ddl;
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

-- ============================================================
-- users (kept compatible with the existing authentication)
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- cities
-- ============================================================

CREATE TABLE IF NOT EXISTS cities (
    city_id INT PRIMARY KEY AUTO_INCREMENT,
    city_name VARCHAR(100) NOT NULL UNIQUE,
    latitude DECIMAL(9,6) NULL,
    longitude DECIMAL(9,6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CALL add_column_if_missing('cities', 'latitude',
    'ALTER TABLE cities ADD COLUMN latitude DECIMAL(9,6) NULL AFTER city_name');

CALL add_column_if_missing('cities', 'longitude',
    'ALTER TABLE cities ADD COLUMN longitude DECIMAL(9,6) NULL AFTER latitude');

-- Coordinates refresh safely; existing city rows are never deleted.
INSERT INTO cities (city_name, latitude, longitude) VALUES
    ('Yangon',      16.866100, 96.195100),
    ('Mandalay',    21.958800, 96.089100),
    ('Naypyidaw',   19.763300, 96.078500),
    ('Bagan',       21.190700, 94.873800),
    ('Taunggyi',    20.781800, 97.038200),
    ('Mawlamyine',  16.485500, 97.626000),
    ('Inle Lake',   20.553100, 96.913300),
    ('Pathein',     16.783300, 94.733300)
AS new
ON DUPLICATE KEY UPDATE
    latitude  = new.latitude,
    longitude = new.longitude;

-- ============================================================
-- transportations
-- ============================================================

CREATE TABLE IF NOT EXISTS transportations (
    transportation_id INT PRIMARY KEY AUTO_INCREMENT,
    transport_name VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO transportations (transport_name) VALUES ('Bus'), ('Train'), ('Flight'), ('Auto');

-- ============================================================
-- routes
-- ============================================================

CREATE TABLE IF NOT EXISTS routes (
    route_id INT PRIMARY KEY AUTO_INCREMENT,
    starting_city_id INT NOT NULL,
    destination_city_id INT NOT NULL,
    transportation_id INT NOT NULL,
    distance_km DECIMAL(10,2) NOT NULL,
    travel_time_hours DECIMAL(10,2) NOT NULL,
    estimated_cost DECIMAL(12,2) NOT NULL,
    route_info VARCHAR(255) NULL,
    UNIQUE KEY uq_routes_trip (starting_city_id, destination_city_id, transportation_id),
    FOREIGN KEY (starting_city_id) REFERENCES cities(city_id),
    FOREIGN KEY (destination_city_id) REFERENCES cities(city_id),
    FOREIGN KEY (transportation_id) REFERENCES transportations(transportation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CALL add_column_if_missing('routes', 'route_info',
    'ALTER TABLE routes ADD COLUMN route_info VARCHAR(255) NULL AFTER estimated_cost');

CALL add_index_if_missing('routes', 'uq_routes_trip',
    'ALTER TABLE routes ADD UNIQUE KEY uq_routes_trip (starting_city_id, destination_city_id, transportation_id)');

-- Additional realistic route combinations (existing routes are kept).
INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 575.00, 13.00, 52000.00, 'Direct overnight bus via Yangon - Mandalay highway'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Yangon' AND dc.city_name = 'Inle Lake' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 470.00, 1.50, 150000.00, 'Flight via Heho Airport'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Yangon' AND dc.city_name = 'Inle Lake' AND t.transport_name = 'Flight';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 575.00, 13.00, 52000.00, 'Direct overnight bus via Yangon - Mandalay highway'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Inle Lake' AND dc.city_name = 'Yangon' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 470.00, 1.50, 150000.00, 'Flight via Heho Airport'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Inle Lake' AND dc.city_name = 'Yangon' AND t.transport_name = 'Flight';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 235.00, 7.00, 28000.00, 'Bus via Taunggyi road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Mandalay' AND dc.city_name = 'Inle Lake' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 230.00, 1.00, 110000.00, 'Flight via Heho Airport'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Mandalay' AND dc.city_name = 'Inle Lake' AND t.transport_name = 'Flight';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 235.00, 7.00, 28000.00, 'Bus via Taunggyi road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Inle Lake' AND dc.city_name = 'Mandalay' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 230.00, 1.00, 110000.00, 'Flight via Heho Airport'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Inle Lake' AND dc.city_name = 'Mandalay' AND t.transport_name = 'Flight';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 630.00, 7.00, 45000.00, 'Direct bus via Magway road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Yangon' AND dc.city_name = 'Bagan' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 480.00, 1.50, 160000.00, 'Flight via Nyaung U Airport'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Yangon' AND dc.city_name = 'Bagan' AND t.transport_name = 'Flight';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 630.00, 7.00, 45000.00, 'Direct bus via Magway road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Bagan' AND dc.city_name = 'Yangon' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 480.00, 1.50, 160000.00, 'Flight via Nyaung U Airport'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Bagan' AND dc.city_name = 'Yangon' AND t.transport_name = 'Flight';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 430.00, 10.00, 40000.00, 'Bus via Highway 1 and Shwenyaung road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Yangon' AND dc.city_name = 'Taunggyi' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 430.00, 10.00, 40000.00, 'Bus via Highway 1 and Shwenyaung road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Taunggyi' AND dc.city_name = 'Yangon' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 180.00, 5.00, 22000.00, 'Bus via Taunggyi - Mandalay road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Mandalay' AND dc.city_name = 'Taunggyi' AND t.transport_name = 'Bus';

INSERT IGNORE INTO routes (starting_city_id, destination_city_id, transportation_id, distance_km, travel_time_hours, estimated_cost, route_info)
SELECT sc.city_id, dc.city_id, t.transportation_id, 180.00, 5.00, 22000.00, 'Bus via Taunggyi - Mandalay road'
FROM cities sc, cities dc, transportations t
WHERE sc.city_name = 'Taunggyi' AND dc.city_name = 'Mandalay' AND t.transport_name = 'Bus';

-- ============================================================
-- hotels
-- ============================================================

CREATE TABLE IF NOT EXISTS hotels (
    hotel_id INT PRIMARY KEY AUTO_INCREMENT,
    city_id INT NOT NULL,
    hotel_name VARCHAR(150) NOT NULL,
    category VARCHAR(20) NOT NULL,
    price_per_night DECIMAL(12,2) NOT NULL,
    room_capacity INT NOT NULL,
    rating DECIMAL(3,2) NOT NULL,
    location_info VARCHAR(255) NULL,
    UNIQUE KEY uq_hotel_city_name (city_id, hotel_name),
    FOREIGN KEY (city_id) REFERENCES cities(city_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO hotels (hotel_id, city_id, hotel_name, category, price_per_night, room_capacity, rating, location_info) VALUES
    (1,  1, 'Ocean Pearl Inn',               'Budget',   25000.00,  2, 8.20, 'Botahtaung, Yangon'),
    (2,  1, 'Hotel Grand United',            'Standard', 60000.00,  3, 8.50, 'Ahlone Road, Yangon'),
    (3,  1, 'Chatrium Hotel Yangon',         'Luxury',   120000.00, 4, 8.90, 'Kabar Aye Pagoda Road, Yangon'),
    (4,  2, 'Royal Yatana Hotel',            'Budget',   22000.00,  2, 8.00, '25th Street, Mandalay'),
    (5,  2, 'Hotel Yadanarpon',              'Standard', 55000.00,  3, 8.40, '26th Street, Mandalay'),
    (6,  2, 'Mandalay Hill Resort',          'Luxury',   105000.00, 4, 8.80, 'Mandalay Hill, Mandalay'),
    (7,  3, 'Hotel Eastern Palace',          'Budget',   20000.00,  2, 7.90, 'Thapyay Gone, Naypyidaw'),
    (8,  3, 'Hotel Amazing Naypyidaw',       'Standard', 50000.00,  3, 8.30, 'Jade Villa, Naypyidaw'),
    (9,  3, 'Mingalar Thiri Hotel',          'Luxury',   95000.00,  4, 8.60, 'Ottara Thiri, Naypyidaw'),
    (10, 4, 'Bagan Empress Hotel',           'Budget',   30000.00,  2, 8.30, 'Nyaung U, Bagan'),
    (11, 4, 'Tharabar Gate Hotel',           'Standard', 65000.00,  3, 8.70, 'Old Bagan'),
    (12, 4, 'Aureum Palace Hotel',           'Luxury',   180000.00, 4, 9.00, 'Old Bagan, near temples'),
    (13, 5, 'Golden Sunrise Guesthouse',     'Budget',   18000.00,  2, 7.80, 'Kyaunggone, Taunggyi'),
    (14, 5, 'Hotel Samrat Taunggyi',         'Standard', 45000.00,  3, 8.20, 'Tun Aye Kyaung, Taunggyi'),
    (15, 5, 'Amazing Grand Hotel',           'Luxury',   90000.00,  4, 8.50, 'Bawrithat Street, Taunggyi'),
    (16, 6, 'Cinderella Hotel',              'Budget',   20000.00,  2, 8.10, 'Strand Road, Mawlamyine'),
    (17, 6, 'Attran Hotel',                  'Standard', 48000.00,  3, 8.40, 'Strand Road, Mawlamyine'),
    (18, 6, 'Royal Mawlamyine Hotel',        'Luxury',   88000.00,  4, 8.60, 'Kyaikthanlan, Mawlamyine'),
    (19, 8, 'Pristine Lotus Resort',         'Budget',   35000.00,  2, 8.40, 'Nyaung Shwe, Inle Lake'),
    (20, 8, 'Golden Island Cottages',        'Standard', 70000.00,  3, 8.80, 'Mine Thauk, Inle Lake'),
    (21, 8, 'Aureum Inle Palace',            'Luxury',   150000.00, 4, 9.10, 'Nyaung Shwe, Inle Lake'),
    (22, 7, 'Pathein Hotel',                 'Budget',   20000.00,  2, 7.90, 'Mahabandula Road, Pathein'),
    (23, 7, 'Shwe Pathein Hotel',            'Standard', 45000.00,  3, 8.10, 'Myoma Street, Pathein'),
    (24, 7, 'Deluxe Pathein Hotel',          'Luxury',   85000.00,  4, 8.40, 'Sapal Road, Pathein');

-- ============================================================
-- food_estimates
-- ============================================================

CREATE TABLE IF NOT EXISTS food_estimates (
    food_estimate_id INT PRIMARY KEY AUTO_INCREMENT,
    city_id INT NOT NULL,
    tier VARCHAR(20) NOT NULL,
    daily_cost_per_person DECIMAL(12,2) NOT NULL,
    UNIQUE KEY uq_food_city_tier (city_id, tier),
    FOREIGN KEY (city_id) REFERENCES cities(city_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO food_estimates (food_estimate_id, city_id, tier, daily_cost_per_person) VALUES
    (1,  1, 'Economy',  8000.00),
    (2,  1, 'Standard', 15000.00),
    (3,  1, 'Premium',  30000.00),
    (4,  2, 'Economy',  7000.00),
    (5,  2, 'Standard', 13000.00),
    (6,  2, 'Premium',  25000.00),
    (7,  3, 'Economy',  7000.00),
    (8,  3, 'Standard', 12000.00),
    (9,  3, 'Premium',  24000.00),
    (10, 4, 'Economy',  6000.00),
    (11, 4, 'Standard', 12000.00),
    (12, 4, 'Premium',  26000.00),
    (13, 5, 'Economy',  6000.00),
    (14, 5, 'Standard', 11000.00),
    (15, 5, 'Premium',  22000.00),
    (16, 6, 'Economy',  6500.00),
    (17, 6, 'Standard', 12000.00),
    (18, 6, 'Premium',  23000.00),
    (19, 8, 'Economy',  7000.00),
    (20, 8, 'Standard', 14000.00),
    (21, 8, 'Premium',  28000.00),
    (22, 7, 'Economy',  6000.00),
    (23, 7, 'Standard', 11000.00),
    (24, 7, 'Premium',  22000.00);

-- ============================================================
-- attractions
-- ============================================================

CREATE TABLE IF NOT EXISTS attractions (
    attraction_id INT PRIMARY KEY AUTO_INCREMENT,
    city_id INT NOT NULL,
    attraction_name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    entrance_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
    image_path VARCHAR(255) NULL,
    latitude DECIMAL(9,6) NULL,
    longitude DECIMAL(9,6) NULL,
    UNIQUE KEY uq_attraction_city_name (city_id, attraction_name),
    FOREIGN KEY (city_id) REFERENCES cities(city_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO attractions (attraction_id, city_id, attraction_name, description, entrance_fee, image_path, latitude, longitude) VALUES
    (1,  1, 'Shwedagon Pagoda',           'The most sacred Buddhist pagoda in Myanmar, a golden landmark towering over Yangon.', 20000.00, '/images/attractions/shwedagon.jpg',     16.798400, 96.150500),
    (2,  1, 'Botahtaung Pagoda',          'A riverside pagoda with a sacred relic, located near the Yangon waterfront.',       5000.00,  '/images/attractions/botahtaung.jpg',    16.766300, 96.170000),
    (3,  1, 'National Museum',            'Museum showcasing Myanmar history, royal regalia and ancient artefacts.',          5000.00,  '/images/attractions/national-museum.jpg', 16.795000, 96.162600),
    (4,  1, 'Kandawgyi Lake',             'Scenic lake with a wooden boardwalk and views of Shwedagon Pagoda.',              3000.00,  '/images/attractions/kandawgyi.jpg',      16.793000, 96.171000),
    (5,  2, 'Mandalay Palace',            'The last royal palace of the Konbaung dynasty, surrounded by a moat.',            15000.00, '/images/attractions/mandalay-palace.jpg', 21.990000, 96.099500),
    (6,  2, 'Kuthodaw Pagoda',            'Known as the world largest book, with 729 marble slabs of Buddhist scripture.',    5000.00,  '/images/attractions/kuthodaw.jpg',       21.982600, 96.087000),
    (7,  2, 'U Bein Bridge',              'The world longest teak bridge, best seen at sunset over Taungthaman Lake.',       0.00,     '/images/attractions/ubein-bridge.jpg',    21.999100, 96.068700),
    (8,  2, 'Mandalay Hill',              'A scenic hill with a pagoda at the summit offering panoramic city views.',        10000.00, '/images/attractions/mandalay-hill.jpg',   22.010700, 96.105000),
    (9,  3, 'Uppatasanti Pagoda',         'A golden pagoda in Naypyidaw, modeled after Shwedagon Pagoda.',                   0.00,     '/images/attractions/uppatasanti.jpg',     19.780000, 96.166000),
    (10, 3, 'Naypyidaw Zoo',              'A large zoo featuring elephants, giraffes and other animals.',                    5000.00,  '/images/attractions/naypyidaw-zoo.jpg',   19.749000, 96.128000),
    (11, 3, 'Water Fountain Garden',      'A landscaped garden with colorful fountains and light shows in the evenings.',    3000.00,  '/images/attractions/fountain-garden.jpg', 19.758000, 96.121000),
    (12, 3, 'Gem Museum',                 'Museum of Myanmar gemstones, jewellery and mining displays.',                     5000.00,  '/images/attractions/gem-museum.jpg',      19.778000, 96.179000),
    (13, 4, 'Ananda Temple',              'One of the finest and best-preserved temples of Bagan with four standing Buddhas.', 5000.00, '/images/attractions/ananda.jpg',          21.170500, 94.868900),
    (14, 4, 'Shwezigon Pagoda',           'A gilded pagoda that predates the Bagan era, still an important pilgrimage site.', 5000.00, '/images/attractions/shwezigon.jpg',       21.196500, 94.888000),
    (15, 4, 'Dhammayangyi Temple',        'The largest temple in Bagan, known for its massive brickwork.',                   5000.00,  '/images/attractions/dhammayangyi.jpg',    21.161600, 94.869600),
    (16, 4, 'Shwesandaw Pagoda',          'A five-tiered pagoda famous for its sunset views over the Bagan plain.',          5000.00,  '/images/attractions/shwesandaw.jpg',      21.168000, 94.875000),
    (17, 5, 'Kakku Pagoda',               'An ancient complex of thousands of stupas nestled among the hills near Pinlaung.', 10000.00, '/images/attractions/kakku.jpg',           20.523000, 97.182000),
    (18, 5, 'Pindaya Caves',              'Limestone caves filled with thousands of Buddha images on the road to Inle.',    5000.00,  '/images/attractions/pindaya.jpg',         20.918000, 96.650000),
    (19, 5, 'Taunggyi Market',            'A lively highland market with local produce, textiles and handicrafts.',          0.00,     '/images/attractions/taunggyi-market.jpg', 20.784000, 97.039000),
    (20, 6, 'Kyaikthanlan Pagoda',        'A hilltop pagoda with fine views over Mawlamyine and the Thanlwin river.',        5000.00,  '/images/attractions/kyaikthanlan.jpg',    16.493000, 97.630000),
    (21, 6, 'Saya San Memorial',          'A monument honouring the leader of the 1930 farmers uprising.',                   0.00,     '/images/attractions/saya-san.jpg',        16.508000, 97.622000),
    (22, 6, 'Setse Beach',                'A quiet beach resort south of Mawlamyine along the Gulf of Mottama.',             0.00,     '/images/attractions/setse-beach.jpg',     16.086000, 97.646000),
    (23, 8, 'Phaung Daw Oo Pagoda',       'The most venerated shrine on Inle Lake, housing five ancient Buddha images.',     10000.00, '/images/attractions/phaung-daw-oo.jpg',   20.477000, 96.936000),
    (24, 8, 'Indein Village',             'Lakeside village with a crumbling complex of hundreds of ancient stupas.',       5000.00,  '/images/attractions/indein.jpg',          20.430000, 96.872000),
    (25, 8, 'Nyaung Shwe',                'The gateway town to Inle Lake, with markets, monasteries and canal boat trips.',  0.00,     '/images/attractions/nyaung-shwe.jpg',     20.655000, 96.929000),
    (26, 7, 'Shwemokhtaw Pagoda',         'A famous golden pagoda in Pathein built in the ancient Mon style.',               5000.00,  '/images/attractions/shwemokhtaw.jpg',     16.773000, 94.723000),
    (27, 7, 'Pathein Umbrella Workshop',  'Watch artisans craft the colourful paper umbrellas Pathein is famous for.',      3000.00,  '/images/attractions/umbrella-workshop.jpg', 16.779000, 94.733000);

-- ============================================================
-- trips (extended; existing rows are preserved and backfilled)
-- ============================================================

CREATE TABLE IF NOT EXISTS trips (
    trip_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    route_id INT NOT NULL,
    starting_city_id INT NULL,
    destination_city_id INT NULL,
    transportation_id INT NULL,
    travel_date DATE NOT NULL,
    return_date DATE NULL,
    number_of_travellers INT NOT NULL DEFAULT 1,
    budget DECIMAL(12,2) NOT NULL,
    preference VARCHAR(20) NOT NULL,
    recommended_cost DECIMAL(12,2) NOT NULL,
    transportation_cost DECIMAL(12,2) NULL,
    hotel_cost DECIMAL(12,2) NULL,
    food_cost DECIMAL(12,2) NULL,
    attraction_cost DECIMAL(12,2) NULL,
    total_estimated_cost DECIMAL(12,2) NULL,
    selected_hotel_id INT NULL,
    budget_status VARCHAR(20) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (route_id) REFERENCES routes(route_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CALL add_column_if_missing('trips', 'starting_city_id',
    'ALTER TABLE trips ADD COLUMN starting_city_id INT NULL AFTER route_id');
CALL add_column_if_missing('trips', 'destination_city_id',
    'ALTER TABLE trips ADD COLUMN destination_city_id INT NULL AFTER starting_city_id');
CALL add_column_if_missing('trips', 'transportation_id',
    'ALTER TABLE trips ADD COLUMN transportation_id INT NULL AFTER destination_city_id');
CALL add_column_if_missing('trips', 'transportation_cost',
    'ALTER TABLE trips ADD COLUMN transportation_cost DECIMAL(12,2) NULL AFTER transportation_id');
CALL add_column_if_missing('trips', 'hotel_cost',
    'ALTER TABLE trips ADD COLUMN hotel_cost DECIMAL(12,2) NULL AFTER transportation_cost');
CALL add_column_if_missing('trips', 'food_cost',
    'ALTER TABLE trips ADD COLUMN food_cost DECIMAL(12,2) NULL AFTER hotel_cost');
CALL add_column_if_missing('trips', 'attraction_cost',
    'ALTER TABLE trips ADD COLUMN attraction_cost DECIMAL(12,2) NULL AFTER food_cost');
CALL add_column_if_missing('trips', 'total_estimated_cost',
    'ALTER TABLE trips ADD COLUMN total_estimated_cost DECIMAL(12,2) NULL AFTER attraction_cost');
CALL add_column_if_missing('trips', 'selected_hotel_id',
    'ALTER TABLE trips ADD COLUMN selected_hotel_id INT NULL AFTER total_estimated_cost');
CALL add_column_if_missing('trips', 'budget_status',
    'ALTER TABLE trips ADD COLUMN budget_status VARCHAR(20) NULL AFTER selected_hotel_id');

CALL add_foreign_key_if_missing('trips', 'fk_trips_starting_city',
    'ALTER TABLE trips ADD CONSTRAINT fk_trips_starting_city FOREIGN KEY (starting_city_id) REFERENCES cities(city_id)');
CALL add_foreign_key_if_missing('trips', 'fk_trips_destination_city',
    'ALTER TABLE trips ADD CONSTRAINT fk_trips_destination_city FOREIGN KEY (destination_city_id) REFERENCES cities(city_id)');
CALL add_foreign_key_if_missing('trips', 'fk_trips_transportation',
    'ALTER TABLE trips ADD CONSTRAINT fk_trips_transportation FOREIGN KEY (transportation_id) REFERENCES transportations(transportation_id)');
CALL add_foreign_key_if_missing('trips', 'fk_trips_selected_hotel',
    'ALTER TABLE trips ADD CONSTRAINT fk_trips_selected_hotel FOREIGN KEY (selected_hotel_id) REFERENCES hotels(hotel_id)');

-- Backfill existing trips from their route so the new columns are populated.
UPDATE trips tr
JOIN routes r ON tr.route_id = r.route_id
SET tr.starting_city_id    = r.starting_city_id,
    tr.destination_city_id = r.destination_city_id,
    tr.transportation_id   = r.transportation_id,
    tr.transportation_cost = r.estimated_cost,
    tr.total_estimated_cost = COALESCE(tr.total_estimated_cost, r.estimated_cost),
    tr.budget_status = CASE
        WHEN tr.budget >= COALESCE(tr.total_estimated_cost, r.estimated_cost) THEN 'Within Budget'
        ELSE 'Over Budget'
    END
WHERE tr.starting_city_id IS NULL;

-- ============================================================
-- trip_attractions (many-to-many between trips and attractions)
-- ============================================================

CREATE TABLE IF NOT EXISTS trip_attractions (
    trip_id INT NOT NULL,
    attraction_id INT NOT NULL,
    PRIMARY KEY (trip_id, attraction_id),
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES attractions(attraction_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Cleanup helper procedures
-- ============================================================

DROP PROCEDURE IF EXISTS add_column_if_missing;
DROP PROCEDURE IF EXISTS add_index_if_missing;
DROP PROCEDURE IF EXISTS add_foreign_key_if_missing;
