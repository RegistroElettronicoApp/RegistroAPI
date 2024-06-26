<#import "components.ftl" as components>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Admin page registro - Feedback</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
              crossorigin="anonymous">
        <script>
            function onReplySubmit(id) {
                let reply = document.getElementById("textAreaReply-" + id).value
                let res = fetch("/feedback", {
                    method: "PATCH",
                    body: '{"id": ' + id + ', reply: "' + reply + '"}',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                res.then((res) => {
                    if (res.status === 200) {
                        var myModalEl = document.getElementById("replyModal" + id);
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

            function deleteAllFeedbackDanger() {
                let res = fetch("/deleteAllFeedback", {
                    method: "DELETE"
                })
                res.then((res) => {
                    if (res.status === 200) {
                        var myModalEl = document.getElementById("deleteAllModal");
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
        <@components.navBar logged=logged ind=1 dev=dev />
        <!-- Login Modal -->
        <#if logged == false>
            <@components.loginModal error=error/>
        </#if>

        <div class="row">
            <div class="mx-auto col-md-8">
                <h1 class="text-center">Feedback:</h1>
                <div class="mx-auto col-md-2 text-center">
                    <#list feedbacks as item>
                        <div class="card justify-content-center" style="width: 100%; margin-bottom:20px!important;">
                            <div class="card-body">
                                <h5 class="card-title">${item.name}</h5>
                                <h6 class="card-subtitle mb-2 text-body-secondary">ID: ${item.id}</h6>
                                <h6 class="card-subtitle mb-2 text-body-secondary card-date-to-edit"
                                    data-date="${item.date}">Data: ${item.date}</h6>
                                <#if item.reply != "">
                                    <h6 class="card-subtitle mb-2 text-body-secondary">Risposta: ${item.reply}</h6>
                                </#if>

                                <p class="card-text">${item.description}</p>
                                <#if item.reply == "">
                                    <a class="btn btn-primary" data-bs-toggle="modal"
                                       data-bs-target="#replyModal${item.id}">Rispondi</a>
                                </#if>
                                <#if item.reply != "">
                                    <a href="#" class="btn btn-primary disabled" aria-disabled="true">Risposto</a>
                                </#if>
                            </div>
                        </div>
                        <div class="modal fade" id="replyModal${item.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Rispondi a ${item.name}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="input-group">
                                            <span class="input-group-text">Inserisci descrizione</span>
                                            <textarea class="form-control" id="textAreaReply-${item.id}"
                                                      aria-label="Inserisci descrizione"></textarea>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                        </button>
                                        <button type="button" class="btn btn-primary"
                                                onclick="onReplySubmit(${item.id})">
                                            Invia risposta
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#list>
                    <#if feedbacks?size == 0>
                        <span class="text-center">Nessun feedback</span>
                    </#if>
                </div>
                <div class="modal fade" id="deleteAllModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Vuoi davvero eliminare tutti i feedback?</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <span>L'operazione è irreversibile!</span>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi
                                </button>
                                <button type="button" class="btn btn-danger"
                                        onclick="deleteAllFeedbackDanger()">
                                    Elimina
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="danger-area" class="text-center">
                    <h2 class="text-center text-danger">Area pericolo</h2>
                    <a class="btn btn-danger justify-content-center align-content-center" data-bs-toggle="modal"
                       data-bs-target="#deleteAllModal">Elimina tutti i feedback</a>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL"
                crossorigin="anonymous"></script>
        <script>
            for (let cardDateElem of document.getElementsByClassName("card-date-to-edit")) {
                let unixSeconds = cardDateElem.attributes["data-date"].value.replaceAll(".", "").replaceAll(",", "")
                const date = new Date(unixSeconds * 1000);

                const day = "0" + date.getDate();
                const month = "0" + date.getMonth();
                const year = date.getYear() + 1900;

                const hours = "0" + date.getHours();

                const minutes = "0" + date.getMinutes();
                const formattedTime = day.substr(-2) + "/" + month.substr(-2) + "/" + year + " " + hours.substr(-2) + ':' + minutes.substr(-2);
                cardDateElem.innerText = "Data: " + formattedTime
            }
        </script>
    </body>
</html>