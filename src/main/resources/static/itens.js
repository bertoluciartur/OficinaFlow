// Linhas de item da OS (nova OS e adicionar itens): campos por tipo, valor da linha e resumo.
// Ids esperados na página: itens, linha (template), subtotal, valorDesconto, total, desconto; base é opcional.
const CAMPOS = {
  '': {},
  PECA: { peca: 'Peça', qtd: 'Quantidade' },
  MAO_DE_OBRA: { desc: 'Descrição do serviço', v1: 'Horas trabalhadas', v2: 'Valor da hora (R$)' },
  SERVICO_FIXO: { desc: 'Descrição do serviço', v1: 'Valor (R$)' },
  DIAGNOSTICO: { qtd: 'Nº de sistemas analisados', v1: 'Taxa fixa (R$)', v2: 'Valor por sistema (R$)' }
};
const brl = n => n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
const num = s => parseFloat(String(s).replace(',', '.')) || 0;
const campo = (it, n) => it.querySelector('[name=' + n + ']');

function addItem() {
  document.getElementById('itens').appendChild(document.getElementById('linha').content.cloneNode(true));
  mudouTipo(document.querySelector('#itens .item:last-child [name=tipo]'));
}

function mudouTipo(sel) {
  const item = sel.closest('.item'), cfg = CAMPOS[sel.value];
  item.querySelectorAll('[data-campo]').forEach(l => {
    const nome = l.dataset.campo, ativo = nome in cfg;
    l.style.display = ativo ? '' : 'none';
    if (!ativo) { l.querySelector('input,select').value = ''; }
    else if (nome !== 'peca') { l.querySelector('span').textContent = cfg[nome]; }
  });
  recalcular();
}

function valorItem(it) {
  const tipo = campo(it, 'tipo').value, q = num(campo(it, 'qtd').value);
  const v1 = num(campo(it, 'v1').value), v2 = num(campo(it, 'v2').value);
  if (tipo === 'PECA') {
    const o = campo(it, 'peca').selectedOptions[0];
    return (o && o.dataset.valor ? num(o.dataset.valor) : 0) * q;
  }
  if (tipo === 'MAO_DE_OBRA') return v1 * v2;
  if (tipo === 'SERVICO_FIXO') return v1;
  if (tipo === 'DIAGNOSTICO') return v1 + v2 * q;
  return 0;
}

let pedido = 0;
async function recalcular() {
  const base = document.getElementById('base');
  let subtotal = base ? num(base.value) : 0;
  document.querySelectorAll('#itens .item').forEach(it => {
    const v = valorItem(it);
    it.querySelector('.vl').textContent = brl(v);
    subtotal += v;
  });
  document.getElementById('subtotal').textContent = brl(subtotal);
  // O desconto é calculado no servidor, com a mesma política que será gravada na OS.
  const meu = ++pedido, codigo = document.getElementById('desconto').value;
  let desconto = 0;
  try {
    const r = await fetch('/os/desconto?codigo=' + encodeURIComponent(codigo) + '&subtotal=' + subtotal);
    desconto = r.ok ? parseFloat(await r.text()) || 0 : 0;
  } catch (e) { desconto = 0; }
  if (meu !== pedido) return;
  document.getElementById('valorDesconto').textContent = '- ' + brl(desconto);
  document.getElementById('total').textContent = brl(subtotal - desconto);
}
