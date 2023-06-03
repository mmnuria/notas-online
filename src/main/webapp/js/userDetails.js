$(document).ready(function() {
	fetchUserDetails();
});

function fetchUserDetails() {
	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);

	const rootUrl = `${window.location.origin}/notas-online`;
	const dni = urlParams.get('dni');

	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/UserDetails${dni ? `?dni=${dni}` : ''}`)
		.then(response => response.json())
		.then(user => {
			const userNameElement = document.getElementById('user-name');
			if (userNameElement != null) {
				userNameElement.innerHTML = `${user.nombre} ${user.apellidos}`;
			}

			const userDniElement = document.getElementById('user-dni');
			if (userDniElement != null) {
				userDniElement.innerHTML = `${user.dni}`;

				// For pages that require the student's image
				if (document.getElementById('student-image')) {
					fetchStudentImage(user.dni, 'student-image');
				}
			}
		})
		.catch(error => {
			console.error('Error fetching user details:', error);
		});
}
