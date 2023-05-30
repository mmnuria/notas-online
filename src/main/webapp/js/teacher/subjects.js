$(document).ready(function() {
	fetchSubjects();
});

function fetchSubjects() {
	const rootUrl = `${window.location.origin}/notas-online`;

	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/Teacher/Subjects`)
		.then(response => response.json()) // Parse the response as JSON
		.then(subjects => {
			const subjectListElement = document.getElementById('subject-list');
			const subjectTabsElement = document.getElementById('subject-tabs');
			const subjectContentElement = document.getElementById('subject-content');

			// Clear any existing subjects and tabs
			subjectListElement.innerHTML = '';
			subjectTabsElement.innerHTML = '';
			subjectContentElement.innerHTML = '';

			// Iterate over the subjects and create a card for each subject
			subjects.forEach(subject => {
				const subjectCard = document.createElement('div');
				subjectCard.innerHTML = `
					<a href="#subject-${subject.acronimo}" class="subject-link" data-toggle="tab" data-subject="${subject.nombre}">${subject.nombre}</a>
				`;
				subjectListElement.appendChild(subjectCard);

				// Create the tab for the subject
				const tabLink = document.createElement('li');
				tabLink.classList.add('nav-item');
				tabLink.innerHTML = `
                    <a class="nav-link" id="${subject.acronimo}-tab" data-toggle="tab" href="#subject-${subject.acronimo}">${subject.acronimo}</a>
                `;
				subjectTabsElement.appendChild(tabLink);

				// Create the content for the subject tab
				const tabContent = document.createElement('div');
				tabContent.classList.add('tab-pane', 'fade');
				tabContent.id = `subject-${subject.acronimo}`;

				fetchStudentsData(subject)
					.then(studentsTableElement => {
						tabContent.appendChild(studentsTableElement);
					})
					.catch(error => {
						console.error('Error fetching student grades:', error);
					});

				subjectContentElement.appendChild(tabContent);
			});

			displayTabElements();
		})
		.catch(error => {
			console.error('Error fetching subjects:', error);
		});
}

async function fetchStudentsData(subject) {
	const rootUrl = `${window.location.origin}/notas-online`;

	const response = await fetch(`${rootUrl}/API/Subject/Students?acronimo=${subject.acronimo}`);
	const students = await response.json();
	const tableElement = document.createElement('table');
	// Create table header
	const tableHeaderRow = document.createElement('tr');
	const nameHeaderCell = document.createElement('th');
	nameHeaderCell.textContent = 'Student Name';
	tableHeaderRow.appendChild(nameHeaderCell);
	const gradeHeaderCell = document.createElement('th');
	gradeHeaderCell.textContent = 'Grade';
	tableHeaderRow.appendChild(gradeHeaderCell);
	tableElement.appendChild(tableHeaderRow);
	students.forEach(student => {
		const studentRow = document.createElement('tr');

		// Student name cell
		const nameCell = document.createElement('td');
		fetch(`${rootUrl}/API/UserDetails?dni=${student.alumno}`)
			.then(response_1 => response_1.json()) // Parse the response as JSON
			.then(user => {
				nameCell.textContent = `${user.nombre} ${user.apellidos}`;
			})
			.catch(error => {
				console.error('Error fetching student details:', error);
			});
		studentRow.appendChild(nameCell);

		// Grade cell
		const gradeCell = document.createElement('td');
		gradeCell.textContent = student.nota;
		studentRow.appendChild(gradeCell);

		tableElement.appendChild(studentRow);
	});
	return tableElement;
}
