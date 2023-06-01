function enableGradeEditing(event) {
	const editButton = event.target; // Get the clicked Edit button
	const cancelBtn = document.createElement('button');
	cancelBtn.classList.add('btn', 'btn-danger', 'cancel-button'); // Add the "cancel-button" class
	cancelBtn.textContent = 'Cancel';
	cancelBtn.addEventListener('click', cancelGradeEditing);

	// Insert a space element
	const spaceElement = document.createTextNode(' ');

	// Insert the Cancel button and space after the Edit button
	editButton.parentNode.insertBefore(cancelBtn, editButton.nextSibling);
	editButton.parentNode.insertBefore(spaceElement, editButton.nextSibling);

	const currentTab = editButton.closest('.tab-pane'); // Get the parent tab of the clicked Edit button
	const gradeCells = currentTab.getElementsByClassName('grade-cell');

	for (let i = 0; i < gradeCells.length; i++) {
		const cell = gradeCells[i];
		const gradeValue = cell.textContent;
		const inputElement = document.createElement('input');
		inputElement.type = 'number';
		inputElement.step = '1';
		inputElement.min = 0;
		inputElement.max = 100;
		inputElement.classList.add('form-control');
		inputElement.value = gradeValue;
		inputElement.dataset.originalValue = gradeValue; // Save original value
		cell.textContent = '';
		cell.appendChild(inputElement);
	}

	editButton.textContent = 'Confirm edit';
	editButton.classList.remove('btn-secondary');
	editButton.classList.add('btn-success');
	editButton.removeEventListener('click', enableGradeEditing);
	editButton.addEventListener('click', updateGrades);
}

function cancelGradeEditing(event) {
	const cancelBtn = event.target; // Get the clicked Cancel button
	const editButton = cancelBtn.previousSibling; // Get the associated Edit button

	const currentTab = editButton.closest('.tab-pane'); // Get the parent tab of the associated Edit button
	const gradeCells = currentTab.getElementsByClassName('grade-cell');

	cancelBtn.parentNode.removeChild(cancelBtn);

	for (let i = 0; i < gradeCells.length; i++) {
		const cell = gradeCells[i];
		const inputElement = cell.querySelector('input');
		const originalValue = inputElement.dataset.originalValue;
		inputElement.value = originalValue; // Set input value to original value
		cell.textContent = originalValue; // Set cell text content to original value
	}

	editButton.textContent = 'Edit grades';
	editButton.classList.remove('btn-success');
	editButton.classList.add('btn-secondary');
	editButton.removeEventListener('click', updateGrades);
	editButton.addEventListener('click', enableGradeEditing);
}

function updateGrades() {
	const editButton = this;
	const currentTab = editButton.closest('.tab-pane');
	const gradeCells = currentTab.getElementsByClassName('grade-cell');
	const invalidValues = [];
	const updatePromises = [];

	// Find the cancel button within the current tab
	const cancelBtn = currentTab.querySelector('.cancel-button');

	for (let i = 0; i < gradeCells.length; i++) {
		const cell = gradeCells[i];
		const inputElement = cell.querySelector('input');
		const gradeValue = parseFloat(inputElement.value.trim());

		if (isNaN(gradeValue) || gradeValue < 0 || gradeValue > 100) {
			cell.classList.add('invalid-grade');
			invalidValues.push(gradeValue);
		} else {
			cell.classList.remove('invalid-grade');
		}
	}

	if (invalidValues.length > 0) {
		alert('Invalid grade values: ' + invalidValues.join(', '));
		return;
	}

	cancelBtn.remove();
	editButton.textContent = 'Edit grades';
	editButton.classList.remove('btn-success');
	editButton.classList.add('btn-secondary');
	editButton.removeEventListener('click', updateGrades);
	editButton.addEventListener('click', enableGradeEditing);

	for (let i = 0; i < gradeCells.length; i++) {
		const cell = gradeCells[i];
		const inputElement = cell.querySelector('input');
		const gradeValue = parseFloat(inputElement.value.trim());
		const originalValue = inputElement.dataset.originalValue;

		if (gradeValue !== parseFloat(originalValue)) {
			const acronimo = cell.dataset.acronimo;
			const dni = cell.dataset.dni;
			const url = `/notas-online/API/Student/Grades?dni=${dni}&acronimo=${acronimo}`;

			updatePromises.push(
				fetch(url, {
					method: 'PUT',
					headers: {
						'Content-Type': 'application/json'
					},
					body: gradeValue
				})
					.then(response => {
						if (!response.ok) {
							console.error(`Failed to update grade for cell ${i}. Status code: ${response.status}`);
							return Promise.reject(response.status);
						}
					})
					.catch(error => {
						console.error(`Failed to update grade for cell ${i}:`, error);
						return Promise.reject(error);
					})
			);
		}
	}

	Promise.all(updatePromises)
		.then(() => {
			alert('Grades updated successfully!');
			fetchSubjects();
		})
		.catch(() => {
			for (let i = 0; i < gradeCells.length; i++) {
				const cell = gradeCells[i];
				const inputElement = cell.querySelector('input');
				const originalValue = inputElement.dataset.originalValue;
				cell.textContent = originalValue; // Revert to original value
			}
			alert('Failed to update grades. Please try again.');
		});
}


