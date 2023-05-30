// Function to change a student's grade
function changeGrade(studentRow) {
	const gradeCell = studentRow.querySelector('td:nth-child(2)'); // Assuming the grade cell is always the second cell in each row
	const currentGrade = parseFloat(gradeCell.textContent); // Get the current grade value

	const newGrade = prompt('Enter the new grade (0-10):', currentGrade); // Prompt the user for a new grade

	// Validate the new grade
	const parsedNewGrade = parseFloat(newGrade);
	const isValidGrade = !isNaN(parsedNewGrade) && parsedNewGrade >= 0 && parsedNewGrade <= 10;

	if (!isValidGrade) {
		alert('Invalid grade! Grade must be a number between 0 and 10.');
		return;
	}

	// Update the grade cell with the new grade
	gradeCell.textContent = parsedNewGrade.toFixed(2);

	// Call the servlet to update the grade in the database using PUT
	const studentName = studentRow.querySelector('td:first-child').textContent; // Assuming the student name is always the first cell in each row
	const studentId = studentName.split(' ')[0]; // Assuming the student ID is part of the student name
	const rootUrl = `${window.location.origin}/notas-online`;

	// Make the PUT request to update the grade
	fetch(`${rootUrl}/API/Student/Grade`, {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			studentId: studentId,
			newGrade: parsedNewGrade
		})
	})
		.then(response => {
			if (response.ok) {
				alert('Grade updated successfully!');
			} else {
				alert('Failed to update the grade. Please try again.');
				gradeCell.textContent = currentGrade.toFixed(2); // Restore the previous grade on failure
			}
		})
		.catch(error => {
			console.error('Error updating the grade:', error);
			alert('An error occurred while updating the grade. Please try again.');
			gradeCell.textContent = currentGrade.toFixed(2); // Restore the previous grade on error
		});
}
