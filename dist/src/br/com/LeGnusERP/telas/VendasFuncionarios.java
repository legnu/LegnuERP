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
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Ad3ln0r
 */
public class VendasFuncionarios extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public VendasFuncionarios() {
        initComponents();
        conexao = ModuloConexao.conector();
        setIcon();
    }

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
    }

    public void instanciarTabela() {
        try {
            String sql = "select usuario as Vendedor_Funcionario, valorVendidoUsuario as Valor_Comissionado, cargo_usuario as Tipo from tbusuarios where iduser > 1 union select funcionario,valorVendido,tipo from tbFuncionarios where tipo = 'Funcionario'";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void imprimir() {

        try {

            java.util.Date aInicial = DaInicial.getDate();
            java.sql.Date bInicial = new java.sql.Date(aInicial.getTime());
            java.util.Date aFinal = DaFinal.getDate();
            java.sql.Date bFinal = new java.sql.Date(aFinal.getTime());

            int setar = tbPrincipal.getSelectedRow();
            String clase = tbPrincipal.getModel().getValueAt(setar, 2).toString();

            if (clase.equals("Vendedor") == true) {
                int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta Nota?", "Atençao", JOptionPane.YES_OPTION);
                if (confirma == JOptionPane.YES_OPTION) {
                    try {

                        String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                        pst = conexao.prepareStatement(sql);
                        rs = pst.executeQuery();
                        tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                        HashMap filtro = new HashMap();

                        filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                        filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                        filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                        filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                        filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                        filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                        filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                        filtro.put("Inicial", bInicial);
                        filtro.put("Final", bFinal);
                        filtro.put("comissao", tbPrincipal.getModel().getValueAt(setar, 0).toString());
                        String sqh = "select comissao from tbusuarios where usuario=?";
                        pst = conexao.prepareStatement(sqh);
                        pst.setString(1, tbPrincipal.getModel().getValueAt(setar, 0).toString());
                        rs = pst.executeQuery();
                        tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                        filtro.put("Porcentagem", tbAuxilio.getModel().getValueAt(0, 0).toString());
                        filtro.put("Decimal", String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100));
                        filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                        filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");

                        JasperReport jreport = JasperCompileManager.compileReport("src/reports/VendasFuncionarioProdutos_Servico.jrxml");

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
                        JOptionPane.showMessageDialog(null, "Adicione uma imagem no relatorio");

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);

                    }
                }
            } else if (clase.equals("Profissional/Tec") == true || clase.equals("Funcionario") == true) {
                int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta Nota?", "Atençao", JOptionPane.YES_OPTION);
                if (confirma == JOptionPane.YES_OPTION) {
                    try {

                        String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                        pst = conexao.prepareStatement(sql);
                        rs = pst.executeQuery();
                        tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                        HashMap filtro = new HashMap();

                        filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                        filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                        filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                        filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                        filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                        filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                        filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                        filtro.put("Inicial", bInicial);
                        filtro.put("Final", bFinal);
                        filtro.put("comissao", tbPrincipal.getModel().getValueAt(setar, 0).toString());
                        String sqh = "select comissao from tbFuncionarios where funcionario=?";
                        pst = conexao.prepareStatement(sqh);
                        pst.setString(1, tbPrincipal.getModel().getValueAt(setar, 0).toString());
                        rs = pst.executeQuery();
                        tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                        filtro.put("Porcentagem", tbAuxilio.getModel().getValueAt(0, 0).toString());
                        filtro.put("Decimal", String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100));

                        filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                        filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");

                        JasperReport jreport = JasperCompileManager.compileReport("src/reports/VendasFuncionarioProdutos_Servico.jrxml");

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
                        JOptionPane.showMessageDialog(null, "Adicione uma imagem no relatorio");

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);

                    }
                }
            } else if (clase.equals("Administrador") == true || clase.equals("Profissional/Tec e Vendedor") == true) {
                int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta Nota?", "Atençao", JOptionPane.YES_OPTION);
                if (confirma == JOptionPane.YES_OPTION) {
                    try {

                        String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                        pst = conexao.prepareStatement(sql);
                        rs = pst.executeQuery();
                        tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                        HashMap filtro = new HashMap();

                        filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                        filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                        filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                        filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                        filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                        filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                        filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                        filtro.put("Inicial", bInicial);
                        filtro.put("Final", bFinal);
                        filtro.put("comissao", tbPrincipal.getModel().getValueAt(setar, 0).toString());

                        String sqh = "select comissao from tbFuncionarios where funcionario=?";
                        pst = conexao.prepareStatement(sqh);
                        pst.setString(1, tbPrincipal.getModel().getValueAt(setar, 0).toString());
                        rs = pst.executeQuery();
                        tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                        filtro.put("porcentagem2", tbAuxilio.getModel().getValueAt(0, 0).toString());
                        filtro.put("Decimal2", String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100));

                        String sqg = "select comissao from tbusuarios where usuario=?";
                        pst = conexao.prepareStatement(sqg);
                        pst.setString(1, tbPrincipal.getModel().getValueAt(setar, 0).toString());
                        rs = pst.executeQuery();
                        tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                        filtro.put("Porcentagem", tbAuxilio.getModel().getValueAt(0, 0).toString());
                        filtro.put("Decimal", String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100));

                        filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                        filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");

                        JasperReport jreport = JasperCompileManager.compileReport("src/reports/VendasFuncionario.jrxml");

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
                        JOptionPane.showMessageDialog(null, "Adicione uma imagem no relatorio");

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);

                    }
                }
            }

            setarPorData();

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Adicione uma Data Inicial e Final.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void valorTotal() {
        String ValorI = "0";
        String ValorE = "0";
        try {
            for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
                ValorI = String.valueOf(Double.parseDouble(tbPrincipal.getModel().getValueAt(i, 1).toString().replace(".", "")) / 100);
                ValorE = String.valueOf(Double.parseDouble(ValorE) + Double.parseDouble(ValorI));
            }
            lblTotal.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(ValorE)).replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarValor() {
        try {
            String valorU = "0";
            String valorF = "0";
            String Ganho = "0";
            String comissao = "0";

            for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
                String sql = "select preco, tipo from tbvenda where comissao=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                for (int x = 0; x < tbAuxilio.getRowCount(); x++) {
                    valorU = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(x, 0).toString().replace(".", "")) / 100);
                    valorF = String.valueOf(Double.parseDouble(valorF) + Double.parseDouble(valorU));
                }

                if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Funcionario") == true || tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Profissional/Tec") == true) {

                    String sqh = "select comissao from tbFuncionarios where funcionario=?";
                    pst = conexao.prepareStatement(sqh);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    Ganho = String.valueOf(Double.parseDouble(comissao) * Double.parseDouble(valorF));

                    if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Funcionario") == true) {

                        String sqo = "update tbFuncionarios set valorVendido = ? where funcionario=?";
                        pst = conexao.prepareStatement(sqo);
                        pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(Ganho)).replace(",", "."));
                        pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                        pst.executeUpdate();

                    } else if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Profissional/Tec") == true) {

                        String sqo = "update tbusuarios set valorVendidoUsuario = ? where usuario=?";
                        pst = conexao.prepareStatement(sqo);
                        pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(Ganho)).replace(",", "."));
                        pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                        pst.executeUpdate();
                    }

                } else if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Vendedor") == true) {

                    String sqh = "select comissao from tbusuarios where usuario=?";
                    pst = conexao.prepareStatement(sqh);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    Ganho = String.valueOf(Double.parseDouble(comissao) * Double.parseDouble(valorF));

                    String sqo = "update tbusuarios set valorVendidoUsuario = ? where usuario=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(Ganho)).replace(",", "."));
                    pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                } else if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Profissional/Tec e Vendedor") == true || tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Administrador") == true) {
                    valorU = "0";
                    valorF = "0";
                    String ValorP = "0";
                    String comissaoP = "0";
                    String GanhoP = "0";

                    String sqc = "select preco from tbvenda where comissao=? and tipo = 'Produto'";
                    pst = conexao.prepareStatement(sqc);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    for (int x = 0; x < tbAuxilio.getRowCount(); x++) {
                        valorU = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(x, 0).toString().replace(".", "")) / 100);
                        valorF = String.valueOf(Double.parseDouble(valorF) + Double.parseDouble(valorU));
                    }

                    String sqx = "select preco from tbvenda where comissao=? and tipo != 'Produto'";
                    pst = conexao.prepareStatement(sqx);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    for (int x = 0; x < tbAuxilio.getRowCount(); x++) {
                        valorU = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(x, 0).toString().replace(".", "")) / 100);
                        ValorP = String.valueOf(Double.parseDouble(ValorP) + Double.parseDouble(valorU));
                    }

                    String sqh = "select comissao from tbusuarios where usuario=?";
                    pst = conexao.prepareStatement(sqh);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    String squ = "select comissao from tbFuncionarios where funcionario=?";
                    pst = conexao.prepareStatement(squ);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissaoP = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    Ganho = String.valueOf(Double.parseDouble(comissao) * Double.parseDouble(valorF));
                    GanhoP = String.valueOf(Double.parseDouble(comissaoP) * Double.parseDouble(ValorP));

                    String GanhoF = String.valueOf(Double.parseDouble(Ganho) + Double.parseDouble(GanhoP));

                    String sqo = "update tbusuarios set valorVendidoUsuario = ? where usuario=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(GanhoF)).replace(",", "."));
                    pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                }
                valorU = "0";
                valorF = "0";
                Ganho = "0";
                comissao = "0";

                instanciarTabela();

            }

            System.out.println(valorF);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    //and  dia between '" + DaInicial + "' and '" + DaFinal + "'
    public void setarPorData() {
        try {
            String valorU = "0";
            String valorF = "0";
            String Ganho = "0";
            String comissao = "0";

            java.util.Date aInicial = DaInicial.getDate();
            java.sql.Date bInicial = new java.sql.Date(aInicial.getTime());
            java.util.Date aFinal = DaFinal.getDate();
            java.sql.Date bFinal = new java.sql.Date(aFinal.getTime());

            for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
                String sql = "select preco, tipo from tbvenda where comissao=? and date(emicao) between ? and ?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                pst.setDate(2, bInicial);
                pst.setDate(3, bFinal);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                for (int x = 0; x < tbAuxilio.getRowCount(); x++) {
                    valorU = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(x, 0).toString().replace(".", "")) / 100);
                    valorF = String.valueOf(Double.parseDouble(valorF) + Double.parseDouble(valorU));
                }

                if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Funcionario") == true || tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Profissional/Tec") == true) {

                    String sqh = "select comissao from tbFuncionarios where funcionario=?";
                    pst = conexao.prepareStatement(sqh);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    Ganho = String.valueOf(Double.parseDouble(comissao) * Double.parseDouble(valorF));

                    if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Funcionario") == true) {

                        String sqo = "update tbFuncionarios set valorVendido = ? where funcionario=?";
                        pst = conexao.prepareStatement(sqo);
                        pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(Ganho)).replace(",", "."));
                        pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                        pst.executeUpdate();

                    } else if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Profissional/Tec") == true) {

                        String sqo = "update tbusuarios set valorVendidoUsuario = ? where usuario=?";
                        pst = conexao.prepareStatement(sqo);
                        pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(Ganho)).replace(",", "."));
                        pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                        pst.executeUpdate();
                    }

                } else if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Vendedor") == true) {

                    String sqh = "select comissao from tbusuarios where usuario=?";
                    pst = conexao.prepareStatement(sqh);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    Ganho = String.valueOf(Double.parseDouble(comissao) * Double.parseDouble(valorF));

                    String sqo = "update tbusuarios set valorVendidoUsuario = ? where usuario=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(Ganho)).replace(",", "."));
                    pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                } else if (tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Profissional/Tec e Vendedor") == true || tbPrincipal.getModel().getValueAt(i, 2).toString().equals("Administrador") == true) {
                    valorU = "0";
                    valorF = "0";
                    String ValorP = "0";
                    String comissaoP = "0";
                    String GanhoP = "0";

                    String sqc = "select preco from tbvenda where comissao=? and tipo = 'Produto' and date(emicao) between ? and ?";
                    pst = conexao.prepareStatement(sqc);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    pst.setDate(2, bInicial);
                    pst.setDate(3, bFinal);
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    for (int x = 0; x < tbAuxilio.getRowCount(); x++) {
                        valorU = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(x, 0).toString().replace(".", "")) / 100);
                        valorF = String.valueOf(Double.parseDouble(valorF) + Double.parseDouble(valorU));
                    }

                    String sqx = "select preco from tbvenda where comissao=? and tipo != 'Produto' and date(emicao) between ? and ?";
                    pst = conexao.prepareStatement(sqx);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    pst.setDate(2, bInicial);
                    pst.setDate(3, bFinal);
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    for (int x = 0; x < tbAuxilio.getRowCount(); x++) {
                        valorU = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(x, 0).toString().replace(".", "")) / 100);
                        ValorP = String.valueOf(Double.parseDouble(ValorP) + Double.parseDouble(valorU));
                    }

                    String sqh = "select comissao from tbusuarios where usuario=?";
                    pst = conexao.prepareStatement(sqh);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    String squ = "select comissao from tbFuncionarios where funcionario=?";
                    pst = conexao.prepareStatement(squ);
                    pst.setString(1, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                    comissaoP = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);

                    Ganho = String.valueOf(Double.parseDouble(comissao) * Double.parseDouble(valorF));
                    GanhoP = String.valueOf(Double.parseDouble(comissaoP) * Double.parseDouble(ValorP));

                    String GanhoF = String.valueOf(Double.parseDouble(Ganho) + Double.parseDouble(GanhoP));

                    String sqo = "update tbusuarios set valorVendidoUsuario = ? where usuario=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(GanhoF)).replace(",", "."));
                    pst.setString(2, tbPrincipal.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                }
                valorU = "0";
                valorF = "0";
                Ganho = "0";
                comissao = "0";

                instanciarTabela();

            }

            System.out.println(valorF);

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Adicione uma Data Inicial e Final.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

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

        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        DaInicial = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        DaFinal = new com.toedter.calendar.JDateChooser();
        btnSetar = new javax.swing.JToggleButton();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Vendas Funcionarios.");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Valor Vendas(R$):");

        lblTotal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblTotal.setText("#.##");

        jPanel4.setBackground(java.awt.SystemColor.control);
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbPrincipal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbPrincipal.setFocusable(false);
        tbPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPrincipalMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbPrincipal);

        jPanel2.setBackground(java.awt.SystemColor.control);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Inicial", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        DaInicial.setDateFormatString("y-MM-dd");
        DaInicial.setFocusable(false);
        DaInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                DaInicialKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaInicial, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaInicial, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel3.setBackground(java.awt.SystemColor.control);
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Final", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        DaFinal.setDateFormatString("y-MM-dd");
        DaFinal.setFocusable(false);
        DaFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                DaFinalKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaFinal, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaFinal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnSetar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/lupa.png"))); // NOI18N
        btnSetar.setBorderPainted(false);
        btnSetar.setContentAreaFilled(false);
        btnSetar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnSetar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(btnSetar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSetar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(jLabel2))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void DaInicialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DaInicialKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_DaInicialKeyReleased

    private void DaFinalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DaFinalKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_DaFinalKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        instanciarTabela();
        instanciarValor();
        valorTotal();
    }//GEN-LAST:event_formWindowOpened

    private void btnSetarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetarActionPerformed
        // TODO add your handling code here:
        setarPorData();
        valorTotal();
    }//GEN-LAST:event_btnSetarActionPerformed

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked
        // TODO add your handling code here:
        imprimir();
    }//GEN-LAST:event_tbPrincipalMouseClicked

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
            java.util.logging.Logger.getLogger(VendasFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VendasFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VendasFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VendasFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VendasFuncionarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DaFinal;
    private com.toedter.calendar.JDateChooser DaInicial;
    private javax.swing.JToggleButton btnSetar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbPrincipal;
    // End of variables declaration//GEN-END:variables
}
