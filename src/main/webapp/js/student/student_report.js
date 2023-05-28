$(document).ready(function() {
	getSubjectsData();
	getActualDate();

});

function getSubjectsData() {
	// Get the subjects data from the URL parameters
	const urlParams = new URLSearchParams(window.location.search);
	const subjectsParam = urlParams.get('subjects');
	const subjects = JSON.parse(subjectsParam);

	// Populate the table with subjects
	const reportContent = document.getElementById('report-content');
	subjects.forEach(subject => {
		const row = document.createElement('tr');
		row.innerHTML = `
	            <td>${subject.subjectAcronym}</td>
	            <td>${subject.subjectName}</td>
	            <td>${subject.grade}</td>
	        `;
		reportContent.appendChild(row);
	});
}

function getActualDate() {
	// Add the date
	var date = new Date();
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	var formattedDate = day + '/' + month + '/' + year;
	document.getElementById('date').innerText = formattedDate;
}