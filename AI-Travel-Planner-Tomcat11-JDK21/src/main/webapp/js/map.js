(function () {

    "use strict";

    /*
     * TravelMate AI - interactive trip map.
     *
     * Uses Leaflet + OpenStreetMap. The page must provide:
     *   <div id="trip-map"></div>
     *   <script>window.tripMapConfig = {...}</script>
     *
     * Route note: the project stores no road/rail geometry, so the polyline
     * between the two cities is an APPROXIMATE visualisation and is labelled
     * as such. It is never presented as the real railway or road path.
     */

    var container = document.getElementById("trip-map");

    if (!container) {
        return;
    }

    var config = window.tripMapConfig;

    if (!config) {
        container.innerHTML = '<div class="map-fallback">Map configuration is missing.</div>';
        return;
    }

    if (typeof window.L === "undefined") {
        container.innerHTML =
            '<div class="map-fallback">The map library (Leaflet) could not be loaded. Check your internet connection.</div>';
        return;
    }

    var start = config.startingCity;
    var destination = config.destinationCity;
    var attractions = config.attractions || [];

    var hasStartCoords = start && start.lat != null && start.lng != null;
    var hasDestinationCoords = destination && destination.lat != null && destination.lng != null;

    if (!hasStartCoords || !hasDestinationCoords) {
        container.innerHTML = '<div class="map-fallback">Map coordinates are not available for this route.</div>';
        return;
    }

    var startLatLng = L.latLng(start.lat, start.lng);
    var destinationLatLng = L.latLng(destination.lat, destination.lng);

    var map = L.map(container, {
        scrollWheelZoom: false
    });

    L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 18,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    var bounds = L.latLngBounds([startLatLng, destinationLatLng]);

    L.marker(startLatLng, { title: start.name })
        .addTo(map)
        .bindPopup("<strong>" + escapeHtml(start.name) + "</strong><br>Starting city");

    L.marker(destinationLatLng, { title: destination.name })
        .addTo(map)
        .bindPopup("<strong>" + escapeHtml(destination.name) + "</strong><br>Destination city");

    attractions.forEach(function (attraction) {

        if (attraction.lat == null || attraction.lng == null) {
            return;
        }

        var fee = (attraction.fee == null ? 0 : attraction.fee) + " MMK";

        L.marker([attraction.lat, attraction.lng], { title: attraction.name })
            .addTo(map)
            .bindPopup("<strong>" + escapeHtml(attraction.name) + "</strong><br>Entrance fee: " + fee);
    });

    /*
     * Approximate route: no road/rail geometry is stored, so a gentle arc
     * between the two cities is drawn and clearly labelled as approximate.
     */
    var routePoints = buildApproximateRoute(startLatLng, destinationLatLng, 12, 0.18);

    var routeLine = L.polyline(routePoints, {
        color: "#2563eb",
        weight: 4,
        opacity: 0.85,
        dashArray: "6 6"
    }).addTo(map);

    var travelInfo = config.distanceKm + " km, approx. " + config.travelTimeHours + " hours";

    routeLine.bindPopup("<strong>Approximate route</strong><br>" + travelInfo +
        "<br><em>Not the actual road/rail path.</em>");

    if (config.approximateRouteLabel) {
        L.marker(routePoints[Math.floor(routePoints.length / 2)], {
            icon: L.divIcon({
                className: "route-label",
                html: "<div class=\"route-label-text\">Approximate route</div>",
                iconSize: [140, 28]
            })
        }).addTo(map);
    }

    bounds.extend(routePoints[Math.floor(routePoints.length / 2)]);

    map.fitBounds(bounds.pad(0.25), { animate: false });

    /*
     * Keep the map the right size when the layout resizes (the tiles are
     * loaded asynchronously, so we also refresh shortly after startup).
     */
    function resizeMap() {
        map.invalidateSize();
    }

    window.addEventListener("resize", resizeMap);

    setTimeout(resizeMap, 250);

    setTimeout(resizeMap, 1000);

    /*
     * Builds a curved polyline between two points. The offset creates a
     * "route-like" arc instead of a plain straight line; it is still only an
     * approximation and is labelled accordingly.
     */
    function buildApproximateRoute(startPoint, endPoint, segments, bend) {

        var mid = startPoint.add(endPoint).divideBy(2);

        var delta = L.latLng(endPoint.lat - startPoint.lat, endPoint.lng - startPoint.lng);

        var offset = L.latLng(-delta.lng * bend, delta.lat * bend);

        var control = L.latLng(mid.lat + offset.lat, mid.lng + offset.lng);

        var points = [];

        for (var index = 0; index <= segments; index++) {

            var t = index / segments;

            var x = Math.pow(1 - t, 2) * startPoint.lat + 2 * (1 - t) * t * control.lat + Math.pow(t, 2) * endPoint.lat;

            var y = Math.pow(1 - t, 2) * startPoint.lng + 2 * (1 - t) * t * control.lng + Math.pow(t, 2) * endPoint.lng;

            points.push(L.latLng(x, y));
        }

        return points;
    }

    function escapeHtml(value) {

        var element = document.createElement("div");

        element.textContent = value == null ? "" : String(value);

        return element.innerHTML;
    }

})();
