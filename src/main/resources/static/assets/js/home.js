const BASEURL = '';

const CONSTANTES_TIPO_ALERTA = {
    alertaErro: 0,
    alertaSuccesso: 1,
    alertaAviso: 2,
    alertaEdicao: 3
};

let idCategoriaSelecionada = null;
let idCategoriaRemover = null;
let graficoTotalCategoriaDespesas = null;
let mesSelecionado = null;
let idDespesaSelecionada = null;
let numeroParcelaSelecionada = null;
let despesasCarregadasAtual = [];

$(function () {
    'use strict';

    const hoje = new Date();

    mesSelecionado = hoje.getMonth() + 1;

    $("#slc_mes").val(mesSelecionado);
    $("#slc_mes_exportacao").val(mesSelecionado);

    $(".data_agora").text(obterNomeMes(mesSelecionado));

    carregarCategorias();
    carregarTotalDespesas(mesSelecionado);
    carregarTotalCategoriaDespesas(mesSelecionado);
    filtrarDespesas();

    $('#formCategoria').on('submit', async function (event) {
        event.preventDefault();
        event.stopPropagation();

        const form = this;
        const $form = $(form);

        $form.addClass('was-validated');

        if (form.checkValidity() === false) {
            return;
        }

        await criarCategoria($form);
    });

    $(document).on('click', '.botao-remover-despesa', function () {
        idDespesaSelecionada = $(this).data('id');

        const nomeDespesa = $(this).data('nome');

        $('#nomeDespesaRemover').text(nomeDespesa);

        const modal = bootstrap.Modal.getOrCreateInstance(
            document.getElementById('modalRemoverDespesa')
        );

        modal.show();
    });

    $('#btnConfirmarRemocaoDespesa').on('click', async function () {
        if (idDespesaSelecionada === null) {
            return;
        }

        await removerDespesa(idDespesaSelecionada);
    });

    $('#formEditarCategoria').on('submit', async function (event) {
        event.preventDefault();
        event.stopPropagation();

        const form = this;
        const $form = $(form);

        $form.addClass('was-validated');

        if (form.checkValidity() === false) {
            return;
        }

        await editarCategoria($form);
    });

    $('#formDespesa').on('submit', async function (event) {
        event.preventDefault();
        event.stopPropagation();

        const form = this;
        const $form = $(form);

        $form.addClass('was-validated');

        if (form.checkValidity() === false) {
            return;
        }

        await criarDespesa($form);
    });

    $('#formEditarDespesa').on('submit', async function (event) {
        event.preventDefault();
        event.stopPropagation();

        const form = this;
        const $form = $(form);

        $form.addClass('was-validated');

        if (form.checkValidity() === false) {
            return;
        }

        await editarDespesa($form);
    });
	
	$("#formExportacao").on("submit", function(event)
	{
		event.preventDefault();
		
		event.preventDefault();
        event.stopPropagation();

        const form = this;
        const $form = $(form);

        $form.addClass('was-validated');

        if (form.checkValidity() === false) {
            return;
        }
		
		let tipoExportacao = $("#slc_exportacao").val();
		let mes = $("#slc_mes_exportacao").val();
		gerenciarExportacaoDespesas(tipoExportacao, mes, $form);
	})

    $('#aPagar').on('change', function () {
        configurarCamposPagamento();
    });

    $('#parcelado').on('change', function () {
        configurarCampoParcelamento();
    });

    $('#editarAPagar').on('change', function () {
        configurarCamposPagamentoEdicao();
    });

    $('#editarParcelado').on('change', function () {
        configurarCampoParcelamentoEdicao();
    });

    $('#btnLimparFiltro').on('click', function () {
        limparFiltro();
    });

    $(document).on('click', '.botao-editar', function () {
        idCategoriaSelecionada = $(this).data('id');

        const nomeCategoria = $(this).data('nome');

        $('#txtEditarCategoria').val(nomeCategoria);

        $('#formEditarCategoria').removeClass('was-validated');

        const modal = bootstrap.Modal.getOrCreateInstance(
            document.getElementById('modalEditarCategoria')
        );

        modal.show();
    });

    $(document).on('click', '.botao-remover', function () {
        idCategoriaRemover = $(this).data('id');

        const nomeCategoria = $(this).data('nome');

        $('#nomeCategoriaRemover').text(nomeCategoria);

        const modal = bootstrap.Modal.getOrCreateInstance(
            document.getElementById('modalRemoverCategoria')
        );

        modal.show();
    });

    $('.btnSair').on('click', async function () {
        await logout();
    });

    $(document).on('click', '.botao-editar-despesa', function () {
        const id = $(this).data('id');

        const numeroParcela = $(this).data('numero-parcela');

        numeroParcelaSelecionada =
            numeroParcela !== undefined && numeroParcela !== ''
                ? Number(numeroParcela)
                : null;

        abrirModalEditarDespesa(id, numeroParcela);
    });

    $('#btnConfirmarRemocao').on('click', async function () {
        if (idCategoriaRemover === null) {
            return;
        }

        await removerCategoria(idCategoriaRemover);
    });

    buscaDinamicaModulos();

    renderizarSelectCategoria();

    $('#txtValor').mask("#.##0,00", {
        reverse: true
    });

    $('#txtEditarValor').mask("#.##0,00", {
        reverse: true
    });

    buscarDespesas();

    configurarCamposPagamento();

    const ano = hoje.getFullYear();

    const mes = String(hoje.getMonth() + 1).padStart(2, '0');

    const dia = String(hoje.getDate()).padStart(2, '0');

    const dataHoje = `${ano}-${mes}-${dia}`;

    $('#dtVencimento').attr('min', dataHoje);

    $(".data_agora").text(obterNomeMes(mesSelecionado));

    getUsuario();
});

async function getUsuario() {

    try {

        const response = await fetch(`${BASEURL}/auth/me`, {
            credentials: "include"
        });

        if (response.status === 401 || response.status === 403) {
            window.location.href = '/index.html';
            return;
        }

        if (!response.ok) {
            throw new Error("Erro ao consultar o usuario");
        }

        const responseJson = await response.json();

        let nome = responseJson.dados.nome
            .trim()
            .split(/\s+/)
            .slice(0, 3)
            .map(
                (parte) =>
                    parte.charAt(0).toUpperCase() +
                    parte.slice(1).toLowerCase()
            )
            .join(" ");

        let email = responseJson.dados.email;

        $(".nome_usuario").text(nome);
        $(".email_usuario").text(email);

        montaLogotipoUsuario(nome);

    } catch (error) {

        console.error(error);

        window.location.href = '/index.html';
    }
}

async function carregarCategorias() {
    try {
        const response = await fetch(`${BASEURL}/categorias`, {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar categorias');
        }

        const resposta = await response.json();

        renderTabelaCategorias(resposta.dados);

        return resposta.dados;
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao carregar as categorias'
        );

        return [];
    }
}

function renderTabelaCategorias(categorias) {
    const tbody = $('#tabela_categoria tbody');

    tbody.empty();

    if (!Array.isArray(categorias) || categorias.length === 0) {
        tbody.append(`
            <tr>
                <td colspan="2" class="text-center text-muted">

                    <img
                        src="../../assets/img/categoria/categoria-listavazia.png"
                        alt="Nenhuma categoria cadastrada"
                        class="img-fluid mx-auto d-block mb-2"
                        style="max-width: 380px;">

                    Nenhuma categoria cadastrada.

                </td>
            </tr>
        `);

        return;
    }

    categorias.forEach((categoria) => {
        const linha = `
            <tr>

                <td>
                    ${categoria.nome}
                </td>

                <td class="text-end">

                    <button
                        type="button"
                        class="btn btn-primary btn-sm botao-editar"
                        data-id="${categoria.id}"
                        data-nome="${categoria.nome}"
                        title="Editar"
                        style="
                            --bs-btn-padding-y: .25rem;
                            --bs-btn-padding-x: .5rem;
                            --bs-btn-font-size: .75rem;
                        "
                    >
                        <i class="bi bi-pencil-fill"></i>
                    </button>

                    <button
                        type="button"
                        class="btn btn-danger btn-sm botao-remover"
                        data-id="${categoria.id}"
                        data-nome="${categoria.nome}"
                        title="Remover"
						style="
	                        --bs-btn-padding-y: .25rem;
	                        --bs-btn-padding-x: .5rem;
	                        --bs-btn-font-size: .75rem;
	                    "
                    >
                        <i class="bi bi-trash-fill"></i>
                    </button>

                </td>

            </tr>
        `;

        tbody.append(linha);
    });
}

async function criarCategoria($form) {
    const categoria = {
        nome: $('#txtCategoria').val().trim()
    };

    try {
        const response = await fetch(`${BASEURL}/categorias`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(categoria)
        });

        if (!response.ok) {
            throw new Error('Erro ao criar categoria');
        }

        await carregarCategorias();

        await renderizarSelectCategoria();

        $form[0].reset();

        $form.removeClass('was-validated');

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaSuccesso,
            'Categoria criada com sucesso'
        );
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao criar a categoria'
        );
    }
}

async function editarCategoria($form) {
    if (idCategoriaSelecionada === null) {
        return;
    }

    const categoria = {
        nome: $('#txtEditarCategoria').val().trim()
    };

    try {
        const response = await fetch(
            `${BASEURL}/categorias/${idCategoriaSelecionada}`,
            {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(categoria)
            }
        );

        if (!response.ok) {
            throw new Error('Erro ao editar categoria');
        }

        await carregarCategorias();

        await renderizarSelectCategoria();

        const modal = bootstrap.Modal.getInstance(
            document.getElementById('modalEditarCategoria')
        );

        modal.hide();

        idCategoriaSelecionada = null;

        $form[0].reset();

        $form.removeClass('was-validated');

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaEdicao,
            'Categoria editada com sucesso'
        );

        $("#txtCategoria").focus();
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao editar a categoria'
        );
    }
}

async function removerCategoria(idCategoria) {
    try {
        const response = await fetch(
            `${BASEURL}/categorias/${idCategoria}`,
            {
                method: 'PATCH',
                credentials: 'include'
            }
        );

        if (!response.ok) {
            throw new Error('Erro ao remover categoria');
        }

        await carregarCategorias();

        await renderizarSelectCategoria();

        const modal = bootstrap.Modal.getInstance(
            document.getElementById('modalRemoverCategoria')
        );

        modal.hide();

        idCategoriaRemover = null;

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaSuccesso,
            'Categoria removida com sucesso'
        );

        $("#txtCategoria").focus();
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao remover a categoria'
        );
    }
}

function alertaMensagem(tipo, mensagem) {
    if (tipo === undefined || tipo === null || !mensagem) {
        return;
    }

    let configToast = {
        backgroundColor: 'text-bg-primary',
        icone: 'bi-info-circle'
    };

    if (tipo === CONSTANTES_TIPO_ALERTA.alertaErro) {
        configToast.backgroundColor = 'text-bg-danger';
        configToast.icone = 'bi-x-circle';
    } else if (tipo === CONSTANTES_TIPO_ALERTA.alertaSuccesso) {
        configToast.backgroundColor = 'text-bg-success';
        configToast.icone = 'bi-check-circle';
    } else if (tipo === CONSTANTES_TIPO_ALERTA.alertaAviso) {
        configToast.backgroundColor = 'text-bg-warning';
        configToast.icone = 'bi-exclamation-circle';
    } else if (tipo === CONSTANTES_TIPO_ALERTA.alertaEdicao) {
        configToast.backgroundColor = 'text-bg-primary';
        configToast.icone = 'bi-exclamation-circle';
    }

    const $divToast = $(`
        <div
            class="toast align-items-center ${configToast.backgroundColor} border-0"
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
        >

            <div class="d-flex">

                <div class="toast-body">

                    <i class="bi ${configToast.icone}"></i>

                    ${mensagem}

                </div>

                <button
                    type="button"
                    class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast"
                    aria-label="Close"
                >
                </button>

            </div>

        </div>
    `);

    $('#areaToast').append($divToast);

    const toast = new bootstrap.Toast($divToast[0]);

    toast.show();

    $divToast.on('hidden.bs.toast', function () {
        $divToast.remove();
    });
}

function buscaDinamicaModulos() {
    const $modulos = $('#lista-itens-modulo a');

    $('#pesquisarModulo').on('input', function () {
        const moduloPesquisado = $(this).val().toLowerCase().trim();

        $modulos.each(function () {
            const nomeModulo = $(this).text().toLowerCase().trim();

            if (nomeModulo.includes(moduloPesquisado)) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });
}

async function renderizarSelectCategoria() {
    try {
        $("#slcCategoria").empty();

        $("#slcEditarCategoria").empty();

        $("#slcCategoria").append(
            "<option value=''>Selecione uma categoria</option>"
        );

        $("#slcEditarCategoria").append(
            "<option value=''>Selecione uma categoria</option>"
        );

        const categorias = await carregarCategorias();

        if (categorias.length > 0) {
            for (let i = 0; i < categorias.length; i++) {
                const categoria = categorias[i];

                $("#slcCategoria").append(
                    $("<option>", {
                        value: categoria.id,
                        text: categoria.nome
                    })
                );

                $("#slcEditarCategoria").append(
                    $("<option>", {
                        value: categoria.id,
                        text: categoria.nome
                    })
                );
            }
        }
    } catch (error) {
        console.error(error);
    }
}

function configurarCamposPagamento() {
    const aPagar = $('#aPagar').is(':checked');

    $('#grupoDataVencimento').toggle(aPagar);

    $('#grupoParcelado').toggle(aPagar);

    $('#grupoParceladoPago').toggle(aPagar);

    $('#dtVencimento').prop('required', aPagar);

    $('#parcelado').prop('disabled', !aPagar);

    if (!aPagar) {
        $('#parcelado').prop('checked', false);

        $('#grupoQuantidadeParcela').hide();

        $('#quantidadeParcela').val('');

        $('#quantidadeParcela').prop('disabled', true);

        $('#quantidadeParcela').prop('required', false);

        return;
    }

    configurarCampoParcelamento();
}

function configurarCampoParcelamento() {
    const aPagar = $('#aPagar').is(':checked');

    const parcelado = $('#parcelado').is(':checked');

    const habilitado = aPagar && parcelado;

    $('#grupoQuantidadeParcela').toggle(habilitado);

    $('#quantidadeParcela').prop('disabled', !habilitado);

    $('#quantidadeParcela').prop('required', habilitado);

    if (!habilitado) {
        $('#quantidadeParcela').val('');
    }
}

function configurarCamposPagamentoEdicao() {
    const aPagar = $('#editarAPagar').is(':checked');

    $('#grupoEditarDataVencimento').toggle(aPagar);

    $('#grupoEditarParcelado').toggle(aPagar);

    $('#grupoEditarParceladoPago').toggle(aPagar);

    $('#dtEditarVencimento').prop('required', aPagar);

    $('#editarParcelado').prop('disabled', !aPagar);

    if (!aPagar) {
        $('#editarParcelado').prop('checked', false);

        $('#grupoEditarQuantidadeParcela').hide();

        $('#editarQuantidadeParcela').val('');

        $('#editarQuantidadeParcela').prop('disabled', true);

        $('#editarQuantidadeParcela').prop('required', false);

        return;
    }

    configurarCampoParcelamentoEdicao();
}

function configurarCampoParcelamentoEdicao() {
    const aPagar = $('#editarAPagar').is(':checked');

    const parcelado = $('#editarParcelado').is(':checked');

    const habilitado = aPagar && parcelado;

    $('#grupoEditarQuantidadeParcela').toggle(habilitado);

    $('#editarQuantidadeParcela').prop('disabled', !habilitado);

    $('#editarQuantidadeParcela').prop('required', habilitado);

    if (!habilitado) {
        $('#editarQuantidadeParcela').val('');
    }
}

async function criarDespesa($form) {
    const aPagar = $('#aPagar').is(':checked');

    const parcelado = $('#parcelado').is(':checked');

    const parcelaPaga = $('#parcelaPaga').is(':checked');

    const valor = Number(
        $('#txtValor')
            .val()
            .trim()
            .replace(/\./g, '')
            .replace(',', '.')
    );

    const despesa = {
        nome: $('#txtDespesa').val().trim(),

        dataVencimento: aPagar ? $('#dtVencimento').val() : null,

        valor: valor,

        descricao: $('#txtDescricao').val().trim(),

        aPagar: aPagar,

        parcelado: aPagar ? parcelado : false,

        quantidadeParcela:
            aPagar && parcelado
                ? Number($('#quantidadeParcela').val())
                : null,

        parcelaPaga: parcelaPaga,

        categoria: Number($('#slcCategoria').val())
    };

    try {
        const response = await fetch(`${BASEURL}/despesas`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(despesa)
        });

        if (!response.ok) {
            const erro = await response.json().catch(() => null);

            console.error(erro);

            throw new Error('Erro ao criar despesa');
        }

        $form[0].reset();

        $form.removeClass('was-validated');

        configurarCamposPagamento();

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaSuccesso,
            'Despesa criada com sucesso'
        );

        await buscarDespesas();

        const modal = bootstrap.Modal.getInstance(
            document.getElementById('modalDespesas')
        );

        modal.hide();

        await carregarTotalDespesas(mesSelecionado);

        await carregarTotalCategoriaDespesas(mesSelecionado);
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao criar a despesa'
        );
    }
}

function abrirModalEditarDespesa(id, numeroParcela) {
    const numeroParcelaClicado =
        numeroParcela === undefined || numeroParcela === ''
            ? null
            : Number(numeroParcela);

    const despesa = despesasCarregadasAtual.find((despesa) => {
        if (Number(despesa.id) !== Number(id)) {
            return false;
        }

        const numeroParcelaDaLinha = despesa.numeroParcela ?? null;

        return Number(numeroParcelaDaLinha) === Number(numeroParcelaClicado);
    });


    if (!despesa) {
        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, não foi possível encontrar essa despesa'
        );

        return;
    }

    idDespesaSelecionada = despesa.id;

    $('#txtEditarDespesa').val(despesa.nome);

    $('#txtEditarDescricao').val(despesa.descricao);

    $('#slcEditarCategoria').val(despesa.categoria);

    $('#txtEditarValor').val(
        Number(despesa.valor).toLocaleString('pt-BR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        })
    );

    $('#editarAPagar').prop('checked', !!despesa.aPagar);

    $('#editarParcelado').prop('checked', !!despesa.parcelado);

    $('#editarParcelaPaga').prop('checked', !!despesa.parcelaPaga);

    $('#editarQuantidadeParcela').val(despesa.quantidadeParcela ?? '');

    $('#dtEditarVencimento').val(
        despesa.dataVencimento ? despesa.dataVencimento.split('T')[0] : ''
    );

    configurarCamposPagamentoEdicao();

    $('#formEditarDespesa').removeClass('was-validated');

    const modal = bootstrap.Modal.getOrCreateInstance(
        document.getElementById('modalEditarDespesa')
    );

    modal.show();
}

async function editarDespesa($form) {
    if (idDespesaSelecionada === null) {
        return;
    }

    const aPagar = $('#editarAPagar').is(':checked');

    const parcelado = $('#editarParcelado').is(':checked');

    const parcelaPaga = $('#editarParcelaPaga').is(':checked');

    const valor = Number(
        $('#txtEditarValor')
            .val()
            .trim()
            .replace(/\./g, '')
            .replace(',', '.')
    );

    const despesa = {
        nome: $('#txtEditarDespesa').val().trim(),

        dataVencimento: aPagar ? $('#dtEditarVencimento').val() : null,

        valor: valor,

        descricao: $('#txtEditarDescricao').val().trim(),

        aPagar: aPagar,

        parcelado: aPagar ? parcelado : false,

        quantidadeParcela:
            aPagar && parcelado
                ? Number($('#editarQuantidadeParcela').val())
                : null,

        parcelaPaga: parcelaPaga,

        numeroParcelaEditada: numeroParcelaSelecionada,

        categoria: Number($('#slcEditarCategoria').val())
    };

    try {
        const response = await fetch(
            `${BASEURL}/despesas/${idDespesaSelecionada}`,
            {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(despesa)
            }
        );

        if (!response.ok) {
            const erro = await response.json().catch(() => null);

            console.error("Erro retornado pelo backend:", erro);

            throw new Error('Erro ao editar despesa');
        }

        $form[0].reset();

        $form.removeClass('was-validated');

        configurarCamposPagamentoEdicao();

        const modal = bootstrap.Modal.getInstance(
            document.getElementById('modalEditarDespesa')
        );

        modal.hide();

        idDespesaSelecionada = null;

        numeroParcelaSelecionada = null;

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaEdicao,
            'Despesa editada com sucesso'
        );

        await buscarDespesas();

        await carregarTotalDespesas(mesSelecionado);

        await carregarTotalCategoriaDespesas(mesSelecionado);
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao editar a despesa'
        );
    }
}

async function buscarDespesas(page = 0, size = 6) {

    try {

        let url = `${BASEURL}/despesas?page=${page}&size=${size}`;

        if (mesSelecionado !== null) {


            url += `&mes=${mesSelecionado}`;
        }


        const response = await fetch(url, {

            credentials: 'include'

        });

        if (!response.ok) {

            throw new Error('Erro ao carregar despesas');

        }

        const resposta = await response.json();

        if (resposta.dados) {

            $("#qtd_despesa").text(resposta.dados.totalElements || 0);

        }

        despesasCarregadasAtual = resposta.dados.content || [];

        renderizarCardDespesas(resposta.dados.content);

        renderizarBotoes(resposta.dados, page);

    } catch (error) {

        console.error(error);

        alertaMensagem(

            CONSTANTES_TIPO_ALERTA.alertaErro,

            'Opss, houve um erro ao carregar as despesas'

        );

    }

}

function formatarData(dataInput, mes) {
    if (!dataInput) {
        return "";
    }

    const meses = [
        'Jan.',
        'Fev.',
        'Mar.',
        'Abr.',
        'Mai.',
        'Jun.',
        'Jul.',
        'Ago.',
        'Set.',
        'Out.',
        'Nov.',
        'Dez.'
    ];

    if (!mes) {
        let ano;
        let mesIndice;
        let dia;

        if (dataInput instanceof Date) {
            ano = dataInput.getFullYear();
            mesIndice = dataInput.getMonth();
            dia = dataInput.getDate();
        } else if (typeof dataInput === 'string') {
            const apenasData = dataInput.split('T')[0];

            const partes = apenasData.split('-');

            ano = parseInt(partes[0]);
            mesIndice = parseInt(partes[1]) - 1;
            dia = parseInt(partes[2]);
        }

        const nomeMes = meses[mesIndice];

        return `${dia} ${nomeMes} de ${ano}`;
    }

    const mesesCompletos = [
        'Janeiro',
        'Fevereiro',
        'Março',
        'Abril',
        'Maio',
        'Junho',
        'Julho',
        'Agosto',
        'Setembro',
        'Outubro',
        'Novembro',
        'Dezembro'
    ];

    let mesIndice;

    if (dataInput instanceof Date) {
        mesIndice = dataInput.getMonth();
    }

    return mesesCompletos[mesIndice];
}

function obterNomeMes(mes) {
    const meses = [
        'Janeiro',
        'Fevereiro',
        'Março',
        'Abril',
        'Maio',
        'Junho',
        'Julho',
        'Agosto',
        'Setembro',
        'Outubro',
        'Novembro',
        'Dezembro'
    ];

    return meses[Number(mes) - 1];
}

async function carregarTotalDespesas(mes) {
    try {
        let url = `${BASEURL}/despesas/total`;

        if (mes != null) {
            url += `?mes=${mes}`;
        }

        const response = await fetch(url, {
            method: "GET",
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar total de despesas');
        }

        const resultado = await response.json();

        $(".valor_despesas").text(
            Number(resultado.dados).toLocaleString("pt-BR", {
                style: "currency",
                currency: "BRL"
            })
        );
    } catch (error) {
        console.error(error);
    }
}

async function carregarTotalCategoriaDespesas(mes) {
    try {
        let url = `${BASEURL}/despesas/totalCategoriaDespesas`;

        if (mes != null) {
            url += `?mes=${mes}`;
        }

        const response = await fetch(url, {
            method: "GET",
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar total por categoria');
        }

        const resultado = await response.json();

        renderizarGraficoTotalCategoriaDespesas(resultado.dados);
    } catch (error) {
        console.error(error);
    }
}

function renderizarGraficoTotalCategoriaDespesas(dados) {
    if (!Array.isArray(dados)) {
        console.error("DADOS ESTÁ UNDEFINED OU NÃO É ARRAY!");

        return;
    }

    const canvas = document.getElementById("grafico-categorias-despesas");

    if (!canvas) {
        return;
    }

    if (dados.length <= 0) {
        $("#card_grafico_despesas").hide();
    } else {
        $("#card_grafico_despesas").show();
    }

    if (graficoTotalCategoriaDespesas) {
        graficoTotalCategoriaDespesas.destroy();
    }

    const dadosGrafico = dados.map((dado) => ({
        categoria: dado.categoria,
        totalReal: Number(dado.total),
        totalGrafico: Math.max(Number(dado.total), 5)
    }));

    graficoTotalCategoriaDespesas = new Chart(canvas, {
        type: 'doughnut',

        data: {
            labels: dadosGrafico.map((dado) => dado.categoria),

            datasets: [
                {
                    label: 'Total',

                    data: dadosGrafico.map((dado) => dado.totalGrafico),

                    backgroundColor: [
                        '#6366F1',
                        '#22C55E',
                        '#F59E0B',
                        '#EF4444',
                        '#06B6D4',
                        '#A855F7',
                        '#EC4899',
                        '#14B8A6',
                        '#F97316',
                        '#8B5CF6',
                        '#84CC16',
                        '#E11D48'
                    ],

                    borderColor: '#FFFFFF',

                    borderWidth: 4,

                    borderRadius: 10,

                    spacing: 2,

                    hoverOffset: 4
                }
            ]
        },

        options: {
            responsive: true,

            maintainAspectRatio: false,

            cutout: '68%',

            plugins: {
                legend: {
                    display: true,

                    position: 'bottom',

                    labels: {
                        usePointStyle: true,

                        pointStyle: 'circle',

                        padding: 20,

                        font: {
                            size: 13
                        }
                    }
                },

                tooltip: {
                    backgroundColor: '#212529',

                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },

                    bodyFont: {
                        size: 13
                    },

                    padding: 12,

                    cornerRadius: 8,

                    callbacks: {
                        label: function (context) {
                            const dado = dadosGrafico[context.dataIndex];

                            return ` R$ ${dado.totalReal.toLocaleString('pt-BR', {
                                minimumFractionDigits: 2,
                                maximumFractionDigits: 2
                            })}`;
                        }
                    }
                }
            },

            animation: {
                animateRotate: true,
                animateScale: true,
                duration: 1000
            }
        }
    });
}

function renderizarBotoes(dados, paginaAtual) {
    if (!dados) {
        return;
    }

    const $botoes_paginacao = $("#botoes_paginacao");

    $botoes_paginacao.empty();

    const totalPaginas = dados.totalPages;

    if (!totalPaginas || totalPaginas <= 0) {
        return;
    }

    for (let i = 0; i < totalPaginas; i++) {
        const pagina = i + 1;

        const $botao = $(`
            <button
                class="btn btn-light botao_pagina border"
                value="${i}"
            >
                ${pagina}
            </button>
        `);

        if (i === Number(paginaAtual)) {
            $botao.addClass("ativo");
        }

        $botoes_paginacao.append($botao);
    }
}

$(document).on("click", ".botao_pagina", function () {
    const pagina = $(this).val();

    buscarDespesas(Number(pagina));
});

function filtrarDespesas() {
    $("#pesquisar_filtro").on("click", function (event) {
        event.preventDefault();

        const mes = $("#slc_mes").val();

        mesSelecionado = mes === "" ? null : Number(mes);

        buscarDespesas(0);

        carregarTotalCategoriaDespesas(mesSelecionado);

        carregarTotalDespesas(mesSelecionado);

        if (mesSelecionado !== null) {
            $(".data_agora").text(obterNomeMes(mesSelecionado));
        } else {
            $(".data_agora").text("Todos os meses");
        }
    });
}

function verificarPrazoVencimento(dataVencimento) {
    if (!dataVencimento) {
        return {
            status: "PAGA",
            classe: "paga",
            dias: null
        };
    }

    const hoje = new Date();

    const [anoHoje, mesHoje, diaHoje] = [
        hoje.getFullYear(),
        hoje.getMonth() + 1,
        hoje.getDate()
    ];

    const [anoVencimento, mesVencimento, diaVencimento] = dataVencimento
        .split("-")
        .map(Number);

    const hojeSemHora = new Date(anoHoje, mesHoje - 1, diaHoje);

    const vencimentoSemHora = new Date(
        anoVencimento,
        mesVencimento - 1,
        diaVencimento
    );

    const diferenca = vencimentoSemHora - hojeSemHora;

    const dias = diferenca / (1000 * 60 * 60 * 24);

    if (dias < 0) {
        return {
            status: "VENCIDA",
            classe: "vencida",
            dias: dias
        };
    }

    if (dias <= 5) {
        return {
            status: "URGENTE",
            classe: "urgente",
            dias: dias
        };
    }

    if (dias < 10) {
        return {
            status: "ATENÇÃO",
            classe: "atencao",
            dias: dias
        };
    }

    return {
        status: "NO PRAZO",
        classe: "no-prazo",
        dias: dias
    };
}

function montaLogotipoUsuario(nomeUsuario) {
    let letras = nomeUsuario != null ? nomeUsuario.split(" ") : "";

    let sigla = "";

    let qtdLimteSiglas = 0;

    for (let i = 0; i < letras.length; i++) {
        sigla += letras[i].charAt(0);

        qtdLimteSiglas++;

        if (qtdLimteSiglas === 2) {
            break;
        }
    }

    $(".siglas_usuario").text(sigla);

    $(".siglas_usuario").prop('title', nomeUsuario);
}

async function removerDespesa(idDespesa) {
    try {
        const response = await fetch(`${BASEURL}/despesas/${idDespesa}`, {
            method: 'PATCH',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Erro ao remover despesa');
        }

        const modal = bootstrap.Modal.getInstance(
            document.getElementById('modalRemoverDespesa')
        );

        modal.hide();

        idDespesaSelecionada = null;

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaSuccesso,
            'Despesa removida com sucesso'
        );

        await buscarDespesas();

        await carregarTotalDespesas(mesSelecionado);

        await carregarTotalCategoriaDespesas(mesSelecionado);
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao remover a despesa'
        );
    }
}

async function logout() {
    try {
        const response = await fetch(`${BASEURL}/auth/logout`, {
            method: 'POST',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Erro ao realizar logout');
        }

        window.location.href = '/index.html';
    } catch (error) {
        console.error(error);

        alertaMensagem(
            CONSTANTES_TIPO_ALERTA.alertaErro,
            'Opss, houve um erro ao sair da conta'
        );
    }
}

function gerenciarExportacaoDespesas(tipoExportacao, mes, $form)
{
	if(!tipoExportacao){return;};
	
	if(tipoExportacao === 'excel')
	{
		gerarRelatorioEXCEL(tipoExportacao, mes, $form);
	}
	else
	{
		gerarRelatorioPDF(tipoExportacao, mes, $form)
	}
}

async function gerarRelatorioPDF(tipoExportacao, mes, $form) {

    if (!tipoExportacao || !$form) {
        return;
    }

    let url = `${BASEURL}/relatorio/despesas`;

    if (mes) {
        url += `?mes=${mes}`;
    }

    const response = await fetch(url);

    if (!response.ok) 
	{
		if(response.status === 404)
		{
			alertaMensagem(CONSTANTES_TIPO_ALERTA.alertaAviso, "Não existem despesas para o período selecionado");
			return;
		}
		
        alertaMensagem(CONSTANTES_TIPO_ALERTA.alertaErro, "Ops, houve um erro ao gerar o relatório");
        return;
    }

    const blob = await response.blob();

    const urlArquivo = window.URL.createObjectURL(blob);

    window.open(urlArquivo, '_blank');
	
	$form[0].reset();

	const modal = bootstrap.Modal.getInstance(document.getElementById('modalExportar'));
    modal.hide();
	
    $form.removeClass('was-validated');
	
}

async function gerarRelatorioEXCEL(tipoExportacao, mes, $form) {

    if (!tipoExportacao || !$form) {
        return;
    }

    let url = `${BASEURL}/relatorio/despesas/excel`;

    if (mes) {
        url += `?mes=${mes}`;
    }

    const response = await fetch(url, {
        credentials: 'include'
    });

    if (!response.ok) {

        if (response.status === 404) {
            alertaMensagem(CONSTANTES_TIPO_ALERTA.alertaAviso, "Não existem despesas para o período selecionado");
            return;
        }

        alertaMensagem(CONSTANTES_TIPO_ALERTA.alertaErro, "Ops, houve um erro ao gerar o relatório excel");
        return;
    }

    const blob = await response.blob();

    const urlArquivo = window.URL.createObjectURL(blob);

    const link = document.createElement('a');

    link.href = urlArquivo;
    link.download = 'controle-financeiro.xlsx';

    document.body.appendChild(link);

    link.click();

    link.remove();

    window.URL.revokeObjectURL(urlArquivo);

    $form[0].reset();

    const modal = bootstrap.Modal.getInstance(document.getElementById('modalExportar'));

    modal.hide();

    $form.removeClass('was-validated');
}

function renderizarCardDespesas(despesas) {
    if (!Array.isArray(despesas)) {
        return;
    }

    const $row = $("#cards-despesas");

    $row.empty();

    if (despesas.length <= 0) {
        const $img = $(`
            <div class="col-12 text-center">

                <img
                    src="assets/img/despesas-vazia.png"
                    class="img-fluid"
                    alt="Nenhuma despesa cadastrada"
                    style="max-width: 500px"
                >

                <h3 class="text-secondary display-6">
                    Você ainda não possui despesas neste mês
                </h3>

            </div>
        `);

        $row.append($img);

        return;
    }

    for (let i = 0; i < despesas.length; i++) {
        const despesa = despesas[i];

        if (!despesa.categoria) {
            continue;
        }

        const prazo = verificarPrazoVencimento(despesa.dataVencimento);

        const valor = Number(despesa.valor).toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL"
        });

        let configBagde = {
            alert: "alert-info",
            texto: "No prazo"
        };

        if (despesa.parcelaPaga) {
            configBagde.alert = "alert-success";
            configBagde.texto = "Paga";
        } else if (prazo.classe === "vencida") {
            configBagde.alert = "alert-danger";
            configBagde.texto = "Vencida";
        } else if (prazo.classe === "urgente") {
            configBagde.alert = "alert-danger";
            configBagde.texto = "Urgente";
        } else if (prazo.classe === "atencao") {
            configBagde.alert = "alert-warning";
            configBagde.texto = "Atenção";
        } else if (prazo.classe === "paga") {
            configBagde.alert = "alert-success";
            configBagde.texto = "Paga";
        }

        const vencimento = despesa.dataVencimento
            ? `Vencimento, ${formatarData(despesa.dataVencimento, null)}`
            : '';

        const nomeFormatado =
            despesa.nome.charAt(0).toUpperCase() +
            despesa.nome.slice(1).toLowerCase();

        const textoTipoValor = despesa.parcelado
            ? '/ Valor da parcela'
            : '/ Total da despesa';

        const htmlParcela = despesa.parcelado
            ? `
                <div class="text-secondary font-control-finance-12px">

                    Parcela
                    ${despesa.numeroParcela}

                    de
                    ${despesa.quantidadeParcela}

                </div>
            `
            : '';

        const $card = $(`
            <div class="col-12 col-sm-6 d-flex">

                <div class="card card-despesas rounded-3 w-100 h-100">

                    <div class="card-body d-flex flex-column gap-2 p-2">

                        <div class="d-flex align-items-center justify-content-between">

                            <div>

                                <h3 class="font-control-finance-16px fw-bold m-0 nome-despesas">
                                    ${nomeFormatado}
                                </h3>

                                <p class="mt-1 border font-control-finance-12px nome-categoria">
                                    ${despesa.categoriaNome}
                                </p>

                            </div>

                            <div class="d-flex align-items-center gap-2">

                                <a
                                    href="javascript:void(0)"
                                    class="text-decoration-none text-secondary botao-editar-despesa"
                                    data-id="${despesa.id}"
                                    data-numero-parcela="${despesa.numeroParcela ?? ''}"
                                    title="Editar despesa"
                                >
                                    <i class="bi bi-pencil-fill icone-card"></i>
                                </a>

                                <a
                                    href="javascript:void(0)"
                                    class="text-decoration-none text-secondary botao-remover-despesa"
                                    data-id="${despesa.id}"
                                    data-nome="${despesa.nome}"
                                    title="Remover despesa"
                                >
                                    <i class="bi bi-trash icone-card"></i>
                                </a>

                            </div>

                        </div>

                        <p class="text-dark fw-bold fs-5 m-0">

                            ${valor}

                            <span class="text-secondary fw-normal text-opacity-50 font-control-finance-12px">
                                ${textoTipoValor}
                            </span>

                        </p>

                        ${htmlParcela}

                    </div>

                    <div class="card-footer border-0 p-2 bg-white rounded-3 mt-auto d-flex align-items-center justify-content-between">

                        <span class="alert ${configBagde.alert} fw-bold px-1 py-0 font-control-finance-12px m-0">
                            ${configBagde.texto}
                        </span>

                        <p class="m-0 font-control-finance-12px text-secondary">
                            ${vencimento}
                        </p>

                    </div>

                </div>

            </div>
        `);

        $row.append($card);
    }
}