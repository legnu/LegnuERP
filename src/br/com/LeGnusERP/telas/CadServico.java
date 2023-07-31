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
import static br.com.LeGnusERP.telas.SubClienteAnimal.IdCliente;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
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
public class CadServico extends javax.swing.JFrame {

    /**
     * Creates new form CadServico
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String tipo;
    String referencia;
    String nome;

    public CadServico() {
        initComponents();
        conexao = ModuloConexao.conector();
        setIcon();
    }

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/com/LeGnusERP/icones/ERPGestao64.png")));
    }

    public void InstanciarCombobox() {
        try {
            cbFuncionario.removeAllItems();
            cbFuncionario.addItem("Selecione");
            String sql = "select funcionario from tbFuncionarios where tipo != 'Vendedor'";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbFuncionario.addItem(rs.getString("funcionario"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void pesquisar() {

        if (txtTipo.getText().equals("Cliente") == true) {

            try {
                String sql = "select idcli as ID, nomecli as Cliente from tbclientes where nomecli like ? and idcli > 1";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
                tipo = "Cliente";
                txtTipo.setText("Cliente");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        }
    }

    public void fecharServico() {

        String Perfil;
        limpar();
        try {
            String sqy = "select perfil from tbusuarios where usuario=?";
            pst = conexao.prepareStatement(sqy);
            pst.setString(1, lblUsuario.getText());
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            Perfil = tbAuxilio.getModel().getValueAt(0, 0).toString();
            if (Perfil.equals("Usuario") == true) {

                TelaLimitada limitada = new TelaLimitada();
                limitada.setVisible(true);
                TelaLimitada.btnPDV.setEnabled(false);
                TelaLimitada.btnCadOS.setEnabled(false);
                TelaLimitada.btnCadServiço.setEnabled(false);
                TelaLimitada.lblNome.setText(lblUsuario.getText());
                this.dispose();
                conexao.close();

            }

        } catch (java.lang.ArrayIndexOutOfBoundsException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabela() {
        try {
            String sql = "select idcli as ID, nomecli as Cliente from tbclientes where idcli > 1";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
            tipo = "Cliente";
            txtTipo.setText("Cliente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void instanciarTabelaSubCliente() {

        int setar = tbPrincipal.getSelectedRow();
        referencia = tbPrincipal.getModel().getValueAt(setar, 0).toString();
        nome = tbPrincipal.getModel().getValueAt(setar, 1).toString();
        try {
            String sqo = "select idsub as ID, nome as Nome, tipo as Tipo from tbsubclientes where referencia=?";
            pst = conexao.prepareStatement(sqo);
            pst.setString(1, referencia);
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
            tipo = "SubCliente";
            txtTipo.setText("SubCliente");
            txtPesquisa.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void adicionar() {
        String sql = "insert into tbservicos(servico,id_cliente,id_C_referencia,data_agendada,valor,obs,tipo,funcionario,cliente,subCliente)values(?,?,?,?,?,?,?,?,?,?)";
        try {
            System.out.println("aqui" + IDReferencia.getText());

            //Validação dos Campos Obrigatorios
            if ((txtServico.getText().isEmpty() == true)) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");
                limpar();
            } else if ((Double.parseDouble(txtValor.getText().replace(".", "")) / 100) <= 0) {
                JOptionPane.showMessageDialog(null, "Campo Valor deve ser maior que 0.");
                limpar();
            } else if (cbFuncionario.getSelectedItem().toString().equals("Selecione") == true) {
                JOptionPane.showMessageDialog(null, "Adicione um funcionario.");
                limpar();
            } else if (Integer.parseInt(txtHora.getText().replace(":", "")) > 2359) {
                JOptionPane.showMessageDialog(null, "Horario maximo cadastravel é 23:59");
                limpar();
            } else {

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat dc = new SimpleDateFormat("yyyy-MM-dd ");
                Date d = dtAgendamento.getDate();
                String hora = txtHora.getText() + ":00";

                Date data = df.parse(String.valueOf(dc.format(d)) + hora);
                df.format(data);
                System.out.println(df.format(data));

                java.sql.Date dSql = new java.sql.Date(data.getTime());
                System.out.println(dSql);
                df.format(dSql);
                System.out.println(df.format(dSql));

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtServico.getText());
                pst.setString(2, IDCliente.getText());
                pst.setString(3, IDReferencia.getText());
                pst.setTimestamp(4, Timestamp.valueOf(df.format(data)));
                pst.setString(5, new DecimalFormat("#,##0.00").format(Double.parseDouble(txtValor.getText().replace(",", "."))).replace(",", "."));
                pst.setString(6, txtOBS.getText());
                pst.setString(7, "Agendada");
                pst.setString(8, cbFuncionario.getSelectedItem().toString());

                pst.setString(9, txtCliente.getText());

                if (txtTipo.getText().equals("SubCliente") == true) {
                    pst.setString(10, txtSubCliente.getText());
                } else {
                    pst.setString(10, "");
                }
                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Agendado com sucesso");
                    limpar();
                }
            }
        } catch (java.lang.IllegalArgumentException e) {
            if (txtHora.getText().equals("  :  ")) {
                JOptionPane.showMessageDialog(null, "Campo Hora não pode estar vazio.");
                limpar();
            } else {
                JOptionPane.showMessageDialog(null, "Valor só suporta Numeros.");
                limpar();
            }
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(null, "Campos Hora e Data só suportam Numeros.");
            limpar();

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "Esse horario já foi Agendado.");
            limpar();

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Campo Data só suporta uma data.");
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void setar_campos() {

        if (txtTipo.getText().equals("Cliente") == true) {

            int confirma = JOptionPane.showConfirmDialog(null, "Deseja registrar um serviço a um SubCliente do Cliente Selecionado?", "Atenção", JOptionPane.YES_NO_OPTION);

            if (confirma == JOptionPane.YES_OPTION) {
                int setar = tbPrincipal.getSelectedRow();
                txtCliente.setText(tbPrincipal.getModel().getValueAt(setar, 1).toString());
                instanciarTabelaSubCliente();
                dtAgendamento.setEnabled(false);
                txtHora.setEnabled(false);
                txtOBS.setEnabled(false);
                txtServico.setEnabled(false);
                txtValor.setEnabled(false);
                btnAdicionar.setEnabled(false);

                dtAgendamento.setDate(null);
                txtHora.setText(null);
                txtOBS.setText(null);
                txtPesquisa.setText(null);
                txtServico.setText(null);

                txtValor.setText("0.00");

            } else if (confirma == JOptionPane.NO_OPTION) {
                int setar = tbPrincipal.getSelectedRow();
                dtAgendamento.setEnabled(true);
                txtHora.setEnabled(true);
                txtOBS.setEnabled(true);
                txtServico.setEnabled(true);
                txtValor.setEnabled(true);
                cbFuncionario.setEnabled(true);
                btnAdicionar.setEnabled(true);
                IDCliente.setText(tbPrincipal.getModel().getValueAt(setar, 0).toString());
                txtCliente.setText(tbPrincipal.getModel().getValueAt(setar, 1).toString());
                IDReferencia.setText(IDCliente.getText());

                btnAdicionar.setEnabled(true);
                btnAtualizar.setEnabled(true);
            }
        } else if (txtTipo.getText().equals("SubCliente") == true) {
            int setar = tbPrincipal.getSelectedRow();
            dtAgendamento.setEnabled(true);
            txtHora.setEnabled(true);
            txtOBS.setEnabled(true);
            txtServico.setEnabled(true);
            txtValor.setEnabled(true);
            cbFuncionario.setEnabled(true);
            btnAdicionar.setEnabled(true);
            txtSubCliente.setText(tbPrincipal.getModel().getValueAt(setar, 1).toString());
            IDCliente.setText(tbPrincipal.getModel().getValueAt(setar, 0).toString());
            IDReferencia.setText(referencia);

            btnAdicionar.setEnabled(true);
            btnAtualizar.setEnabled(true);

        }
    }

    public void limpar() {
        dtAgendamento.setDate(null);
        txtHora.setText(null);
        txtOBS.setText(null);
        txtPesquisa.setText(null);
        txtServico.setText(null);
        txtValor.setText("0.00");
        IDCliente.setText(null);
        IDReferencia.setText(null);
        txtTipo.setText("Cliente");
        txtCliente.setText(null);
        txtSubCliente.setText(null);

        dtAgendamento.setEnabled(false);
        cbFuncionario.setEnabled(false);
        txtHora.setEnabled(false);
        txtOBS.setEnabled(false);
        txtServico.setEnabled(false);
        txtValor.setEnabled(false);
        btnAdicionar.setEnabled(false);
        txtPesquisa.setEnabled(true);
        referencia = null;
        nome = null;
        tipo = null;
        instanciarTabela();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        IDCliente = new javax.swing.JTextField();
        IDReferencia = new javax.swing.JTextField();
        txtCliente = new javax.swing.JTextField();
        txtSubCliente = new javax.swing.JTextField();
        lblUsuario = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOBS = new javax.swing.JTextArea();
        btnAdicionar = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        txtValor = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtHora = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        dtAgendamento = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        txtServico = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        cbFuncionario = new javax.swing.JComboBox<>();
        pnTabela = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTipo = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();

        txtSubCliente.setText("jTextField1");

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
        jScrollPane3.setViewportView(tbAuxilio);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Cadastro de Servico");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel7.setBackground(java.awt.SystemColor.control);
        jPanel7.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        jPanel5.setBackground(java.awt.SystemColor.control);
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OBS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtOBS.setColumns(20);
        txtOBS.setRows(5);
        txtOBS.setEnabled(false);
        jScrollPane1.setViewportView(txtOBS);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/iconeAdicionar-removebg-preview.png"))); // NOI18N
        btnAdicionar.setToolTipText("");
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnAdicionar.setEnabled(false);
        btnAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnAtualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/iconeRestart-removebg-preview.png"))); // NOI18N
        btnAtualizar.setToolTipText("");
        btnAtualizar.setBorderPainted(false);
        btnAtualizar.setContentAreaFilled(false);
        btnAtualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        jPanel3.setBackground(java.awt.SystemColor.control);
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Valor Serviço(R$)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtValor.setText("0.00");
        txtValor.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtValor, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBackground(java.awt.SystemColor.control);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Horario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        try {
            txtHora.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtHora.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtHora, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtHora, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Data Agendamento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        dtAgendamento.setBackground(java.awt.SystemColor.control);
        dtAgendamento.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(dtAgendamento, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dtAgendamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel4.setBackground(java.awt.SystemColor.control);
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtServico.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtServico)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel6.setBackground(java.awt.SystemColor.control);
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Funcionario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbFuncionario.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFuncionario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(cbFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnTabela.setBackground(java.awt.SystemColor.control);
        pnTabela.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
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
        jScrollPane2.setViewportView(tbPrincipal);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setText("Pesquisa: ");

        txtPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Tipo: ");

        txtTipo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtTipo.setText("Cliente");
        txtTipo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTipoPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout pnTabelaLayout = new javax.swing.GroupLayout(pnTabela);
        pnTabela.setLayout(pnTabelaLayout);
        pnTabelaLayout.setHorizontalGroup(
            pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTabelaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTabelaLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, 0)
                        .addComponent(txtTipo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTabelaLayout.createSequentialGroup()
                        .addGroup(pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnTabelaLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(16, 16, 16)
                                .addComponent(txtPesquisa, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(16, 16, 16))))
        );
        pnTabelaLayout.setVerticalGroup(
            pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTabelaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(16, 16, 16)
                .addGroup(pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTipo))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        lblCamposObrigatorios.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblCamposObrigatorios.setText("* Campos Obrigatorios");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lblCamposObrigatorios))
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(16, 16, 16)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 126, Short.MAX_VALUE)))
                                .addGap(16, 16, 16)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnAdicionar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAtualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(16, 16, 16))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lblCamposObrigatorios)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        adicionar();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        // TODO add your handling code here:
        limpar();

    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbPrincipalMouseClicked

    private void txtTipoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTipoPropertyChange
        // TODO add your handling code here:

    }//GEN-LAST:event_txtTipoPropertyChange

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        instanciarTabela();
        InstanciarCombobox();
    }//GEN-LAST:event_formWindowOpened

    private void txtPesquisaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txtPesquisaKeyReleased

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        fecharServico();
    }//GEN-LAST:event_formWindowClosed

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
            java.util.logging.Logger.getLogger(CadServico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadServico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadServico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadServico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadServico().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IDCliente;
    private javax.swing.JTextField IDReferencia;
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JToggleButton btnAtualizar;
    private javax.swing.JComboBox<String> cbFuncionario;
    private com.toedter.calendar.JDateChooser dtAgendamento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCamposObrigatorios;
    public javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnTabela;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JFormattedTextField txtHora;
    private javax.swing.JTextArea txtOBS;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtServico;
    private javax.swing.JTextField txtSubCliente;
    private javax.swing.JLabel txtTipo;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
