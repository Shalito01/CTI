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
		var send = new FormData(form);
		request.send(send);

		if (reset) {
			form.reset();
		}
	}
}

function validate_name(string) {
	const regx = /^[a-zA-Z0-9 ]+$/g;
	return regx.test(string);
}

function validate_id(string) {
	const regx = /^[0-9]*$/g;
	return regx.test(string);
}

function sanitize(string) {
  const map = {
      '&': '#',
      '<': '#',
      '>': '#',
      '"': '#',
      "'": '#',
      "/": '#',
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
