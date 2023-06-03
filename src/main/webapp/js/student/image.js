function fetchStudentImage(dni, elementId) {
	const rootUrl = `${window.location.origin}/notas-online`;
	
	fetch(`${rootUrl}/Student/Images?dni=${dni}`)
		.then(response => {
			if (response.ok) {
				return response.json();
			} else {
				throw new Error('Image request failed');
			}
		})
		.then(data => {
			document.getElementById(elementId).src = "data:image/png;base64," + data.img;
		})
		.catch(error => {
			console.error(error);
		});
}
