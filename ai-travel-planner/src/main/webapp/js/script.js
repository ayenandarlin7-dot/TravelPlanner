function validateRegistrationForm() {

    var passwordElement = document.getElementById("password");
    var confirmPasswordElement = document.getElementById("confirmPassword");
    var validationMessage = document.getElementById("validationMessage");

    var password = "";
    var confirmPassword = "";

    if (passwordElement) {
        password = passwordElement.value;
    }

    if (confirmPasswordElement) {
        confirmPassword = confirmPasswordElement.value;
    }

    if (validationMessage) {
        validationMessage.textContent = "";
    }

    if (password.length < 6) {

        if (validationMessage) {
            validationMessage.textContent =
                "Password must contain at least 6 characters.";
        }

        return false;
    }

    if (password !== confirmPassword) {

        if (validationMessage) {
            validationMessage.textContent =
                "Passwords do not match.";
        }

        return false;
    }

    return true;
}


/* ================================
   TRIP FORM VALIDATION
================================ */

var tripForm = document.getElementById("tripForm");

if (tripForm) {

    var start = document.getElementById("startingCityId");
    var destination = document.getElementById("destinationCityId");
    var date = document.getElementById("travelDate");
    var budget = document.getElementById("budget");
    var message = document.getElementById("tripValidationMessage");


    /* ================================
       Prevent same city selection
    ================================= */

    function refreshDestinationOptions() {

        if (!start || !destination) {
            return;
        }

        var options = destination.options;

        for (var i = 0; i < options.length; i++) {

            var option = options[i];

            if (option.value !== "" &&
                option.value === start.value) {

                option.disabled = true;

            } else {

                option.disabled = false;
            }
        }

        if (destination.value === start.value) {
            destination.value = "";
        }
    }


    if (start) {
        start.addEventListener("change", refreshDestinationOptions);
    }

    refreshDestinationOptions();


    /* ================================
       Set minimum travel date
    ================================= */

    if (date) {

        var today = new Date();

        var yyyy = today.getFullYear();

        var mm = String(today.getMonth() + 1);

        if (mm.length < 2) {
            mm = "0" + mm;
        }

        var dd = String(today.getDate());

        if (dd.length < 2) {
            dd = "0" + dd;
        }

        date.min = yyyy + "-" + mm + "-" + dd;
    }


    /* ================================
       Trip form submit validation
    ================================= */

    tripForm.addEventListener("submit", function(event) {

        if (message) {
            message.textContent = "";
        }


        /* Required fields */

        if (
            !start ||
            !destination ||
            !date ||
            !budget ||
            !start.value ||
            !destination.value ||
            !date.value ||
            !budget.value
        ) {

            event.preventDefault();

            if (message) {
                message.textContent =
                    "Please complete the basic trip information.";
            }

            return;
        }


        /* Same city check */

        if (start.value === destination.value) {

            event.preventDefault();

            if (message) {
                message.textContent =
                    "Starting city and destination must be different.";
            }

            return;
        }


        /* Travel date check */

        var selectedDate =
            new Date(date.value + "T00:00:00");

        var currentDate = new Date();

        currentDate.setHours(0, 0, 0, 0);


        if (selectedDate < currentDate) {

            event.preventDefault();

            if (message) {
                message.textContent =
                    "Travel date cannot be in the past.";
            }

            return;
        }


        /* Budget check */

        if (Number(budget.value) < 0) {

            event.preventDefault();

            if (message) {
                message.textContent =
                    "Budget cannot be negative.";
            }

            return;
        }

    });
}