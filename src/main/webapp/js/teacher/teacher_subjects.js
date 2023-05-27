function fetchSubjects() {
	rootUrl = `${window.location.origin}/notas-online`;
	
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
				subjectContentElement.appendChild(tabContent);
			});

			// Add click event listeners to the subject links
			const subjectLinks = document.querySelectorAll('.subject-link');
			subjectLinks.forEach(subjectLink => {
				subjectLink.addEventListener('click', function(event) {
					event.preventDefault(); // Prevent the default link behavior
					const subjectName = this.getAttribute('data-subject');
					const subjectAcronym = this.getAttribute('href').substring(1); // Remove the leading #

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
					console.log('Clicked subject:', subjectName);
				});
			});
		})
		.catch(error => {
			console.error('Error fetching subjects:', error);
		});
}

fetchSubjects();
