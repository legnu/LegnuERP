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
public class telaCaixa extends javax.swing.JFrame {

    /**
     * Creates new form telaCaixa
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public telaCaixa() {
        initComponents();
        conexao = ModuloConexao.conector();
        setIcon();        
    } 
    
    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
    }

    
    public void contador(){
        try {
            String sql = "update tbgastos set comunicarGasto=null where idgastos";
            pst = conexao.prepareStatement(sql);                        
            pst.executeUpdate();
            String squ = "update tbtotalvendas set comunicarVenda=null where id";
            pst = conexao.prepareStatement(squ);                        
            pst.executeUpdate();
            
         
            int contagem = 1;
            
            for(int i = 0; i < tbCaixaPago.getRowCount(); i++){            
            String sqh = "update tbgastos set comunicarGasto=? where idgastos=?";
            pst = conexao.prepareStatement(sqh);
            pst.setInt(1, contagem);
            pst.setString(2, tbCaixaPago.getModel().getValueAt(i, 0).toString());            
            pst.executeUpdate();
            contagem++;            
            }
            
           
            contagem = 1;
            
            for(int i = 0; i < tbCaixaRecebido.getRowCount(); i++){           
            String sqk = "update tbtotalvendas set comunicarVenda=? where id=?";
            pst = conexao.prepareStatement(sqk);
            pst.setInt(1, contagem);
            pst.setString(2, tbCaixaRecebido.getModel().getValueAt(i, 0).toString());            
            pst.executeUpdate();
            contagem++;
            }
            
        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, e);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }        
    }

    public void instanciarTabela() {
        try {
            String sql = "select idgastos, data_pagamento from tbgastos";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

            double caixa = Double.parseDouble(lblTotalRecebido.getText().replace(".", "")) / 100 - Double.parseDouble(lblTotalPago.getText().replace(".", "")) / 100;
            lblTotalCaixa.setText(new DecimalFormat("#,##0.00").format(caixa).replace(",", "."));

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }



    public void instanciarTabelaCliente() {
        try {
            String sql = "select id from tbtotalvendas where tipo='Venda'";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbAuxilio1.getRowCount(); i++) {

                String sqo = "select idcliente from tbtotalvendas where id=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, tbAuxilio1.getModel().getValueAt(i, 0).toString());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                String id = tbAuxilio.getModel().getValueAt(0, 0).toString();

                String sqy = "select nomecli from tbclientes where idcli=?";
                pst = conexao.prepareStatement(sqy);
                pst.setString(1, id);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                String nome = tbAuxilio.getModel().getValueAt(0, 0).toString();
                System.out.println(nome + " " + id);

                String sqr = "update tbtotalvendas set cliente=? where id=?";
                pst = conexao.prepareStatement(sqr);
                pst.setString(1, nome);
                pst.setString(2, tbAuxilio1.getModel().getValueAt(i, 0).toString());
                pst.executeUpdate();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public void Imprimir(){
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão deste relatorio?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            
            try {
                JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                setarPorData();
                contador();
                
                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs)); 
                HashMap filtro = new HashMap();               
                
                filtro.put("di", df.format(DaInicial.getDate()));
                filtro.put("df", df.format(DaFinal.getDate()));
                filtro.put("entrada", lblTotalRecebido.getText());
                filtro.put("Saida", lblTotalPago.getText());
                filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                filtro.put("lucro", lblTotalCaixa.getText());
                filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");
                System.out.println(df.format(DaInicial.getDate()));
                
                JasperReport jreport = JasperCompileManager.compileReport("src/reports/Caixa.jrxml");
                
                JasperPrint jprint = JasperFillManager.fillReport(jreport,filtro,conexao);
                
                JDialog tela = new JDialog(this,"LeGnu's - TelaRelatorio", true);
                
                tela.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                tela.setBackground(java.awt.SystemColor.control);
                tela.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
                tela.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                tela.setLocationRelativeTo(null);

                JRViewer painelRelatorio = new JRViewer(jprint);
                tela.getContentPane().add(painelRelatorio);
                tela.setVisible(true);  
                
            } catch (java.lang.NullPointerException e) {
                JOptionPane.showMessageDialog(null, "Adicione uma Data inicial e final.");
                

            }catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                

            }
        }
    }
    
     public void ImprimirGanho(){
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão da descrição de ganho?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            
            try {
                JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                setarPorData();
                contador();
                
                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs)); 
                HashMap filtro = new HashMap();               
                
                filtro.put("di", df.format(DaInicial.getDate()));
                filtro.put("df", df.format(DaFinal.getDate()));
                filtro.put("entrada", lblTotalRecebido.getText());
                filtro.put("Saida", lblTotalPago.getText());
                filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                filtro.put("lucro", lblTotalCaixa.getText());
                filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");
                System.out.println(df.format(DaInicial.getDate()));
                
                JasperReport jreport = JasperCompileManager.compileReport("src/reports/CaixaGanho.jrxml");
                
                JasperPrint jprint = JasperFillManager.fillReport(jreport,filtro,conexao);
                
                JDialog tela = new JDialog(this,"LeGnu's - TelaRelatorio", true);
                
                tela.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                tela.setBackground(java.awt.SystemColor.control);
                tela.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
                tela.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                tela.setLocationRelativeTo(null);

                JRViewer painelRelatorio = new JRViewer(jprint);
                tela.getContentPane().add(painelRelatorio);
                tela.setVisible(true);  
                
            } catch (java.lang.NullPointerException e) {
                JOptionPane.showMessageDialog(null, "Adicione uma Data inicial e final.");
                

            }catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                

            }
        }
    }
     
      public void ImprimirGasto(){
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão da descrição de gastos?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            
            try {
                JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                setarPorData();
                contador();
                
                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs)); 
                HashMap filtro = new HashMap();               
                
                filtro.put("di", df.format(DaInicial.getDate()));
                filtro.put("df", df.format(DaFinal.getDate()));
                filtro.put("entrada", lblTotalRecebido.getText());
                filtro.put("Saida", lblTotalPago.getText());
                filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                filtro.put("lucro", lblTotalCaixa.getText());
                filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");
                System.out.println(df.format(DaInicial.getDate()));
                
                JasperReport jreport = JasperCompileManager.compileReport("src/reports/CaixaGasto.jrxml");
                
                JasperPrint jprint = JasperFillManager.fillReport(jreport,filtro,conexao);
                
                JDialog tela = new JDialog(this,"LeGnu's - TelaRelatorio", true);
                
                tela.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                tela.setBackground(java.awt.SystemColor.control);
                tela.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
                tela.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                tela.setLocationRelativeTo(null);

                JRViewer painelRelatorio = new JRViewer(jprint);
                tela.getContentPane().add(painelRelatorio);
                tela.setVisible(true);  
                
            } catch (java.lang.NullPointerException e) {
                JOptionPane.showMessageDialog(null, "Adicione uma Data inicial e final.");
                

            }catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                

            }
        }
    }

    public void setarPorData() {

        try {

            java.util.Date aInicial = DaInicial.getDate();
            java.sql.Date bInicial = new java.sql.Date(aInicial.getTime());
            System.out.println(tbCaixaRecebido.getRowCount());

            java.util.Date aFinal = DaFinal.getDate();
            java.sql.Date bFinal = new java.sql.Date(aFinal.getTime());

            String sql = "select id as ID ,dia as Data_Emissao, hora as Hora, cliente as Cliente_Suprimento, venda as Valor , dia_Pagamento as Dia_Pagamento from tbtotalvendas where status_pagamento='Pago' and  dia between '" + bInicial + "' and '" + bFinal + "'";

            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbCaixaRecebido.setModel(DbUtils.resultSetToTableModel(rs));

            double entrada, saida, x, y;

            entrada = 0;
            saida = 0;

            String sqo = "select idgastos as ID, nome as Identificador, data_pagamento as Dia_Pagamento, valor as Valor, tipo as Tipo  from tbgastos where status_pagamento='Pago' and data_pagamento between '" + bInicial + "' and '" + bFinal + "';";

            pst = conexao.prepareStatement(sqo);
            rs = pst.executeQuery();
            tbCaixaPago.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbCaixaRecebido.getRowCount(); i++) {
                x = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaRecebido.getModel().getValueAt(i, 4).toString().replace(".", "")) / 100).replace(",", "."));

                entrada = entrada + x;
                lblTotalRecebido.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(entrada))).replace(",", "."));

            }

            for (int i = 0; i < tbCaixaPago.getRowCount(); i++) {
                y = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaPago.getModel().getValueAt(i, 3).toString().replace(".", "")) / 100).replace(",", "."));
                saida = saida + y;
                lblTotalPago.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(saida))).replace(",", "."));

            }

            if (tbCaixaRecebido.getRowCount() == 0) {
                lblTotalRecebido.setText("0.00");
            }

            if (tbCaixaPago.getRowCount() == 0) {
                lblTotalPago.setText("0.00");

            }

            double caixa = Double.parseDouble(lblTotalRecebido.getText().replace(".", "")) / 100 - Double.parseDouble(lblTotalPago.getText().replace(".", "")) / 100;
            lblTotalCaixa.setText(new DecimalFormat("#,##0.00").format(caixa).replace(",", "."));

        } catch (java.lang.NullPointerException e) {
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void Status() {

        instanciarTabela();

        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            String x;

            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                x = tbAuxilio.getModel().getValueAt(i, 1).toString();
                Date data = df.parse(x);
                System.out.println();

                if (d.before(data)) {
                    String sqr = "update tbgastos set status_pagamento='Pendente' where idgastos=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                }

            }

            instanciarTabela();

        
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaVendas() {

        try {

            String sql = "select id as ID ,dia as Data_Emissao, hora as Hora, cliente as Cliente_Suprimento, venda as Valor , dia_Pagamento as Dia_Pagamento from tbtotalvendas where status_pagamento='Pago'";
            pst = conexao.prepareStatement(sql);

            rs = pst.executeQuery();
            tbCaixaRecebido.setModel(DbUtils.resultSetToTableModel(rs));

            double preco, x;

            preco = 0;

            for (int i = 0; i < tbCaixaRecebido.getRowCount(); i++) {
                x = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaRecebido.getModel().getValueAt(i, 4).toString().replace(".", "")) / 100).replace(",", "."));

                preco = preco + x;
                lblTotalRecebido.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));

            }

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarTabelaGasto() {

        try {
            Status();

            String sql = "select idgastos as ID, nome as Identificador, data_pagamento as Dia_Pagamento, valor as Valor , tipo as Tipo from tbgastos where status_pagamento='Pago'";
            pst = conexao.prepareStatement(sql);

            rs = pst.executeQuery();
            tbCaixaPago.setModel(DbUtils.resultSetToTableModel(rs));

            double preco, x;

            preco = 0;

            for (int i = 0; i < tbCaixaPago.getRowCount(); i++) {
                x = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaPago.getModel().getValueAt(i, 3).toString().replace(".", "")) / 100).replace(",", "."));
                preco = preco + x;
                lblTotalPago.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));

            }

        } catch (java.lang.NullPointerException e) {

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

        grupo1 = new javax.swing.ButtonGroup();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        scAuxilioPago = new javax.swing.JScrollPane();
        tbAuxilioPago = new javax.swing.JTable();
        scAuxilioRecebido = new javax.swing.JScrollPane();
        tbAuxilioRecebido = new javax.swing.JTable();
        scAuxilio1 = new javax.swing.JScrollPane();
        tbAuxilio1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        DaInicial = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        DaFinal = new com.toedter.calendar.JDateChooser();
        btnPesquisar = new javax.swing.JToggleButton();
        pnRelatorio = new javax.swing.JPanel();
        lblNome1 = new javax.swing.JLabel();
        lblTotalCaixa = new javax.swing.JLabel();
        btnImprimir = new javax.swing.JToggleButton();
        pnPago = new javax.swing.JPanel();
        scCaixaPago = new javax.swing.JScrollPane();
        tbCaixaPago = new javax.swing.JTable();
        lblTotalPago = new javax.swing.JLabel();
        lblNome2 = new javax.swing.JLabel();
        btnImprimirGasto = new javax.swing.JToggleButton();
        pnRecebido = new javax.swing.JPanel();
        scCaixaRecebido = new javax.swing.JScrollPane();
        tbCaixaRecebido = new javax.swing.JTable();
        lblTotalRecebido = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        btnImprimirGanho = new javax.swing.JToggleButton();

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

        tbAuxilioPago.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilioPago.setViewportView(tbAuxilioPago);

        tbAuxilioRecebido.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilioRecebido.setViewportView(tbAuxilioRecebido);

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Caixa");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel3.setBackground(java.awt.SystemColor.control);
        jPanel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Inicial", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        DaInicial.setDateFormatString("y-MM-dd");
        DaInicial.setFocusable(false);
        DaInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                DaInicialKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaInicial, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );

        jPanel2.setBackground(java.awt.SystemColor.control);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Final", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        DaFinal.setDateFormatString("y-MM-dd");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaFinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnPesquisar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/lupa.png"))); // NOI18N
        btnPesquisar.setSelected(true);
        btnPesquisar.setContentAreaFilled(false);
        btnPesquisar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnPesquisar.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        pnRelatorio.setBackground(java.awt.SystemColor.control);
        pnRelatorio.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Total", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        lblNome1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome1.setText("Valor Lucrado(R$):");

        lblTotalCaixa.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotalCaixa.setText("0.00");

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/ImpresoraIcon.png"))); // NOI18N
        btnImprimir.setBorderPainted(false);
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnRelatorioLayout = new javax.swing.GroupLayout(pnRelatorio);
        pnRelatorio.setLayout(pnRelatorioLayout);
        pnRelatorioLayout.setHorizontalGroup(
            pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRelatorioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblNome1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalCaixa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnImprimir)
                .addContainerGap())
        );
        pnRelatorioLayout.setVerticalGroup(
            pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRelatorioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome1)
                    .addComponent(lblTotalCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
            .addGroup(pnRelatorioLayout.createSequentialGroup()
                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnPago.setBackground(java.awt.SystemColor.control);
        pnPago.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Saida", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        pnPago.setPreferredSize(new java.awt.Dimension(464, 398));

        tbCaixaPago = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbCaixaPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCaixaPago.setFocusable(false);
        tbCaixaPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCaixaPagoMouseClicked(evt);
            }
        });
        scCaixaPago.setViewportView(tbCaixaPago);

        lblTotalPago.setBackground(new java.awt.Color(51, 255, 0));
        lblTotalPago.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblTotalPago.setText("0.00");

        lblNome2.setBackground(new java.awt.Color(51, 255, 0));
        lblNome2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblNome2.setText("Valor Pago(R$):");

        btnImprimirGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/ImpresoraIcon.png"))); // NOI18N
        btnImprimirGasto.setBorderPainted(false);
        btnImprimirGasto.setContentAreaFilled(false);
        btnImprimirGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirGastoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnPagoLayout = new javax.swing.GroupLayout(pnPago);
        pnPago.setLayout(pnPagoLayout);
        pnPagoLayout.setHorizontalGroup(
            pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPagoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scCaixaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnPagoLayout.createSequentialGroup()
                        .addComponent(btnImprimirGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(lblNome2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalPago)))
                .addContainerGap())
        );
        pnPagoLayout.setVerticalGroup(
            pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scCaixaPago, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addGroup(pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPagoLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNome2)
                            .addComponent(lblTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnPagoLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(btnImprimirGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        pnRecebido.setBackground(java.awt.SystemColor.control);
        pnRecebido.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Entrada", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbCaixaRecebido = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbCaixaRecebido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCaixaRecebido.setFocusable(false);
        tbCaixaRecebido.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCaixaRecebidoMouseClicked(evt);
            }
        });
        scCaixaRecebido.setViewportView(tbCaixaRecebido);

        lblTotalRecebido.setBackground(new java.awt.Color(51, 255, 0));
        lblTotalRecebido.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblTotalRecebido.setText("0.00");

        lblNome.setBackground(new java.awt.Color(51, 255, 0));
        lblNome.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblNome.setText("Valor Recebido(R$):");

        btnImprimirGanho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/ImpresoraIcon.png"))); // NOI18N
        btnImprimirGanho.setBorderPainted(false);
        btnImprimirGanho.setContentAreaFilled(false);
        btnImprimirGanho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirGanhoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnRecebidoLayout = new javax.swing.GroupLayout(pnRecebido);
        pnRecebido.setLayout(pnRecebidoLayout);
        pnRecebidoLayout.setHorizontalGroup(
            pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRecebidoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnRecebidoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnImprimirGanho, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(lblNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalRecebido))
                    .addComponent(scCaixaRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnRecebidoLayout.setVerticalGroup(
            pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRecebidoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scCaixaRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGroup(pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnRecebidoLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNome)
                            .addComponent(lblTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRecebidoLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(btnImprimirGanho, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnRelatorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(pnRecebido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16)
                        .addComponent(pnPago, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnPago, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addComponent(pnRecebido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addComponent(pnRelatorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
    
    }//GEN-LAST:event_formWindowActivated

    private void DaInicialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DaInicialKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_DaInicialKeyReleased

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        // TODO add your handling code here:
        setarPorData();

    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void tbCaixaPagoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCaixaPagoMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_tbCaixaPagoMouseClicked

    private void tbCaixaRecebidoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCaixaRecebidoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbCaixaRecebidoMouseClicked

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        // TODO add your handling code here:
        Imprimir();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        
        instanciarTabelaCliente();
        instanciarTabelaGasto();
        instanciarTabelaVendas();
        instanciarTabela();
       
    }//GEN-LAST:event_formWindowOpened

    private void btnImprimirGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirGastoActionPerformed
        // TODO add your handling code here:
        ImprimirGasto();
    }//GEN-LAST:event_btnImprimirGastoActionPerformed

    private void btnImprimirGanhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirGanhoActionPerformed
        // TODO add your handling code here:
        ImprimirGanho();
    }//GEN-LAST:event_btnImprimirGanhoActionPerformed

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
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaCaixa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DaFinal;
    private com.toedter.calendar.JDateChooser DaInicial;
    private javax.swing.JToggleButton btnImprimir;
    private javax.swing.JToggleButton btnImprimirGanho;
    private javax.swing.JToggleButton btnImprimirGasto;
    private javax.swing.JToggleButton btnPesquisar;
    private javax.swing.ButtonGroup grupo1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblNome1;
    private javax.swing.JLabel lblNome2;
    private javax.swing.JLabel lblTotalCaixa;
    private javax.swing.JLabel lblTotalPago;
    private javax.swing.JLabel lblTotalRecebido;
    private javax.swing.JPanel pnPago;
    private javax.swing.JPanel pnRecebido;
    private javax.swing.JPanel pnRelatorio;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilio1;
    private javax.swing.JScrollPane scAuxilioPago;
    private javax.swing.JScrollPane scAuxilioRecebido;
    private javax.swing.JScrollPane scCaixaPago;
    private javax.swing.JScrollPane scCaixaRecebido;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilio1;
    private javax.swing.JTable tbAuxilioPago;
    private javax.swing.JTable tbAuxilioRecebido;
    private javax.swing.JTable tbCaixaPago;
    private javax.swing.JTable tbCaixaRecebido;
    // End of variables declaration//GEN-END:variables
}
