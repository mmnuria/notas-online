function fetchSubjectsCommon(rootUrl, subjects) {
	// Fetch the list of all subjects
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
				var matchedSubject = null;
				// Find the subject with matching acronimo
				if (subject.asignatura != undefined) {
					matchedSubject = allSubjects.find(item => item.acronimo === subject.asignatura);
				} else {
					matchedSubject = allSubjects.find(item => item.acronimo === subject.acronimo);
				}

				if (matchedSubject) {
					const subjectCard = document.createElement('div');
					subjectCard.innerHTML = `
                				<a href="#subject-${matchedSubject.acronimo}" class="subject-link" data-toggle="tab" data-subject="${subject.asignatura}">${matchedSubject.nombre}</a>
              				`;
					subjectListElement.appendChild(subjectCard);

					// Create the tab for the subject
					const tabLink = document.createElement('li');
					tabLink.classList.add('nav-item');
					tabLink.innerHTML = `
                				<a class="nav-link" id="${matchedSubject.acronimo}-tab" data-toggle="tab" href="#subject-${matchedSubject.acronimo}">${matchedSubject.acronimo}</a>
              				`;
					subjectTabsElement.appendChild(tabLink);

					// Create the content for the subject tab
					const tabContent = document.createElement('div');
					tabContent.classList.add('tab-pane', 'fade');
					tabContent.id = `subject-${matchedSubject.acronimo}`;

					// Retrieve and display the grade
					if (subject.nota != undefined) {
						const gradeElement = document.createElement('p');
						gradeElement.innerText = `Nota: ${subject.nota}`;
						tabContent.appendChild(gradeElement);
					}

					subjectContentElement.appendChild(tabContent);
				}
			});

			// Add click event listeners to the subject links
			// const subjectLinks = Array.from(document.getElementsByClassName('subject-link'));
			const subjectLinks = document.querySelectorAll('.subject-link');
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
				});
			});
		})
		.catch(error => {
			console.error('Error fetching all subjects:', error);
		});
}
