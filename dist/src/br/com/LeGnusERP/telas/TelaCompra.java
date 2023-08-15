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
import static br.com.LeGnusERP.telas.PontoDeVendas.lblValorTotal;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JRViewer;

public class TelaCompra extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaCompra() {
        initComponents();
        conexao = ModuloConexao.conector();
        setIcon();
    }

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
    }

    public void InstanciarCombobox() {
        try {

            String sql = "select nome_fornecedor from tbfornecedor";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbFornecedor.addItem(rs.getString("nome_fornecedor"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

            Limpar();

        }
    }

    public void InstanciarComboboxPagamento() {
        if (String.valueOf(cbPagamento.getSelectedItem()).equals("A Vista (Dinhero)") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Debito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Pix") == true) {
            cbNumero.setSelectedItem("1x");
            cbNumero.setEnabled(false);
            dtPagamento.setEnabled(true);

        } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Credito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Crediario") == true) {
            cbNumero.setEnabled(true);
            dtPagamento.setDate(null);
            dtPagamento.setEnabled(false);
        }
    }

    public void iniciar() {
        instanciarTabela();
        InstanciarCombobox();
        instanciarTabelaNotaCompra();
        instanciarTabelaSoma();
        txtValorUnidade.setText("0.00");
        txtValorFinal.setText("0.00");
    }

    public void adicionarCompra() {
        try {
            int quantidadeAdicionada = Integer.parseInt(txtQuantidade.getText());
            int quantidadeInicial = Integer.parseInt(txtQuantidadeInicial.getText());
            int quantidadeFinal = quantidadeAdicionada + quantidadeInicial;

            String sql = "insert into tbcompra(nome_produto, valor, valor_unidade, fornecedor, quantidade_comprada,identificador)values(?,?,?,?,?,?)";

            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtProduto.getText());
            pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtValorFinal.getText())).replace(",", "."));
            pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtValorUnidade.getText())).replace(",", "."));
            pst.setString(4, cbFornecedor.getSelectedItem().toString());
            pst.setString(5, txtQuantidade.getText());
            pst.setString(6, "Compra");

            //Validação dos Campos Obrigatorios
            if ((txtProduto.getText().isEmpty()) || (txtValorFinal.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                Limpar();

            } else {

                if (quantidadeAdicionada <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantidade Adicionada deve ser maior que 0.");
                    Limpar();
                }
                if ((txtProduto.getText().isEmpty()) || (txtValorFinal.getText().isEmpty()) || (txtQuantidade.getText().isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                    Limpar();

                } else {
                    pst.executeUpdate();

                    String sqr = "update tbprodutos set quantidade=? where idproduto=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setInt(1, quantidadeFinal);
                    pst.setString(2, txtID.getText());
                    int adicionado = pst.executeUpdate();

                    double valor_compra = Double.parseDouble(txtValorUnidade.getText().replace(".", "")) / 100;
                    double referencial_compra = quantidadeFinal * valor_compra;

                    String sqy = "update tbprodutos set referencial_compra=? where idproduto=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(referencial_compra))).replace(",", "."));
                    pst.setString(2, txtID.getText());
                    pst.executeUpdate();

                    String squ = "select valor_venda from tbprodutos where produto=?";

                    pst = conexao.prepareStatement(squ);
                    pst.setString(1, txtProduto.getText());
                    rs = pst.executeQuery();
                    tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));

                    double valor_venda = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;
                    double referencial_venda = quantidadeFinal * valor_venda;

                    String sqo = "update tbprodutos set referencial_venda=? where idproduto=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(referencial_venda))).replace(",", "."));
                    pst.setString(2, txtID.getText());
                    pst.executeUpdate();

                    //A Linha abaixo serve de apoio ao entendimento da logica
                    //System.out.println(adicionado);
                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Produto(s) Adicionado(s) com sucesso.");
                        Limpar();

                        instanciarTabelaNotaCompra();
                        instanciarTabelaSoma();

                    }

                }

            }
        } catch (java.lang.NumberFormatException e) {
            if (txtQuantidade.getText().isEmpty() == true) {
                JOptionPane.showMessageDialog(null, "Quantidade não pode ser nula.");
                Limpar();
            } else {
                JOptionPane.showMessageDialog(null, "Quantidade deve ser um numero Inteiro.");
                Limpar();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();
        }
    }

    public void instanciarTabela() {
        String sql = "select idproduto as ID, produto as Produto,valor_compra as Preço, quantidade as Quantidade, fornecedor as Fornecedor from tbprodutos where estoque=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, "Com controle de estoque.");
            rs = pst.executeQuery();
            tbProduto.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    public void instanciarTabelaNotaCompra() {
        String sqr = "select idcompra as ID_Compra, nome_produto as Produto,fornecedor as Fornecedor, quantidade_comprada as Quantidade_Adicionada, valor as Valor_Total from tbcompra where identificador = 'Compra'";
        try {
            pst = conexao.prepareStatement(sqr);
            rs = pst.executeQuery();
            tbNotaCompra.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    public void instanciarTabelaSoma() {
        double preco, x;

        String sql = "select valor from tbcompra where identificador = 'Compra'";
        try {

            preco = 0;

            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbSoma.setModel(DbUtils.resultSetToTableModel(rs));
            for (int i = 0; i < tbSoma.getRowCount(); i++) {
                x = Double.parseDouble(tbSoma.getModel().getValueAt(i, 0).toString().replace(".", "")) / 100;
                preco = preco + x;

            }

            lblValorTotal.setText(new DecimalFormat("#,##0.00").format(preco).replace(",", "."));
        } catch (java.lang.NullPointerException e) {
            lblValorTotal.setText("0.00");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    public void Calculo_valor_final() {

        double calculo;
        int quantidade;
        double custo;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText());
            custo = Double.parseDouble(txtValorUnidade.getText());
            calculo = quantidade * custo;
            txtValorFinal.setText(String.valueOf(calculo));
            if (Integer.parseInt(txtQuantidade.getText()) <= 0) {
                txtValorFinal.setText("0.00");
            }

        } catch (java.lang.NumberFormatException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    public void comprar() {
        int acumulo = 1;
        int identificador;
        try {
            String compra = JOptionPane.showInputDialog("Escreva um identificador para esta compra.", JOptionPane.OK_OPTION);
            if (compra.isEmpty() == false) {
                
                String squ = "select identificador from tbgastos order by identificador desc";
                pst = conexao.prepareStatement(squ);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                if (tbAuxilio.getModel().getValueAt(0, 0) != null) {
                    identificador = Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString()) + 1;
                } else {
                    identificador = 1;
                }

                if (Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100 <= 0) {
                    JOptionPane.showMessageDialog(null, "Valor total deve ser maior que 0");
                    Limpar();
                } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("A Vista (Dinhero)") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Debito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Pix") == true) {
                    double preco;

                    preco = Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100;

                    Date d = new Date();
                    Date y = dtPagamento.getDate();
                    String status;

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                    java.sql.Date dSql = new java.sql.Date(d.getTime());
                    df.format(dSql);

                    java.sql.Date dSqo = new java.sql.Date(y.getTime());
                    df.format(dSqo);

                    if (d.before(y)) {
                        status = "Pendente";
                    } else {
                        status = "Pago";

                    }

                    String sql = "insert into tbgastos(valor, data_emissao, status_pagamento, data_pagamento, nome, forma_pagamento,tipo,identificador)values(?,?,?,?,?,?,?,?)";

                    pst = conexao.prepareStatement(sql);

                    pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(preco))).replace(",", "."));
                    pst.setDate(2, dSql);
                    pst.setString(3, status);
                    pst.setDate(4, dSqo);
                    pst.setString(5, compra);
                    pst.setString(6, cbPagamento.getSelectedItem().toString());
                    pst.setString(7, "Compra");
                    pst.setInt(8, identificador);
                    pst.executeUpdate();

                } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Credito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Crediario") == true) {

                    Date d = new Date();
                    int parcela = 1;

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                    java.sql.Date dSql = new java.sql.Date(d.getTime());
                    df.format(dSql);

                    for (int i = 0; i < Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", "")); i++) {

                        String mes = new SimpleDateFormat("MM").format(d);
                        String ano = new SimpleDateFormat("yyyy").format(d);

                        System.out.println(mes + "x" + ano);

                        int limite = Integer.parseInt(mes) + acumulo;

                        if (limite > 12) {
                            limite = limite - 12;
                            ano = String.valueOf(Integer.parseInt(ano) + 1);
                        }

                        String dataLimite = ano + "-" + limite + "-05";

                        Date data = df.parse(dataLimite);
                        java.sql.Date dSqt = new java.sql.Date(data.getTime());
                        df.format(dSqt);

                        String sql = "insert into tbgastos(valor, data_emissao, status_pagamento, data_pagamento, nome, forma_pagamento, tipo,identificador)values(?,?,?,?,?,?,?,?)";

                        pst = conexao.prepareStatement(sql);

                        pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100)) / Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", ""))).replace(",", "."));
                        pst.setDate(2, dSql);
                        pst.setString(3, "Pendente");
                        pst.setDate(4, dSqt);
                        pst.setString(5, compra + " Parcela: " + parcela + "/" + String.valueOf(cbNumero.getSelectedItem()).replace("x", ""));
                        pst.setString(6, cbPagamento.getSelectedItem().toString());
                        pst.setString(7, "Compra");
                        pst.setInt(8, identificador);
                        pst.executeUpdate();
                        acumulo++;
                        parcela++;
                    }
                }
                JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                for (int i = 0; i < tbNotaCompra.getRowCount(); i++) {
                    String sqr = "update tbcompra set identificador=? where idcompra=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setInt(1, identificador);
                    pst.setString(2, tbNotaCompra.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                }
                JOptionPane.showMessageDialog(null, "Produto(s) comprado(s) com sucesso.");
                Limpar();
            } else {
                JOptionPane.showMessageDialog(null, "Identificador não pode ser nulo");
            }
        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Adicione uma Data de Pagamento.");
            Limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }

    }

    public void remover() {

        int quantidadeAdicionada;
        int quantidadeAtual;
        int quantidade;
        double referencial_compra;

        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este produto?", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {

            try {

                quantidadeAdicionada = Integer.parseInt(txtQuantidade.getText());
                quantidadeAtual = Integer.parseInt(txtQuantidadeInicial.getText());

                quantidade = quantidadeAtual - quantidadeAdicionada;

                String sqy = "select referencial_compra from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqy);
                pst.setString(1, txtProduto.getText());
                rs = pst.executeQuery();
                tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));

                referencial_compra = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String squ = "select valor_venda from tbprodutos where produto=?";

                pst = conexao.prepareStatement(squ);
                pst.setString(1, txtProduto.getText());
                rs = pst.executeQuery();
                tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));

                double valor_venda = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String sqt = "select referencial_venda from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqt);
                pst.setString(1, txtProduto.getText());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                double referencial_venda = Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                double x = valor_venda * Double.parseDouble(txtQuantidade.getText());

                String sqr = "update tbprodutos set quantidade=?, referencial_compra=?, referencial_venda=? where produto=?";
                double y = referencial_compra - Double.parseDouble(txtValorFinal.getText());
                double z = referencial_venda - x;

                pst = conexao.prepareStatement(sqr);
                pst.setInt(1, quantidade);
                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(y))).replace(",", "."));
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(z))).replace(",", "."));
                pst.setString(4, txtProduto.getText());
                pst.executeUpdate();

                String sql = "delete from tbcompra where idcompra=?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtID.getText());
                int apagado = pst.executeUpdate();

                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "Produto removido com sucesso.");
                    Limpar();

                }
            } catch (java.lang.NullPointerException e) {

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                Limpar();

            }
        }
    }

    public void setar_campos() {
        Limpar();
        int setar = tbProduto.getSelectedRow();
        cbFornecedor.addItem(tbProduto.getModel().getValueAt(setar, 4).toString());
        txtID.setText(tbProduto.getModel().getValueAt(setar, 0).toString());
        txtProduto.setText(tbProduto.getModel().getValueAt(setar, 1).toString());
        String valor = new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(Double.parseDouble(tbProduto.getModel().getValueAt(setar, 2).toString().replace(".", "")) / 100))).replace(",", ".");
        txtValorUnidade.setText(String.valueOf(valor));
        txtQuantidadeInicial.setText(tbProduto.getModel().getValueAt(setar, 3).toString());
        cbFornecedor.setSelectedItem(tbProduto.getModel().getValueAt(setar, 4).toString());

        //A Linha Abaixo desabilita o botão adicionar
        txtPesquisa.setText(null);
        txtQuantidade.setEnabled(true);
        btnAdicionar.setEnabled(true);

    }

    public void setar_campos_remover() {
        try {

            int setar = tbNotaCompra.getSelectedRow();
            txtID.setText(tbNotaCompra.getModel().getValueAt(setar, 0).toString());
            txtProduto.setText(tbNotaCompra.getModel().getValueAt(setar, 1).toString());
            cbFornecedor.setSelectedItem(tbNotaCompra.getModel().getValueAt(setar, 2).toString());
            txtQuantidade.setText(tbNotaCompra.getModel().getValueAt(setar, 3).toString());
            double valor = Double.parseDouble(tbNotaCompra.getModel().getValueAt(setar, 4).toString().replace(".", "")) / 100;
            txtValorFinal.setText(String.valueOf(valor));

            String sql = "select quantidade from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduto.getText());
            rs = pst.executeQuery();
            tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));
            txtQuantidadeInicial.setText(tbEstoque.getModel().getValueAt(0, 0).toString());

            String sqy = "select valor_compra from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sqy);
            pst.setString(1, txtProduto.getText());
            rs = pst.executeQuery();
            tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));
            double valorUnidade = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;
            txtValorUnidade.setText(String.valueOf(valorUnidade));

            txtPesquisa.setText(null);
            txtQuantidade.setEnabled(false);
            btnAdicionar.setEnabled(false);
            btnRemove.setEnabled(true);
        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    private void criarId() {
        String sql = "alter table tbcompra add idcompra MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    private void tirarId() {

        String sql = "alter table tbcompra drop idcompra";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }

    }

    public void Limpar() {
        instanciarTabelaSoma();
        instanciarTabelaNotaCompra();

        dtPagamento.setDate(null);
        txtProduto.setText(null);
        txtID.setText(null);
        txtQuantidade.setText(null);
        txtValorUnidade.setText("0.00");
        txtValorFinal.setText("0.00");
        txtQuantidadeInicial.setText(null);
        txtQuantidade.setEnabled(false);
        btnAdicionar.setEnabled(false);
        btnRemove.setEnabled(false);

    }

    public void CadastroProduto() {
        CadProduto produto = new CadProduto();
        produto.setVisible(true);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scSoma = new javax.swing.JScrollPane();
        tbSoma = new javax.swing.JTable();
        scEstoque = new javax.swing.JScrollPane();
        tbEstoque = new javax.swing.JTable();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        txtID = new javax.swing.JTextField();
        jScrollBar1 = new javax.swing.JScrollBar();
        jPanel8 = new javax.swing.JPanel();
        pnNota = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        pnTbNota = new javax.swing.JPanel();
        scNotaProduto1 = new javax.swing.JScrollPane();
        tbNotaCompra = new javax.swing.JTable();
        pnPagamento = new javax.swing.JPanel();
        cbPagamento = new javax.swing.JComboBox<>();
        cbNumero = new javax.swing.JComboBox<>();
        btnComprar = new br.com.LeGnusERP.Swing.botaoArredondado();
        btnRemove = new br.com.LeGnusERP.Swing.botaoArredondado();
        jPanel7 = new javax.swing.JPanel();
        dtPagamento = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        cbFornecedor = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        txtQuantidadeInicial = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        txtQuantidade = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        txtValorUnidade = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        txtValorFinal = new javax.swing.JTextField();
        pnProduto = new javax.swing.JPanel();
        scProduto = new javax.swing.JScrollPane();
        tbProduto = new javax.swing.JTable();
        lblPesquisar = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        txtProduto = new javax.swing.JTextField();
        btnAdicionar = new br.com.LeGnusERP.Swing.botaoArredondado();

        tbSoma.setModel(new javax.swing.table.DefaultTableModel(
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
        scSoma.setViewportView(tbSoma);

        tbEstoque.setModel(new javax.swing.table.DefaultTableModel(
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
        scEstoque.setViewportView(tbEstoque);

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

        txtID.setEditable(false);
        txtID.setEnabled(false);
        txtID.setFocusable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Tela de Compra");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanel8.setBackground(java.awt.SystemColor.control);
        jPanel8.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        pnNota.setBackground(java.awt.SystemColor.control);
        pnNota.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTotal.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lblTotal.setText("Total:");

        lblValorTotal.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lblValorTotal.setText("0.00");

        pnTbNota.setBackground(java.awt.SystemColor.control);
        pnTbNota.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Nota", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbNotaCompra = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbNotaCompra.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbNotaCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbNotaCompra.setFocusable(false);
        tbNotaCompra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbNotaCompraMouseClicked(evt);
            }
        });
        scNotaProduto1.setViewportView(tbNotaCompra);

        javax.swing.GroupLayout pnTbNotaLayout = new javax.swing.GroupLayout(pnTbNota);
        pnTbNota.setLayout(pnTbNotaLayout);
        pnTbNotaLayout.setHorizontalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTbNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scNotaProduto1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnTbNotaLayout.setVerticalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTbNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scNotaProduto1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        pnPagamento.setBackground(java.awt.SystemColor.control);
        pnPagamento.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Forma de Pagamento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbPagamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A Vista (Dinhero)", "Cartão Debito", "Pix", "Cartão Credito", "Crediario" }));
        cbPagamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPagamentoActionPerformed(evt);
            }
        });

        cbNumero.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbNumero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1x", "2x", "3x", "4x", "5x", "6x", "7x", "8x", "9x", "10x", "11x", "12x" }));
        cbNumero.setEnabled(false);

        javax.swing.GroupLayout pnPagamentoLayout = new javax.swing.GroupLayout(pnPagamento);
        pnPagamento.setLayout(pnPagamentoLayout);
        pnPagamentoLayout.setHorizontalGroup(
            pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagamentoLayout.createSequentialGroup()
                .addComponent(cbPagamento, 0, 133, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        pnPagamentoLayout.setVerticalGroup(
            pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbPagamento)
                .addComponent(cbNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btnComprar.setForeground(new java.awt.Color(0, 0, 153));
        btnComprar.setText("Comprar");
        btnComprar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });

        btnRemove.setForeground(new java.awt.Color(153, 0, 0));
        btnRemove.setText("Remover");
        btnRemove.setToolTipText("");
        btnRemove.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        jPanel7.setBackground(java.awt.SystemColor.control);
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data do Pagamento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dtPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dtPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout pnNotaLayout = new javax.swing.GroupLayout(pnNota);
        pnNota.setLayout(pnNotaLayout);
        pnNotaLayout.setHorizontalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnNotaLayout.createSequentialGroup()
                                .addComponent(btnComprar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(16, 16, 16)
                                .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNotaLayout.createSequentialGroup()
                                .addComponent(lblTotal)
                                .addGap(16, 16, 16)
                                .addComponent(lblValorTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnTbNota, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16))))
        );
        pnNotaLayout.setVerticalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(pnTbNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTotal)
                        .addComponent(lblValorTotal)))
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        jPanel2.setBackground(java.awt.SystemColor.control);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fornecedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbFornecedor.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFornecedor, 0, 175, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBackground(java.awt.SystemColor.control);
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quant. Estoque", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtQuantidadeInicial.setEditable(false);
        txtQuantidadeInicial.setEnabled(false);
        txtQuantidadeInicial.setFocusable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidadeInicial)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidadeInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel4.setBackground(java.awt.SystemColor.control);
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quant. Comprada", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtQuantidade.setEnabled(false);
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidade)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel5.setBackground(java.awt.SystemColor.control);
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Valor Unitario(R$)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtValorUnidade.setEditable(false);
        txtValorUnidade.setEnabled(false);
        txtValorUnidade.setFocusable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtValorUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtValorUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel6.setBackground(java.awt.SystemColor.control);
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Valor Total(R$)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtValorFinal.setEditable(false);
        txtValorFinal.setEnabled(false);
        txtValorFinal.setFocusable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtValorFinal, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnProduto.setBackground(java.awt.SystemColor.control);
        pnProduto.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbProduto = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbProduto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbProduto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbProduto.setFocusable(false);
        tbProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProdutoMouseClicked(evt);
            }
        });
        scProduto.setViewportView(tbProduto);

        lblPesquisar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisar.setText("Pesquisar:");

        jToggleButton1.setBackground(new java.awt.Color(0, 0, 0));
        jToggleButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jToggleButton1.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButton1.setText("Cadastrar Produto");
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnProdutoLayout = new javax.swing.GroupLayout(pnProduto);
        pnProduto.setLayout(pnProdutoLayout);
        pnProdutoLayout.setHorizontalGroup(
            pnProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnProdutoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnProdutoLayout.createSequentialGroup()
                        .addComponent(lblPesquisar)
                        .addGap(16, 16, 16)
                        .addComponent(txtPesquisa))
                    .addGroup(pnProdutoLayout.createSequentialGroup()
                        .addComponent(jToggleButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        pnProdutoLayout.setVerticalGroup(
            pnProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnProdutoLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPesquisar))
                .addGap(16, 16, 16)
                .addComponent(jToggleButton1)
                .addGap(16, 16, 16)
                .addComponent(scProduto, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtProduto.setEditable(false);
        txtProduto.setEnabled(false);
        txtProduto.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtProduto, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        btnAdicionar.setForeground(new java.awt.Color(0, 204, 51));
        btnAdicionar.setText("Adicionar");
        btnAdicionar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnProduto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16)
                .addComponent(pnNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(pnProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        iniciar();
    }//GEN-LAST:event_formWindowActivated

    private void tbProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProdutoMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbProdutoMouseClicked

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        // TODO add your handling code here:
        Calculo_valor_final();
    }//GEN-LAST:event_txtQuantidadeKeyReleased

    private void tbNotaCompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbNotaCompraMouseClicked
        // TODO add your handling code here:
        setar_campos_remover();
    }//GEN-LAST:event_tbNotaCompraMouseClicked

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        CadastroProduto();

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void cbPagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPagamentoActionPerformed
        // TODO add your handling code here:
        InstanciarComboboxPagamento();
    }//GEN-LAST:event_cbPagamentoActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        adicionarCompra();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarActionPerformed
        // TODO add your handling code here:
        comprar();
    }//GEN-LAST:event_btnComprarActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        // TODO add your handling code here:
        remover();
    }//GEN-LAST:event_btnRemoveActionPerformed

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
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaCompra().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private br.com.LeGnusERP.Swing.botaoArredondado btnAdicionar;
    private br.com.LeGnusERP.Swing.botaoArredondado btnComprar;
    private br.com.LeGnusERP.Swing.botaoArredondado btnRemove;
    private javax.swing.JComboBox<String> cbFornecedor;
    private javax.swing.JComboBox<String> cbNumero;
    private javax.swing.JComboBox<String> cbPagamento;
    private com.toedter.calendar.JDateChooser dtPagamento;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblPesquisar;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JPanel pnNota;
    private javax.swing.JPanel pnPagamento;
    private javax.swing.JPanel pnProduto;
    private javax.swing.JPanel pnTbNota;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scEstoque;
    private javax.swing.JScrollPane scNotaProduto1;
    private javax.swing.JScrollPane scProduto;
    private javax.swing.JScrollPane scSoma;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbEstoque;
    private javax.swing.JTable tbNotaCompra;
    private javax.swing.JTable tbProduto;
    private javax.swing.JTable tbSoma;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtProduto;
    private javax.swing.JTextField txtQuantidade;
    private javax.swing.JTextField txtQuantidadeInicial;
    private javax.swing.JTextField txtValorFinal;
    private javax.swing.JTextField txtValorUnidade;
    // End of variables declaration//GEN-END:variables
}
