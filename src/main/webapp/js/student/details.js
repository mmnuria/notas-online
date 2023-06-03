$(document).ready(function() {
	fetchStudentSubjects();
	goToPreviousOrNextStudent();
});

function fetchStudentSubjects() {
	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);

	const rootUrl = `${window.location.origin}/notas-online`;
	const dni = urlParams.get('dni');

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
				if (i != subjects.length) {
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

function goToPreviousOrNextStudent() {
	// Retrieve the student DNI from the query parameter
	const urlParams = new URLSearchParams(window.location.search);
	const dni = urlParams.get('dni');

	const studentsParam = urlParams.get('students');
	students = JSON.parse(decodeURIComponent(studentsParam));

	// Retrieve the previous and next buttons
	const previousButton = document.getElementById('previous-button');
	const nextButton = document.getElementById('next-button');

	// Find the index of the current student in the currentStudents array
	const currentIndex = students.findIndex(student => student.alumno === dni);

	// Enable or disable the Previous and Next buttons based on the current student's index
	previousButton.disabled = currentIndex === 0;
	nextButton.disabled = currentIndex === students.length - 1;

	// Add click event listeners to the Previous and Next buttons
	previousButton.addEventListener('click', () => {
		const previousStudentDetailUrl = `${window.location.origin}/notas-online/Student/Details?dni=${students[currentIndex - 1].alumno}&students=${encodeURIComponent(JSON.stringify(students))}`;
		window.location.href = previousStudentDetailUrl;
	});

	nextButton.addEventListener('click', () => {
		const nextStudentDetailUrl = `${window.location.origin}/notas-online/Student/Details?dni=${students[currentIndex + 1].alumno}&students=${encodeURIComponent(JSON.stringify(students))}`;
		window.location.href = nextStudentDetailUrl;
	});
}