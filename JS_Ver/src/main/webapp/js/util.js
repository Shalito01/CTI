function sendRequest(method, url, form, callback, reset = true) {
	var request = new XMLHttpRequest();

	request.onreadystatechange = function() {
		callback(request);
	};

	request.open(method, url);

	if (form == null) {
		request.send();
	} else {
		//request.setRequestHeader("Content-Type", "*/*");
		// request.overrideMimeType("application/x-www-form-urlencoded");
		request.send(form);
	}
}

function sendJson(method, url, data, callback) {
	var request = new XMLHttpRequest();

	request.onreadystatechange = function() {
		callback(request);
	};

	request.open(method, url);


	request.setRequestHeader("Content-Type", "application/json");
	request.overrideMimeType("application/json");
	request.send(data);
}

function validate_name(string) {
	const regx = /^[a-zA-Z0-9 ]+$/g;
	return regx.test(string);
}

function validate_id(string) {
	const regx = /^[1-9]*$/g;
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
