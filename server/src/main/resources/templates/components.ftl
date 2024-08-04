<#macro loginModal error>
    <div class="modal fade" id="loginModal" tabindex="-1" aria-labelledby="loginModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="/" method="POST">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5" id="loginModalLabel">Login</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Chiudi"></button>
                    </div>
                    <div class="modal-body">
                        <#if error!false>
                            <div class="alert alert-danger" role="alert">
                                Dati non corretti
                            </div>
                        </#if>
                        <div class="alert alert-danger" role="alert" style="display: none" id="loginNecessary">
                            Per questa azione è necessario il login!
                        </div>
                        <div class="input-group flex-nowrap p-2">
                            <input type="text" class="form-control" placeholder="Username" aria-label="Username"
                                   aria-describedby="addon-wrapping" name="username">
                        </div>
                        <div class="input-group flex-nowrap p-2">
                            <input type="password" class="form-control" placeholder="Password" aria-label="Password"
                                   aria-describedby="addon-wrapping" name="password">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="Accedi" id="loginButton" name="button">
                    </div>
                </form>
            </div>
        </div>
    </div>
</#macro>
<!-- actual è "se true, allora fai 'if disabled', sennò fai 'if disabled == actual', cioè == false"-->
<#macro navItem disabled href title index exp>
    <li class="nav-item">
        <a class="nav-link <#if disabled>disabled</#if> <#if index == exp>active</#if>"
           <#if disabled>aria-disabled="true"</#if>
                <#if index == exp>aria-current="page"</#if>
           href="${href}">
            ${title}
        </a>
    </li>
</#macro>

<#macro navBar logged ind dev>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
            <a class="navbar-brand" href="#">
                <img src="assets/regicon.png" alt="Logo" height="28" class="d-inline-block align-text-top">
                Registro admin page <#if dev>dev</#if>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                    aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <@navItem disabled=false href="#" title="Home" index=ind exp=0/>
                    <@navItem disabled=false href="feedbackManager" title="Feedback" index=ind exp=1/>
                    <@navItem disabled=true href="#" title="Notifiche" index=ind exp=2/>
                    <@navItem disabled=false href="accessKeysManager" title="Chiavi di accesso" index=ind exp=3/>
                    <@navItem disabled=false href="changelogManager" title="Changelog" index=ind exp=4/>
                </ul>
            </div>
            <div id="navbarLogin">
                <#if logged == false>
                    <button class="btn btn-outline-success" type="submit" data-bs-toggle="modal"
                            data-bs-target="#loginModal">Login
                    </button>
                <#else>
                    <form action="/logout" method="POST">
                        <input type="submit" class="btn btn-danger" value="Esci" id="logoutButton" name="button">
                    </form>
                </#if>
            </div>
        </div>
    </nav>
</#macro>

