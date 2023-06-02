$(document).ready(function() {
	fetchSubjects();
});

function fetchSubjects() {
	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);

	const rootUrl = `${window.location.origin}/notas-online`;
	const dni = urlParams.get('dni');
	
	fetch(`${rootUrl}/API/UserDetails?dni=${dni}`)
		.then(response_1 => response_1.json())
		.then(user => {
			const studentNameElement = document.getElementById('student-name');
			if (studentNameElement != null) {
				studentNameElement.innerHTML = `${user.apellidos}, ${user.nombre}`;
			}

			const studentDniElement = document.getElementById('student-dni');
			if (studentDniElement != null) {
				studentDniElement.innerHTML = `${user.dni}`;
			}
		})
		.catch(error => {
			console.error('Error fetching student details:', error);
		});

	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/Student/Subjects?dni=${dni}`)
		.then(response => response.json()) // Parse the response as JSON
		.then(subjects => {
			
			const subjectsSpanElement = document.getElementById('subjects');

			// Clear any existing subjects
			subjectsSpanElement.innerHTML = '';
			var subjectsString = '';
			var i = 1;

			// Iterate over the subjects and create a card for each subject
			subjects.forEach(subject => {
				subjectsString += subject.asignatura;
				if(i != subjects.length){
					subjectsString += ", ";
					i++;
				}
			});
			
			subjectsSpanElement.innerHTML = subjectsString;
		})
		.catch(error => {
			console.error('Error fetching subjects:', error);
		});
}