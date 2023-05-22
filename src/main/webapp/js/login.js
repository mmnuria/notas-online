document.getElementById("login-form").addEventListener("submit", function (event) {
    event.preventDefault(); // Prevent the form from being submitted

    var form = event.target;
    var dni = form.elements["dni"].value;
    var password = form.elements["password"].value;

    // Perform any desired processing on the form data
    // For example, you might store it in a variable or pass it to other functions

    // Proceed with submitting the form
    form.submit();
});