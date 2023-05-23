function fetchSubjects() {
  // Make an AJAX request to the servlet endpoint
  fetch('Subject')
    .then(response => response.json()) // Parse the response as JSON
    .then(subjects => {
      const subjectListElement = document.getElementById('subject-list');

      // Clear any existing subjects
      subjectListElement.innerHTML = '';

      // Iterate over the subjects and create a card for each subject
      subjects.forEach(subject => {
        const subjectCard = document.createElement('div');
        subjectCard.classList.add('col-md-4');
        subjectCard.innerHTML = `
          <div class="card mb-3">
            <div class="card-body">
              <h5 class="card-title">${subject.nombre}</h5>
            </div>
          </div>
        `;
        subjectListElement.appendChild(subjectCard);
      });
    })
    .catch(error => {
      console.error('Error fetching subjects:', error);
    });
}

window.onload = fetchSubjects;