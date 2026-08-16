# AI Travel Planner — Final Project Report

**Project:** TravelMate AI — an intelligent trip planning web application.
**Stack:** Jakarta Servlet 6.1 (Tomcat 11) · JDK 21 · Maven WAR · MySQL 8 · HTML/CSS/JS (Vanilla)
**Context path:** `/ai-travel-planner`
**Build artifact:** `target/ai-travel-planner.war` (builds with `mvn clean package`)

---

## 1. Overview

TravelMate AI lets a logged-in user plan a multi-city Myanmar trip from a city, on given
dates, for a number of travellers and a budget. The system automatically picks a
transportation route, hotels, food tiers and attractions that fit the budget, shows an
itemised cost breakdown, an approximate-route map, and lets the user save trips, review
them in their history, and view or delete them later — with per-user ownership enforced
on every screen.

## 2. Features

- **Registration / Login / Logout** with SHA-256 password hashing (never plain text),
  session-based auth, and an `AuthenticationFilter` guarding all protected pages.
- **Trip planning wizard** (`/dashboard`): starting city, destination, travel/return
  dates, travellers, budget, transport preference and cost preference
  (Cheapest / Fastest / Shortest / Balanced).
- **Route engine** (`/route`): real DB routes with per-person prices for Bus / Train /
  Flight; "Auto" compares all three.
- **Smart budget allocation**: transportation first, food at 25% of the remainder
  (Economy / Standard / Premium), then hotels (preference-weighted affordable
  categories), then attractions (free/low-cost first). Over-budget plans get
  **suggestions** (cheaper food tier, cheaper hotel, swap transport, remove an
  attraction) with projected savings.
- **Cost breakdown page** (`recommendation.jsp`): itemised transport / hotel / food /
  attraction costs, total, remaining budget, and budget status
  (Within / Nearly Over / Over Budget).
- **Trip summary** (`tripSummary.jsp`): route info, itinerary dates, selected hotel and
  attractions with details.
- **Approximate-route map** (`js/map.js`): draws a polyline between the cities on a
  static SkyBlue-style map (no external API key required).
- **Save trips** (`/save-trip`): persists the full plan including all costs.
- **Trip history** (`/trip-history`): per-user list with dates, budget, cost and status,
  plus **View Details** and **Delete**.
- **Trip details** (`/trip-details`): full saved-plan view with an itemised cost grid,
  the map, and delete/back actions. Ownership is verified server-side (404 if the trip
  belongs to another user).
- **Profile** (`/profile`) and **Dashboard stats** (trips planned / saved / total spent).
- **Responsive design**: tablet (992px), mobile (768px) and small phone (480px)
  breakpoints. Theme: deep teal `#0f667c`, amber `#f5a623`, sky blue `#38bdf8`.

## 3. Project structure

```
src/main/java/com/travelplanner/
  controller/  DashboardServlet(/dashboard)  DeleteTripServlet(/delete-trip)
               HistoryServlet(/trip-history) LoginServlet(/login)
               LogoutServlet(/logout)        ProfileServlet(/profile)
               RegisterServlet(/register)    RouteServlet(/route)
               SaveTripServlet(/save-trip)   TripDetailsServlet(/trip-details)
               TripServlet(/trip)
  dao/         UserDAO CityDAO RouteDAO HotelDAO FoodEstimateDAO AttractionDAO TripDAO
  filter/      AuthenticationFilter  (guards /dashboard /route /trip /trip-history
                /trip-details /save-trip /delete-trip /profile)
  model/       User City Transportation Route Hotel FoodEstimate Attraction
               TripPlan Trip BudgetSuggestion
  service/     TripPlanService RecommendationService BudgetSuggestionService TripService
  util/        PasswordUtil EscapeUtil
  db/          DbConnector (DataSource, MySQL)

src/main/webapp/
  index.jsp (public landing)   login.jsp   register.jsp   dashboard.jsp
  recommendation.jsp  tripSummary.jsp  trip-history.jsp  tripDetails.jsp
  profile.jsp  error.jsp  WEB-INF/web.xml
  css/ style.css auth.css landing.css dashboard.css responsive.css
  js/  script.js planner.js map.js
  images/ hero + destination SVGs
```

## 4. Database (MySQL `travel_planner`)

| Table | Rows | Notes |
|---|---|---|
| `users` | 17 | id, full_name, email, password_hash (SHA-256 hex) |
| `cities` | 8 | Yangon, Mandalay, Bagan, Inle Lake, ... |
| `transportations` | 4 | Bus, Train, Flight, Auto |
| `routes` | 36 | from/to city, transport, estimated_cost per person |
| `hotels` | 24 | category, price_per_night, room_capacity, rating |
| `food_estimates` | 24 | per city × tier (Economy/Standard/Premium), daily cost per person |
| `attractions` | 27 | entrance fee, description, image |
| `trips` | 4 | per-user saved plans with all costs + status |
| `trip_attractions` | 0 | junction for saved trip attractions |

Connection: localhost:3306, database `travel_planner`, user `root`, password `travel@123`
(configurable in `db/DbConnector.java`).

## 5. Test accounts

| Account | Email | Password |
|---|---|---|
| Demo User | `demo@travelmate.app` | `password123` |
| Existing user (has trip) | `htarhtar13@gmail.com` | any registered password |

Registration now works end-to-end, so any new account created in the UI is usable.

## 6. Cost rules (as implemented)

- Trip Days = return − departure + 1; Hotel Nights = return − departure.
- Rooms = ceil(travellers / room capacity).
- Transport = price-per-person × travellers.
- Hotel = price-per-night × nights × rooms.
- Food = daily-cost-per-person × days × travellers.
- Attractions = Σ entrance fees × travellers.
- Total = transport + hotel + food + attractions.
- Status: Within Budget (≤ budget) / Nearly Over Budget (≤ budget × 1.10) / Over Budget.

**Verified end-to-end** (route 21 Yangon→Inle Lake, Bus 52,000; 2026-09-01 → 09-04;
2 travellers; budget 500,000; cheapest):

> Days 4, Nights 3, Rooms 1 · Transport 104,000 · Hotel (id 19, 35,000 × 3) 105,000 ·
> Food Economy (7,000 × 4 × 2) 56,000 · Attractions 30,000 · **Total 295,000** ·
> Remaining 205,000 · **Within Budget** ✓

## 7. Phase 7 — fixes applied during final review

1. **Critical registration bug**: `register.jsp` sent `name="name"` while
   `RegisterServlet` reads `fullName` — every registration failed. Fixed the field name
   and wired the existing client-side `validateRegistrationForm()` (validation message +
   `onsubmit` + context-path script include).
2. **Context paths**: `index.jsp`, `login.jsp`, `register.jsp` now load CSS/JS/images via
   `request.getContextPath()` so the app works under any deployment name.
3. **XSS hardening**: new `util/EscapeUtil.escapeHtml()` applied to every user-influenced
   output — login page `savedEmail` cookie and error/success messages, dashboard
   `errorMessage` and `selected*` field values, and the register error message.
4. **Friendly error page**: rewrote `error.jsp` (themed, no internals) and added
   `WEB-INF/web.xml` with 404 / 500 / `java.lang.Throwable` mappings so stack traces are
   never shown to users.
5. **Ownership & deletion** (from Phase 6, re-verified): `TripDetailsServlet` 404s for
   non-owners via a single `WHERE trip_id = ? AND user_id = ?` query; `DeleteTripServlet`
   honours the delete result; history only ever lists the current user's trips.

## 8. Verification performed

- **Compilation**: all 40 Java files compile clean (JDK 21 target) — javac and Maven.
- **Maven build**: `mvn clean package` produces `target/ai-travel-planner.war` (~4.3 MB)
  containing `WEB-INF/web.xml`, compiled `EscapeUtil`, and all corrected JSPs.
- **Calculation test**: programmatic run of `TripPlanService` matched every expected
  figure above exactly.
- **Ownership tests (DB)**: user A sees only their trips; user B sees none; B is blocked
  from viewing and deleting A's trips; A's delete succeeds. All checks passed.
- **Form → servlet mappings**: verified `/login`, `/register`, `/route`, `/trip`,
  `/save-trip`, `/delete-trip` targets are correct.
- **Auth review**: no user-supplied `userId` anywhere; every DAO uses prepared
  statements; the public home page does not auto-redirect to login.

## 9. Run instructions

1. **MySQL**: start the server; ensure database `travel_planner` exists with the schema
   and seed data (see Section 4). Adjust credentials in `DbConnector` if different.
2. **Build** (Java 21+): `mvn clean package` → `target/ai-travel-planner.war`.
3. **Deploy**: copy the WAR into `apache-tomcat-11.x/webapps/` and start Tomcat, or in
   Eclipse: Import → Existing Maven Project → Run on Server (Tomcat 11).
4. **Open**: `http://localhost:8080/ai-travel-planner/`
5. Register an account (or use the Demo User) and plan a trip.

## 10. Known limitations

- Passwords use unsalted SHA-256 (better than plain text, but not bcrypt/Argon2).
- No CSRF token on POST forms; no rate limiting on login.
- The map is a static approximate-route sketch (intentional — no map API key).
- Trips are per-user; there is no admin console.
- Demo/test users were created during development and remain in the database.
