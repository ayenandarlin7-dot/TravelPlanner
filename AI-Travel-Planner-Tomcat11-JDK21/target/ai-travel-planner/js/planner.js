(function () {

    "use strict";

    var tripForm = document.getElementById("tripForm");

    if (!tripForm) {
        return;
    }

    var startingCity = document.getElementById("startingCityId");
    var destinationCity = document.getElementById("destinationCityId");
    var travelDate = document.getElementById("travelDate");
    var returnDate = document.getElementById("returnDate");
    var budget = document.getElementById("budget");
    var travellers = document.getElementById("travellers");
    var message = document.getElementById("tripValidationMessage");

    function showError(text) {
        message.textContent = text;
        message.style.display = "block";
    }

    function clearError() {
        if (message) {
            message.textContent = "";
            message.style.display = "none";
        }
    }

    function localDateString(date) {
        var year = date.getFullYear();
        var month = String(date.getMonth() + 1).padStart(2, "0");
        var day = String(date.getDate()).padStart(2, "0");
        return year + "-" + month + "-" + day;
    }

    var today = localDateString(new Date());

    if (travelDate) {
        travelDate.min = today;
    }

    if (travelDate && returnDate) {
        travelDate.addEventListener("change", function () {
            if (travelDate.value) {
                returnDate.min = travelDate.value;
            }
        });
    }

    if (travelDate) {
        travelDate.addEventListener("input", clearError);
    }
    if (returnDate) {
        returnDate.addEventListener("input", clearError);
    }
    if (budget) {
        budget.addEventListener("input", clearError);
    }
    if (travellers) {
        travellers.addEventListener("input", clearError);
    }
    if (startingCity) {
        startingCity.addEventListener("change", clearError);
    }
    if (destinationCity) {
        destinationCity.addEventListener("change", clearError);
    }

    tripForm.addEventListener("submit", function (event) {

        clearError();

        var startingValue = startingCity ? startingCity.value : "";
        var destinationValue = destinationCity ? destinationCity.value : "";

        if (!startingValue) {
            event.preventDefault();
            showError("Please select a starting city.");
            return;
        }

        if (!destinationValue) {
            event.preventDefault();
            showError("Please select a destination city.");
            return;
        }

        if (startingValue === destinationValue) {
            event.preventDefault();
            showError("Starting City and Destination cannot be the same.");
            return;
        }

        if (!travelDate || !travelDate.value) {
            event.preventDefault();
            showError("Please select a departure date.");
            return;
        }

        if (travelDate.value < today) {
            event.preventDefault();
            showError("Departure date cannot be in the past.");
            return;
        }

        if (returnDate && returnDate.value && returnDate.value < travelDate.value) {
            event.preventDefault();
            showError("Return date cannot be earlier than departure date.");
            return;
        }

        var budgetValue = budget ? budget.value : "";

        if (budgetValue === "" || isNaN(Number(budgetValue)) || Number(budgetValue) <= 0) {
            event.preventDefault();
            showError("Budget must be greater than zero.");
            return;
        }

        var travellersValue = travellers ? travellers.value : "";

        if (travellersValue === "" || isNaN(Number(travellersValue)) || Number(travellersValue) < 1) {
            event.preventDefault();
            showError("Number of travellers must be at least one.");
            return;
        }
    });

})();
