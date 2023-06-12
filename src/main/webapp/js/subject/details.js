$(document).ready(function() {
    fetchSubjectDetails();
});

function fetchSubjectDetails() {
    const rootUrl = `${window.location.origin}/notas-online`;

    // Extract the subject parameter from the URL
    const urlParams = new URLSearchParams(window.location.search);
    const subject = urlParams.get('subject');

    // Make an AJAX request to fetch the subject details
    fetch(`${rootUrl}/API/SubjectByAcronym?subject=${subject}`)
        .then(response => response.json())
        .then(subjectDetails => {
            const subjectNameElement = document.getElementById('subject-name');
            const subjectAcronymElement = document.getElementById('subject-acronym');

            // Update the subject name and acronym
            subjectNameElement.innerText = subjectDetails.nombre;
            subjectAcronymElement.innerText = subjectDetails.acronimo;

            // Add other subject details
            const subjectDetailsElement = document.getElementById('subject-details');
            subjectDetailsElement.innerHTML = `
                <p>Creditos: ${subjectDetails.creditos}</p>
                <p>Cuatrimestre: ${subjectDetails.cuatrimestre}</p>
                <p>Curso: ${subjectDetails.curso}</p>
            `;
        })
        .catch(error => {
            console.error('Error fetching subject details:', error);
        });
}
