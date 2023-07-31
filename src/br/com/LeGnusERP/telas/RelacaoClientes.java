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
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ad3ln0r
 */
public class RelacaoClientes extends javax.swing.JFrame {

    /**
     * Creates new form RelacaoClientes
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String Cliente;

    public RelacaoClientes() {
        initComponents();
        conexao = ModuloConexao.conector();
      setIcon();        
    }
    
    private void setIcon() {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
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

    public void habilitarCrediario() {
        try {
            String sqt = "update tbclientes set crediario='Habilitado' where idcli=?";
            pst = conexao.prepareStatement(sqt);
            pst.setString(1, txtId.getText());
            pst.executeUpdate();
            instanciarTabelaClientes();
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }
    
     public void receber() {
        int confirma;
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dSql = new java.sql.Date(d.getTime());
        df.format(dSql);

       
        confirma = JOptionPane.showConfirmDialog(null, "Deseja Receber esta Conta.", "Atençao", JOptionPane.YES_OPTION);
        

        if (confirma == JOptionPane.YES_OPTION) {
            try {
                int setar = tbContas.getSelectedRow();
                String id = tbContas.getModel().getValueAt(setar, 0).toString();

                
                    String sqo = "update tbtotalvendas set status_pagamento='Pago', dia_Pagamento=? where id=?";

                    pst = conexao.prepareStatement(sqo);
                    pst.setDate(1, dSql);
                    pst.setString(2, id);
                    pst.executeUpdate();
                    instanciarTabelaContas();

                

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);

            }
        }
    }
    
    public void Pesquisar() {
        try {
            String sqt = "select idcli as ID, nomecli as Cliente, crediario as Crediario from tbclientes where nomecli like ?";
            pst = conexao.prepareStatement(sqt);
            pst.setString(1, txtPesquisar.getText() + "%");
            rs = pst.executeQuery();       
            tbClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void desabilitarCrediario() {
        try {
            String sqt = "update tbclientes set crediario='Desabilitado' where idcli=?";
            pst = conexao.prepareStatement(sqt);
            pst.setString(1, txtId.getText());
            pst.executeUpdate();
            instanciarTabelaClientes();
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void instanciarTabelaClientes() {
        try {
            String sql = "select idcli as ID, nomecli as Cliente, crediario as Crediario from tbclientes";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbClientes.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabelaContas() {
        try {
            String sql = "select id as ID, cliente as Cliente ,venda as Valor, dia_Pagamento as Data_Pagamento from tbtotalvendas where cliente='" + txtClientes.getText() + "' and status_pagamento='Pendente'";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbContas.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void setarCamposCliente() {
        int setar = tbClientes.getSelectedRow();

        try {
            txtId.setText(tbClientes.getModel().getValueAt(setar, 0).toString());
            txtClientes.setText(tbClientes.getModel().getValueAt(setar, 1).toString());
            txtCrediario.setText(tbClientes.getModel().getValueAt(setar, 2).toString());
            pnContas.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()), "Contas a receber de: " + txtClientes.getText(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
            instanciarTabelaContas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void limpar() {
        txtClientes.setText("#");
        txtCrediario.setText("#");
        txtId.setText(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtId = new javax.swing.JTextField();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        scAuxilio1 = new javax.swing.JScrollPane();
        tbAuxilio1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        pnContas = new javax.swing.JPanel();
        scContas = new javax.swing.JScrollPane();
        tbContas = new javax.swing.JTable();
        pnClientes = new javax.swing.JPanel();
        scClientes = new javax.swing.JScrollPane();
        tbClientes = new javax.swing.JTable();
        lblClientes = new javax.swing.JLabel();
        txtClientes = new javax.swing.JLabel();
        btnHabilitar = new javax.swing.JToggleButton();
        btnDesabilitar = new javax.swing.JToggleButton();
        lblCrediario = new javax.swing.JLabel();
        txtCrediario = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtPesquisar = new javax.swing.JTextField();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Gerenciador de Clientes");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        pnContas.setBackground(java.awt.SystemColor.control);
        pnContas.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Contas a Receber", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbContas = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbContas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbContas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbContas.setFocusable(false);
        tbContas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbContasMouseClicked(evt);
            }
        });
        scContas.setViewportView(tbContas);

        javax.swing.GroupLayout pnContasLayout = new javax.swing.GroupLayout(pnContas);
        pnContas.setLayout(pnContasLayout);
        pnContasLayout.setHorizontalGroup(
            pnContasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContasLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scContas, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnContasLayout.setVerticalGroup(
            pnContasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContasLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scContas, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        pnClientes.setBackground(java.awt.SystemColor.control);
        pnClientes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbClientes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbClientes.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbClientes.setFocusable(false);
        tbClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbClientesMouseClicked(evt);
            }
        });
        scClientes.setViewportView(tbClientes);

        lblClientes.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblClientes.setText("Cliente Selecionado:");

        txtClientes.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtClientes.setText("#");

        btnHabilitar.setBackground(new java.awt.Color(0, 204, 51));
        btnHabilitar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnHabilitar.setForeground(new java.awt.Color(255, 255, 255));
        btnHabilitar.setText("Habilitar Crediario");
        btnHabilitar.setBorderPainted(false);
        btnHabilitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHabilitarActionPerformed(evt);
            }
        });

        btnDesabilitar.setBackground(new java.awt.Color(204, 0, 0));
        btnDesabilitar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnDesabilitar.setForeground(new java.awt.Color(255, 255, 255));
        btnDesabilitar.setText("Desabilitar Crediario");
        btnDesabilitar.setBorderPainted(false);
        btnDesabilitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesabilitarActionPerformed(evt);
            }
        });

        lblCrediario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCrediario.setText("Crediario:");

        txtCrediario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtCrediario.setText("#");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setText("Pesquisar:");

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnClientesLayout = new javax.swing.GroupLayout(pnClientes);
        pnClientes.setLayout(pnClientesLayout);
        pnClientesLayout.setHorizontalGroup(
            pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addComponent(btnHabilitar)
                        .addGap(16, 16, 16)
                        .addComponent(btnDesabilitar)
                        .addGap(0, 105, Short.MAX_VALUE))
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPesquisar))
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addComponent(lblClientes)
                        .addGap(6, 6, 6)
                        .addComponent(txtClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addComponent(lblCrediario)
                        .addGap(6, 6, 6)
                        .addComponent(txtCrediario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        pnClientesLayout.setVerticalGroup(
            pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClientes)
                    .addComponent(txtClientes))
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCrediario)
                    .addComponent(txtCrediario))
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHabilitar)
                    .addComponent(btnDesabilitar))
                .addGap(16, 16, 16)
                .addComponent(scClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(pnContas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnContas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void tbClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesMouseClicked
        // TODO add your handling code here:
        setarCamposCliente();
    }//GEN-LAST:event_tbClientesMouseClicked

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        instanciarTabelaCliente();
        instanciarTabelaClientes();
    }//GEN-LAST:event_formWindowActivated

    private void btnHabilitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHabilitarActionPerformed
        // TODO add your handling code here:
        habilitarCrediario();
    }//GEN-LAST:event_btnHabilitarActionPerformed

    private void btnDesabilitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesabilitarActionPerformed
        // TODO add your handling code here:
        desabilitarCrediario();
    }//GEN-LAST:event_btnDesabilitarActionPerformed

    private void txtPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyReleased
        // TODO add your handling code here:
        Pesquisar();
    }//GEN-LAST:event_txtPesquisarKeyReleased

    private void tbContasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbContasMouseClicked
        // TODO add your handling code here:
        receber();
    }//GEN-LAST:event_tbContasMouseClicked

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
            java.util.logging.Logger.getLogger(RelacaoClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RelacaoClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RelacaoClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RelacaoClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RelacaoClientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnDesabilitar;
    private javax.swing.JToggleButton btnHabilitar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblClientes;
    private javax.swing.JLabel lblCrediario;
    private javax.swing.JPanel pnClientes;
    private javax.swing.JPanel pnContas;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilio1;
    private javax.swing.JScrollPane scClientes;
    private javax.swing.JScrollPane scContas;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilio1;
    private javax.swing.JTable tbClientes;
    private javax.swing.JTable tbContas;
    private javax.swing.JLabel txtClientes;
    private javax.swing.JLabel txtCrediario;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtPesquisar;
    // End of variables declaration//GEN-END:variables
}
