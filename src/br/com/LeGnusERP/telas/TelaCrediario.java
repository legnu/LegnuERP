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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ad3ln0r
 */
public class TelaCrediario extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String tipo = "Pagar";

    /**
     * Creates new form TelaCrediario
     */
    public TelaCrediario() {
        initComponents();
        conexao = ModuloConexao.conector();
        setIcon();        
    }
    
    private void setIcon() {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
    }


    public void aPagar() {
        tipo = "Pagar";
        instanciarTabelaContas();
    }

    public void aReceber() {
        tipo = "Receber";
        instanciarTabelaContas();
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

    
    public void receber_pagar() {
        int confirma;
        String opcao = "Vazio";
        String data;
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dSql = new java.sql.Date(d.getTime());
        df.format(dSql);

        try {
            if (tipo.equals("Pagar") == true) {
                opcao = JOptionPane.showInputDialog("Pagar Conta >>> Digite 'P'.\n"
                        + "Alterar Data >>> Digite 'A'.\n"
                        + "Deletar Conta >>> Digite 'D'.\n\n"
                        + "OBS:Caracteres aceitados são somente P,A,D.");

            } else if (tipo.equals("Pagar") == false) {
                opcao = JOptionPane.showInputDialog("Receber Conta >>> Digite 'R'.\n"
                        + "Alterar Data >>> Digite 'A'.\n"
                        + "Deletar Conta >>> Digite 'D'.\n\n"
                        + "OBS:Caracteres aceitados são somente R,A,D.");
            }

            if (opcao.equals("P") == true & tipo.equals("Pagar") == true) {
                confirma = JOptionPane.showConfirmDialog(null, "Deseja Pagar esta Conta.", "Atençao", JOptionPane.YES_OPTION);
            } else if (opcao.equals("R") == true & tipo.equals("Pagar") == false) {
                confirma = JOptionPane.showConfirmDialog(null, "Deseja Receber esta Conta.", "Atençao", JOptionPane.YES_OPTION);
            } else if (opcao.equals("A") == true) {
                confirma = JOptionPane.showConfirmDialog(null, "Deseja Alterar a data desta Conta.", "Atençao", JOptionPane.YES_OPTION);
            } else if (opcao.equals("D") == true) {
                confirma = JOptionPane.showConfirmDialog(null, "Deseja Deletar esta Conta.", "Atençao", JOptionPane.YES_OPTION);
            } else {
                JOptionPane.showMessageDialog(null, "Alternativa invalida, as Disponiveis são P,R,A,D.");
                confirma = 1;
            }

            if (confirma == JOptionPane.YES_OPTION) {
                try {
                    int setar = tbContas.getSelectedRow();
                    String id = tbContas.getModel().getValueAt(setar, 0).toString();

                    if (opcao.equals("P") & tipo.equals("Pagar") == true) {
                        String sqo = "update tbgastos set status_pagamento='Pago', data_pagamento=? where idgastos=?";

                        pst = conexao.prepareStatement(sqo);
                        pst.setDate(1, dSql);
                        pst.setString(2, id);
                        pst.executeUpdate();
                        instanciarTabelaContas();

                    } else if (opcao.equals("R") & tipo.equals("Pagar") == false) {
                        String sqo = "update tbtotalvendas set status_pagamento='Pago', dia_Pagamento=? where id=?";

                        pst = conexao.prepareStatement(sqo);
                        pst.setDate(1, dSql);
                        pst.setString(2, id);
                        pst.executeUpdate();
                        instanciarTabelaContas();

                    } else if (opcao.equals("A") & tipo.equals("Pagar") == true) {
                        try {
                            data = JOptionPane.showInputDialog("Insira uma nova data no formato yyyy-MM-dd.");

                            Date alterada = df.parse(data);
                            java.sql.Date dSqt = new java.sql.Date(alterada.getTime());
                            df.format(dSqt);

                            String sqo = "update tbgastos set data_pagamento=? where idgastos=?";

                            pst = conexao.prepareStatement(sqo);
                            pst.setDate(1, dSqt);
                            pst.setString(2, id);
                            pst.executeUpdate();
                            instanciarTabelaContas();
                        } catch (java.text.ParseException e) {
                            JOptionPane.showMessageDialog(null, "Data deve ser salva no formato yyyy-MM-dd.");

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Data deve ser salva no formato yyyy-MM-dd.");

                        }

                    } else if (opcao.equals("A") & tipo.equals("Pagar") == false) {
                        try {
                            data = JOptionPane.showInputDialog("Insira uma nova data no formato yyyy-MM-dd.");

                            Date alterada = df.parse(data);
                            java.sql.Date dSqt = new java.sql.Date(alterada.getTime());
                            df.format(dSqt);

                            String sqo = "update tbtotalvendas set dia_Pagamento=? where id=?";

                            pst = conexao.prepareStatement(sqo);
                            pst.setDate(1, dSqt);
                            pst.setString(2, id);
                            pst.executeUpdate();
                            instanciarTabelaContas();
                        } catch (java.text.ParseException e) {
                            JOptionPane.showMessageDialog(null, "Data deve ser salva no formato yyyy-MM-dd.");

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Data deve ser salva no formato yyyy-MM-dd.");

                        }

                    } else if (opcao.equals("D") & tipo.equals("Pagar") == true) {
                        String sqo = "delete from tbgastos where idgastos=?";
                        pst = conexao.prepareStatement(sqo);
                        pst.setString(1, id);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Clique no Ok e Aguarde.");
                      
                        JOptionPane.showMessageDialog(null, "Concluido.");
                        instanciarTabelaContas();

                    } else if (opcao.equals("D") & tipo.equals("Pagar") == false) {
                        String sqo = "delete from tbtotalvendas where id=?";
                        pst = conexao.prepareStatement(sqo);
                        pst.setString(1, id);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Clique no Ok e Aguarde.");
                        
                        JOptionPane.showMessageDialog(null, "Concluido.");
                        instanciarTabelaContas();

                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);

                }
            }
        } catch (java.lang.NullPointerException e) {            

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }


    

    public void instanciarTabelaContas() {
        try {
            if (tipo.equals("Pagar") == true) {
                String sqr = "select idgastos as ID, nome as Identificador, data_pagamento as Dia_Pagamento, valor as Valor  from tbgastos where status_pagamento='Pendente'";
                pst = conexao.prepareStatement(sqr);
                rs = pst.executeQuery();
                tbContas.setModel(DbUtils.resultSetToTableModel(rs));
                pnTabela.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()), "Contas a Pagar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } else {
                String sqr = "select id as ID ,dia as Data_Emissao, hora as Hora, cliente as Cliente, venda as Valor , dia_Pagamento as Dia_Pagamento from tbtotalvendas where status_pagamento='Pendente'";
                pst = conexao.prepareStatement(sqr);
                rs = pst.executeQuery();
                tbContas.setModel(DbUtils.resultSetToTableModel(rs));
                pnTabela.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()), "Contas a Receber", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            }

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
        scAuxilio1 = new javax.swing.JScrollPane();
        tbAuxilio1 = new javax.swing.JTable();
        pnPrincipal = new javax.swing.JPanel();
        pnTabela = new javax.swing.JPanel();
        scContas = new javax.swing.JScrollPane();
        tbContas = new javax.swing.JTable();
        rbPagar = new javax.swing.JRadioButton();
        rbReceber = new javax.swing.JRadioButton();

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
        setTitle("LeGnu`s_EPR- Tela Contas");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnPrincipal.setBackground(java.awt.SystemColor.control);
        pnPrincipal.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        pnTabela.setBackground(java.awt.SystemColor.control);
        pnTabela.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Contas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

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

        rbPagar.setBackground(java.awt.SystemColor.control);
        grupo1.add(rbPagar);
        rbPagar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbPagar.setText("A Pagar");
        rbPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPagarActionPerformed(evt);
            }
        });

        rbReceber.setBackground(java.awt.SystemColor.control);
        grupo1.add(rbReceber);
        rbReceber.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbReceber.setText("A Receber");
        rbReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbReceberActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTabelaLayout = new javax.swing.GroupLayout(pnTabela);
        pnTabela.setLayout(pnTabelaLayout);
        pnTabelaLayout.setHorizontalGroup(
            pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTabelaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(rbPagar)
                .addGap(16, 16, 16)
                .addComponent(rbReceber)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnTabelaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scContas, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnTabelaLayout.setVerticalGroup(
            pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTabelaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbPagar)
                    .addComponent(rbReceber))
                .addGap(16, 16, 16)
                .addComponent(scContas, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout pnPrincipalLayout = new javax.swing.GroupLayout(pnPrincipal);
        pnPrincipal.setLayout(pnPrincipalLayout);
        pnPrincipalLayout.setHorizontalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnPrincipalLayout.setVerticalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void rbPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPagarActionPerformed
        // TODO add your handling code here:
        aPagar();
    }//GEN-LAST:event_rbPagarActionPerformed

    private void rbReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbReceberActionPerformed
        // TODO add your handling code here:
        aReceber();
    }//GEN-LAST:event_rbReceberActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        
    }//GEN-LAST:event_formWindowActivated

    private void tbContasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbContasMouseClicked
        // TODO add your handling code here:
        receber_pagar();
    }//GEN-LAST:event_tbContasMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        instanciarTabelaCliente();
    }//GEN-LAST:event_formWindowOpened

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
            java.util.logging.Logger.getLogger(TelaCrediario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCrediario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCrediario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCrediario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaCrediario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup grupo1;
    private javax.swing.JPanel pnPrincipal;
    private javax.swing.JPanel pnTabela;
    private javax.swing.JRadioButton rbPagar;
    private javax.swing.JRadioButton rbReceber;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilio1;
    private javax.swing.JScrollPane scContas;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilio1;
    private javax.swing.JTable tbContas;
    // End of variables declaration//GEN-END:variables
}
