$(document).ready(function() {
	fetchSubjects();
	sendReport();
});

function fetchSubjects() {
	const rootUrl = `${window.location.origin}/notas-online`;

	// Make an AJAX request to the servlet endpoint
	fetch(`${rootUrl}/API/Student/Subjects`)
		.then(response => response.json()) // Parse the response as JSON
		.then(subjects => {
			// Fetch the list of all subjects as JSON from the new servlet call
			fetch(`${rootUrl}/API/Subjects`)
				.then(response => response.json())
				.then(allSubjects => {
					const subjectListElement = document.getElementById('subject-list');
					const subjectTabsElement = document.getElementById('subject-tabs');
					const subjectContentElement = document.getElementById('subject-content');

					// Clear any existing subjects and tabs
					subjectListElement.innerHTML = '';
					subjectTabsElement.innerHTML = '';
					subjectContentElement.innerHTML = '';

					// Iterate over the subjects and create a card for each subject
					subjects.forEach(subject => {
						// Find the subject with matching acronimo
						const matchedSubject = allSubjects.find(item => item.acronimo === subject.asignatura);

						if (matchedSubject) {
							const subjectCard = document.createElement('div');
							subjectCard.innerHTML = `
                				<a href="#subject-${subject.asignatura}" class="subject-link" data-toggle="tab" data-subject="${subject.asignatura}">${matchedSubject.nombre}</a>
              				`;
							subjectListElement.appendChild(subjectCard);

							// Create the tab for the subject
							const tabLink = document.createElement('li');
							tabLink.classList.add('nav-item');
							tabLink.innerHTML = `
                				<a class="nav-link" id="${subject.asignatura}-tab" data-toggle="tab" href="#subject-${subject.asignatura}">${subject.asignatura}</a>
              				`;
							subjectTabsElement.appendChild(tabLink);

							// Create the content for the subject tab
							const tabContent = document.createElement('div');
							tabContent.classList.add('tab-pane', 'fade');
							tabContent.id = `subject-${subject.asignatura}`;

							// Retrieve and display the grade
							const gradeElement = document.createElement('p');
							gradeElement.innerText = `Nota: ${subject.nota}`;
							tabContent.appendChild(gradeElement);

							subjectContentElement.appendChild(tabContent);
						}
					});

					// Add click event listeners to the subject links
					const subjectLinks = Array.from(document.getElementsByClassName('subject-link'));
					subjectLinks.forEach(subjectLink => {
						subjectLink.addEventListener('click', function(event) {
							const subjectName = subjectLink.getAttribute('data-subject');
							const subjectAcronym = subjectLink.getAttribute('href').substring(1); // Remove the leading #

							// Show the tab for the selected subject
							const tabElement = document.getElementById(subjectAcronym);
							const tabContainerElement = document.querySelector('.tab-content');
							const tabPaneElements = tabContainerElement.querySelectorAll('.tab-pane');

							// Show the selected tab and hide other tabs
							tabPaneElements.forEach(tabPane => {
								if (tabPane.id === subjectAcronym) {
									tabPane.classList.add('active', 'show');
								} else {
									tabPane.classList.remove('active', 'show');
								}
							});

							// Activate the selected tab navigation link
							const tabNavLinks = document.querySelectorAll('.nav-link');
							tabNavLinks.forEach(tabNavLink => {
								if (tabNavLink.getAttribute('href') === `#${subjectAcronym}`) {
									tabNavLink.classList.add('active');
								} else {
									tabNavLink.classList.remove('active');
								}
							});

							// TODO: Add code to display other information for the selected subject
						});
					});
				})
				.catch(error => {
					console.error('Error fetching all subjects:', error);
				});
		})
		.catch(error => {
			console.error('Error fetching subjects:', error);
		});
}

function sendReport() {
	$(document).on('click', '#show-report-button', function() {
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
		const url = "../Student/Report?subjects=" + encodeURIComponent(JSON.stringify(subjects));

		// Send the GET request to the servlet using window.location.href
		window.location.href = url;
	});
}
