function closest(element, className) {
	while (element && !hasClass(element, className)) {
		element = element.parentNode;
	}
	return element;
}

function hasClass(element, className) {
	if (element.classList) {
		return element.classList.contains(className);
	} else {
		return new RegExp('(^|\\s)' + className + '(\\s|$)').test(element.className);
	}
}

function enableGradeEditing(event) {
	const editButton = event.target; // Get the clicked Edit button

	// Store the original button text and class in data attributes
	editButton.dataset.originalText = editButton.textContent;
	editButton.dataset.originalClass = editButton.className;

	const cancelBtn = document.createElement('button');
	cancelBtn.className = 'btn btn-danger cancel-button'; // Add the "cancel-button" class
	cancelBtn.textContent = 'Cancel';
	cancelBtn.addEventListener('click', function() {
		cancelGradeEditing(cancelBtn, editButton); // Pass the cancel button and edit button as arguments
	});

	// Insert a space element
	const spaceElement = document.createTextNode(' ');

	// Insert the Cancel button and space after the Edit button
	editButton.parentNode.insertBefore(cancelBtn, editButton.nextSibling);
	editButton.parentNode.insertBefore(spaceElement, editButton.nextSibling);

	const currentTab = closest(editButton, 'tab-pane'); // Get the parent tab of the clicked Edit button
	const gradeCells = currentTab.getElementsByClassName('grade-cell');

	for (let i = 0; i < gradeCells.length; i++) {
		const cell = gradeCells[i];
		const gradeValue = cell.textContent;
		const inputElement = document.createElement('input');
		inputElement.type = 'number';
		inputElement.step = '1';
		inputElement.min = 0;
		inputElement.max = 100;
		inputElement.className = 'form-control';
		inputElement.value = gradeValue;
		inputElement.dataset.originalValue = gradeValue; // Save original value
		cell.textContent = '';
		cell.appendChild(inputElement);
	}

	editButton.textContent = 'Confirm edit';
	editButton.className = 'btn btn-success';
	editButton.removeEventListener('click', enableGradeEditing);
	editButton.addEventListener('click', updateGrades);
}

function cancelGradeEditing(cancelBtn, editButton) {
	const currentTab = closest(editButton, 'tab-pane'); // Get the parent tab of the associated Edit button
	const gradeCells = currentTab.getElementsByClassName('grade-cell');

	cancelBtn.parentNode.removeChild(cancelBtn);

	for (let i = 0; i < gradeCells.length; i++) {
		const cell = gradeCells[i];
		const inputElement = cell.querySelector('input');
		const originalValue = inputElement.dataset.originalValue;

		// Remove the input element and restore the original value
		cell.removeChild(inputElement);
		cell.textContent = originalValue;
	}

	// Restore the original button text and class
	editButton.textContent = editButton.dataset.originalText;
	editButton.className = editButton.dataset.originalClass;
	editButton.removeEventListener('click', updateGrades);
	editButton.addEventListener('click', enableGradeEditing);
}

function updateGrades() {
	const editButton = this;
	const currentTab = closest(editButton, 'tab-pane');
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
	editButton.className = 'btn btn-secondary';
	editButton.removeEventListener('click', updateGrades);
	editButton.addEventListener('click', enableGradeEditing);

	const updateGradePromises = [];

	for (let i = 0; i < gradeCells.length; i++) {
		const cell = gradeCells[i];
		const inputElement = cell.querySelector('input');
		const gradeValue = parseFloat(inputElement.value.trim());
		const originalValue = inputElement.dataset.originalValue;

		if (gradeValue !== parseFloat(originalValue)) {
			const acronimo = cell.dataset.acronimo;
			const dni = cell.dataset.dni;
			const url = `/notas-online/API/Student/Grades?dni=${dni}&acronimo=${acronimo}`;

			updateGradePromises.push(
				fetch(url, {
					method: 'PUT',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify(gradeValue),
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

	Promise.all(updateGradePromises)
		.then(() => {
			alert('Grades updated successfully!');

			// Remove input fields and display updated grades
			for (let i = 0; i < gradeCells.length; i++) {
				const cell = gradeCells[i];
				const inputElement = cell.querySelector('input');
				const updatedGrade = parseFloat(inputElement.value.trim());

				cell.removeChild(inputElement);
				cell.textContent = updatedGrade;

				// Recalculate the mean and update the mean cell
				const meanRow = currentTab.querySelector('.mean-row');
				const meanCells = meanRow.querySelectorAll('.mean-cell');
				const students = Array.from(gradeCells).map(cell => ({
					nota: parseFloat(cell.textContent)
				}));
				const mean = calculateMean(students);
				meanCells[1].textContent = mean;
			}
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



