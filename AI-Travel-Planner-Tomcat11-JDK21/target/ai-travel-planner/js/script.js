// Destination image fallback: when an SVG is missing,
// hide the image so the card's gradient background shows.
document.querySelectorAll(".destination-image img").forEach(function(img) {
    if (!img.complete) {
        img.addEventListener("error", function() {
            var container = img.closest(".destination-image");
            if (container) {
                container.classList.add("img-missing");
            }
            img.style.display = "none";
        });
    }
});

function validateRegistrationForm() {

    const password =
        document.getElementById("password").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;

    const validationMessage =
        document.getElementById("validationMessage");

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

const tripForm =
    document.getElementById("tripForm");

if (tripForm) {

    tripForm.addEventListener(
        "submit",
        function(event) {

            const startingCity =
                document.getElementById(
                    "startingCityId"
                ).value;

            const destination =
                document.getElementById(
                    "destinationCityId"
                ).value;

            const travelDate =
                document.getElementById(
                    "travelDate"
                ).value;

            const message =
                document.getElementById(
                    "tripValidationMessage"
                );

            if (message) {
                message.textContent = "";
            }

            if (startingCity === destination) {

                event.preventDefault();

                if (message) {
                    message.textContent =
                        "Starting city and destination must be different.";
                }

                return;
            }

            const selectedDate =
                new Date(travelDate);

            const today =
                new Date();

            today.setHours(0, 0, 0, 0);

            if (selectedDate < today) {

                event.preventDefault();

                if (message) {
                    message.textContent =
                        "Travel date cannot be in the past.";
                }
            }

        }
    );

}