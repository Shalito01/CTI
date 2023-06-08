
(function() {
	if (sessionStorage.getItem("username") == null) {
		redirect("login.html");
		return;
	}

	var header,
		modal,
		catalog_tree,
		dragTarget,
		username,
		page = new PageOrchestrator();

	window.addEventListener("load", () => {
		page.start();
		page.show();
	});

	var logout_btn = document.getElementById("logout-btn");
	logout_btn.addEventListener("click", () => {
		sendRequest("GET", "logout", null, (req) => {
			if(req.readyState != XMLHttpRequest.DONE) return;

			if(req.status != 200) {
				window.alert(req.responseText);
				return;
			}

			sessionStorage.removeItem("username");
			redirect("login.html");
		});
	});

	function CatalogTree(catalog_list) {
		this.catalog_list = catalog_list;

		this.show = (to_fetch = true) => {
			if(!to_fetch) {
				enable(this.catalog_list);
				return;
			}

			sendRequest("GET", "catalog", null, (req) => {
				if(req.readyState != XMLHttpRequest.DONE) return;

				var msg = req.responseText;

				if(req.status != 200) {
					window.alert(msg);
					return;
				}

				console.log(JSON.parse(msg));
				// this.buildTree(JSON.parse(msg));
				var obj = JSON.parse(msg);
				this.print(obj["tree"], document.getElementById("tree-wrapper"));
				enable(this.catalog_list);
			});
		};

		this.print = (tree, parent) => {
			if (!tree.hasOwnProperty("id")) return;

			var div = document.createElement("div");
			div.classList.add("node");
			div.id = tree["id"];
			div.draggable = true;

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

			if (tree["nomeCatalogo"] === "ROOT") {
				nome.classList.add("root");
				idCatalogo.classList.add("root");
				idSpan.classList.add("root");
			}
			div.appendChild(idCatalogo);
			div.appendChild(idSpan)
			div.appendChild(nome);

			parent.appendChild(div);
			// console.log("Appeso " + div.id);

			if (tree["subCatalogs"] == null || tree["subCatalogs"].length == 0) return;

			for (var i = 0; i < tree["subCatalogs"].length; i++) {
				// console.log("figlio: " + i);
				this.print(tree["subCatalogs"][i], div);
			}

		};

		this.buildTree = (json_catalog, onNameClick = null, onFormClick = null) => {
			if(json_catalog.hasOwnProperty("tree")) return;

			var nodes = json_catalog;
			var div = document.createElement("div");
			div.classList.add("node");
			div.draggable = true;

			var name, id;
			catalog_list.innerHTML = "";


			if(onNameClick == null) {
				name = document.createElement("span");
				div.addEventListener("dragend", () => {
					this.handleDragEnd();
				});
			} else {
				name = document.createElement("a");
				name.addEventListener("click", () => {
					onNameClick(json_catalog.id);
				});

				div.addEventListener("dragstart", (e) => {
					for(subnode of document.getElementsByClassName("node"))
					{
						subnode.addEventListener("dragover", this.preventDefault);
					};
				});

			}

			if(nodes.length == 0) {
				catalog_list.innerHTML = "You don't have any category. Just create one!";
			}

			nodes.forEach((item) => {
				var node = document.createElement("div");
				node.appendChild(this.buildListItem(item, "", "", null, this.handleCreateClick));
				node.appendChild(this.buildSubList(item));

				catalog_list.appendChild(node);
			});

			this.buildSubList = (sublistJson) => {
				if(!sublistJson.hasOwnProperty("subCatalogs")) return;
				var subCatalogs = sublistJson.subCatalogs;

				var subCatalogList = document.createElement("div");

				subCatalogs.forEach((catalog) => {
					var subcatalog = document.createElement("div");
					subcatalog.className = "catalog";

					subcatalog.appendChild(this.buildListItem(catalog, "", "", null, this.handleCreateClick, true));
					subcatalog.appendChild(this.buildSubList(catalog));

					subcatalog.addEventListener("drop", (e) =>{
						e.preventDefault();

						// Prompt Confirm
					});

					subCatalogList.appendChild(subcatalog);
				});
			
				return subCatalogList;
			};

			this.buildListItem = (json, icon, icons, onNameClick = null, onFormClick = null, isDocumentForm = false) => {
				var div = document.createElement("div");

				div.id = json.id;
				div.classList.add("node");
				div.draggable = true;

				var name;
				if(onNameClick == null) {
					name = document.createElement("span");

					div.addEventListener("dragend", () => {
						this.handleDragEnd();
					});

				} else {
					name = document.createElement("a");
					name.addEventListener("click", () => {
						onNameClick(json.id);
					});
				
					div.addEventListener("dragstart", (e) => {
						for(subDiv of document.getElementsByClassName("node")) {
							subDiv.addEventListener("dragover", this.preventDefault);
						};

						var thisDiv = e.target.closest(".node");
						thisDiv.classList.add("deselected");
						thisDiv.removeEventListener("dragover", this.preventDefault);
						
						this.handleDragStart("Catalog", json);
					});

					div.addEventListener("dragend", () => {
						for(subDiv of document.getElementsByClassName("node")) {
							subDiv.removeEventListener("dragover", this.preventDefault);
							subDiv.classList.remove("deselected");
						}

						this.handleDragEnd();
					});
				}

				name.textContent = json.nomeCatalogo;
				div.appendChild(name);

				if(onFormClick != null) {
					var addItem = document.createElement("a");
					addItem.classList.add("button");
					addItem.textContent = "+";
					addItem.classList.add("add-button");
					addItem.addEventListener("click", (event) => {
						var target = event.target.closest(".add-button");
						
						if (target.textContent === "+") {
							target.innerHTML = "&#8722;";
							enable(formItem);
						} else {
							target.textContent = "+";
							disable(formItem);
						}
					})

					var innerHTML = `<input name="old_id" type="hidden" value="${json.id}">`;
					div.addEventListener("dragstart", () => {
						this.handleDragStart("NODE");
					});

					formItem.innerHTML = innerHTML;
					// formItem.classList.add("inline-form");
					// formItem.classList.add("h-container");
					
					// var formButton = document.createElement("input");
					// formButton.classList.add("button");
					// formButton.value = "Create";
					// formButton.type = "submit";
					// formButton.addEventListener("click", (event) => {
					// 	event.preventDefault();
					// 	onFormClick(event.target.closest("form"), () => {
					// 		enable(addItem);
					// 		disable(formItem);
					// 	});
					// });
					// formItem.appendChild(formButton);
					// formItem.classList.add("disabled");
					
					div.appendChild(addItem);
					div.appendChild(formItem);
				}

			};

			return div;
		};
	
		this.handleCreateClick = (form, callback = null) => {
			if(form == null) return;

			if(!form.checkValidity()) {
				form.reportValidity();
				return;
			}

			sendRequest("POST", "catalog", form, (req) => {
				if(req.readyState != XMLHttpRequest.DONE) return;

				if(req.status != 200) {
					window.alert(req.responseText);
					return;
				}

				if(callback != null) callback();

				this.show(true);
			});
		};

		this.handleDragStart = (json) => {
			dragTarget = { id: json.id, nomeCatalogo: json.nomeCatalogo };
		};

		this.handleDragEnd = () => {
			dragTarget = null;
		};

		this.preventDefault = (e) => {
			e.preventDefault();
		};

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

			// modal = new Modal(
			// 	document.getElementById("modal"),
			// 	document.getElementById("close-button"),
			// 	document.getElementById("content-name"),
			// 	document.getElementById("confirm-button")
			// );
            // modal.start();

		};

		this.show = () => {
			catalog_tree.show();
		};
	}




})();
