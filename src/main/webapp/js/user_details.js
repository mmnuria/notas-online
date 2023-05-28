function fetchUserDetails() {
	rootUrl = `${window.location.origin}/notas-online`;

	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/User-Details`)
		.then(response => response.json()) // Parse the response as JSON
		.then(user => {
			const userNameElement = document.getElementById('user-name');
			if (userNameElement != null) {
				userNameElement.innerHTML = `${user.nombre} ${user.apellidos}`;
			}

			const userDniElement = document.getElementById('user-dni');
			if (userDniElement != null) {
				userDniElement.innerHTML = `${user.dni}`;
			}
		})

		.catch(error => {
			console.error('Error fetching user details:', error);
		});
}

fetchUserDetails();
