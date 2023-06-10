function sendRequest(method, url, form, callback, reset = true) {
	var request = new XMLHttpRequest();

	request.onreadystatechange = function() {
		callback(request);
	};

	request.open(method, url);

	if (form == null) {
		request.send();
	} else {
		request.overrideMimeType("application/x-www-form-urlencoded");
		request.send(new FormData(form));

		if (reset) {
			form.reset();
		}
	}
}

function sanitize(string) {
  const map = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#x27;',
      "/": '&#x2F;',
  };
  const reg = /[&<>"'/]/ig;
  return string.replace(reg, (match)=>(map[match]));
}

function redirect(path) {
	window.location.href = path;
}

function select(element) {
	element.classList.add("selected");
	element.children.classList.add("hide");
}

function disable(element) {
	element.classList.add("disabled");
}

function enable(element) {
	element.classList.remove("disabled");
}
