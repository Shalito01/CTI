
(function() {
	if (sessionStorage.getItem("username") != null) {
		redirect("/home.html");
		return;
	}

	const form = document.getElementById("login-form");
	var login_btn = document.getElementById("login-btn");
	var error = document.getElementById("error");

	form.addEventListener("submit", (e) => {
		e.preventDefault();

		if (!form.checkValidity()) {
			form.reportValidiy();
			return;
		}

		sendForm("/login", form);
	});

	function sendForm(url, form) {
		sendRequest("POST", url, form, function(req) {
			if (req.readyState != XMLHttpRequest.DONE) return;

			var msg = req.responseText;

			if (req.status != 200) {
				showError(msg);
				return;
			}

			var json = JSON.parse(msg);

			if (!json.hasOwnProperty("username")) {
				showError("Something went wrong, try later!");
				return;
			}

			sessionStorage.setItem("username", json.username);
			redirect("/home.html");
		});
	}

	function showError(errorText) {
		error.textContent = errorText;
	}

})();
