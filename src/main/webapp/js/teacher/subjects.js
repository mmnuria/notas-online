$(document).ready(function() {
	fetchSubjects();
});

function fetchSubjects() {
    const rootUrl = `${window.location.origin}/notas-online`;

    // Make an AJAX request to the servlet endpoint
    fetch(`${rootUrl}/API/Teacher/Subjects`)
        .then(response => response.json()) // Parse the response as JSON
        .then(subjects => {
            // Call the common function with the required arguments
            fetchSubjectsCommon(rootUrl, subjects);
        })
        .catch(error => {
            console.error('Error fetching subjects:', error);
        });
}
