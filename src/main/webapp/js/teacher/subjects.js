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

						// Create a container div for the table and button
						const containerDiv = document.createElement('div');
						containerDiv.classList.add('table-container');

						// Add the Edit grades button to the container div
						const editButton = document.createElement('button');
						editButton.classList.add('btn', 'btn-secondary', 'edit-button');
						editButton.textContent = 'Edit grades';
						editButton.addEventListener('click', enableGradeEditing);
						containerDiv.appendChild(editButton);

						// Append the container div below the table
						tabContent.appendChild(containerDiv);
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
	tableElement.classList.add('table', 'table-bordered', 'table-striped', 'table-hover'); // Add table classes from the CSS stylesheet

	// Create table header
	const tableHeaderRow = document.createElement('tr');
	const nameHeaderCell = document.createElement('th');
	nameHeaderCell.textContent = 'Student Name';
	nameHeaderCell.style.width = '60%'; // Allocate more width for the student name column
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
			.then(response_1 => response_1.json())
			.then(user => {
				nameCell.textContent = `${user.nombre} ${user.apellidos}`;
			})
			.catch(error => {
				console.error('Error fetching student details:', error);
			});
		studentRow.appendChild(nameCell);

		// Grade cell
		const gradeCell = document.createElement('td');
		gradeCell.classList.add('grade-cell');
		gradeCell.textContent = student.nota;
		gradeCell.dataset.dni = student.alumno; // Set the student's dni as a data attribute
		gradeCell.dataset.acronimo = subject.acronimo;
		studentRow.appendChild(gradeCell);

		tableElement.appendChild(studentRow);
	});

	// Add a row for mean calculation
	const meanRow = document.createElement('tr');
	meanRow.classList.add('mean-row'); // Add the class for the mean row

	const meanNameCell = document.createElement('td');
	meanNameCell.textContent = 'Mean';
	meanNameCell.classList.add('mean-cell'); // Add the class for the mean cell
	meanRow.appendChild(meanNameCell);

	const meanGradeCell = document.createElement('td');
	meanGradeCell.classList.add('mean-cell'); // Add the class for the mean cell
	meanGradeCell.textContent = calculateMean(students); // Call the function to calculate the mean
	meanRow.appendChild(meanGradeCell);

	tableElement.appendChild(meanRow);

	return tableElement;
}

function calculateMean(students) {
	const grades = students.map(student => parseFloat(student.nota));
	const sum = grades.reduce((total, grade) => total + grade, 0);
	const mean = sum / grades.length;
	return mean.toFixed(2);
}
