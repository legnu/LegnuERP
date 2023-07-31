/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.LeGnusERP.telas;

import java.sql.*;
import br.com.LeGnusERP.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Leandro Clemente
 */
public class TelaLogin extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    String Senha;

    /**
     * Creates new form TelaLogin
     */
    public TelaLogin() {
        try {
            initComponents();
            conexao = ModuloConexao.conector();
            //A linha abaixo serve de apoio ao Status da conexão
            //System.out.println(conexao);
            if (conexao != null) {
                lblStatus.setText("Conectado ao Banco de Dados.");
                lblStatus.setForeground(Color.BLUE);
            } else {
                lblStatus.setText("Não Conectado ao MySQL.");
                lblStatus.setForeground(Color.RED);
            }
            lblSenha.setVisible(false);
            lblUsuario.setVisible(false);
            txtUsuario.setVisible(false);
            txtSenha.setVisible(false);
            btnLogin.setVisible(false);

            lblSenha.setEnabled(false);
            lblUsuario.setEnabled(false);
            txtUsuario.setEnabled(false);
            txtSenha.setEnabled(false);
            btnLogin.setEnabled(false);
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void logar() {
        try {
            String sql = "select * from tbusuarios where login = ? and senha = ?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtUsuario.getText());
            String captura = new String(txtSenha.getPassword());
            pst.setString(2, captura);
            rs = pst.executeQuery();

            if (rs.next()) {
                String perfil = rs.getString(7);
                String nome = rs.getString(2);

                //System.out.println(perfil);
                //Linha acima competi a teste e a estrutura abaixo trata do perfil do usuario
                if (perfil.equals("Administrador") == true) {
                    TelaPrincipal principal = new TelaPrincipal();
                    principal.setVisible(true);

                    TelaPrincipal.MenCadUsu.setEnabled(true);
                    TelaPrincipal.lblUsuario.setText(nome);
                    TelaPrincipal.lblUsuario.setForeground(Color.red);
                    this.dispose();
                    conexao.close();
                } else if (perfil.equals("Usuario") == true) {

                    TelaLimitada limitada = new TelaLimitada();
                    limitada.setVisible(true);
                    TelaLimitada.btnPDV.setEnabled(false);
                    TelaLimitada.btnCadOS.setEnabled(false);
                    TelaLimitada.btnCadServiço.setEnabled(false);
                    TelaLimitada.lblNome.setText(nome);
                    this.dispose();
                    conexao.close();

                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuário e/ou Senha inválido(s)");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void CriarSenha() {
        try {

            java.util.Date d = new java.util.Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-07");
            Senha = df.format(d).replace("-", "").replace("1", "G").replace("2", "Q").replace("3", "Z").replace("4", "W").replace("5", "S").replace("6", "N").replace("7", "B").replace("8", "L").replace("9", "T").replace("0", "H");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void primeiraConta() {
        try {
            String sqo = "select senha from tbVale where idVale=1";
            pst = conexao.prepareStatement(sqo);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

            if (tbAuxilio.getModel().getValueAt(0, 0) == null == true) {
                CriarSenha();

                java.util.Date data = new java.util.Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                String mes = new SimpleDateFormat("MM").format(data);
                String ano = new SimpleDateFormat("yyyy").format(data);

                int limite = Integer.parseInt(mes) + 1;

                if (limite > 12) {
                    limite = limite - 12;
                    ano = String.valueOf(Integer.parseInt(ano) + 1);
                }

                String dataLimite = ano + "-" + limite + "-07";

                java.util.Date d = df.parse(dataLimite);
                java.sql.Date dSqt = new java.sql.Date(d.getTime());
                df.format(dSqt);

                String sql = "update tbVale set senha=?, vencimento=? where idVale=1";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, Senha);
                pst.setDate(2, dSqt);
                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void AtualizarDados() {
        try {
            java.util.Date data = new java.util.Date();
            String sqo = "select vencimento from tbVale where idVale=1";
            pst = conexao.prepareStatement(sqo);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            if (data.after(df.parse(tbAuxilio.getModel().getValueAt(0, 0).toString())) == true) {

                CriarSenha();

                String mes = new SimpleDateFormat("MM").format(data);
                String ano = new SimpleDateFormat("yyyy").format(data);

                int limite = Integer.parseInt(mes) + 1;

                if (limite > 12) {
                    limite = limite - 12;
                    ano = String.valueOf(Integer.parseInt(ano) + 1);
                }

                String dataLimite = ano + "-" + limite + "-07";

                java.util.Date d = df.parse(dataLimite);
                java.sql.Date dSqt = new java.sql.Date(d.getTime());
                df.format(dSqt);

                String sql = "update tbVale set senha=?, vencimento=? where idVale=1";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, Senha);
                pst.setDate(2, dSqt);

                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void VerificarSenha() {
        try {
            java.util.Date data = new java.util.Date();
            DateFormat Dia = new SimpleDateFormat("dd");
            AtualizarDados();

            String sqy = "select senha from tbVale where idVale=1";
            pst = conexao.prepareStatement(sqy);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            //tbauxilio.getModel().getValueAt(i, 2).toString()

            if (txtVale.getText().equals(tbAuxilio.getModel().getValueAt(0, 0).toString()) == true) {

                if (Dia.format(data).equals("04") == true) {
                    JOptionPane.showMessageDialog(null, "Antepenúltimo dia para vencimento da Senha Mensal.");
                }
                if (Dia.format(data).equals("05") == true) {
                    JOptionPane.showMessageDialog(null, "Penúltimo dia para vencimento da Senha Mensal.");
                }
                if (Dia.format(data).equals("06") == true) {
                    JOptionPane.showMessageDialog(null, "Último dia para vencimento da Senha Mensal.");
                }

                lblSenha.setVisible(true);
                lblUsuario.setVisible(true);
                txtUsuario.setVisible(true);
                txtSenha.setVisible(true);
                btnLogin.setVisible(true);

                lblSenha.setEnabled(true);
                lblUsuario.setEnabled(true);
                txtUsuario.setEnabled(true);
                txtSenha.setEnabled(true);
                btnLogin.setEnabled(true);

                lblVale.setVisible(false);
                txtVale.setVisible(false);
                btnEntrar.setVisible(false);

                btnEntrar.setEnabled(false);
                txtVale.setEnabled(false);

            } else {
                JOptionPane.showMessageDialog(null, "Senha Invalida entre em contato com 31 98235-2599.");
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

        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        lblStatus = new javax.swing.JLabel();
        btnLogin = new br.com.LeGnusERP.Swing.botaoArredondado();
        txtUsuario = new javax.swing.JTextField();
        lblUsuario = new javax.swing.JLabel();
        txtSenha = new javax.swing.JPasswordField();
        lblSenha = new javax.swing.JLabel();
        txtVale = new javax.swing.JTextField();
        lblVale = new javax.swing.JLabel();
        btnEntrar = new javax.swing.JToggleButton();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Tela de Login");
        setBackground(new java.awt.Color(204, 204, 204));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        jToggleButton1.setBackground(java.awt.SystemColor.control);
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/LogoLegnuPadrão_550x350.png"))); // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblStatus.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblStatus.setText("Status");

        btnLogin.setForeground(new java.awt.Color(0, 0, 204));
        btnLogin.setText("Logar");
        btnLogin.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        lblUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUsuario.setText("Usuario:");

        lblSenha.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblSenha.setText("Senha:");

        txtVale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValeActionPerformed(evt);
            }
        });

        lblVale.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblVale.setText("Senha do Mes:");

        btnEntrar.setBackground(new java.awt.Color(0, 0, 204));
        btnEntrar.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnEntrar.setForeground(new java.awt.Color(255, 255, 255));
        btnEntrar.setText("Ativar");
        btnEntrar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnEntrar.setBorderPainted(false);
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addComponent(lblVale)
                .addGap(14, 14, 14)
                .addComponent(txtVale, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(176, 176, 176)
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(264, 264, 264)
                        .addComponent(lblSenha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(254, 254, 254)
                        .addComponent(lblUsuario)
                        .addGap(6, 6, 6)
                        .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblVale))
                    .addComponent(btnEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUsuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSenha))
                .addGap(16, 16, 16)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrarActionPerformed
        // TODO add your handling code here:
        VerificarSenha();
    }//GEN-LAST:event_btnEntrarActionPerformed

    private void txtValeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValeActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        if (conexao != null) {
                primeiraConta();
            } else {
            
            }
        
    }//GEN-LAST:event_formWindowOpened

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // TODO add your handling code here:
        logar();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

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
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnEntrar;
    private br.com.LeGnusERP.Swing.botaoArredondado btnLogin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JLabel lblVale;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JPasswordField txtSenha;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txtVale;
    // End of variables declaration//GEN-END:variables
}
