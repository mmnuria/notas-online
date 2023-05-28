function fetchContactDetails() {
	rootUrl = `${window.location.origin}/notas-online`;
	
	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/User-Details`)
		.then(response => response.json()) // Parse the response as JSON
		.then(contact => {
			const contactNameElement = document.getElementById('contact-name');
			contactNameElement.innerHTML = `${contact.nombre} ${contact.apellidos}`;
			
			const contactDniElement = document.getElementById('contact-dni');
			contactDniElement.innerHTML = `${contact.dni}`;
		})
		
		.catch(error => {
			console.error('Error fetching contact details:', error);
		});
}

fetchContactDetails();
