(function() {
	if (sessionStorage.getItem("username") == null) {
		redirect("login.html");
		return;
	}
	var old_id,
		new_id,
		catalog_tree,
		page = new PageOrchestrator();
	var confirmed = false;
	var only_once = true;
	var undo = false;
	var sendDict = [];
	var save_btn = document.getElementById("save-btn");
	save_btn.style.display = "none";
	window.addEventListener("load", () => {
		page.start();
		page.show();
	});
	var insert_btn = document.getElementById("insert-btn");
	var logout_btn = document.getElementById("logout-btn");
	logout_btn.addEventListener("click", () => {
		sendRequest("GET", "logout", null, (req) => {
			if (req.readyState !== XMLHttpRequest.DONE) return;
			if (req.status != 200) {
				window.alert(req.responseText);
				return;
			}
			sessionStorage.removeItem("username");
			redirect("login.html");
		});
	});
	function CatalogTree(catalog_list) {
		this.catalog_list = catalog_list;
		var dataTransfer;
		this.show = (to_fetch = true) => {
			if (!to_fetch) {
				enable(this.catalog_list);
				return;
			}
			sendRequest("GET", "catalog", null, (req) => {
				if (req.readyState !== XMLHttpRequest.DONE) return;
				var msg = req.responseText;
				if (req.status != 200) {
					window.alert(msg);
					return;
				}

				var obj = JSON.parse(msg);
				this.print(obj["tree"], document.getElementById("tree-wrapper"));
				enable(this.catalog_list);
			});
		};
		this.updateSubTree = (tree) => {
			var x = document.getElementById(tree.idPadre);
			x.removeChild(document.getElementById(tree.id));
			this.print(tree, x);
		}
		this.print = (tree, parent, selected = false) => {
			if (!tree.hasOwnProperty("id")) return;
			var div = document.createElement("div");
			div.classList.add("node");
			div.id = tree["id"];
			div.draggable = true;
			if (selected) div.classList.add("selected");
			var div_content = document.createElement("div");
			div_content.classList.add("node-content");
			var nome = document.createElement("span");
			nome.classList.add("nomeCatalogo");
			nome.textContent = tree["nomeCatalogo"];
			var idCatalogo = document.createElement("input");
			idCatalogo.type = "hidden";
			idCatalogo.name = "old_id";
			idCatalogo.value = tree["id"];
			var idSpan = document.createElement("span");
			idSpan.classList.add("id-catalogo");
			idSpan.textContent = tree["id"];
			var input_name = document.createElement("input");
			input_name.style.display = "none";
			if (tree["nomeCatalogo"] === "ROOT") {
				nome.classList.add("root");
				nome.textContent = "--- RADICE: COPIA QUI ---"
				div.id = "ROOT";
				idCatalogo.classList.add("root");
				idSpan.classList.add("root");
			} else {
				input_name.type = "text";
				input_name.name = "catalog_name";
			}
			input_name.placeholder = nome.textContent;
			div_content.appendChild(idCatalogo);
			div_content.appendChild(idSpan)
			div_content.appendChild(nome);
			div_content.appendChild(input_name);
			div.appendChild(div_content);
			parent.appendChild(div);
			nome.addEventListener("click", (e) => {
				e.preventDefault();
				if (nome.classList.contains("root")) return;
				nome.style.display = "none";
				input_name.style.display = "block";
				input_name.addEventListener("focusout", (e) => {
					e.preventDefault();
					if (input_name.value === "" || input_name.value === nome.textContent) {
						input_name.style.display = "none";
						nome.style.display = "block";
						return;
					}
					input_name.value = sanitize(input_name.value);
					var form = new FormData();
					form.append("catalog_name", input_name.value);
					form.append("id", div.id);
					if (!validate_id(div.id) || !validate_name(input_name.value)) {
						window.alert("Error parsing input");
						form.reportValidity();
						return;
					}
					sendRequest("POST", "/catalog/update", form, (req) => {
						if (req.readyState !== XMLHttpRequest.DONE) return;
						if (req.status != 200) {
							window.alert(req.responseText);
							window.location.reload();
							return;
						}
						var res = JSON.parse(req.responseText);
						nome.textContent = res.name;
					}, false);
					input_name.placeholder = nome.textContent;
					input_name.value = "";
					input_name.style.display = "none";
					nome.style.display = "block";
				});
			});
			div.addEventListener("dragstart", (e) => {
				only_once = true;
				confirmed = false;
				undo = false;


				dataTransfer = e.target.id;
				e.dataTransfer.dropEffect = "copy";
				document.getElementById(dataTransfer).style.cursor = "grabbing";
			});
			div.addEventListener("dragenter", (e) => {
				var el = e.target;
				if (!(e.target.tagName === "DIV")) {
					el = e.target.parentNode;
				}
				el.style.backdropFilter = "drop-shadow(0 0 1rem black)";
				el.style.background = "rgba(0,0,0,0.3)";
			});
			div.addEventListener("dragleave", (e) => {
				var el = e.target;
				if (!(e.target.tagName === "DIV")) {
					el = e.target.parentNode;
				}
				el.style.backdropFilter = "none";
				el.style.background = "transparent";
			});
			div.addEventListener("dragover", (e) => {
				e.preventDefault();
			});
			div.addEventListener("dragend", (e) => {
				e.preventDefault();
				e.target.style.cursor = "grab";
			});
			div.addEventListener("drop", (e) => {
				e.preventDefault();
				var el = e.target;
				if (!(e.target.tagName === "DIV")) {
					el = e.target.parentNode;
				}
				if (el.classList.contains("node-content")) {
					el = el.parentNode;
				}
				el.style.backdropFilter = "none";
				el.style.background = "transparent";
				if (only_once) {
					var obj = document.getElementById(el.id);




					if (e.target.id == "tree-wrapper") {
						obj = document.getElementById("ROOT");
					}
					var new_id = obj;
					if (e.target.id != "ROOT") {
						new_id = document.getElementById(obj.id);
					}
					if (old_id === new_id.id) return;
					if (!confirmed && !undo) {
						confirmed = confirm("Do you really want to copy here?");
						if (!confirmed) undo = true;
					}
					if (confirmed) {
						if (new_id.children >= 10) {
							window.alert("Already 9 children for this node. You can't copy here");
							return;
						}
						old_id = dataTransfer;

						var url = `/catalog/subcatalog?old_id=${old_id}&new_id=${new_id.id}`;
						sendRequest("GET", url, null, (req) => {
							if (req.readyState !== XMLHttpRequest.DONE) return;
							if (req.status != 200) {
								window.alert(req.responseText);
								return;
							}
							var msg = JSON.parse(req.responseText);
							this.print(msg.tree, new_id, true);
						});
						sendDict.push({
							"old_id": old_id,
							"new_id": new_id.id
						});
						document.getElementById("save-btn").style.display = "block";
						only_once = false;
					}
				}
			});
			if (tree["subCatalogs"] == null || tree["subCatalogs"].length == 0) return;
			for (var i = 0; i < tree["subCatalogs"].length; i++) {
				this.print(tree["subCatalogs"][i], div);
			}
		};
		insert_btn.addEventListener("click", (e) => {
			e.preventDefault();
			var in_name = sanitize(document.getElementById("in-name").value);
			var in_pid = document.getElementById("in-pid").value;
			if (!validate_id(in_pid) || !validate_name(in_name)) {
				window.alert("Errore formato input");
				return;
			}
			if (in_pid === "") in_pid = "ROOT";
			if (document.getElementById(in_pid) === null) {
				window.alert("Nodo padre non presente");
				return;
			} else if (document.getElementById(in_pid).children.length >= 10) {
				window.alert("Questo Nodo padre ha già 9 figli");
				return;
			}
			var forms = document.getElementById("insert-form");
			var form = new FormData(forms);
			if (!validate_name(in_name)) {
				window.alert("Error parsing input");
				forms.reportValidity();
				return;
			}
			sendRequest("POST", "/catalog", form, (req) => {
				if (req.readyState !== XMLHttpRequest.DONE) return;
				if (req.status != 200) {
					window.alert(req.responseText);
					return;
				}
				var msg = JSON.parse(req.responseText).msg;
				this.print(msg, document.getElementById(msg.idPadre));
				forms.reset();
			}, false);
		});
		save_btn.addEventListener("click", (e) => {
			e.preventDefault();
			document.querySelectorAll(".selected").forEach((e) => e.classList.remove("selected"));
			if (old_id == null && new_id == null) return;
			var data = JSON.stringify(sendDict);
			sendJson("POST", "copy", data, (req) => {
				if (req.readyState !== XMLHttpRequest.DONE) return;
				if (req.status != 200) {
					window.alert(req.responseText);
					return;
				}
				var msg = JSON.parse(req.responseText);

				if (msg.msg === "refresh") {
					location.reload();
				}
			});
			document.getElementById("save-btn").style.display = "none";
		});
		this.hide = () => {
			disable(this.catalog_list);
		};
	}
	function PageOrchestrator() {
		this.start = () => {
			username = sessionStorage.getItem("username");
			catalog_tree = new CatalogTree(
				document.getElementById("tree-wrapper")
			);
		};
		this.show = () => {
			catalog_tree.show();
		};
	}
})();
