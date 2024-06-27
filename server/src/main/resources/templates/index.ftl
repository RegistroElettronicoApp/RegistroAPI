<#import "components.ftl" as components>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Admin page registro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
              crossorigin="anonymous">

    </head>
    <body>
        <@components.navBar logged=logged ind=0 dev=dev />
        <!-- Login Modal -->
        <#if logged == false>
            <@components.loginModal error=error/>
        </#if>

        <#if logout!false>
            <div class="alert alert-success" role="alert">
                Disconnesso con successo!
            </div>
        </#if>
        <#if logged>
            <div class="row">
                <div class="mx-auto col-md-8">
                    <h1 class="text-center">Ciao ${name}!</h1>
                </div>
            </div>
        <#else>
            <div class="row">
                <div class="mx-auto col-md-8">
                    <h1 class="text-center">Devi prima fare il login.</h1>
                </div>
            </div>
        </#if>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL"
                crossorigin="anonymous"></script>
        <script>
            if (${(error!false)?string('true', 'false')}) {
                (new bootstrap.Modal("#loginModal")).show()
            }
            if (location.hash === "#loginNec") {
                (new bootstrap.Modal("#loginModal")).show()
                document.getElementById("loginNecessary").style.display = "inherit"
            }
        </script>
    </body>
</html>