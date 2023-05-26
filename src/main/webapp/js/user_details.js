function fetchContactDetails() {
	rootUrl = `${window.location.origin}/notas-online`;
	
	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/Person-details`)
		.then(response => response.json()) // Parse the response as JSON
		.then(contact => {
			const contactNameElement = document.getElementById('contact-name');
			contactNameElement.innerHTML = `${contact.nombre} ${contact.apellidos}`;
		})
		.catch(error => {
			console.error('Error fetching contact details:', error);
		});
}

fetchContactDetails();
