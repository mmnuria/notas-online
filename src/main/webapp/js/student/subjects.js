$(document).ready(function() {
	fetchSubjects();
	sendCertificationData();
});

function fetchSubjects() {
	const rootUrl = `${window.location.origin}/notas-online`;

	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/Student/Subjects`)
		.then(response => response.json()) // Parse the response as JSON
		.then(subjects => {
			// Call the common function with the required arguments
			fetchSubjectsCommon(rootUrl, subjects);
		})
		.catch(error => {
			console.error('Error fetching subjects:', error);
		});
}

function sendCertificationData() {
	$(document).on('click', '#show-certificate-button', function() {
		const subjects = [];

		// Iterate over the subject tabs and gather information
		$("#subject-tabs .nav-link").each(function() {
			const subjectAcronym = $(this).text().trim();
			const subjectName = $(`#subject-list a[data-subject="${subjectAcronym}"]`).text().trim();
			const grade = $(`#subject-${subjectAcronym} p`).text().trim().split(":")[1].trim();

			subjects.push({
				subjectAcronym: subjectAcronym,
				subjectName: subjectName,
				grade: grade
			});
		});

		// Create the request URL with query parameters
		const url = "../Student/Certification?subjects=" + encodeURIComponent(JSON.stringify(subjects));

		// Send the GET request to the servlet using window.location.href
		window.location.href = url;
	});
}
