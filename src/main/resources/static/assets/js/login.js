function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    return regex.test(email);
}

function validarSenha(senha) {
    return senha.length >= 6
        && /[a-z]/.test(senha)
        && /[A-Z]/.test(senha)
        && /\d/.test(senha)
        && /[@$!%*?&.]/.test(senha);
}

function setErro(campo, mensagem) {
    campo.setCustomValidity(mensagem);

    const feedback = campo.nextElementSibling;
    if (feedback && feedback.classList.contains("invalid-feedback")) {
        feedback.textContent = mensagem;
    }
}

function limparErro(campo) {
    campo.setCustomValidity("");
}

function marcarCampoInvalido(campo, mensagem) {
    campo.classList.add("is-invalid");
    setErro(campo, mensagem);
}


function validarFormulario(form) {

    const email = $("#txtEmail").val().trim();
    const senha = $("#txtSenha").val();

    const campoEmail = $("#txtEmail")[0];
    const campoSenha = $("#txtSenha")[0];

    [campoEmail, campoSenha].forEach(limparErro);

    if (!validarEmail(email)) {
        setErro(campoEmail, "Informe um email válido.");
    }

    if (!validarSenha(senha)) {
        setErro(campoSenha, "A senha deve conter pelo menos 6 caracteres, uma letra maiúscula, uma letra minúscula, um número e um caractere especial.");
    }

    return form.checkValidity();
}


function criarUsuario() {

    return {
        email: $("#txtEmail").val().trim(),
        senha: $("#txtSenha").val(),
    };
}


function mensagemErroLogin() {

    $("#div-mensagem-login-erro").remove();

    let div = $("<div />");

    div.attr({
        id: 'div-mensagem-login-erro',
        class: 'alert alert-danger mt-2 text-center'
    });

    div.text(" Usuário inexistente ou senha inválida");

    $("#formulario_cadastro").append(div);

    const campoEmail = $("#txtEmail")[0];
    const campoSenha = $("#txtSenha")[0];

    marcarCampoInvalido(campoEmail, "Verifique seu email.");
    marcarCampoInvalido(campoSenha, "Verifique sua senha.");
}


$(document).ready(() => {

    const form = $("#formulario_cadastro")[0];

    $("#txtEmail, #txtSenha").on("input blur", function () {

        this.classList.remove("is-invalid");

        if (!form.classList.contains("was-validated")) {
            return;
        }

        validarFormulario(form);
    });

    $("#formulario_cadastro").on("submit", async (event) => {

        event.preventDefault();

        if (!validarFormulario(form)) {
            form.classList.add("was-validated");
            return;
        }

        form.classList.add("was-validated");

        const usuario = criarUsuario();

        await loginUsuario(usuario);

    });

});

async function loginUsuario(usuario) {

    if (!usuario) { return; }

    try {

        const response = await fetch('/auth/login', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuario)
        });

        if (!response.ok) {
            mensagemErroLogin();
			return;
        }

        window.location.href = 'home.html';

    } catch (error) {
        console.error(error);
    }

}