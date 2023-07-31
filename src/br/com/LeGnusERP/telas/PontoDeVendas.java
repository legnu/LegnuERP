/*
 * The MIT License
 *
 * Copyright 2022 Ad3ln0r.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.LeGnusERP.telas;

import br.com.LeGnusERP.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JRViewer;

/**
 *
 * @author Ad3ln0r
 */
public class PontoDeVendas extends javax.swing.JFrame {

    /**
     * Creates new form PontoDeVendas
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String tipo;
    String funcionario;
    String identificador;
    String identificadorGasto;
    String formaPagamento = "";
    String formaPagamentoNota = "";

    String nomeMaquininha;
    String taxaDebito;
    String taxaCredito;
    String antecipacao;
    String taxaAntecipacao;
    String compensacaoDebito;
    String compensacaoCredito;

    String vezesCrediario;
    String vezesBoleto;

    String selecionada;
    int certo;
    float taxaBoleto = 0;

    String valorTotal = "0";

    String imgPix = "";

    public PontoDeVendas() {
        initComponents();
        conexao = ModuloConexao.conector();
        setIcon();
        taxaBoleto();
        imgPIX();
    }

    private void taxaBoleto() {
        try {
            pst = conexao.prepareStatement("select taxaBoleto from tbrelatorio where idRelatorio=1");
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            if ((Float.parseFloat(tbAuxilio.getModel().getValueAt(0, 0).toString()) > 0) == true) {
                taxaBoleto = Float.parseFloat(tbAuxilio.getModel().getValueAt(0, 0).toString());
            } else {
                taxaBoleto = 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Conclua a configuração da tela de configurações");
        }
    }

    private void imgPIX() {
        try {
            pst = conexao.prepareStatement("select pix from tbrelatorio where idRelatorio=1");
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            imgPix = tbAuxilio.getModel().getValueAt(0, 0).toString();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Conclua a configuração da tela de configurações");
        }
    }

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
    }

    public void iniciar() {
        instanciarTabela();
        InstanciarComboboxMaquininha();
        InstanciarComboboxComanda();
        instanciarTabelaVenda();
        instanciarTabelaCliente();
        txtPreco.setText("0.00");
        soma();
    }

    public void instanciarTabela() {
        try {
            if (rbProduto.isSelected() == true) {
                instanciarTabelaAuxilioProduto();
                String sql = "select idproduto as ID, produto as Produto, valor_venda as Preço, obs as Observações from tbprodutos where quantidade > 0";
                tipo = "Produto";
                txtCodigo.setEnabled(false);
                txtPesquisa.setEnabled(true);
                taDescricao.setEnabled(true);
                btnMostrarFoto.setEnabled(true);
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                txtQuantidade.setEnabled(true);
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } else if (rbOrdemDeServico.isSelected() == true) {
                String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, funcionario as Funcionario, data_os as Data, cliente as Cliente from tbos where tipo='Ordem de Serviço'";
                tipo = "OS";
                txtCodigo.setEnabled(false);
                txtPesquisa.setEnabled(true);
                taDescricao.setEnabled(true);
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                txtQuantidade.setEnabled(false);
                btnMostrarFoto.setEnabled(false);
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "OS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } else if (rbServico.isSelected() == true) {
                String sql = "select servico as Servico, valor as Valor, obs as OBS, funcionario as Funcionario, cliente as Cliente, subCliente as Sub_Cliente from tbservicos where tipo='Concluido'";
                tipo = "Serviço";
                txtCodigo.setEnabled(false);
                txtPesquisa.setEnabled(true);
                taDescricao.setEnabled(true);
                pst = conexao.prepareStatement(sql);
                btnMostrarFoto.setEnabled(false);
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                txtQuantidade.setEnabled(false);
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
            } else if (rbCodigo.isSelected() == true) {
                tipo = "Produto";
                txtCodigo.setEnabled(true);
                txtPesquisa.setEnabled(false);
                btnAdicionar.setEnabled(false);
                btnMostrarFoto.setEnabled(false);
                taDescricao.setEnabled(false);
                taDescricao.setText(null);
                txtQuantidade.setEnabled(false);
                txtQuantidade.setText(null);
                ((DefaultTableModel) tbListaDeInformaçoes.getModel()).setRowCount(0);

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabelaAuxilioProduto() {

        try {
            String sqo = "select idproduto, produto, valor_venda, obs, quantidade, estoque from tbprodutos";
            limpar();
            pst = conexao.prepareStatement(sqo);
            rs = pst.executeQuery();
            tbSetar.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabelaVenda() {
        String sql = "select idvenda as ID, nome as Nome, preco as Preço, quantidade as Qtde,tipo as Tipo,cliente as Cliente,subCliente as Sub_Cliente from tbvenda where comanda_nota=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, cbComanda.getSelectedItem().toString());
            rs = pst.executeQuery();
            tbItem.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void instanciarTabelaCliente() {
        String sql = "select idcli as ID, nomecli as Cliente from tbclientes";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbCliente.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void pesquisar() {
        if (tipo.equals("Produto") == true) {
            try {
                String sql = "select idproduto as ID, produto as Produto, valor_venda as Preço, obs as Observações from tbprodutos where produto like ? and quantidade > 0";
                rbProduto.setSelected(true);
                tipo = "Produto";
                pst = conexao.prepareStatement(sql);
                btnMostrarFoto.setEnabled(true);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        } else if (tipo.equals("OS") == true) {

            String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, funcionario as Funcionario, data_os as Data, cliente as Cliente from tbos where tipo='Ordem de Serviço' and servico like ?";

            try {
                rbOrdemDeServico.setSelected(true);
                tipo = "OS";
                pst = conexao.prepareStatement(sql);
                btnMostrarFoto.setEnabled(false);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "OS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        } else if (tipo.equals("Serviço") == true) {

            String sql = "select servico as Servico, valor as Valor, obs as OBS, funcionario as Funcionario, cliente as Cliente from tbservicos where tipo='Concluido' and funcionario like ?";

            try {
                rbServico.setSelected(true);
                tipo = "Serviço";
                btnMostrarFoto.setEnabled(false);
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        }

    }

    public void CadastroCliente() {
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);

    }

    public void InstanciarComboboxCredito() {
        if (rbCredito.isSelected() == true) {
            cbNumero.setSelectedItem("1x");
            cbNumero.setEnabled(true);

        } else if (rbCredito.isSelected() == false) {
            cbNumero.setSelectedItem("1x");
            cbNumero.setEnabled(false);
        }
    }

    public void InstanciarComboboxMaquininha() {
        try {
            String sql = "select nome from tbMaquininha";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbMaquininha.removeItem(rs.getString("nome"));
                cbMaquininha.addItem(rs.getString("nome"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void InstanciarComboboxComanda() {
        try {
            String sql = "select nomeComanda from tbComanda";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbComanda.removeItem(rs.getString("nomeComanda"));
                cbComanda.addItem(rs.getString("nomeComanda"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void criarComanda() {
        try {
            String Comanda = JOptionPane.showInputDialog("Nomeie a nova comanda.");
            if (Comanda.isEmpty() == false) {
                JOptionPane.showMessageDialog(null, "Aguarde.");
                String sql = "insert into tbComanda(nomeComanda)values(?)";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, Comanda);
                pst.executeUpdate();
                InstanciarComboboxComanda();
                JOptionPane.showMessageDialog(null, "Comanda Adicionada com sucesso.");
            }

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void AdicionarDesconto() {

        String sql = "select idvenda from tbvenda where nome='Desconto'";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbDesconto.setModel(DbUtils.resultSetToTableModel(rs));

            String sqo = "delete from tbvenda where idvenda=" + tbDesconto.getModel().getValueAt(0, 0).toString();

            pst = conexao.prepareStatement(sqo);
            pst.executeUpdate();
            limpar();
            telaDesconto desconto = new telaDesconto();
            desconto.setVisible(true);

        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            telaDesconto desconto = new telaDesconto();
            desconto.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void criarNota() {

        SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");
        String timeStamp = date.format(new Date());

        try {
            if ((txtNome.getText().isEmpty()) || (txtPreco.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");

            } else if ((txtQuantidade.getText().isEmpty()) && tipo.equals("Produto")) {
                JOptionPane.showMessageDialog(null, "Adicione uma Quantidade.");
                limpar();

            } else if (Integer.parseInt(txtQuantidade.getText()) <= 0 && tipo.equals("Produto")) {
                JOptionPane.showMessageDialog(null, "Adicione uma quantidade maior que 0");
                limpar();

            } else if (Integer.parseInt(txtEstoque.getText()) <= 0 && tipo.equals("Produto") && txtTipo.getText().equals("Com controle de estoque.") == true) {
                JOptionPane.showMessageDialog(null, "Sem estoque de " + txtNome.getText() + ".");
                limpar();

            } else if (Integer.parseInt(txtQuantidade.getText()) > Integer.parseInt(txtEstoque.getText()) && txtTipo.getText().equals("Com controle de estoque.") == true) {
                JOptionPane.showMessageDialog(null, "Estoque insuficiente de " + txtNome.getText() + ".");
                limpar();

            } else if (tipo.equals("Produto")) {

                String sqt = "insert into tbvenda(nome, preco, quantidade, tipo, comissao, vendedor, comanda_nota, emicao, cliente)values(?,?,?,?,?,?,?,?,?)";
                pst = conexao.prepareStatement(sqt);

                pst.setString(1, txtNome.getText());

                if (tipo.equals("Produto") == true) {

                    pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText()) * Integer.parseInt(txtQuantidade.getText())))).replace(",", "."));
                } else if (tipo.equals("OS") == true) {

                    pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
                } else if (tipo.equals("Serviço") == true) {

                    pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
                }

                pst.setString(3, txtQuantidade.getText());
                pst.setString(4, tipo);
                if (tipo.equals("Produto") == true) {
                    pst.setString(5, lblUsuarioPDV.getText());
                } else {
                    pst.setString(5, funcionario);
                }
                pst.setString(6, lblUsuarioPDV.getText());
                pst.setString(7, cbComanda.getSelectedItem().toString());
                pst.setString(8, timeStamp);
                pst.setString(9, txtAux.getText());
                pst.executeUpdate();
                QuantidadeTirada();
                instanciarTabelaVenda();
                JOptionPane.showMessageDialog(null, "Produto adicionado com sucesso.");

                limpar();

            } else if (tipo.equals("OS")) {
                int adicionado = 0;

                String sqo = "select nome from tbvenda where comanda_nota != '' and tipo='OS'";
                pst = conexao.prepareStatement(sqo);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                    String Nome = tbAuxilio.getModel().getValueAt(i, 0).toString();
                    if (Nome.equals(txtNome.getText()) == true) {
                        JOptionPane.showMessageDialog(null, "OS já esta na Nota.");
                        adicionado = 1;
                        limpar();
                    }
                }

                if (adicionado == 0) {
                    String sqy = "insert into tbvenda(nome, preco, quantidade, tipo, comissao, vendedor, comanda_nota,emicao,cliente)values(?,?,?,?,?,?,?,?,?)";
                    pst = conexao.prepareStatement(sqy);

                    pst.setString(1, txtNome.getText());

                    if (tipo.equals("Produto") == true) {

                        pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText()) * Integer.parseInt(txtQuantidade.getText())))).replace(",", "."));
                    } else if (tipo.equals("OS") == true) {

                        pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
                    } else if (tipo.equals("Serviço") == true) {

                        pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
                    }

                    pst.setString(3, txtQuantidade.getText());
                    pst.setString(4, tipo);
                    if (tipo.equals("Produto") == true) {
                        pst.setString(5, lblUsuarioPDV.getText());
                    } else {
                        pst.setString(5, funcionario);
                    }
                    pst.setString(6, lblUsuarioPDV.getText());
                    pst.setString(7, cbComanda.getSelectedItem().toString());
                    pst.setString(8, timeStamp);
                    pst.setString(9, txtAux.getText());
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "OS adicionada com sucesso.");
                    instanciarTabelaVenda();
                    limpar();

                }

            } else if (tipo.equals("Serviço")) {
                int adicionado = 0;

                String sqo = "select nome from tbvenda where comanda_nota != '' and tipo='Serviço'";
                pst = conexao.prepareStatement(sqo);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                    String Nome = tbAuxilio.getModel().getValueAt(i, 0).toString();
                    if (Nome.equals(txtNome.getText()) == true) {
                        JOptionPane.showMessageDialog(null, "Este Serviço já esta na Nota.");
                        adicionado = 1;
                        limpar();
                    }
                }

                if (adicionado == 0) {
                    String sqn = "insert into tbvenda(nome, preco, quantidade, tipo, comissao, vendedor, comanda_nota,emicao,cliente,subCliente)values(?,?,?,?,?,?,?,?,?,?)";
                    pst = conexao.prepareStatement(sqn);

                    pst.setString(1, txtNome.getText());

                    if (tipo.equals("Produto") == true) {

                        pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText()) * Integer.parseInt(txtQuantidade.getText())))).replace(",", "."));
                    } else if (tipo.equals("OS") == true) {

                        pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
                    } else if (tipo.equals("Serviço") == true) {

                        pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
                    }

                    pst.setString(3, txtQuantidade.getText());
                    pst.setString(4, tipo);
                    if (tipo.equals("Produto") == true) {
                        pst.setString(5, lblUsuarioPDV.getText());
                    } else {
                        pst.setString(5, funcionario);
                    }
                    pst.setString(6, lblUsuarioPDV.getText());
                    pst.setString(7, cbComanda.getSelectedItem().toString());
                    pst.setString(8, timeStamp);
                    pst.setString(9, txtAux.getText());
                    pst.setString(10, txtSubCliente.getText());
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Serviço adicionado com sucesso.");
                    instanciarTabelaVenda();
                    limpar();

                }
            }

        } catch (java.lang.NumberFormatException e) {
            if (txtQuantidade.getText().isEmpty() == true) {
                JOptionPane.showMessageDialog(null, "Quantidade não pode ser nula.");
                limpar();
            } else {
                JOptionPane.showMessageDialog(null, "Quantidade deve ser um numero Inteiro.");
                limpar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void pesquisarCliente() {

        String sql = "select nomecli as Cliente from tbclientes where nomecli like ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarCliente.getText() + "%");
            rs = pst.executeQuery();
            tbCliente.setModel(DbUtils.resultSetToTableModel(rs));
            pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente Selecionado:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void remover() {
        try {
            int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este item?", "Atenção", JOptionPane.YES_NO_OPTION);
            if (confirma == JOptionPane.YES_OPTION) {

                if (txtQuantidade.getText().equals("0")) {

                    String sql = "delete from tbvenda where idvenda=?";
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtID.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");

                        JOptionPane.showMessageDialog(null, "OS/Serviço removida com sucesso");
                        limpar();

                    }

                } else {

                    QuantidadeAdicionada();
                    String sql = "delete from tbvenda where idvenda=?";
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtID.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");

                        JOptionPane.showMessageDialog(null, "Produto removido com sucesso");
                        limpar();

                    }
                }

            } else {
                limpar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void QuantidadeTirada() {
        try {
            if (txtTipo.getText().equals("Sem controle de estoque.")) {

            } else {
                int quantidadeEstoque = Integer.parseInt(txtEstoque.getText());
                int quantidadeVendida = Integer.parseInt(txtQuantidade.getText());
                int quantidade = quantidadeEstoque - quantidadeVendida;

                int auxilioQuantidade;

                double valor_compra;
                double valor_venda;

                String sqy = "select valor_compra from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqy);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                valor_compra = Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String sqk = "select valor_venda from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqk);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbAuxilio2.setModel(DbUtils.resultSetToTableModel(rs));
                valor_venda = Double.parseDouble(tbAuxilio2.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String sql = "update tbprodutos set quantidade=? where produto=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, String.valueOf(quantidade));
                pst.setString(2, txtNome.getText());
                pst.executeUpdate();

                String squ = "select quantidade from tbprodutos where produto=?";

                pst = conexao.prepareStatement(squ);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                auxilioQuantidade = Integer.parseInt(tbAuxilio1.getModel().getValueAt(0, 0).toString());

                String sqo = "update tbprodutos set referencial_compra=?, referencial_venda=? where produto=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_compra))).replace(",", "."));
                pst.setString(2, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_venda))).replace(",", "."));
                pst.setString(3, txtNome.getText());
                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void QuantidadeAdicionada() {
        try {

            int quantidadeEstoque = Integer.parseInt(txtEstoque.getText());
            int quantidadeVendida = Integer.parseInt(txtQuantidade.getText());
            int quantidade = quantidadeEstoque + quantidadeVendida;
            double auxilioQuantidade;

            double valor_compra;
            double valor_venda;

            String sqy = "select valor_compra from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sqy);
            pst.setString(1, txtNome.getText());
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            valor_compra = Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

            String sqk = "select valor_venda from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sqk);
            pst.setString(1, txtNome.getText());
            rs = pst.executeQuery();
            tbAuxilio2.setModel(DbUtils.resultSetToTableModel(rs));
            valor_venda = Double.parseDouble(tbAuxilio2.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

            String sql = "update tbprodutos set quantidade=? where produto=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, String.valueOf(quantidade));
            pst.setString(2, txtNome.getText());
            pst.executeUpdate();

            String squ = "select quantidade from tbprodutos where produto=?";

            pst = conexao.prepareStatement(squ);
            pst.setString(1, txtNome.getText());
            rs = pst.executeQuery();
            tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
            auxilioQuantidade = Integer.parseInt(tbAuxilio1.getModel().getValueAt(0, 0).toString());

            String sqo = "update tbprodutos set referencial_compra=?, referencial_venda=? where produto=?";
            pst = conexao.prepareStatement(sqo);
            pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_compra))).replace(",", "."));
            pst.setString(2, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_venda))).replace(",", "."));
            pst.setString(3, txtNome.getText());
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void imprimir_nota() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta Nota?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {

                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                String sqo = "select endcli from tbclientes where idcli=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, idCliente.getText());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                //tbAuxilio1.getModel().getValueAt(0, 0).toString()
                HashMap filtro = new HashMap();

                filtro.put("IT", Integer.parseInt(identificador));
                filtro.put("cliente", txtCliente.getText());
                filtro.put("total", lblValorTotal.getText());
                filtro.put("empresa", tbAuxilio1.getModel().getValueAt(0, 0).toString());
                filtro.put("nome", tbAuxilio1.getModel().getValueAt(0, 1).toString());
                filtro.put("email", tbAuxilio1.getModel().getValueAt(0, 2).toString());
                filtro.put("descricao", tbAuxilio1.getModel().getValueAt(0, 3).toString());
                filtro.put("OBS", tbAuxilio1.getModel().getValueAt(0, 4).toString());
                filtro.put("numero", tbAuxilio1.getModel().getValueAt(0, 5).toString());
                filtro.put("imagem", tbAuxilio1.getModel().getValueAt(0, 6).toString());
                filtro.put("formaPagamento", formaPagamentoNota);
                filtro.put("endereco", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("consultor", lblUsuarioPDV.getText());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");

                JasperReport jreport = JasperCompileManager.compileReport("src/reports/NotaPdv.jrxml");

                JasperPrint jprint = JasperFillManager.fillReport(jreport, filtro, conexao);

                JDialog tela = new JDialog(this, "LeGnu's - TelaRelatorio", true);

                tela.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                tela.setBackground(java.awt.SystemColor.control);
                tela.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
                tela.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                tela.setLocationRelativeTo(null);

                JRViewer painelRelatorio = new JRViewer(jprint);
                tela.getContentPane().add(painelRelatorio);
                tela.setVisible(true);
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Adicione uma imagem no relatorio");

                limpar();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }
        }
    }

    public void identificadorGanho() {
        try {
            String sqy = "select identificador from tbtotalvendas where id ORDER BY identificador desc;";
            pst = conexao.prepareStatement(sqy);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

            if (tbAuxilio.getModel().getValueAt(0, 0) != null) {
                identificador = String.valueOf(Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString()) + 1);
            } else {
                identificador = "1";
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void identificadorGasto() {
        try {
            String squ = "select identificador from tbgastos order by identificador desc";
            pst = conexao.prepareStatement(squ);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

            if (tbAuxilio.getModel().getValueAt(0, 0) != null) {
                identificadorGasto = String.valueOf(Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString()) + 1);
            } else {
                identificadorGasto = "1";
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void soma() {

        try {
            double preco, x;

            preco = 0;

            for (int i = 0; i < tbItem.getRowCount(); i++) {
                x = Double.parseDouble(tbItem.getModel().getValueAt(i, 2).toString().replace(".", "")) / 100;
                preco = preco + x;

            }

            lblValorTotal.setText(new DecimalFormat("#,##0.00").format(preco).replace(",", "."));
        } catch (java.lang.NullPointerException e) {
            lblValorTotal.setText("0.00");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void inserirDadosCliente() {
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dSql = new java.sql.Date(d.getTime());
        df.format(dSql);
        try {
            for (int i = 0; i < tbItem.getRowCount(); i++) {
                if (tbItem.getModel().getValueAt(0, 5).toString().equals("") == false) {
                    String squ = "select quantidade_comprada from tbclientes where nomecli=?";

                    pst = conexao.prepareStatement(squ);
                    pst.setString(1, tbItem.getModel().getValueAt(0, 5).toString());
                    rs = pst.executeQuery();
                    tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                    int numero_compra = Integer.parseInt(tbAuxilio1.getModel().getValueAt(0, 0).toString()) + 1;

                    String sqt = "update tbclientes set quantidade_comprada=? where nomecli=?";
                    pst = conexao.prepareStatement(sqt);
                    pst.setInt(1, numero_compra);
                    pst.setString(2, tbItem.getModel().getValueAt(0, 5).toString());
                    pst.executeUpdate();

                    String sqh = "update tbclientes set ultima_compra=? where nomecli=?";
                    pst = conexao.prepareStatement(sqh);
                    pst.setDate(1, dSql);
                    pst.setString(2, tbItem.getModel().getValueAt(0, 5).toString());
                    pst.executeUpdate();

                    String sqb = "select valor_gasto from tbclientes where nomecli=?";

                    pst = conexao.prepareStatement(sqb);
                    pst.setString(1, tbItem.getModel().getValueAt(0, 5).toString());
                    rs = pst.executeQuery();
                    tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                    double Valor_final = Double.parseDouble(String.valueOf(Double.parseDouble(tbAuxilio1.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100)) + Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100;

                    String sqn = "update tbclientes set valor_gasto=? where nomecli=?";
                    pst = conexao.prepareStatement(sqn);
                    pst.setString(1, String.valueOf(new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Valor_final)))).replace(",", "."));
                    pst.setString(2, tbItem.getModel().getValueAt(0, 5).toString());
                    pst.executeUpdate();
                }
            }

            if (txtCliente.getText().isEmpty() == false) {
                String squ = "select quantidade_comprada from tbclientes where nomecli=?";

                pst = conexao.prepareStatement(squ);
                pst.setString(1, txtCliente.getText());
                rs = pst.executeQuery();
                tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                int numero_compra = Integer.parseInt(tbAuxilio1.getModel().getValueAt(0, 0).toString()) + 1;

                String sqt = "update tbclientes set quantidade_comprada=? where nomecli=?";
                pst = conexao.prepareStatement(sqt);
                pst.setInt(1, numero_compra);
                pst.setString(2, txtCliente.getText());
                pst.executeUpdate();

                String sqh = "update tbclientes set ultima_compra=? where nomecli=?";
                pst = conexao.prepareStatement(sqh);
                pst.setDate(1, dSql);
                pst.setString(2, txtCliente.getText());
                pst.executeUpdate();

                String sqb = "select valor_gasto from tbclientes where nomecli=?";

                pst = conexao.prepareStatement(sqb);
                pst.setString(1, txtCliente.getText());
                rs = pst.executeQuery();
                tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                double Valor_final = Double.parseDouble(String.valueOf(Double.parseDouble(tbAuxilio1.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100)) + Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100;

                String sqn = "update tbclientes set valor_gasto=? where nomecli=?";
                pst = conexao.prepareStatement(sqn);
                pst.setString(1, String.valueOf(new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Valor_final)))).replace(",", "."));
                pst.setString(2, txtCliente.getText());
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Dados Clientes");
            limpar();

        }
    }

    public void removerOS() {
        try {
            for (int i = 0; i < tbItem.getRowCount(); i++) {
                if (tbItem.getModel().getValueAt(i, 4).toString().equals("OS") == true) {
                    String sqr = "select os from tbos where servico=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setString(1, tbItem.getModel().getValueAt(i, 1).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    String sqy = "delete from tbos where os=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(0, 0).toString());
                    pst.executeUpdate();
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void AlterarServico() {
        try {
            for (int i = 0; i < tbItem.getRowCount(); i++) {
                if (tbItem.getModel().getValueAt(i, 4).toString().equals("Serviço") == true) {
                    String sqo = "update tbservicos set tipo='Pago' where servico=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, tbItem.getModel().getValueAt(i, 1).toString());
                    pst.executeUpdate();
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void tirarComanda() {
        try {
            if (cbComanda.getSelectedItem().toString().equals("Caixa Principal") == false) {
                for (int i = 0; i < tbItem.getRowCount(); i++) {
                    String id = tbItem.getModel().getValueAt(i, 0).toString();
                    String sqo = "update tbvenda set comanda_nota='Caixa Principal' where idvenda=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, id);
                    pst.executeUpdate();
                }
                String sqy = "delete from tbComanda where nomeComanda=?";
                pst = conexao.prepareStatement(sqy);
                pst.setString(1, cbComanda.getSelectedItem().toString());
                pst.executeUpdate();
                cbComanda.removeItem(cbComanda.getSelectedItem().toString());
                JOptionPane.showMessageDialog(null, "Comanda removida com sucesso.");
                InstanciarComboboxComanda();
                instanciarTabelaVenda();
            } else {
                JOptionPane.showMessageDialog(null, "Caixa Principal não pode ser removido.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    public void adicionarComissao() {
        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);
            String sqo = "select preco, tipo, comissao,idvenda from tbvenda where comanda_nota=? and comissao != ''";
            pst = conexao.prepareStatement(sqo);
            pst.setString(1, cbComanda.getSelectedItem().toString());
            rs = pst.executeQuery();
            tbComissao.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbComissao.getRowCount(); i++) {
                String tipoVenda = tbComissao.getModel().getValueAt(i, 1).toString();

                if (tipoVenda.equals("Produto") == true) {
                    String sqy = "select comissao from tbusuarios where usuario=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, lblUsuarioPDV.getText());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    String sqz = "update tbvenda set porcentagem=?  where idvenda=?";
                    pst = conexao.prepareStatement(sqz);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(0, 0).toString());
                    pst.setString(2, tbComissao.getModel().getValueAt(i, 3).toString());
                    pst.executeUpdate();

                    String comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);
                    String valorComissao = new DecimalFormat("#,##0.00").format(Double.parseDouble(tbComissao.getModel().getValueAt(i, 0).toString().replace(".", "")) / 100 * Double.parseDouble(comissao)).replace(",", ".");
                    System.out.println(valorComissao);

                    if (Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) > 0) {

                        String sql = "insert into tbgastos(nome, data_pagamento, status_pagamento, valor, tipo)values(?,?,?,?,?)";
                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, "Comiçâo de " + lblUsuarioPDV.getText());
                        pst.setDate(2, dSql);
                        pst.setString(3, "Pendente");
                        pst.setString(4, valorComissao);
                        pst.setString(5, "Comissao");
                        pst.executeUpdate();

                    }

                } else {
                    String sqy = "select comissao from tbFuncionarios where funcionario=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, tbComissao.getModel().getValueAt(i, 2).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    String sqz = "update tbvenda set porcentagem=?  where idvenda=?";
                    pst = conexao.prepareStatement(sqz);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(0, 0).toString());
                    pst.setString(2, tbComissao.getModel().getValueAt(i, 3).toString());
                    pst.executeUpdate();

                    String comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);
                    String valorComissao = new DecimalFormat("#,##0.00").format(Double.parseDouble(tbComissao.getModel().getValueAt(i, 0).toString().replace(".", "")) / 100 * Double.parseDouble(comissao)).replace(",", ".");
                    System.out.println(valorComissao);

                    if (Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) > 0) {

                        String sql = "insert into tbgastos(nome, data_pagamento, status_pagamento, valor, tipo)values(?,?,?,?,?)";
                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, "Comiçâo de " + tbComissao.getModel().getValueAt(i, 2).toString());
                        pst.setDate(2, dSql);
                        pst.setString(3, "Pendente");
                        pst.setString(4, valorComissao);
                        pst.setString(5, "Comissao");
                        pst.executeUpdate();

                    }

                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void finalizarVenda() {
        try {
            for (int i = 0; i < tbItem.getRowCount(); i++) {
                String id = tbItem.getModel().getValueAt(i, 0).toString();
                String sqo = "update tbvenda set comanda_nota='',identificador=? where idvenda=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, identificador);
                pst.setString(2, id);
                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void montarFormaDePagamento() {

        try {
            formaPagamento = "";
            formaPagamentoNota = "";
            if (rbDinheiro.isSelected() == true) {
                formaPagamento = formaPagamento + " Dinheiro";
                formaPagamentoNota = formaPagamentoNota + " - Dinheiro - ";
            }
            if (rbPix.isSelected() == true) {
                formaPagamento = formaPagamento + " Pix";
                formaPagamentoNota = formaPagamentoNota + " - Pix - ";
            }
            if (rbBoleto.isSelected() == true) {
                formaPagamentoNota = formaPagamentoNota + " - Boleto - ";
            }
            if (rbCredito.isSelected() == true) {
                formaPagamentoNota = formaPagamentoNota + " - Credito - ";
            }
            if (rbCrediario.isSelected() == true) {
                formaPagamentoNota = formaPagamentoNota + " - Crediario - ";
            }
            if (rbDebito.isSelected() == true) {
                formaPagamentoNota = formaPagamentoNota + " - Debito - ";
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void instanciarMaquinha(String nome) {
        try {
            pst = conexao.prepareStatement("select taxaDebito,taxaCredito,antecipacao,taxaAntecipacao,compensacaoDebito,compensacaoCredito from tbMaquininha where nome=?");
            pst.setString(1, nome);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            nomeMaquininha = nome;
            taxaDebito = tbAuxilio.getModel().getValueAt(0, 0).toString();
            taxaCredito = tbAuxilio.getModel().getValueAt(0, 1).toString();
            antecipacao = tbAuxilio.getModel().getValueAt(0, 2).toString();
            taxaAntecipacao = tbAuxilio.getModel().getValueAt(0, 3).toString();
            compensacaoDebito = tbAuxilio.getModel().getValueAt(0, 4).toString();
            compensacaoCredito = tbAuxilio.getModel().getValueAt(0, 5).toString();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void ativarFormasExtras() {
        try {
            if (rbAtivar.isSelected() == true) {
                rbCrediario.setText("Crediario");
                rbBoleto.setText("Boleto");
                rbCrediario.setEnabled(true);
                rbBoleto.setEnabled(true);
            } else {
                rbCrediario.setText("#########");
                rbBoleto.setText("######");
                rbCrediario.setEnabled(false);
                rbBoleto.setEnabled(false);
                rbCrediario.setSelected(false);
                rbBoleto.setSelected(false);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void PagamentoDinheiro(String pagamento, String forma) {
        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            valorTotal = String.valueOf(Float.parseFloat(valorTotal) + Float.parseFloat(pagamento));

            pst = conexao.prepareStatement("update tbtotalvendas set dia=?,hora=?,venda=?,idcliente=?,forma_pagamento=?,status_pagamento=?,dia_Pagamento=?,tipo=? where identificador=? and dia_Pagamento=?");
            pst.setString(1, dSql.toString());
            pst.setString(2, new SimpleDateFormat("HH:mm:ss").format(d));
            pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valorTotal)).replace(",", "."));
            pst.setString(4, idCliente.getText());
            pst.setString(5, forma);
            pst.setString(6, "Pago");
            pst.setDate(7, dSql);
            pst.setString(8, "Venda");
            pst.setString(9, identificador);
            pst.setDate(10, dSql);
            pst.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void PagamentoPix(String pagamento, String forma, String pagamentoD) {
        try {

            JDialog tela = new JDialog(this, "LeGnu's - Pix_QRCode", true);

            tela.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            tela.setBackground(java.awt.SystemColor.control);
            tela.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
            tela.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            tela.setLocationRelativeTo(null);

            JLabel foto = new JLabel();
            foto.setText(null);
            foto.setIcon(new javax.swing.ImageIcon(imgPix));
            tela.getContentPane().add(foto);
            tela.setVisible(true);

            certo = JOptionPane.showConfirmDialog(null, "O Pix foi realizado com sucesso?", "Atenção", JOptionPane.YES_NO_OPTION);

            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            if ((certo == JOptionPane.YES_OPTION) == true) {

                valorTotal = String.valueOf(Float.parseFloat(valorTotal) + Float.parseFloat(pagamento));

                pst = conexao.prepareStatement("update tbtotalvendas set dia=?,hora=?,venda=?,idcliente=?,forma_pagamento=?,status_pagamento=?,dia_Pagamento=?,tipo=? where identificador=? and dia_Pagamento=?");
                pst.setString(1, dSql.toString());
                pst.setString(2, new SimpleDateFormat("HH:mm:ss").format(d));
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valorTotal)).replace(",", "."));
                pst.setString(4, idCliente.getText());
                pst.setString(5, forma);
                pst.setString(6, "Pago");
                pst.setDate(7, dSql);
                pst.setString(8, "Venda");
                pst.setString(9, identificador);
                pst.setDate(10, dSql);
            } else {
                JOptionPane.showMessageDialog(null, "Invalido");
                if ((pagamentoD.equals("Unico")) == true) {
                    String sql = "delete from tbtotalvendas where dia_Pagamento=? and identificador =?";
                    pst = conexao.prepareStatement(sql);
                    pst.setDate(1, dSql);
                    pst.setString(2, identificador);
                    pst.executeUpdate();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void PagamentoDebito(String maquininha, String pagamentoD, String pagamentoA, String forma) {
        try {
            instanciarMaquinha(maquininha);

            Date d = new Date();

            String dia = new SimpleDateFormat("dd").format(d);
            String mes = new SimpleDateFormat("MM").format(d);
            String ano = new SimpleDateFormat("yyyy").format(d);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dia = String.valueOf(Integer.parseInt(dia) + Integer.parseInt(compensacaoDebito));

            if (mes.equals("02") == true && Integer.parseInt(dia) >= 28) {
                mes = String.valueOf(Integer.parseInt(mes) + 1);
                dia = String.valueOf(Integer.parseInt(dia) - 28);
            } else if (Integer.parseInt(dia) >= 30) {
                mes = String.valueOf(Integer.parseInt(mes) + 1);
                dia = String.valueOf(Integer.parseInt(dia) - 30);
            }

            if (Integer.parseInt(mes) > 12) {
                mes = String.valueOf(Integer.parseInt(mes) - 12);
                ano = String.valueOf(Integer.parseInt(ano) + 1);
            }

            if (mes.length() == 1) {
                mes = "0" + mes;
            }

            if (dia.length() == 1) {
                dia = "0" + dia;
            }

            String dataLimite = ano + "-" + mes + "-" + dia;
            Date data = df.parse(dataLimite);

            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            java.sql.Date dSqt = new java.sql.Date(data.getTime());
            df.format(dSqt);

            if ((pagamentoD.equals("Unico")) == true) {
                String sql = "delete from tbtotalvendas where dia_Pagamento=? and identificador =?";
                pst = conexao.prepareStatement(sql);
                pst.setDate(1, dSql);
                pst.setString(2, identificador);
                pst.executeUpdate();
            }

            String sqo = "insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)";
            pst = conexao.prepareStatement(sqo);
            pst.setString(1, dSql.toString());
            pst.setString(2, new SimpleDateFormat("HH:mm:ss").format(d));
            pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(pagamentoA)).replace(",", "."));
            pst.setString(4, idCliente.getText());
            pst.setString(5, "Debito");
            pst.setString(6, "Pendente");
            pst.setDate(7, dSqt);
            pst.setString(8, "venda");
            pst.setString(9, identificador);
            pst.executeUpdate();

            String valor = String.valueOf((Float.parseFloat(pagamentoA) / 100) * Float.parseFloat(taxaDebito.replace("%", "")));

            pst = conexao.prepareStatement("insert into tbgastos(data_emissao, nome, valor, forma_pagamento, status_pagamento, data_pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?)");
            pst.setString(1, dSql.toString());
            pst.setString(2, "Taxa Maquininha(" + nomeMaquininha + ") Debito");
            pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valor)).replace(",", "."));
            pst.setString(4, "");
            pst.setString(5, "Pendente");
            pst.setDate(6, dSqt);
            pst.setString(7, "Venda");
            pst.setString(8, identificadorGasto);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void PagamentoCredito(String maquininha, int xVezes, String pagamentoD, String pagamentoA, String forma) {
        try {
            instanciarMaquinha(maquininha);
            if ((antecipacao.equals("Sim")) == true) {
                Date d = new Date();

                String dia = new SimpleDateFormat("dd").format(d);
                String mes = new SimpleDateFormat("MM").format(d);
                String ano = new SimpleDateFormat("yyyy").format(d);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                dia = String.valueOf(Integer.parseInt(dia) + Integer.parseInt(compensacaoCredito));

                if (mes.equals("02") == true && Integer.parseInt(dia) >= 28) {
                    mes = String.valueOf(Integer.parseInt(mes) + 1);
                    dia = String.valueOf(Integer.parseInt(dia) - 28);
                } else if (Integer.parseInt(dia) >= 30) {
                    mes = String.valueOf(Integer.parseInt(mes) + 1);
                    dia = String.valueOf(Integer.parseInt(dia) - 30);
                }

                if (Integer.parseInt(mes) > 12) {
                    mes = String.valueOf(Integer.parseInt(mes) - 12);
                    ano = String.valueOf(Integer.parseInt(ano) + 1);
                }

                if (mes.length() == 1) {
                    mes = "0" + mes;
                }

                if (dia.length() == 1) {
                    dia = "0" + dia;
                }

                String dataLimite = ano + "-" + mes + "-" + dia;
                Date data = df.parse(dataLimite);

                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                java.sql.Date dSqt = new java.sql.Date(data.getTime());
                df.format(dSqt);

                if ((pagamentoD.equals("Unico")) == true) {
                    String sql = "delete from tbtotalvendas where dia_Pagamento=? and identificador =?";
                    pst = conexao.prepareStatement(sql);
                    pst.setDate(1, dSql);
                    pst.setString(2, identificador);
                    pst.executeUpdate();
                }

                pst = conexao.prepareStatement("insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)");
                pst.setString(1, dSql.toString());
                pst.setString(2, new SimpleDateFormat("HH:mm:ss").format(d));
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(pagamentoA)).replace(",", "."));
                pst.setString(4, idCliente.getText());
                pst.setString(5, "Credito(Antecipado)");
                pst.setString(6, "Pendente");
                pst.setDate(7, dSqt);
                pst.setString(8, "Venda");
                pst.setString(9, identificador);
                pst.executeUpdate();

                String valor = String.valueOf((Float.parseFloat(pagamentoA) / 100) * ((Float.parseFloat(taxaCredito.replace("%", "")) * xVezes) + (Float.parseFloat(taxaAntecipacao.replace("%", "")) * xVezes)));

                pst = conexao.prepareStatement("insert into tbgastos(data_emissao, nome, valor, forma_pagamento, status_pagamento, data_pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?)");
                pst.setString(1, dSql.toString());
                pst.setString(2, "Taxa Maquininha(" + nomeMaquininha + ") Credito(Antecipado)");
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valor)).replace(",", "."));
                pst.setString(4, "");
                pst.setString(5, "Pendente");
                pst.setDate(6, dSqt);
                pst.setString(7, "Venda");
                pst.setString(8, identificadorGasto);
                pst.executeUpdate();
            } else if ((antecipacao.equals("Não")) == true) {
                Date d = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                String dia = new SimpleDateFormat("dd").format(d);
                String mes = new SimpleDateFormat("MM").format(d);
                String ano = new SimpleDateFormat("yyyy").format(d);

                if ((pagamentoD.equals("Unico")) == true) {
                    String sql = "delete from tbtotalvendas where dia_Pagamento=? and identificador =?";
                    pst = conexao.prepareStatement(sql);
                    pst.setDate(1, dSql);
                    pst.setString(2, identificador);
                    pst.executeUpdate();
                }

                for (int i = 0; i < xVezes; i++) {

                    mes = String.valueOf(Integer.parseInt(mes) + 1);

                    if ((Integer.parseInt(dia) > 30) == true) {
                        dia = "30";
                    } else if ((Integer.parseInt(mes) == 2) == true && (Integer.parseInt(dia) > 28) == true) {
                        mes = String.valueOf(Integer.parseInt(mes) + 1);
                        if ((Integer.parseInt(dia) == 29) == true) {
                            dia = "01";
                        } else if ((Integer.parseInt(dia) == 30) == true) {
                            dia = "02";
                        }
                    }

                    if (Integer.parseInt(mes) > 12) {
                        mes = String.valueOf(Integer.parseInt(mes) - 12);
                        ano = String.valueOf(Integer.parseInt(ano) + 1);
                    }

                    if (mes.length() == 1) {
                        mes = "0" + mes;
                    }

                    if (dia.length() == 1) {
                        dia = "0" + dia;
                    }

                    String dataLimite = ano + "-" + mes + "-" + dia;
                    Date data = df.parse(dataLimite);

                    java.sql.Date dSqt = new java.sql.Date(data.getTime());
                    df.format(dSqt);

                    String valorDividido = String.valueOf(Float.parseFloat(pagamentoA) / xVezes);

                    pst = conexao.prepareStatement("insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)");
                    pst.setString(1, dSql.toString());
                    pst.setString(2, new SimpleDateFormat("HH:mm:ss").format(d));
                    pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valorDividido)).replace(",", "."));
                    pst.setString(4, idCliente.getText());
                    pst.setString(5, "Credito");
                    pst.setString(6, "Pendente");
                    pst.setDate(7, dSqt);
                    pst.setString(8, "Venda");
                    pst.setString(9, identificador);
                    pst.executeUpdate();

                    String valor = String.valueOf((Float.parseFloat(valorDividido) / 100) * ((Float.parseFloat(taxaCredito.replace("%", "")))));

                    pst = conexao.prepareStatement("insert into tbgastos(data_emissao, nome, valor, forma_pagamento, status_pagamento, data_pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?)");
                    pst.setString(1, dSql.toString());
                    pst.setString(2, "Taxa Maquininha(" + nomeMaquininha + ") Credito");
                    pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valor)).replace(",", "."));
                    pst.setString(4, "");
                    pst.setString(5, "Pendente");
                    pst.setDate(6, dSqt);
                    pst.setString(7, "Venda");
                    pst.setString(8, identificadorGasto);
                    pst.executeUpdate();

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void PagamentoBoleto(int xVezes, String pagamentoD, String pagamentoA, String forma) {
        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            String dia = new SimpleDateFormat("dd").format(d);
            String mes = new SimpleDateFormat("MM").format(d);
            String ano = new SimpleDateFormat("yyyy").format(d);

            if ((pagamentoD.equals("Unico")) == true) {
                String sql = "delete from tbtotalvendas where dia_Pagamento=? and identificador =?";
                pst = conexao.prepareStatement(sql);
                pst.setDate(1, dSql);
                pst.setString(2, identificador);
                pst.executeUpdate();
            }

            for (int i = 0; i < xVezes; i++) {

                mes = String.valueOf(Integer.parseInt(mes) + 1);

                if ((Integer.parseInt(dia) > 30) == true) {
                    dia = "30";
                } else if ((Integer.parseInt(mes) == 2) == true && (Integer.parseInt(dia) > 28) == true) {
                    mes = String.valueOf(Integer.parseInt(mes) + 1);
                    if ((Integer.parseInt(dia) == 29) == true) {
                        dia = "01";
                    } else if ((Integer.parseInt(dia) == 30) == true) {
                        dia = "02";
                    }
                }

                if (Integer.parseInt(mes) > 12) {
                    mes = String.valueOf(Integer.parseInt(mes) - 12);
                    ano = String.valueOf(Integer.parseInt(ano) + 1);
                }

                if (mes.length() == 1) {
                    mes = "0" + mes;
                }

                if (dia.length() == 1) {
                    dia = "0" + dia;
                }

                String dataLimite = ano + "-" + mes + "-" + dia;
                Date data = df.parse(dataLimite);

                java.sql.Date dSqt = new java.sql.Date(data.getTime());
                df.format(dSqt);

                String valorDividido = String.valueOf(Float.parseFloat(pagamentoA) / xVezes);

                pst = conexao.prepareStatement("insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)");
                pst.setString(1, dSql.toString());
                pst.setString(2, new SimpleDateFormat("HH:mm:ss").format(d));
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valorDividido)).replace(",", "."));
                pst.setString(4, idCliente.getText());
                pst.setString(5, "Boleto");
                pst.setString(6, "Pendente");
                pst.setDate(7, dSqt);
                pst.setString(8, "Venda");
                pst.setString(9, identificador);
                pst.executeUpdate();

                String valor = String.valueOf((Float.parseFloat(valorDividido) / 100) * taxaBoleto);

                pst = conexao.prepareStatement("insert into tbgastos(data_emissao, nome, valor, forma_pagamento, status_pagamento, data_pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?)");
                pst.setString(1, dSql.toString());
                pst.setString(2, "Taxa Boleto");
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valor)).replace(",", "."));
                pst.setString(4, "Boleto");
                pst.setString(5, "Pendente");
                pst.setDate(6, dSqt);
                pst.setString(7, "Venda");
                pst.setString(8, identificadorGasto);
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void PagamentoCrediario(int xVezes, String pagamentoD, String pagamentoA, String forma) {
        try {
            if (txtCrediario.getText().equals("Habilitado") == true) {
                Date d = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                String dia = new SimpleDateFormat("dd").format(d);
                String mes = new SimpleDateFormat("MM").format(d);
                String ano = new SimpleDateFormat("yyyy").format(d);

                if ((pagamentoD.equals("Unico")) == true) {
                    String sql = "delete from tbtotalvendas where dia_Pagamento=? and identificador =?";
                    pst = conexao.prepareStatement(sql);
                    pst.setDate(1, dSql);
                    pst.setString(2, identificador);
                    pst.executeUpdate();
                }

                for (int i = 0; i < xVezes; i++) {

                    mes = String.valueOf(Integer.parseInt(mes) + 1);

                    if ((Integer.parseInt(dia) > 30) == true) {
                        dia = "30";
                    } else if ((Integer.parseInt(mes) == 2) == true && (Integer.parseInt(dia) > 28) == true) {
                        mes = String.valueOf(Integer.parseInt(mes) + 1);
                        if ((Integer.parseInt(dia) == 29) == true) {
                            dia = "01";
                        } else if ((Integer.parseInt(dia) == 30) == true) {
                            dia = "02";
                        }
                    }

                    if (Integer.parseInt(mes) > 12) {
                        mes = String.valueOf(Integer.parseInt(mes) - 12);
                        ano = String.valueOf(Integer.parseInt(ano) + 1);
                    }

                    if (mes.length() == 1) {
                        mes = "0" + mes;
                    }

                    if (dia.length() == 1) {
                        dia = "0" + dia;
                    }

                    String dataLimite = ano + "-" + mes + "-" + dia;
                    Date data = df.parse(dataLimite);

                    java.sql.Date dSqt = new java.sql.Date(data.getTime());
                    df.format(dSqt);

                    String valorDividido = String.valueOf(Float.parseFloat(pagamentoA) / xVezes);

                    pst = conexao.prepareStatement("insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)");
                    pst.setString(1, dSql.toString());
                    pst.setString(2, new SimpleDateFormat("HH:mm:ss").format(d));
                    pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(valorDividido)).replace(",", "."));
                    pst.setString(4, idCliente.getText());
                    pst.setString(5, "Crediario");
                    pst.setString(6, "Pendente");
                    pst.setDate(7, dSqt);
                    pst.setString(8, "Venda");
                    pst.setString(9, identificador);
                    pst.executeUpdate();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Este Cliente não pode comprar no crediario.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void MontarJOPMaquininha() {
        try {
            pst = conexao.prepareStatement("select id,nome from tbMaquininha");
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            String maquinas = "";
            int c = 0;
            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                maquinas = maquinas + "\n" + tbAuxilio.getModel().getValueAt(i, 0).toString() + ") " + tbAuxilio.getModel().getValueAt(i, 1).toString();
            }

            while (c == 0) {
                selecionada = JOptionPane.showInputDialog("Escreva o nome da maquininha que será utilizada.\n alternativas: " + maquinas);
                for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                    if ((selecionada != null) == true) {
                        if (selecionada.equals(tbAuxilio.getModel().getValueAt(i, 1).toString())) {
                            c = 1;
                            JOptionPane.showMessageDialog(null, "Maquininha Selecionada com Sucesso.");
                        }
                    }
                }
                if (c != 1) {
                    JOptionPane.showMessageDialog(null, "Maquininha não encontrada tente novamente.");
                }
            }
            System.out.println(maquinas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void concluir() {
        try {
            identificadorGanho();
            identificadorGasto();
            instanciarTabelaVenda();
            montarFormaDePagamento();
            soma();

            if (Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100 <= 0) {
                JOptionPane.showMessageDialog(null, "Valor total deve ser maior que 0");
                limpar();

            } else {
                Date d = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                String sqo = "insert into tbtotalvendas(status_pagamento,tipo, identificador,dia_Pagamento)values(?,?,?,?)";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, "Processando");
                pst.setString(2, "Montagem");
                pst.setString(3, identificador);
                pst.setDate(4, dSql);
                pst.executeUpdate();

                if (rbDinheiro.isSelected() == true && rbPix.isSelected() == false && rbDebito.isSelected() == false && rbCrediario.isSelected() == false && rbCredito.isSelected() == false && rbBoleto.isSelected() == false) {
                    PagamentoDinheiro(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100), "Dinheiro");
                    finalizarConcluir();
                } else if (rbPix.isSelected() == true && rbDinheiro.isSelected() == false && rbDebito.isSelected() == false && rbCrediario.isSelected() == false && rbCredito.isSelected() == false && rbBoleto.isSelected() == false) {
                    PagamentoPix(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100), "PIX", "Unico");
                    if (certo == JOptionPane.YES_OPTION) {
                        finalizarConcluir();
                    }
                } else if (rbDebito.isSelected() == true && rbPix.isSelected() == false && rbDinheiro.isSelected() == false && rbCrediario.isSelected() == false && rbCredito.isSelected() == false && rbBoleto.isSelected() == false) {
                    PagamentoDebito(cbMaquininha.getSelectedItem().toString(), "Unico", String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100), "Debito");
                    finalizarConcluir();
                } else if (rbCrediario.isSelected() == true && rbDinheiro.isSelected() == false && rbPix.isSelected() == false && rbDebito.isSelected() == false && rbCredito.isSelected() == false && rbBoleto.isSelected() == false) {
                    PagamentoCrediario(Integer.parseInt(vezesCrediario), "Unico", String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100), "Crediario");
                    finalizarConcluir();
                } else if (rbCredito.isSelected() == true && rbDinheiro.isSelected() == false && rbPix.isSelected() == false && rbDebito.isSelected() == false && rbCrediario.isSelected() == false && rbBoleto.isSelected() == false) {
                    PagamentoCredito(cbMaquininha.getSelectedItem().toString(), Integer.parseInt(cbNumero.getSelectedItem().toString().replace("x", "")), "Unico", String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100), "Credito");
                    finalizarConcluir();
                } else if (rbBoleto.isSelected() == true && rbDinheiro.isSelected() == false && rbPix.isSelected() == false && rbDebito.isSelected() == false && rbCrediario.isSelected() == false && rbCredito.isSelected() == false) {
                    PagamentoBoleto(Integer.parseInt(vezesBoleto), "Unico", String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100), "Boleto");
                    finalizarConcluir();
                } else {
                    int pergunta = JOptionPane.showConfirmDialog(null, "Deseja fazer uma venda com mais de uma forma de pagamento?", "Atenção", JOptionPane.YES_NO_OPTION);
                    if (pergunta == JOptionPane.YES_OPTION) {
                        float valorGerenciado = Float.parseFloat(String.valueOf(Float.parseFloat(lblValorTotal.getText().replace(".", "")) / 100));
                        float valorInteiro = Float.parseFloat(String.valueOf(Float.parseFloat(lblValorTotal.getText().replace(".", "")) / 100));
                        float valorTirado = 0;
                        String proseguir;
                        String vezes;
                        if (rbDinheiro.isSelected() == true) {
                            if (valorInteiro > 0) {

                                valorTirado = 0;
                                while (valorTirado <= 0) {
                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto a vista? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                    if ((teste != null) == true) {
                                        valorTirado = Float.parseFloat(teste);
                                    }
                                }

                                if ((valorTirado > valorInteiro) == true) {
                                    valorTirado = valorInteiro;
                                }
                                PagamentoDinheiro(String.valueOf(valorTirado), formaPagamento);
                                valorInteiro = valorInteiro - valorTirado;
                            } else {
                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Dinheiro, pois o valor da venda ja foi batido");
                            }
                        }

                        if (rbPix.isSelected() == true) {
                            if (valorInteiro > 0) {

                                valorTirado = 0;
                                while (valorTirado <= 0) {
                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no PIX? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                    if ((teste != null) == true) {
                                        valorTirado = Float.parseFloat(teste);
                                    }
                                }

                                if ((valorTirado > valorInteiro) == true) {
                                    valorTirado = valorInteiro;
                                }
                                PagamentoPix(String.valueOf(valorTirado), formaPagamento, "");
                                if (certo == JOptionPane.YES_OPTION) {
                                    valorInteiro = valorInteiro - valorTirado;
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma PIX, pois o valor da venda ja foi batido");
                            }
                        }

                        if (rbDebito.isSelected() == true) {
                            if (valorInteiro > 0) {

                                valorTirado = 0;
                                while (valorTirado <= 0) {
                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no Debito? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                    if ((teste != null) == true) {
                                        valorTirado = Float.parseFloat(teste);
                                    }
                                }

                                if ((valorTirado > valorInteiro) == true) {
                                    valorTirado = valorInteiro;
                                }

                                valorGerenciado = valorGerenciado - valorTirado;

                                PagamentoDebito(cbMaquininha.getSelectedItem().toString(), String.valueOf(valorGerenciado), String.valueOf(valorTirado), formaPagamento);
                                valorInteiro = valorInteiro - valorTirado;
                            } else {
                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Debito, pois o valor da venda ja foi batido");
                            }
                        }

                        if (rbCrediario.isSelected() == true) {
                            if (valorInteiro > 0) {

                                valorTirado = 0;
                                while (valorTirado <= 0) {
                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no Crediario? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                    if ((teste != null) == true) {
                                        valorTirado = Float.parseFloat(teste);
                                    }
                                }

                                if ((valorTirado > valorInteiro) == true) {
                                    valorTirado = valorInteiro;
                                }

                                valorGerenciado = valorGerenciado - valorTirado;

                                PagamentoCrediario(Integer.parseInt(vezesCrediario), String.valueOf(valorGerenciado), String.valueOf(valorTirado), formaPagamento);
                                valorInteiro = valorInteiro - valorTirado;
                            } else {
                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Crediario, pois o valor da venda ja foi batido");
                            }
                        }

                        if (rbCredito.isSelected() == true) {
                            if (valorInteiro > 0) {

                                valorTirado = 0;
                                while (valorTirado <= 0) {
                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no Credito? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                    if ((teste != null) == true) {
                                        valorTirado = Float.parseFloat(teste);
                                    }
                                }

                                if ((valorTirado > valorInteiro) == true) {
                                    valorTirado = valorInteiro;
                                }

                                valorGerenciado = valorGerenciado - valorTirado;

                                PagamentoCredito(cbMaquininha.getSelectedItem().toString(), Integer.parseInt(cbNumero.getSelectedItem().toString().replace("x", "")), String.valueOf(valorGerenciado), String.valueOf(valorTirado), formaPagamento);
                                valorInteiro = valorInteiro - valorTirado;
                            } else {
                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Credito, pois o valor da venda ja foi batido");
                            }
                        }

                        if (rbBoleto.isSelected() == true) {
                            if (valorInteiro > 0) {

                                valorTirado = 0;
                                while (valorTirado <= 0) {
                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no Boleto? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                    if ((teste != null) == true) {
                                        valorTirado = Float.parseFloat(teste);
                                    }
                                }

                                if ((valorTirado > valorInteiro) == true) {
                                    valorTirado = valorInteiro;
                                }

                                valorGerenciado = valorGerenciado - valorTirado;

                                PagamentoBoleto(Integer.parseInt(vezesBoleto), String.valueOf(valorGerenciado), String.valueOf(valorTirado), formaPagamento);
                                valorInteiro = valorInteiro - valorTirado;
                            } else {
                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Boleto, pois o valor da venda ja foi batido");
                            }
                        }

                        while (valorInteiro > 0) {

                            proseguir = JOptionPane.showInputDialog("O Valor da venda não foi concluido, como deseja pagar o valor restante?\n\nAlternativas(escolha um dos numeros)\n1)Dinheiro\n2)PIX\n3)Debito\n4)Credito\n5)" + rbCrediario.getText() + "\n6)" + rbBoleto.getText() + "\n\nValor Restante: R$ " + valorInteiro);

                            if ((proseguir != null) == true) {
                                if (proseguir.equals("1") == true) {
                                    if (valorInteiro > 0) {

                                        valorTirado = 0;
                                        while (valorTirado <= 0) {
                                            String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto a vista? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                            if ((teste != null) == true) {
                                                valorTirado = Float.parseFloat(teste);
                                            }
                                        }

                                        if ((valorTirado > valorInteiro) == true) {
                                            valorTirado = valorInteiro;
                                        }
                                        PagamentoDinheiro(String.valueOf(valorTirado), formaPagamento);
                                        valorInteiro = valorInteiro - valorTirado;
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Dinheiro, pois o valor da venda ja foi batido");
                                    }
                                } else if (proseguir.equals("2") == true) {
                                    if (valorInteiro > 0) {

                                        valorTirado = 0;
                                        while (valorTirado <= 0) {
                                            String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no PIX? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                            if ((teste != null) == true) {
                                                valorTirado = Float.parseFloat(teste);
                                            }
                                        }

                                        if ((valorTirado > valorInteiro) == true) {
                                            valorTirado = valorInteiro;
                                        }
                                        PagamentoPix(String.valueOf(valorTirado), formaPagamento, "");

                                        if (certo == JOptionPane.YES_OPTION) {
                                            valorInteiro = valorInteiro - valorTirado;
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma PIX, pois o valor da venda ja foi batido");
                                    }
                                } else if (proseguir.equals("3") == true) {
                                    MontarJOPMaquininha();
                                    if (valorInteiro > 0) {

                                        valorTirado = 0;
                                        while (valorTirado <= 0) {
                                            String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no debito? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                            if ((teste != null) == true) {
                                                valorTirado = Float.parseFloat(teste);
                                            }
                                        }

                                        if ((valorTirado > valorInteiro) == true) {
                                            valorTirado = valorInteiro;
                                        }

                                        valorGerenciado = valorGerenciado - valorTirado;

                                        PagamentoDebito(selecionada, String.valueOf(valorGerenciado), String.valueOf(valorTirado), formaPagamento);
                                        valorInteiro = valorInteiro - valorTirado;
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Debito, pois o valor da venda ja foi batido");
                                    }

                                } else if (proseguir.equals("4") == true) {
                                    MontarJOPMaquininha();
                                    vezes = JOptionPane.showInputDialog("Quantas vezes deseja dividir?");
                                    if (vezes.equals("1") == true || vezes.equals("2") == true || vezes.equals("3") == true || vezes.equals("4") == true || vezes.equals("5") == true || vezes.equals("6") == true || vezes.equals("7") == true || vezes.equals("8") == true || vezes.equals("9") == true || vezes.equals("10") == true || vezes.equals("11") == true || vezes.equals("12") == true) {
                                        if (valorInteiro > 0) {

                                            valorTirado = 0;
                                            while (valorTirado <= 0) {
                                                String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no Credito? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                                if ((teste != null) == true) {
                                                    valorTirado = Float.parseFloat(teste);
                                                }
                                            }

                                            if ((valorTirado > valorInteiro) == true) {
                                                valorTirado = valorInteiro;
                                            }

                                            valorGerenciado = valorGerenciado - valorTirado;

                                            PagamentoCredito(selecionada, Integer.parseInt(vezes), String.valueOf(valorGerenciado), String.valueOf(valorTirado), formaPagamento);
                                            valorInteiro = valorInteiro - valorTirado;
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Credito, pois o valor da venda ja foi batido");
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Numeros para Dividir devem estar entre 1 e 12.");
                                    }
                                } else if (proseguir.equals("5") == true) {
                                    if (rbCrediario.getText().equals("Crediario") == true) {
                                        vezes = JOptionPane.showInputDialog("Quantas vezes deseja dividir?");
                                        if (vezes.equals("1") == true || vezes.equals("2") == true || vezes.equals("3") == true || vezes.equals("4") == true || vezes.equals("5") == true || vezes.equals("6") == true || vezes.equals("7") == true || vezes.equals("8") == true || vezes.equals("9") == true || vezes.equals("10") == true || vezes.equals("11") == true || vezes.equals("12") == true) {
                                            if (valorInteiro > 0) {

                                                valorTirado = 0;
                                                while (valorTirado <= 0) {
                                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no Crediario? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                                    if ((teste != null) == true) {
                                                        valorTirado = Float.parseFloat(teste);
                                                    }
                                                }

                                                if ((valorTirado > valorInteiro) == true) {
                                                    valorTirado = valorInteiro;
                                                }

                                                PagamentoCrediario(Integer.parseInt(vezes), "", String.valueOf(valorTirado), "Crediario");
                                                valorInteiro = valorInteiro - valorTirado;
                                            } else {
                                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Credito, pois o valor da venda ja foi batido");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Numeros para Dividir devem estar entre 1 e 12.");
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Opção não Disponivel.");
                                    }
                                } else if (proseguir.equals("6") == true) {
                                    if (rbBoleto.getText().equals("Boleto") == true) {
                                        vezes = JOptionPane.showInputDialog("Quantas vezes deseja dividir?");
                                        if (vezes.equals("1") == true || vezes.equals("2") == true || vezes.equals("3") == true || vezes.equals("4") == true || vezes.equals("5") == true || vezes.equals("6") == true || vezes.equals("7") == true || vezes.equals("8") == true || vezes.equals("9") == true || vezes.equals("10") == true || vezes.equals("11") == true || vezes.equals("12") == true) {
                                            if (valorInteiro > 0) {

                                                valorTirado = 0;
                                                while (valorTirado <= 0) {
                                                    String teste = JOptionPane.showInputDialog(null, "Deseja passar quanto no Boleto? \nValor restante: R$" + valorInteiro, "Atenção", JOptionPane.YES_OPTION);
                                                    if ((teste != null) == true) {
                                                        valorTirado = Float.parseFloat(teste);
                                                    }
                                                }

                                                if ((valorTirado > valorInteiro) == true) {
                                                    valorTirado = valorInteiro;
                                                }

                                                valorGerenciado = valorGerenciado - valorTirado;

                                                PagamentoBoleto(Integer.parseInt(vezes), String.valueOf(valorGerenciado), String.valueOf(valorTirado), formaPagamento);
                                                valorInteiro = valorInteiro - valorTirado;
                                            } else {
                                                JOptionPane.showMessageDialog(null, "Não foi possivel adicionar a forma Boleto, pois o valor da venda ja foi batido");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Numeros para Dividir devem estar entre 1 e 12.");
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Opção não Disponivel.");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Alternativa Desconhecida");
                                }
                            }
                        }

                        finalizarConcluir();

                    } else {
                        limpar();
                    }
                }
            }

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Os valores só aceitam numeros!");
            limpar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void finalizarConcluir() {
        try {
            for (int i = 0; i < tbItem.getRowCount(); i++) {
                if (tbItem.getModel().getValueAt(i, 5).toString().equals("") == true) {
                    pst = conexao.prepareStatement("update tbvenda set cliente = ? where idvenda=?");
                    pst.setString(1, txtCliente.getText());
                    pst.setString(2, tbItem.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                }
            }

            adicionarComissao();
            inserirDadosCliente();
            finalizarVenda();
            imprimir_nota();

            removerOS();
            AlterarServico();

            JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
            lblValorTotal.setText("0.00");
            JOptionPane.showMessageDialog(null, "Concluido.");
            limpar();
            instanciarTabela();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void dividirCrediario() {
        try {
            if (rbCrediario.isSelected() == true) {
                vezesCrediario = JOptionPane.showInputDialog(null, "Quantas vezes será dividido");
                vezesCrediario = String.valueOf(Integer.parseInt(vezesCrediario));
            }

        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Somente Numeros!");
            limpar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void dividirboleto() {
        try {
            if (rbBoleto.isSelected() == true) {
                vezesBoleto = JOptionPane.showInputDialog(null, "Quantas vezes será dividido");
                vezesBoleto = String.valueOf(Integer.parseInt(vezesBoleto));
            }

        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Somente Numeros!");
            limpar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void setar_campos() {
        int setar = tbListaDeInformaçoes.getSelectedRow();

        try {

            if (tipo.equals("OS") == true) {
                txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                txtAux.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 8).toString());
                txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(".", "")) / 100));
                funcionario = tbListaDeInformaçoes.getModel().getValueAt(setar, 6).toString();

                taDescricao.setText("Equipamento: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString() + "\nDefeito: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 4).toString() + "\nServiço: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString() + "\nFuncionario: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 6).toString() + "\nData Entrega: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString() + "\n\nPreço: R$ " + String.valueOf(new DecimalFormat("#,##0.00").format(Float.parseFloat(txtPreco.getText()))).replace(",", "."));

                txtQuantidade.setEnabled(false);
                txtQuantidade.setText("0");
                txtEstoque.setText("0");
                btnAdicionar.setEnabled(true);
                btnMostrarFoto.setEnabled(false);

            } else if (tipo.equals("Produto") == true) {
                txtID.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString());
                txtAux.setText("");
                txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString().replace(".", "")) / 100));

                taDescricao.setText("Produto: " + txtNome.getText() + "\nObservação: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString() + "\n\nPreço: R$ " + String.valueOf(new DecimalFormat("#,##0.00").format(Float.parseFloat(txtPreco.getText()))).replace(",", "."));

                String sql = "select estoque, foto FROM tbprodutos where idproduto=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                rs = pst.executeQuery();
                tbSetar.setModel(DbUtils.resultSetToTableModel(rs));
                txtTipo.setText(tbSetar.getModel().getValueAt(0, 0).toString());
                if (tbSetar.getModel().getValueAt(0, 1) != null) {
                    txtFoto.setText(tbSetar.getModel().getValueAt(0, 1).toString());
                } else if (txtQuantidade.getText().equals("0") == false) {
                    String sqo = "select quantidade FROM tbprodutos where idproduto=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                    rs = pst.executeQuery();
                    tbSetar.setModel(DbUtils.resultSetToTableModel(rs));
                    txtEstoque.setText(tbSetar.getModel().getValueAt(0, 0).toString());
                }
                btnMostrarFoto.setEnabled(true);

            } else if (tipo.equals("Serviço") == true) {
                txtAux.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 4).toString());
                if ((tbListaDeInformaçoes.getModel().getValueAt(setar, 5) != null) == true) {
                    txtSubCliente.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 5).toString());
                } else {
                    txtSubCliente.setText("");
                }
                txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(".", "")) / 100));
                funcionario = tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString();

                taDescricao.setText("Cliente: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 4).toString() + "\nSubCliente: " + txtSubCliente.getText() + "\nServiço: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString() + "\nFuncionario: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString() + "\nObservação: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString() + "\n\nValor: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString());

                txtQuantidade.setEnabled(false);
                txtQuantidade.setText("0");
                txtEstoque.setText("0");
                btnAdicionar.setEnabled(true);
                btnMostrarFoto.setEnabled(false);

            }

            btnAdicionar.setEnabled(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void setarCamposRemover() {
        int setar = tbItem.getSelectedRow();
        limpar();
        try {
            txtID.setText(tbItem.getModel().getValueAt(setar, 0).toString());
            txtNome.setText(tbItem.getModel().getValueAt(setar, 1).toString());
            txtPreco.setText(String.valueOf(Double.parseDouble(tbItem.getModel().getValueAt(setar, 2).toString().replace(".", "")) / 100));
            txtQuantidade.setText(tbItem.getModel().getValueAt(setar, 3).toString());

            if (txtQuantidade.getText().equals("0") == false) {
                String sql = "select quantidade FROM tbprodutos where produto=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbSetar.setModel(DbUtils.resultSetToTableModel(rs));
                txtEstoque.setText(tbSetar.getModel().getValueAt(0, 0).toString());
            }

            remover();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void MostrarFoto() {
        if (tipo.equals("Produto") == true) {
            JDialog tela = new JDialog(this, "LeGnu's - TelaFoto", true);

            tela.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            tela.setBackground(java.awt.SystemColor.control);
            tela.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
            tela.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            tela.setLocationRelativeTo(null);

            JLabel foto = new JLabel();
            foto.setText(null);
            foto.setIcon(new javax.swing.ImageIcon(txtFoto.getText()));
            tela.getContentPane().add(foto);
            tela.setVisible(true);
        } else {

        }
    }

    public void setarCamposCliente() {
        int setar = tbCliente.getSelectedRow();
        limpar();
        try {
            idCliente.setText(tbCliente.getModel().getValueAt(setar, 0).toString());
            txtCliente.setText(tbCliente.getModel().getValueAt(setar, 1).toString());
            String sqy = "select crediario from tbclientes where idcli=?";
            pst = conexao.prepareStatement(sqy);
            pst.setString(1, tbCliente.getModel().getValueAt(setar, 0).toString());
            rs = pst.executeQuery();
            tbAuxilio2.setModel(DbUtils.resultSetToTableModel(rs));
            txtCrediario.setText(tbAuxilio2.getModel().getValueAt(0, 0).toString());

            pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente Selecionado: " + txtCliente.getText(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void fecharPdv() {

        String Perfil;
        limpar();
        try {
            String sqy = "select perfil from tbusuarios where usuario=?";
            pst = conexao.prepareStatement(sqy);
            pst.setString(1, lblUsuarioPDV.getText());
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            Perfil = tbAuxilio.getModel().getValueAt(0, 0).toString();
            if (Perfil.equals("Administrador")) {
                TelaPrincipal principal = new TelaPrincipal();
                principal.setVisible(true);
                TelaPrincipal.MenCadUsu.setEnabled(true);
                TelaPrincipal.lblUsuario.setText(lblUsuarioPDV.getText());
                TelaPrincipal.lblUsuario.setForeground(Color.red);
                this.dispose();
                conexao.close();
            } else if (Perfil.equals("Usuario") == true) {

                TelaLimitada limitada = new TelaLimitada();
                limitada.setVisible(true);
                TelaLimitada.btnPDV.setEnabled(false);
                TelaLimitada.btnCadOS.setEnabled(false);
                TelaLimitada.btnCadServiço.setEnabled(false);
                TelaLimitada.lblNome.setText(lblUsuarioPDV.getText());
                this.dispose();
                conexao.close();

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void limpar() {
        vezesBoleto = "0";
        vezesCrediario = "0";
        rbAtivar.setSelected(false);
        cbMaquininha.setSelectedItem("*");
        taDescricao.setText(null);
        txtNome.setText(null);
        txtFoto.setText(null);
        txtSubCliente.setText(null);
        txtPreco.setText(null);
        txtID.setText(null);
        idCliente.setText("1");
        txtEstoque.setText(null);
        txtTipo.setText(null);
        txtQuantidade.setText(null);
        btnAdicionar.setEnabled(false);
        btnMostrarFoto.setEnabled(false);
        valorTotal = "0";
        lblValorTotal.setText("0.00");
        txtCliente.setText("Vendas Sem Registro.");
        txtCrediario.setText("Habilitado");
        cbNumero.setSelectedItem("1x");
        funcionario = null;
        pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente Selecionado: ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
        ativarFormasExtras();
        combMaquininha();
        instanciarTabelaVenda();
        instanciarTabelaCliente();
        soma();
        InstanciarComboboxMaquininha();
    }

    private void inserirProduto() {
        try {
            SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");
            String timeStamp = date.format(new Date());

            pst = conexao.prepareStatement("select produto,valor_venda,estoque,quantidade from tbprodutos where codigo = ?");
            pst.setString(1, txtCodigo.getText());
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

            if (Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 3).toString()) <= 0 && tbAuxilio.getModel().getValueAt(0, 2).toString().equals("Com controle de estoque.") == true) {
                JOptionPane.showMessageDialog(null, "Sem estoque de " + txtNome.getText() + ".");
                limpar();

            } else {

                txtNome.setText(tbAuxilio.getModel().getValueAt(0, 0).toString());
                txtEstoque.setText(tbAuxilio.getModel().getValueAt(0, 3).toString());
                txtQuantidade.setText("1");
                txtPreco.setText(tbAuxilio.getModel().getValueAt(0, 1).toString());

                String sqt = "insert into tbvenda(nome, preco, quantidade, tipo, comissao, vendedor, comanda_nota, emicao,cliente)values(?,?,?,?,?,?,?,?,?)";
                pst = conexao.prepareStatement(sqt);

                pst.setString(1, txtNome.getText());
                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
                pst.setString(3, txtQuantidade.getText());
                pst.setString(4, tipo);
                pst.setString(5, lblUsuarioPDV.getText());
                pst.setString(6, lblUsuarioPDV.getText());
                pst.setString(7, cbComanda.getSelectedItem().toString());
                pst.setString(8, timeStamp);
                pst.setString(9, "");
                pst.executeUpdate();
                QuantidadeTirada();
                instanciarTabelaVenda();

                txtCodigo.setText(null);
                txtNome.setText(null);
                txtEstoque.setText(null);
                txtQuantidade.setText(null);
                txtPreco.setText(null);
                soma();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    private void combMaquininha() {
        if (cbMaquininha.getSelectedItem().toString().equals("*") == false) {
            rbDebito.setEnabled(true);
            rbCredito.setEnabled(true);
        } else {
            cbNumero.setEnabled(false);
            rbDebito.setEnabled(false);
            rbCredito.setEnabled(false);
            rbDebito.setSelected(false);
            rbCredito.setSelected(false);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        OS_Produtos = new javax.swing.ButtonGroup();
        scTotal = new javax.swing.JScrollPane();
        tbTotal = new javax.swing.JTable();
        scSetar = new javax.swing.JScrollPane();
        tbSetar = new javax.swing.JTable();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        scAuxilio1 = new javax.swing.JScrollPane();
        tbAuxilio1 = new javax.swing.JTable();
        scAuxilio2 = new javax.swing.JScrollPane();
        tbAuxilio2 = new javax.swing.JTable();
        scDesconto = new javax.swing.JScrollPane();
        tbDesconto = new javax.swing.JTable();
        scOS = new javax.swing.JScrollPane();
        tbOS = new javax.swing.JTable();
        scComissao = new javax.swing.JScrollPane();
        tbComissao = new javax.swing.JTable();
        lblPrecoFinal = new javax.swing.JLabel();
        lblUsuarioPDV = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        txtCrediario = new javax.swing.JTextField();
        idCliente = new javax.swing.JTextField();
        txtTipo = new javax.swing.JTextField();
        txtID = new javax.swing.JTextField();
        txtEstoque = new javax.swing.JTextField();
        txtNome = new javax.swing.JTextField();
        txtPreco = new javax.swing.JTextField();
        txtFoto = new javax.swing.JTextField();
        txtAux = new javax.swing.JTextField();
        txtSubCliente = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        pnNota = new javax.swing.JPanel();
        pnTbNota = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbItem = new javax.swing.JTable();
        cbComanda = new javax.swing.JComboBox<>();
        btnTirarComanda = new br.com.LeGnusERP.Swing.botaoArredondado();
        btnComanda = new br.com.LeGnusERP.Swing.botaoArredondado();
        lblTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        pnTbClientes = new javax.swing.JPanel();
        lblPesquisarCliente = new javax.swing.JLabel();
        txtPesquisarCliente = new javax.swing.JTextField();
        pnCliente = new javax.swing.JPanel();
        scCliente = new javax.swing.JScrollPane();
        tbCliente = new javax.swing.JTable();
        pnPagamento = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        rbCrediario = new javax.swing.JRadioButton();
        rbBoleto = new javax.swing.JRadioButton();
        rbAtivar = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        rbPix = new javax.swing.JRadioButton();
        rbDinheiro = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        cbMaquininha = new javax.swing.JComboBox<>();
        rbDebito = new javax.swing.JRadioButton();
        rbCredito = new javax.swing.JRadioButton();
        cbNumero = new javax.swing.JComboBox<>();
        btnConcluir = new br.com.LeGnusERP.Swing.botaoArredondado();
        btnDesconto = new br.com.LeGnusERP.Swing.botaoArredondado();
        pnTbPrincipal = new javax.swing.JPanel();
        scPdv = new javax.swing.JScrollPane();
        tbListaDeInformaçoes = new javax.swing.JTable();
        lblPesquisa = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        rbOrdemDeServico = new javax.swing.JRadioButton();
        rbProduto = new javax.swing.JRadioButton();
        rbServico = new javax.swing.JRadioButton();
        btnOS = new javax.swing.JToggleButton();
        btnCliente = new javax.swing.JToggleButton();
        rbCodigo = new javax.swing.JRadioButton();
        txtCodigo = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        pnOBS = new javax.swing.JPanel();
        scText = new javax.swing.JScrollPane();
        taDescricao = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        txtQuantidade = new javax.swing.JTextField();
        btnAdicionar = new br.com.LeGnusERP.Swing.botaoArredondado();
        btnMostrarFoto = new br.com.LeGnusERP.Swing.botaoArredondado();

        tbTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scTotal.setViewportView(tbTotal);

        tbSetar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scSetar.setViewportView(tbSetar);

        tbAuxilio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scAuxilio.setViewportView(tbAuxilio);

        tbAuxilio1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scAuxilio1.setViewportView(tbAuxilio1);

        tbAuxilio2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scAuxilio2.setViewportView(tbAuxilio2);

        tbDesconto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scDesconto.setViewportView(tbDesconto);

        tbOS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scOS.setViewportView(tbOS);

        tbComissao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scComissao.setViewportView(tbComissao);

        lblPrecoFinal.setText("jLabel1");

        lblUsuarioPDV.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUsuarioPDV.setText("Usuario");

        txtCliente.setText("Vendas Sem Registro.");

        idCliente.setText("1");

        txtID.setEnabled(false);

        txtEstoque.setText("jTextField1");

        txtNome.setEditable(false);
        txtNome.setEnabled(false);
        txtNome.setFocusable(false);
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        txtPreco.setEditable(false);
        txtPreco.setEnabled(false);
        txtPreco.setFocusable(false);

        txtFoto.setText("jTextField1");

        txtAux.setText("jTextField1");

        txtSubCliente.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Ponto de Vendas");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel4.setBackground(java.awt.SystemColor.control);
        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        pnNota.setBackground(java.awt.SystemColor.control);
        pnNota.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pnTbNota.setBackground(java.awt.SystemColor.control);
        pnTbNota.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Comanda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 12))); // NOI18N

        tbItem = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbItem.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbItem.setFocusable(false);
        tbItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbItemMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbItem);

        cbComanda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Caixa Principal" }));
        cbComanda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbComandaItemStateChanged(evt);
            }
        });

        btnTirarComanda.setForeground(new java.awt.Color(153, 0, 0));
        btnTirarComanda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/minus.png"))); // NOI18N
        btnTirarComanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTirarComandaActionPerformed(evt);
            }
        });

        btnComanda.setForeground(new java.awt.Color(0, 204, 102));
        btnComanda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/plus.png"))); // NOI18N
        btnComanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComandaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTbNotaLayout = new javax.swing.GroupLayout(pnTbNota);
        pnTbNota.setLayout(pnTbNotaLayout);
        pnTbNotaLayout.setHorizontalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(pnTbNotaLayout.createSequentialGroup()
                        .addComponent(cbComanda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16)
                        .addComponent(btnComanda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(btnTirarComanda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        pnTbNotaLayout.setVerticalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTbNotaLayout.createSequentialGroup()
                .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTbNotaLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnTirarComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnTbNotaLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(cbComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        lblTotal.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lblTotal.setText("Total:");

        lblValorTotal.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lblValorTotal.setText("0.00");

        pnTbClientes.setBackground(java.awt.SystemColor.control);
        pnTbClientes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblPesquisarCliente.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisarCliente.setText("Pesquisar:");

        txtPesquisarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarClienteActionPerformed(evt);
            }
        });
        txtPesquisarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarClienteKeyReleased(evt);
            }
        });

        pnCliente.setBackground(java.awt.SystemColor.control);
        pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente Selecinado:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbCliente = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbCliente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCliente.setFocusable(false);
        tbCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbClienteMouseClicked(evt);
            }
        });
        scCliente.setViewportView(tbCliente);

        javax.swing.GroupLayout pnClienteLayout = new javax.swing.GroupLayout(pnCliente);
        pnCliente.setLayout(pnClienteLayout);
        pnClienteLayout.setHorizontalGroup(
            pnClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClienteLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scCliente)
                .addGap(16, 16, 16))
        );
        pnClienteLayout.setVerticalGroup(
            pnClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClienteLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout pnTbClientesLayout = new javax.swing.GroupLayout(pnTbClientes);
        pnTbClientes.setLayout(pnTbClientesLayout);
        pnTbClientesLayout.setHorizontalGroup(
            pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTbClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTbClientesLayout.createSequentialGroup()
                        .addComponent(lblPesquisarCliente)
                        .addGap(16, 16, 16)
                        .addComponent(txtPesquisarCliente))
                    .addComponent(pnCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnTbClientesLayout.setVerticalGroup(
            pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPesquisarCliente)
                    .addComponent(txtPesquisarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(pnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        pnPagamento.setBackground(java.awt.SystemColor.control);
        pnPagamento.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Forma de Pagamento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        jPanel3.setBackground(java.awt.SystemColor.control);
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Outros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 12))); // NOI18N
        jPanel3.setFocusCycleRoot(true);

        rbCrediario.setText("#########");
        rbCrediario.setEnabled(false);
        rbCrediario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbCrediarioActionPerformed(evt);
            }
        });

        rbBoleto.setText("######");
        rbBoleto.setEnabled(false);
        rbBoleto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbBoletoActionPerformed(evt);
            }
        });

        rbAtivar.setText("Ativar");
        rbAtivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAtivarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(rbCrediario)
                        .addGap(6, 6, 6)
                        .addComponent(rbBoleto))
                    .addComponent(rbAtivar))
                .addGap(6, 6, 6))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(rbAtivar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbCrediario)
                    .addComponent(rbBoleto))
                .addGap(6, 6, 6))
        );

        jPanel6.setBackground(java.awt.SystemColor.control);
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Avista", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        rbPix.setText("PIX");
        rbPix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPixActionPerformed(evt);
            }
        });

        rbDinheiro.setText("Dinheiro");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbDinheiro)
                .addGap(16, 16, 16)
                .addComponent(rbPix)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbDinheiro)
                    .addComponent(rbPix))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(java.awt.SystemColor.control);
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Maquininha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbMaquininha.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "*" }));
        cbMaquininha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMaquininhaItemStateChanged(evt);
            }
        });

        rbDebito.setText("Cartão Debito");
        rbDebito.setEnabled(false);
        rbDebito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDebitoActionPerformed(evt);
            }
        });

        rbCredito.setText("Cartão Credito");
        rbCredito.setEnabled(false);
        rbCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbCreditoActionPerformed(evt);
            }
        });

        cbNumero.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbNumero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1x", "2x", "3x", "4x", "5x", "6x", "7x", "8x", "9x", "10x", "11x", "12x" }));
        cbNumero.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(cbMaquininha, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rbDebito)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(rbCredito)
                                .addGap(6, 6, 6)
                                .addComponent(cbNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(cbMaquininha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(rbDebito)
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbCredito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout pnPagamentoLayout = new javax.swing.GroupLayout(pnPagamento);
        pnPagamento.setLayout(pnPagamentoLayout);
        pnPagamentoLayout.setHorizontalGroup(
            pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagamentoLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        pnPagamentoLayout.setVerticalGroup(
            pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagamentoLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnPagamentoLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8))
        );

        btnConcluir.setForeground(new java.awt.Color(0, 0, 153));
        btnConcluir.setText("Concluir");
        btnConcluir.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnConcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConcluirActionPerformed(evt);
            }
        });

        btnDesconto.setForeground(new java.awt.Color(102, 0, 153));
        btnDesconto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/minus.png"))); // NOI18N
        btnDesconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescontoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnNotaLayout = new javax.swing.GroupLayout(pnNota);
        pnNota.setLayout(pnNotaLayout);
        pnNotaLayout.setHorizontalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnTbNota, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnTbClientes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnNotaLayout.createSequentialGroup()
                                .addComponent(lblTotal)
                                .addGap(16, 16, 16)
                                .addComponent(lblValorTotal)
                                .addGap(16, 16, 16)
                                .addComponent(btnDesconto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)))
                        .addGap(16, 16, 16)
                        .addComponent(pnPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        pnNotaLayout.setVerticalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnTbClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(pnTbNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTotal)
                                .addComponent(lblValorTotal)))
                        .addGap(16, 16, 16)
                        .addComponent(btnConcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        pnTbPrincipal.setBackground(java.awt.SystemColor.control);
        pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        scPdv.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scPdvMouseClicked(evt);
            }
        });

        tbListaDeInformaçoes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbListaDeInformaçoes.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbListaDeInformaçoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbListaDeInformaçoes.setFocusable(false);
        tbListaDeInformaçoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbListaDeInformaçoesMouseClicked(evt);
            }
        });
        scPdv.setViewportView(tbListaDeInformaçoes);

        lblPesquisa.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisa.setText("Pesquisa:");

        txtPesquisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisaActionPerformed(evt);
            }
        });
        txtPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyReleased(evt);
            }
        });

        rbOrdemDeServico.setBackground(java.awt.SystemColor.control);
        OS_Produtos.add(rbOrdemDeServico);
        rbOrdemDeServico.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbOrdemDeServico.setText("OS");
        rbOrdemDeServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbOrdemDeServicoActionPerformed(evt);
            }
        });

        rbProduto.setBackground(java.awt.SystemColor.control);
        OS_Produtos.add(rbProduto);
        rbProduto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbProduto.setSelected(true);
        rbProduto.setText("Produtos");
        rbProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbProdutoActionPerformed(evt);
            }
        });

        rbServico.setBackground(java.awt.SystemColor.control);
        OS_Produtos.add(rbServico);
        rbServico.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbServico.setText("Serviço");
        rbServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbServicoActionPerformed(evt);
            }
        });

        btnOS.setBackground(new java.awt.Color(0, 0, 0));
        btnOS.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnOS.setForeground(new java.awt.Color(255, 255, 255));
        btnOS.setText("Cadastrar OS");
        btnOS.setBorderPainted(false);
        btnOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSActionPerformed(evt);
            }
        });

        btnCliente.setBackground(new java.awt.Color(0, 0, 0));
        btnCliente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnCliente.setText("Cadastrar Cliente");
        btnCliente.setBorderPainted(false);
        btnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteActionPerformed(evt);
            }
        });

        OS_Produtos.add(rbCodigo);
        rbCodigo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        rbCodigo.setText("Codigo de Barras");
        rbCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbCodigoActionPerformed(evt);
            }
        });

        txtCodigo.setEnabled(false);
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodigoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnTbPrincipalLayout = new javax.swing.GroupLayout(pnTbPrincipal);
        pnTbPrincipal.setLayout(pnTbPrincipalLayout);
        pnTbPrincipalLayout.setHorizontalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                        .addComponent(btnOS, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCliente)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbPrincipalLayout.createSequentialGroup()
                        .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                                .addComponent(rbProduto)
                                .addGap(16, 16, 16)
                                .addComponent(rbServico, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(rbOrdemDeServico)
                                .addGap(16, 16, 16)
                                .addComponent(rbCodigo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodigo))
                            .addComponent(scPdv, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnTbPrincipalLayout.createSequentialGroup()
                                .addComponent(lblPesquisa)
                                .addGap(16, 16, 16)
                                .addComponent(txtPesquisa)))
                        .addGap(16, 16, 16))))
        );
        pnTbPrincipalLayout.setVerticalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbPrincipalLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPesquisa))
                .addGap(16, 16, 16)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbProduto)
                    .addComponent(rbServico)
                    .addComponent(rbOrdemDeServico)
                    .addComponent(rbCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOS)
                    .addComponent(btnCliente))
                .addGap(16, 16, 16)
                .addComponent(scPdv, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(19, 19, 19))
        );

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pnOBS.setBackground(java.awt.SystemColor.control);
        pnOBS.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        taDescricao.setEditable(false);
        taDescricao.setColumns(20);
        taDescricao.setRows(5);
        taDescricao.setFocusable(false);
        scText.setViewportView(taDescricao);

        javax.swing.GroupLayout pnOBSLayout = new javax.swing.GroupLayout(pnOBS);
        pnOBS.setLayout(pnOBSLayout);
        pnOBSLayout.setHorizontalGroup(
            pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
        );
        pnOBSLayout.setVerticalGroup(
            pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scText, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
        );

        jPanel2.setBackground(java.awt.SystemColor.control);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quantidade", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtQuantidade.setEnabled(false);
        txtQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeActionPerformed(evt);
            }
        });
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        btnAdicionar.setForeground(new java.awt.Color(0, 204, 102));
        btnAdicionar.setText("Adicionar");
        btnAdicionar.setEnabled(false);
        btnAdicionar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnMostrarFoto.setText("Mostrar Foto");
        btnMostrarFoto.setEnabled(false);
        btnMostrarFoto.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnMostrarFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarFotoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnOBS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMostrarFoto, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnOBS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(btnMostrarFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnTbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addComponent(pnNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(pnTbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void rbProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbProdutoActionPerformed
        // TODO add your handling code here:
        instanciarTabela();

    }//GEN-LAST:event_rbProdutoActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void rbOrdemDeServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbOrdemDeServicoActionPerformed
        // TODO add your handling code here:
        instanciarTabela();

    }//GEN-LAST:event_rbOrdemDeServicoActionPerformed

    private void tbListaDeInformaçoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbListaDeInformaçoesMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbListaDeInformaçoesMouseClicked

    private void txtPesquisaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txtPesquisaKeyReleased

    private void scPdvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scPdvMouseClicked

    }//GEN-LAST:event_scPdvMouseClicked

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txtQuantidadeKeyReleased

    private void tbClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClienteMouseClicked
        // TODO add your handling code here:
        setarCamposCliente();
    }//GEN-LAST:event_tbClienteMouseClicked

    private void txtPesquisarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisarClienteActionPerformed

    private void txtQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        // TODO add your handling code here:
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);
    }//GEN-LAST:event_btnClienteActionPerformed

    private void txtPesquisarClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarClienteKeyReleased
        // TODO add your handling code here:
        pesquisarCliente();
    }//GEN-LAST:event_txtPesquisarClienteKeyReleased

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        fecharPdv();

    }//GEN-LAST:event_formWindowClosed

    private void btnOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSActionPerformed
        // TODO add your handling code here:
        CadOS os = new CadOS();
        os.setVisible(true);
    }//GEN-LAST:event_btnOSActionPerformed

    private void tbItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbItemMouseClicked
        // TODO add your handling code here:
        setarCamposRemover();
    }//GEN-LAST:event_tbItemMouseClicked

    private void rbServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbServicoActionPerformed
        // TODO add your handling code here:
        instanciarTabela();
    }//GEN-LAST:event_rbServicoActionPerformed

    private void cbComandaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbComandaItemStateChanged
        // TODO add your handling code here:
        instanciarTabelaVenda();
        soma();
    }//GEN-LAST:event_cbComandaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        iniciar();
    }//GEN-LAST:event_formWindowOpened

    private void txtPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisaActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        criarNota();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnConcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConcluirActionPerformed
        // TODO add your handling code here:
        concluir();
    }//GEN-LAST:event_btnConcluirActionPerformed

    private void btnDescontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescontoActionPerformed
        // TODO add your handling code here:
        AdicionarDesconto();
    }//GEN-LAST:event_btnDescontoActionPerformed

    private void btnTirarComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTirarComandaActionPerformed
        // TODO add your handling code here:
        tirarComanda();
    }//GEN-LAST:event_btnTirarComandaActionPerformed

    private void btnComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComandaActionPerformed
        // TODO add your handling code here:
        criarComanda();
    }//GEN-LAST:event_btnComandaActionPerformed

    private void btnMostrarFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarFotoActionPerformed
        // TODO add your handling code here:
        MostrarFoto();
    }//GEN-LAST:event_btnMostrarFotoActionPerformed

    private void rbBoletoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBoletoActionPerformed
        // TODO add your handling code here:
        dividirboleto();
    }//GEN-LAST:event_rbBoletoActionPerformed

    private void rbPixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPixActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPixActionPerformed

    private void rbCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCreditoActionPerformed
        // TODO add your handling code here:
        InstanciarComboboxCredito();
    }//GEN-LAST:event_rbCreditoActionPerformed

    private void cbMaquininhaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMaquininhaItemStateChanged
        // TODO add your handling code here:
        combMaquininha();
    }//GEN-LAST:event_cbMaquininhaItemStateChanged

    private void rbAtivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAtivarActionPerformed
        // TODO add your handling code here:
        ativarFormasExtras();
    }//GEN-LAST:event_rbAtivarActionPerformed

    private void rbDebitoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDebitoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbDebitoActionPerformed

    private void rbCrediarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCrediarioActionPerformed
        // TODO add your handling code here:
        dividirCrediario();
    }//GEN-LAST:event_rbCrediarioActionPerformed

    private void rbCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCodigoActionPerformed
        // TODO add your handling code here:
        instanciarTabela();
    }//GEN-LAST:event_rbCodigoActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
        inserirProduto();
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtCodigoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoKeyPressed

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PontoDeVendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PontoDeVendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PontoDeVendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PontoDeVendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PontoDeVendas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup OS_Produtos;
    private br.com.LeGnusERP.Swing.botaoArredondado btnAdicionar;
    private javax.swing.JToggleButton btnCliente;
    private br.com.LeGnusERP.Swing.botaoArredondado btnComanda;
    private br.com.LeGnusERP.Swing.botaoArredondado btnConcluir;
    private br.com.LeGnusERP.Swing.botaoArredondado btnDesconto;
    private br.com.LeGnusERP.Swing.botaoArredondado btnMostrarFoto;
    private javax.swing.JToggleButton btnOS;
    private br.com.LeGnusERP.Swing.botaoArredondado btnTirarComanda;
    public static javax.swing.JComboBox<String> cbComanda;
    public static javax.swing.JComboBox<String> cbMaquininha;
    private javax.swing.JComboBox<String> cbNumero;
    private javax.swing.JTextField idCliente;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPesquisa;
    private javax.swing.JLabel lblPesquisarCliente;
    private javax.swing.JLabel lblPrecoFinal;
    private javax.swing.JLabel lblTotal;
    public static javax.swing.JLabel lblUsuarioPDV;
    public static javax.swing.JLabel lblValorTotal;
    private javax.swing.JPanel pnCliente;
    private javax.swing.JPanel pnNota;
    private javax.swing.JPanel pnOBS;
    private javax.swing.JPanel pnPagamento;
    private javax.swing.JPanel pnTbClientes;
    private javax.swing.JPanel pnTbNota;
    private javax.swing.JPanel pnTbPrincipal;
    private javax.swing.JRadioButton rbAtivar;
    private javax.swing.JRadioButton rbBoleto;
    private javax.swing.JRadioButton rbCodigo;
    private javax.swing.JRadioButton rbCrediario;
    private javax.swing.JRadioButton rbCredito;
    private javax.swing.JRadioButton rbDebito;
    private javax.swing.JRadioButton rbDinheiro;
    private javax.swing.JRadioButton rbOrdemDeServico;
    private javax.swing.JRadioButton rbPix;
    private javax.swing.JRadioButton rbProduto;
    private javax.swing.JRadioButton rbServico;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilio1;
    private javax.swing.JScrollPane scAuxilio2;
    private javax.swing.JScrollPane scCliente;
    private javax.swing.JScrollPane scComissao;
    private javax.swing.JScrollPane scDesconto;
    private javax.swing.JScrollPane scOS;
    private javax.swing.JScrollPane scPdv;
    private javax.swing.JScrollPane scSetar;
    private javax.swing.JScrollPane scText;
    private javax.swing.JScrollPane scTotal;
    private javax.swing.JTextArea taDescricao;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilio1;
    private javax.swing.JTable tbAuxilio2;
    private javax.swing.JTable tbCliente;
    private javax.swing.JTable tbComissao;
    private javax.swing.JTable tbDesconto;
    private javax.swing.JTable tbItem;
    private javax.swing.JTable tbListaDeInformaçoes;
    private javax.swing.JTable tbOS;
    private javax.swing.JTable tbSetar;
    private javax.swing.JTable tbTotal;
    private javax.swing.JTextField txtAux;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCrediario;
    private javax.swing.JTextField txtEstoque;
    private javax.swing.JTextField txtFoto;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtPesquisarCliente;
    private javax.swing.JTextField txtPreco;
    private javax.swing.JTextField txtQuantidade;
    private javax.swing.JTextField txtSubCliente;
    private javax.swing.JTextField txtTipo;
    // End of variables declaration//GEN-END:variables
}
