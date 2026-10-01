function validarNome(nome) {
    return nome.trim().length >= 3;
}

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

function validarCPF(cpf) {

    cpf = cpf.replace(/\D/g, "");

    if (cpf.length !== 11) {
        return false;
    }

    if (/^(\d)\1{10}$/.test(cpf)) {
        return false;
    }

    let soma = 0;

    for (let i = 0; i < 9; i++) {
        soma += Number(cpf.charAt(i)) * (10 - i);
    }

    let resto = soma % 11;
    let primeiroDigito = resto < 2 ? 0 : 11 - resto;

    if (primeiroDigito !== Number(cpf.charAt(9))) {
        return false;
    }

    soma = 0;

    for (let i = 0; i < 10; i++) {
        soma += Number(cpf.charAt(i)) * (11 - i);
    }

    resto = soma % 11;
    let segundoDigito = resto < 2 ? 0 : 11 - resto;

    return segundoDigito === Number(cpf.charAt(10));
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


function validarFormulario(form) {

    const nome = $("#txtNomeCompleto").val().trim();
    const email = $("#txtEmail").val().trim();
    const senha = $("#txtSenha").val();
    const cpf = $("#txtCPF").val();

    const campoNome = $("#txtNomeCompleto")[0];
    const campoEmail = $("#txtEmail")[0];
    const campoSenha = $("#txtSenha")[0];
    const campoCPF = $("#txtCPF")[0];

    [campoNome, campoEmail, campoSenha, campoCPF].forEach(limparErro);

    if (!validarNome(nome)) {
        setErro(campoNome, "O nome deve possuir pelo menos 3 caracteres.");
    }

    if (!validarEmail(email)) {
        setErro(campoEmail, "Informe um email válido.");
    }

    if (!validarSenha(senha)) {
        setErro(campoSenha, "A senha deve conter pelo menos 6 caracteres, uma letra maiúscula, uma letra minúscula, um número e um caractere especial.");
    }

    if (!validarCPF(cpf)) {
        setErro(campoCPF, "Informe um CPF válido.");
    }

    return form.checkValidity();
}


function criarUsuario() {
    return {
        nome: $("#txtNomeCompleto").val().trim(),
        email: $("#txtEmail").val().trim(),
        senha: $("#txtSenha").val(),
        cpf: $("#txtCPF").val().replace(/\D/g, "")
    };
}


$(document).ready(() => {

    $("#txtCPF").mask("000.000.000-00", {
        reverse: true
    });

    const form = $("#formulario_cadastro")[0];

    $("#txtNomeCompleto, #txtEmail, #txtSenha, #txtCPF").on("input blur", function () {

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

        await cadastrarUsuario(usuario)

    });

});

async function cadastrarUsuario(usuario) {

    if (!usuario) { return; }

    try {

        const response = await fetch('/auth/registrar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(usuario)
        });

        if (!response.ok) {

            if(response.status === 409)
            {
                mensagemErrorConflito();
            }

            throw new Error('Ops, houve um erro ao cadastrar o usuário');
        }

        const dados = await response.json();

        if (dados) {
            window.location.href = 'index.html';
        }

    } catch (error) {
        console.error(error);
    }

}

function mensagemErrorConflito() {

    $("#div-mensagem-conflito").remove();

    let div = $("<div />");

    div.attr({
        id: 'div-mensagem-conflito',
        class: 'alert alert-danger mt-2 text-center'
    });

    div.text("Ops, você já possui cadastro no sistema");

    $("#formulario_cadastro").append(div);

    const campoEmail = $("#txtEmail")[0];
    const campoCPF = $("#txtCPF")[0];

    marcarCampoInvalido(campoEmail, "Este email já está cadastrado.");
    marcarCampoInvalido(campoCPF, "Este CPF já está cadastrado.");
}

function marcarCampoInvalido(campo, mensagem) {

    campo.classList.add("is-invalid");
    setErro(campo, mensagem);
}