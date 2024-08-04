<#import "components.ftl" as components>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Admin page registro - Changelog</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
              crossorigin="anonymous">
        <script>
            function createChangelog() {
                let res = fetch("changelog", {
                    method: "PUT",
                    body: JSON.stringify({
                        "versionName": document.getElementById("textAreaNomeNew").value,
                        "buildNumber": parseInt(document.getElementById("textAreaBuildNew").value),
                        "changelogHtml": document.getElementById("textAreaTextNew").value,
                        "availableForUpdate": document.getElementById("textAreaUpdateNew").checked
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

            function editChangelog(id, reg) {
                let res = fetch("changelog", {
                    method: "PATCH",
                    body: JSON.stringify({
                        "id": id,
                        "versionName": document.getElementById("textAreaNomeChangelog" + id).value,
                        "buildNumber": parseInt(document.getElementById("textAreaBuildChangelog" + id).value),
                        "changelogHtml": document.getElementById("textAreaTextChangelog" + id).value,
                        "availableForUpdate": document.getElementById("textAreaUpdateChangelog" + id).checked
                    }),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                res.then((res) => {
                    if (res.status === 200) {
                        var myModalEl = document.getElementById("editModalChangelog" + id);
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

            function deleteChangelog(id) {
                let res = fetch("deleteChangelog", {
                    method: "POST",
                    body: JSON.stringify({
                        "id": id
                    }),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                res.then((res) => {
                    if (res.status === 200) {
                        var myModalEl = document.getElementById("deleteModalChangelog" + id);
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
                <h1 class="text-center">Changelogs:</h1>
                <div class="mx-auto col-md-2 text-center">
                    <#list changelogs as item>
                        <div class="card justify-content-center" style="width: 100%; margin-bottom:20px!important;">
                            <div class="card-body">
                                <h5 class="card-title">${item.versionName}</h5>
                                <h6 class="card-subtitle mb-2 text-body-secondary">Build: ${item.buildNumber}</h6>
                                <h6 class="card-subtitle mb-2 text-body-secondary">ID: ${item.id}</h6>
                                <h6 class="card-subtitle mb-2 text-body-secondary">Disponibile per
                                    aggiornamento: ${item.availableForUpdate?string('Sì', 'No')}</h6>
                                <h6 class="card-subtitle mb-2 text-body-secondary">Changelog: ${item.changelogHtml}</h6>
                                <a class="btn btn-primary" data-bs-toggle="modal"
                                   data-bs-target="#editModalChangelog${item.id}">Modifica</a>
                                <a class="btn btn-danger" data-bs-toggle="modal"
                                   data-bs-target="#deleteModalChangelog${item.id}">Elimina</a>
                            </div>
                        </div>
                        <div class="modal fade" id="editModalChangelog${item.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Modifica la chiave ${item.versionName}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body" style="margin-top: 20px">
                                        <div class="input-group">
                                            <span class="input-group-text">Nome Versione</span>
                                            <textarea class="form-control" id="textAreaNomeChangelog${item.id}"
                                                      aria-label="Inserisci Nome Versione">${item.versionName}</textarea>
                                        </div>
                                        <div class="input-group" style="margin-top: 20px">
                                            <span class="input-group-text">Numero Build</span>
                                            <input type="number" class="form-control"
                                                   id="textAreaBuildChangelog${item.id}"
                                                   aria-label="Inserisci Numero Build" value="${item.buildNumber}">
                                        </div>
                                        <div class="input-group" style="margin-top: 20px">
                                            <span class="input-group-text">Changelog</span>
                                            <textarea class="form-control" id="textAreaTextChangelog${item.id}"
                                                      aria-label="Inserisci Changelog">${item.changelogHtml}</textarea>
                                        </div>
                                        <div class="input-group" style="margin-top: 20px">
                                            <span class="input-group-text">Disponibile per update</span>
                                            <input type="checkbox" class="form-check-input"
                                                   id="textAreaUpdateChangelog${item.id}"
                                                   aria-label="Inserisci Changelog"
                                                   <#if item.availableForUpdate>checked</#if>>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                        </button>
                                        <button type="button" class="btn btn-primary"
                                                onclick="editChangelog(${item.id})">
                                            Modifica
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal fade" id="deleteModalChangelog${item.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Vuoi davvero eliminare questo changelog?</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Close"></button>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                        </button>
                                        <button type="button" class="btn btn-danger"
                                                onclick="deleteChangelog(${item.id})">
                                            Elimina
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#list>
                    <#if changelogs?size == 0>
                        <span class="text-center">Nessun changelog</span>
                    </#if>
                    <a class="btn btn-primary" data-bs-toggle="modal"
                       data-bs-target="#createModal">Crea Changelog</a>
                    <div class="modal fade" id="createModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Creazione chiave</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                </div>
                                <div class="modal-body" style="margin-top: 20px">
                                    <div class="input-group">
                                        <span class="input-group-text">Nome Versione</span>
                                        <textarea class="form-control" id="textAreaNomeNew"
                                                  aria-label="Inserisci Nome Versione"></textarea>
                                    </div>
                                    <div class="input-group" style="margin-top: 20px">
                                        <span class="input-group-text">Numero Build</span>
                                        <input type="number" class="form-control" id="textAreaBuildNew"
                                               aria-label="Inserisci Numero Build">
                                    </div>
                                    <div class="input-group" style="margin-top: 20px">
                                        <span class="input-group-text">Changelog</span>
                                        <textarea class="form-control" id="textAreaTextNew"
                                                  aria-label="Inserisci Changelog"></textarea>
                                    </div>
                                    <div class="input-group" style="margin-top: 20px">
                                        <span class="input-group-text">Disponibile per update</span>
                                        <input type="checkbox" class="form-check-input" id="textAreaUpdateNew"
                                               aria-label="Inserisci Changelog">
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                    </button>
                                    <button type="button" class="btn btn-primary" onclick="createChangelog()">
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