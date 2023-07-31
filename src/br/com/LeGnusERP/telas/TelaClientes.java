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
import javax.swing.table.DefaultTableModel;
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
public class TelaClientes extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String tipo = "Ativo";
    String tempoInativo;
    String ultimaCompra;

    /**
     * Creates new form TelaClientes
     */
    public TelaClientes() {
        initComponents();
        conexao = ModuloConexao.conector();
        setIcon();
        tempoInativo();
    }

    private void tempoInativo() {
        try {
            pst = conexao.prepareStatement("select tempoInativo from tbrelatorio where idRelatorio=1");
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            if ((Float.parseFloat(tbAuxilio.getModel().getValueAt(0, 0).toString()) > 0) == true) {
                tempoInativo = tbAuxilio.getModel().getValueAt(0, 0).toString();
            } else {
                tempoInativo = "0";
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Conclua a configuração da tela de configurações");
        }
    }

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
    }

    public void instanciarTabelaClientesAtivos() {
        String sql = "select idcli as ID,nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and atividade='Ativo' order by quantidade_comprada desc";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
            tipo = "Ativo";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void Pesquisar() {
        try {
            if (tipo.equals("Ativo") == true) {
                String sql = "select idcli as ID,nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and nomecli like ? and atividade='Ativo' order by quantidade_comprada desc ";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisar.getText() + "%");
                System.out.println(sql);
                rs = pst.executeQuery();
                tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
                System.out.println(tipo);
            } else if (tipo.equals("Inativo") == true) {
                String sql = "select idcli as ID,nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and nomecli like ? and atividade='Inativo'  order by quantidade_comprada desc ";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisar.getText() + "%");
                rs = pst.executeQuery();
                tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
                System.out.println(tipo);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void instanciarTabelaClientesInativos() {
        String sql = "select idcli as ID, nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and atividade='Inativo' order by quantidade_comprada desc";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
            tipo = "Inativo";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void instanciarTabelaAuxilioCliente() {
        String sql = "select idcli, valor_gasto, quantidade_comprada, ultima_compra from tbclientes where quantidade_comprada > 0";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void ValidarCliente() {
        try {
            

            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                ultimaCompra = tbAuxilio.getModel().getValueAt(i, 3).toString();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date d = df.parse(ultimaCompra);
                
                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);
                
                String dia = new SimpleDateFormat("dd").format(d);
                String mes = new SimpleDateFormat("MM").format(d);
                String ano = new SimpleDateFormat("yyyy").format(d);
                dia = String.valueOf(Integer.parseInt(dia) + Integer.parseInt(tempoInativo));

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

                df.format(dSql);

                java.sql.Date dSqt = new java.sql.Date(data.getTime());
                df.format(dSqt);

                String sqr = "update tbclientes set prazo_inativo=? where idcli=?";
                pst = conexao.prepareStatement(sqr);
                pst.setDate(1, dSqt);
                pst.setString(2, tbAuxilio.getModel().getValueAt(i, 0).toString());
                pst.executeUpdate();

                if (dSql.after(dSqt) == true) {
                    String sqy = "update tbclientes set atividade='Inativo' where idcli=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                } else {
                    String sqy = "update tbclientes set atividade='Ativo' where idcli=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void CalcularTicket() {

        try {

            double preco, x;

            preco = 0;
            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                x = Double.parseDouble(tbAuxilio.getModel().getValueAt(i, 1).toString().replace(".", "")) / 100;
                preco = x / Double.parseDouble(tbAuxilio.getModel().getValueAt(i, 2).toString());

                String sqr = "update tbclientes set ticket_medio=? where idcli=?";
                pst = conexao.prepareStatement(sqr);
                pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));
                pst.setString(2, tbAuxilio.getModel().getValueAt(i, 0).toString());
                pst.executeUpdate();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void imprimir() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão deste relatorio?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {

                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                //tbAuxilio1.getModel().getValueAt(0, 0).toString()
                HashMap filtro = new HashMap();

                filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");

                JasperReport jreport = JasperCompileManager.compileReport("src/reports/RelClientes.jrxml");

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

    public void imprimirGastoCliente() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão dos gastos deste cliente?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                int setar = tbClientesAtivos.getSelectedRow();
                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                //tbAuxilio1.getModel().getValueAt(0, 0).toString()
                HashMap filtro = new HashMap();

                filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                filtro.put("Cliente", tbClientesAtivos.getModel().getValueAt(setar, 1).toString());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");

                JasperReport jreport = JasperCompileManager.compileReport("src/reports/RelClienteUnico.jrxml");

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

    public void imprimirGastoSubCliente() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão dos gastos deste Sub Cliente?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                int setar = tbSubCliente.getSelectedRow();
                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                //tbAuxilio1.getModel().getValueAt(0, 0).toString()
                HashMap filtro = new HashMap();

                filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                filtro.put("Cliente", tbSubCliente.getModel().getValueAt(setar, 0).toString());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");

                JasperReport jreport = JasperCompileManager.compileReport("src/reports/RelSubCliente.jrxml");

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

    private void instanciarSubClientes() {
        try {
            int setar = tbClientesAtivos.getSelectedRow();
            String sql = "Select nome as Nome, tipo as Tipo from tbsubclientes where referencia = " + tbClientesAtivos.getModel().getValueAt(setar, 0).toString();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbSubCliente.setModel(DbUtils.resultSetToTableModel(rs));
            pnClientes1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "SubClientes Vinculados do(a): " + tbClientesAtivos.getModel().getValueAt(setar, 1).toString(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 12))); // NOI18N

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    private void limpar() {
        pnClientes1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "SubClientes Vinculados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 12))); // NOI18N
        ((DefaultTableModel) tbSubCliente.getModel()).setRowCount(0);
    }

    public void Iniciar() {
        instanciarTabelaAuxilioCliente();
        ValidarCliente();
        CalcularTicket();
        instanciarTabelaClientesAtivos();
        rbAtivo.setSelected(true);
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
        Grupo1 = new javax.swing.ButtonGroup();
        pnPrincipal = new javax.swing.JPanel();
        pnClientes = new javax.swing.JPanel();
        scClientes = new javax.swing.JScrollPane();
        tbClientesAtivos = new javax.swing.JTable();
        rbAtivo = new javax.swing.JRadioButton();
        rbInativo = new javax.swing.JRadioButton();
        lblPesquisar = new javax.swing.JLabel();
        txtPesquisar = new javax.swing.JTextField();
        btnImprimir = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        pnClientes1 = new javax.swing.JPanel();
        scSubClientes = new javax.swing.JScrollPane();
        tbSubCliente = new javax.swing.JTable();

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
        setTitle("LeGnu`s_EPR- Tela Clientes");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        pnPrincipal.setBackground(java.awt.SystemColor.control);
        pnPrincipal.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        pnClientes.setBackground(java.awt.SystemColor.control);
        pnClientes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbClientesAtivos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbClientesAtivos.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbClientesAtivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbClientesAtivos.setFocusable(false);
        tbClientesAtivos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbClientesAtivosMouseClicked(evt);
            }
        });
        scClientes.setViewportView(tbClientesAtivos);

        rbAtivo.setBackground(java.awt.SystemColor.control);
        Grupo1.add(rbAtivo);
        rbAtivo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbAtivo.setText("Ativos");
        rbAtivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAtivoActionPerformed(evt);
            }
        });

        rbInativo.setBackground(java.awt.SystemColor.control);
        Grupo1.add(rbInativo);
        rbInativo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbInativo.setText("Inativos");
        rbInativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbInativoActionPerformed(evt);
            }
        });

        lblPesquisar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisar.setText("Pesquisar:");

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyReleased(evt);
            }
        });

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/ImpresoraIcon.png"))); // NOI18N
        btnImprimir.setBorderPainted(false);
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnClientesLayout = new javax.swing.GroupLayout(pnClientes);
        pnClientes.setLayout(pnClientesLayout);
        pnClientesLayout.setHorizontalGroup(
            pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnClientesLayout.createSequentialGroup()
                                .addComponent(rbAtivo)
                                .addGap(16, 16, 16)
                                .addComponent(rbInativo)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnClientesLayout.createSequentialGroup()
                                .addComponent(lblPesquisar)
                                .addGap(16, 16, 16)
                                .addComponent(txtPesquisar)))
                        .addGap(16, 16, 16)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        pnClientesLayout.setVerticalGroup(
            pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPesquisar)
                            .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbAtivo)
                            .addComponent(rbInativo)))
                    .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setText("OBS: Cliente só aparecera se tiver feito ao menos uma compra.");

        pnClientes1.setBackground(java.awt.SystemColor.control);
        pnClientes1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "SubClientes Vinculados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 12))); // NOI18N

        tbSubCliente = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbSubCliente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbSubCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbSubCliente.setFocusable(false);
        tbSubCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbSubClienteMouseClicked(evt);
            }
        });
        scSubClientes.setViewportView(tbSubCliente);

        javax.swing.GroupLayout pnClientes1Layout = new javax.swing.GroupLayout(pnClientes1);
        pnClientes1.setLayout(pnClientes1Layout);
        pnClientes1Layout.setHorizontalGroup(
            pnClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClientes1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scSubClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnClientes1Layout.setVerticalGroup(
            pnClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClientes1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scSubClientes)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout pnPrincipalLayout = new javax.swing.GroupLayout(pnPrincipal);
        pnPrincipal.setLayout(pnPrincipalLayout);
        pnPrincipalLayout.setHorizontalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addComponent(pnClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnPrincipalLayout.setVerticalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        Iniciar();
    }//GEN-LAST:event_formWindowActivated

    private void rbAtivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAtivoActionPerformed
        // TODO add your handling code here:
        limpar();
        instanciarTabelaClientesAtivos();
    }//GEN-LAST:event_rbAtivoActionPerformed

    private void rbInativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbInativoActionPerformed
        // TODO add your handling code here:
        limpar();
        instanciarTabelaClientesInativos();
    }//GEN-LAST:event_rbInativoActionPerformed

    private void txtPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyReleased
        // TODO add your handling code here:
        Pesquisar();
    }//GEN-LAST:event_txtPesquisarKeyReleased

    private void txtPesquisarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisarKeyPressed

    private void tbClientesAtivosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesAtivosMouseClicked
        // TODO add your handling code here:
        instanciarSubClientes();
        imprimirGastoCliente();
    }//GEN-LAST:event_tbClientesAtivosMouseClicked

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        // TODO add your handling code here:
        imprimir();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void tbSubClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbSubClienteMouseClicked
        // TODO add your handling code here:
        imprimirGastoSubCliente();
    }//GEN-LAST:event_tbSubClienteMouseClicked

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
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaClientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup Grupo1;
    private javax.swing.JToggleButton btnImprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblPesquisar;
    private javax.swing.JPanel pnClientes;
    private javax.swing.JPanel pnClientes1;
    private javax.swing.JPanel pnPrincipal;
    private javax.swing.JRadioButton rbAtivo;
    private javax.swing.JRadioButton rbInativo;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scClientes;
    private javax.swing.JScrollPane scSubClientes;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbClientesAtivos;
    private javax.swing.JTable tbSubCliente;
    private javax.swing.JTextField txtPesquisar;
    // End of variables declaration//GEN-END:variables
}
