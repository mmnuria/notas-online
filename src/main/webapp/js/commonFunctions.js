function displayTabElements() {
	// Add click event listeners to the subject links
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
}
