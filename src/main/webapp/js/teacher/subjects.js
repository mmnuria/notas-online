$(document).ready(function() {
	fetchSubjects();
});

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

			displayTabElements();
		})
		.catch(error => {
			console.error('Error fetching subjects:', error);
		});
}