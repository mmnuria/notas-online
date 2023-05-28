$(document).ready(function() {
	// Logout button click event handler
	$("#logout-button").click(function() {
		rootUrl = `${window.location.origin}/notas-online`;
		window.location.href = `${rootUrl}/Logout`;
	});
});
