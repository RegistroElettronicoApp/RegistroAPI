<#import "components.ftl" as components>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Admin page registro - Chiavi d'accesso</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
              crossorigin="anonymous">
        <script>
            function onRegistroClick(name) {
                document.getElementById("selectorButton").innerText = name
                document.getElementById("schoolCodeGroup").style.display = (name === "Classeviva" ? "none" : "unset")
            }

            function createKey() {
                let registroTesto = document.getElementById("selectorButton").innerText
                let registroId;
                switch (registroTesto) {
                    case "Classeviva":
                        registroId = 0
                        break;
                    case "Argo":
                        registroId = 1
                        break;
                    case "Axios":
                        registroId = 2
                        break;
                    case "Nuvola":
                        registroId = 3
                        break;
                    default:
                        registroId = 4
                        break;
                }
                let res = fetch("accessKey", {
                    method: "POST",
                    body: JSON.stringify({
                        "displayName": document.getElementById("textAreaNomeNew").value,
                        "schoolCode": document.getElementById("textAreaCodiceNew").value,
                        "username": document.getElementById("textAreaUsernameNew").value,
                        "password": document.getElementById("textAreaPasswordNew").value,
                        "reg": registroId,
                        "shareCode": document.getElementById("textAreaShareCodeNew").value,
                    }),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                res.then((res) => {
                    if (res.status === 201) {
                        var myModalEl = document.getElementById("createModal");
                        var modal = bootstrap.Modal.getInstance(myModalEl)
                        modal.hide();
                        setTimeout(() => {
                            location.reload();
                        }, 500)
                    } else {
                        alert("Errore interno: " + res.status)
                    }
                }, () => {
                    alert("Richiesta fallita")
                })
            }

            function editKey(id, reg) {
                let res = fetch("accessKey", {
                    method: "PATCH",
                    body: JSON.stringify({
                        "id": id,
                        "displayName": document.getElementById("textAreaNomeKey" + id).value,
                        "schoolCode": document.getElementById("textAreaCodiceKey" + id).value,
                        "username": document.getElementById("textAreaUsernameKey" + id).value,
                        "password": document.getElementById("textAreaPasswordKey" + id).value,
                        "reg": reg,
                        "shareCode": document.getElementById("textAreaShareCodeKey" + id).value,
                    }),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                res.then((res) => {
                    if (res.status === 200) {
                        var myModalEl = document.getElementById("editModalKey" + id);
                        var modal = bootstrap.Modal.getInstance(myModalEl)
                        modal.hide();
                        setTimeout(() => {
                            location.reload();
                        }, 500)
                    } else {
                        alert("Errore interno: " + res.status)
                    }
                }, () => {
                    alert("Richiesta fallita")
                })
            }

            function deleteKey(id) {
                let res = fetch("accessKey?id=" + id, {
                    method: "DELETE"
                })
                res.then((res) => {
                    if (res.status === 200) {
                        var myModalEl = document.getElementById("deleteModalKey" + id);
                        var modal = bootstrap.Modal.getInstance(myModalEl)
                        modal.hide();
                        setTimeout(() => {
                            location.reload();
                        }, 500)
                    } else {
                        alert("Errore interno: " + res.status)
                    }
                }, () => {
                    alert("Richiesta fallita")
                })
            }
        </script>
    </head>
    <body>
        <@components.navBar logged=logged ind=3 dev=dev />
        <!-- Login Modal -->
        <#if logged == false>
            <@components.loginModal error=error/>
        </#if>

        <#if logout!false>
            <div class="alert alert-success" role="alert">
                Disconnesso con successo!
            </div>
        </#if>
        <div class="row">
            <div class="mx-auto col-md-8">
                <h1 class="text-center">Chiavi d'accesso:</h1>
                <div class="mx-auto col-md-2 text-center">
                    <#list keys as item>
                        <div class="card justify-content-center" style="width: 100%; margin-bottom:20px!important;">
                            <div class="card-body">
                                <h5 class="card-title">${item.displayName}</h5>
                                <h6 class="card-subtitle mb-2 text-body-secondary">Codice: ${item.shareCode}</h6>
                                <h6 class="card-subtitle mb-2 text-body-secondary">ID: ${item.id}</h6>
                                <h6 class="card-subtitle mb-2 text-body-secondary">ID registro: ${item.reg}</h6>
                                <a class="btn btn-primary" data-bs-toggle="modal"
                                   data-bs-target="#editModalKey${item.id}">Modifica</a>
                                <a class="btn btn-danger" data-bs-toggle="modal"
                                   data-bs-target="#deleteModalKey${item.id}">Elimina</a>
                            </div>
                        </div>
                        <div class="modal fade" id="editModalKey${item.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Modifica la chiave ${item.displayName}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body" style="margin-top: 20px">
                                        <div class="input-group">
                                            <span class="input-group-text">Nome</span>
                                            <textarea class="form-control" id="textAreaNomeKey${item.id}"
                                                      aria-label="Inserisci Nome">${item.displayName}</textarea>
                                        </div>
                                        <#if item.reg != 0>
                                            <div class="input-group" style="margin-top: 20px">
                                                <span class="input-group-text">Codice scuola</span>
                                                <textarea class="form-control" id="textAreaCodiceKey${item.id}"
                                                          aria-label="Inserisci Codice scuola">${item.schoolCode}</textarea>
                                            </div>
                                        </#if>
                                        <div class="input-group" style="margin-top: 20px">
                                            <span class="input-group-text">Username</span>
                                            <textarea class="form-control" id="textAreaUsernameKey${item.id}"
                                                      aria-label="Inserisci Username">${item.username}</textarea>
                                        </div>
                                        <div class="input-group" style="margin-top: 20px">
                                            <span class="input-group-text">Password</span>
                                            <textarea class="form-control" id="textAreaPasswordKey${item.id}"
                                                      aria-label="Inserisci password">${item.password}</textarea>
                                        </div>
                                        <div class="input-group" style="margin-top: 20px">
                                            <span class="input-group-text">Codice di condivisione</span>
                                            <textarea class="form-control" id="textAreaShareCodeKey${item.id}"
                                                      aria-label="Inserisci Codice di condivisione">${item.shareCode}</textarea>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                        </button>
                                        <button type="button" class="btn btn-primary"
                                                onclick="editKey(${item.id}, ${item.reg})">
                                            Modifica
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal fade" id="deleteModalKey${item.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Vuoi davvero eliminare questa chiave?</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Close"></button>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                        </button>
                                        <button type="button" class="btn btn-danger" onclick="deleteKey(${item.id})">
                                            Elimina
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#list>
                    <#if keys?size == 0>
                        <span class="text-center">Nessuna chiave</span>
                    </#if>
                    <a class="btn btn-primary" data-bs-toggle="modal"
                       data-bs-target="#createModal">Crea chiave</a>
                    <div class="modal fade" id="createModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Creazione chiave</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="input-group">
                                        <span class="input-group-text">Nome</span>
                                        <input class="form-control" id="textAreaNomeNew"
                                               aria-label="Inserisci Nome">
                                    </div>
                                    <div class="input-group" style="margin-top: 20px">
                                        <span class="input-group-text">Registro elettronico</span>
                                        <div class="dropdown">
                                            <button class="btn btn-secondary dropdown-toggle" id="selectorButton"
                                                    type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                                Classeviva
                                            </button>
                                            <ul class="dropdown-menu">
                                                <li><a class="dropdown-item" href="#"
                                                       onclick="onRegistroClick('Classeviva')">Classeviva</a></li>
                                                <li><a class="dropdown-item" href="#" onclick="onRegistroClick('Argo')">Argo</a>
                                                </li>
                                                <li><a class="dropdown-item" href="#"
                                                       onclick="onRegistroClick('Axios')">Axios</a></li>
                                                <li><a class="dropdown-item" href="#"
                                                       onclick="onRegistroClick('Nuvola')">Nuvola</a></li>
                                                <li><a class="dropdown-item" href="#"
                                                       onclick="onRegistroClick('Sempreverde')">Sempreverde</a></li>
                                            </ul>
                                        </div>
                                    </div>
                                    <div class="input-group" id="schoolCodeGroup"
                                         style="margin-top: 20px; display: none">
                                        <span class="input-group-text">Codice scuola</span>
                                        <input class="form-control" id="textAreaCodiceNew"
                                               aria-label="Inserisci Codice scuola">
                                    </div>
                                    <div class="input-group" style="margin-top: 20px">
                                        <span class="input-group-text">Username</span>
                                        <input class="form-control" id="textAreaUsernameNew"
                                               aria-label="Inserisci Username">
                                    </div>
                                    <div class="input-group" style="margin-top: 20px">
                                        <span class="input-group-text">Password</span>
                                        <input type="password" class="form-control" id="textAreaPasswordNew"
                                               aria-label="Inserisci password">
                                    </div>
                                    <div class="input-group" style="margin-top: 20px">
                                        <span class="input-group-text">Codice di condivisione</span>
                                        <input class="form-control" id="textAreaShareCodeNew"
                                               aria-label="Inserisci Codice di condivisione">
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                    </button>
                                    <button type="button" class="btn btn-primary" onclick="createKey()">
                                        Crea
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL"
                crossorigin="anonymous"></script>
    </body>
</html>