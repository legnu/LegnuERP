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
import org.jfree.chart.title.Title;

/**
 *
 * @author Ad3ln0r
 */
public class CadOS extends javax.swing.JFrame {

    /**
     * Creates new form CadOS
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String tipo;

    public CadOS() {
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

    public void fecharOS() {

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
           

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }
     
    public void InstanciarTabelaCliente() {
        String sql = "select idcli as ID, nomecli as Nome, telefonecli as Telefone from tbclientes where idcli > 1";
        try {
            limpar();
            txtCliPesquisar.setText(null);
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
            tipo = "Cliente";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void pesquisar_cliente() {
        String sql = "select idcli as ID, nomecli as Nome, telefonecli as Telefone from tbclientes where nomecli like ? and idcli > 1";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabelaOrcamento() {
        String sql = "select os as Numero_da_Ordem_de_Serviço, data_os as Emição, tipo as Tipo, situacao as Situação, previsao_entreg_os as Previsão_de_Entrega, equipamento as Equipamento, defeito as Defeito, servico as Serviço, funcionario as Funcionario, valor as Valor, cliente as Cliente from tbos ";
        try {
            limpar();
            txtCliPesquisar.setText(null);
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
            tipo = "Orcamento_OS";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void pesquisar_orcamento() {
        String sql = "select os as Numero_da_Ordem_de_Serviço, data_os as Emição, tipo as Tipo, situacao as Situação, previsao_entreg_os as Previsão_de_Entrega, equipamento as Equipamento, defeito as Defeito, servico as Serviço, funcionario as Funcionario, valor as Valor, cliente as Cliente from tbos where servico like ? ";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void pesquisar() {
        if (tipo.equals("Cliente") == true) {

            pesquisar_cliente();

        } else {

            pesquisar_orcamento();
        }
    }

    private void setar_campos() {
        if (tipo.equals("Cliente") == true) {

            int setar = tbPrincipal.getSelectedRow();
            txtCliente.setText(tbPrincipal.getModel().getValueAt(setar, 1).toString());
            txtOsDef.setEnabled(true);
            txtOsEquip.setEnabled(true);
            txtOsServ.setEnabled(true);
            cbFuncionario.setEnabled(true);
            txtOsValor.setEnabled(true);
            cbTipo.setEnabled(true);
            cboOsSit.setEnabled(true);
            dtPrevisao.setEnabled(true);
            btnAdicionar.setEnabled(true);
            btnResetar.setEnabled(true);
            btnRemover.setEnabled(false);
            btnImprimir.setEnabled(false);
            btnEditar.setEnabled(false);

        } else {

            try {

                int setar = tbPrincipal.getSelectedRow();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                txtOs.setText(tbPrincipal.getModel().getValueAt(setar, 0).toString());
                cbTipo.setSelectedItem(tbPrincipal.getModel().getValueAt(setar, 2).toString());
                cboOsSit.setSelectedItem(tbPrincipal.getModel().getValueAt(setar, 3).toString());
                dtPrevisao.setDate(df.parse(tbPrincipal.getModel().getValueAt(setar, 4).toString()));
                txtOsEquip.setText(tbPrincipal.getModel().getValueAt(setar, 5).toString());
                txtOsDef.setText(tbPrincipal.getModel().getValueAt(setar, 6).toString());
                txtOsServ.setText(tbPrincipal.getModel().getValueAt(setar, 7).toString());
                cbFuncionario.setSelectedItem(tbPrincipal.getModel().getValueAt(setar, 8).toString());
                double valor = Double.parseDouble(tbPrincipal.getModel().getValueAt(setar, 9).toString().replace(".", "")) / 100;
                txtOsValor.setText(String.valueOf(valor));
                txtCliente.setText(tbPrincipal.getModel().getValueAt(setar, 10).toString());
                txtOsDef.setEnabled(true);
                txtOsEquip.setEnabled(true);
                txtOsServ.setEnabled(true);
                cbFuncionario.setEnabled(true);
                txtOsValor.setEnabled(true);
                cbTipo.setEnabled(true);
                cboOsSit.setEnabled(true);
                dtPrevisao.setEnabled(true);
                btnAdicionar.setEnabled(false);
                btnResetar.setEnabled(true);
                btnRemover.setEnabled(true);
                btnImprimir.setEnabled(true);
                btnEditar.setEnabled(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }
        }
    }

    private void emitir_os() {

        try {
            int confirma = 0;
            if ((txtCliente.getText().isEmpty()) || (txtOsEquip.getText().isEmpty()) || (txtOsDef.getText().isEmpty()) || (txtOsServ.getText().isEmpty()) || (txtOsValor.getText().isEmpty())||  dtPrevisao.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                limpar();
                confirma = 1;
            } else if (cbFuncionario.getSelectedItem().toString().equals("Selecione") == true) {
                JOptionPane.showMessageDialog(null, "Adicione um Funcionario.");
                limpar();
            }else if ((Double.parseDouble(txtOsValor.getText().replace(",", ".")) <= 0)) {
                JOptionPane.showMessageDialog(null, "Adicione um valor maior que 0.");
                limpar();
            } else if (confirma == 0) {
                Date d = dtPrevisao.getDate();
                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                String sql = "insert into tbos(tipo,situacao,previsao_entreg_os,equipamento,defeito,servico,funcionario,valor,cliente) values(?,?,?,?,?,?,?,?,?)";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, cbTipo.getSelectedItem().toString());
                pst.setString(2, cboOsSit.getSelectedItem().toString());
                pst.setDate(3, dSql);
                pst.setString(4, txtOsEquip.getText());
                pst.setString(5, txtOsDef.getText());
                pst.setString(6, txtOsServ.getText());
                pst.setString(7, cbFuncionario.getSelectedItem().toString());
                pst.setString(8, new DecimalFormat("#,##0.00").format(Double.parseDouble(txtOsValor.getText().replace(",", "."))).replace(",", "."));
                pst.setString(9, txtCliente.getText());

                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "OS emitida com sucesso.");
                    limpar();

                }
            }
        } catch (java.lang.NumberFormatException c) {
            JOptionPane.showMessageDialog(null, "Campo Valor só suporta Numeros.");
            JOptionPane.showMessageDialog(null, "Campo Valor Deve ser salvo no formato 0.00 .");
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void alterar_os() {
     

        try {
            int confirma = 0;
            if ((txtCliente.getText().isEmpty()) || (txtOsEquip.getText().isEmpty()) || (txtOsDef.getText().isEmpty()) || (txtOsServ.getText().isEmpty()) || (txtOsValor.getText().isEmpty()) || dtPrevisao.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                limpar();
                confirma = 1;
            } else if (cbFuncionario.getSelectedItem().toString().equals("Selecione") == true) {
                JOptionPane.showMessageDialog(null, "Adicione um Funcionario.");
                limpar();
            }else if ((Double.parseDouble(txtOsValor.getText().replace(",", ".")) <= 0)) {
                JOptionPane.showMessageDialog(null, "Adicione um valor maior que 0.");
                limpar();
            } else if (confirma == 0) {
                Date d = dtPrevisao.getDate();
                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                String sql = "update tbos set tipo=?,situacao=?,previsao_entreg_os=?,equipamento=?,defeito=?,servico=?,funcionario=?,valor=? where os=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, cbTipo.getSelectedItem().toString());
                pst.setString(2, cboOsSit.getSelectedItem().toString());
                pst.setDate(3, dSql);
                pst.setString(4, txtOsEquip.getText());
                pst.setString(5, txtOsDef.getText());
                pst.setString(6, txtOsServ.getText());
                pst.setString(7, cbFuncionario.getSelectedItem().toString());
                pst.setString(8, new DecimalFormat("#,##0.00").format(Double.parseDouble(txtOsValor.getText().replace(",", "."))).replace(",", "."));
                pst.setString(9, txtOs.getText());
          

                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "OS alterada com sucesso.");
                    limpar();

                }
            }
        } catch (java.lang.NumberFormatException c) {
            JOptionPane.showMessageDialog(null, "Campo Valor só suporta Numeros.");
            JOptionPane.showMessageDialog(null, "Campo Valor Deve ser salvo no formato 0.00 .");
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void excluir_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir esta OS?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbos where os=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtOs.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "OS excluida com sucesso.");
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        }
    }

    private void tirarId() {

        String sql = "alter table tbos drop os";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void criarId() {
        String sql = "alter table tbos add os MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void imprimir_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta OS?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {

                HashMap filtro = new HashMap();
                filtro.put("os", Integer.parseInt(txtOs.getText()));
                String sql = "select nome_empresa,nome_proprietario,email_proprietario,descricao,obs,numero,imagem from tbrelatorio where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));               
                
                filtro.put("empresa", tbAuxilio.getModel().getValueAt(0, 0).toString());
                filtro.put("nome", tbAuxilio.getModel().getValueAt(0, 1).toString());
                filtro.put("email", tbAuxilio.getModel().getValueAt(0, 2).toString());
                filtro.put("descricao", tbAuxilio.getModel().getValueAt(0, 3).toString());
                filtro.put("obs", tbAuxilio.getModel().getValueAt(0, 4).toString());
                filtro.put("numero", tbAuxilio.getModel().getValueAt(0, 5).toString());
                filtro.put("imagem", tbAuxilio.getModel().getValueAt(0, 6).toString());
                filtro.put("cliente", txtCliente.getText());
                filtro.put("Bandeira", "src/br/com/LeGnusERP/icones/bandeira.PNG");
                filtro.put("Background", "src/br/com/LeGnusERP/icones/papelEnvelhecidoMaisClaro.PNG");
                
                JasperReport jreport = JasperCompileManager.compileReport("src/reports/RelOS.jrxml");
                
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
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }
        }
    }

    private void limpar() {
        txtCliId.setText(null);
        txtCliPesquisar.setText(null);
        txtCliente.setText(null);
        txtOs.setText(null);
        txtOsDef.setText(null);
        txtOsEquip.setText(null);
        txtOsServ.setText(null);
        
        txtOsValor.setText("0.00");
        dtPrevisao.setDate(null);
        txtCliId.setEnabled(false);
        txtOsDef.setEnabled(false);
        txtOsEquip.setEnabled(false);
        txtOsServ.setEnabled(false);
        cbFuncionario.setEnabled(false);
        txtOsValor.setEnabled(false);
        cbTipo.setEnabled(false);
        cboOsSit.setEnabled(false);
        dtPrevisao.setEnabled(false);
        btnAdicionar.setEnabled(false);
        btnResetar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnImprimir.setEnabled(false);
       
       
        btnEditar.setEnabled(false);

        ((DefaultTableModel) tbPrincipal.getModel()).setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtCliId = new javax.swing.JTextField();
        txtOs = new javax.swing.JTextField();
        grupoTbPrincipal = new javax.swing.ButtonGroup();
        lblUsuario = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        txtOsValor = new javax.swing.JTextField();
        btnCliente = new javax.swing.JToggleButton();
        btnAdicionar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnResetar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        cbTipo = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        cbFuncionario = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        txtCliente = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        txtOsEquip = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtOsDef = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        dtPrevisao = new com.toedter.calendar.JDateChooser();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtOsServ = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        cboOsSit = new javax.swing.JComboBox<>();
        pnTbPrincipal = new javax.swing.JPanel();
        scTbPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable();
        lblPesquisar = new javax.swing.JLabel();
        txtCliPesquisar = new javax.swing.JTextField();
        rbClientes = new javax.swing.JRadioButton();
        rbOS_Orcamento = new javax.swing.JRadioButton();
        lblCamposObrigatorios = new javax.swing.JLabel();

        txtCliId.setEditable(false);

        txtOs.setEditable(false);

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
        jScrollPane1.setViewportView(tbAuxilio);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LeGnu`s_EPR- Cadastro de OS");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel10.setBackground(java.awt.SystemColor.control);
        jPanel10.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(204, 204, 204))));

        jPanel9.setBackground(java.awt.SystemColor.control);
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Valor Total(R$)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtOsValor.setText("0.00");
        txtOsValor.setEnabled(false);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtOsValor, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtOsValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

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

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/iconeAdicionar-removebg-preview.png"))); // NOI18N
        btnAdicionar.setToolTipText("Adicionar");
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnAdicionar.setEnabled(false);
        btnAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/iconeEditar-removebg-preview.png"))); // NOI18N
        btnEditar.setToolTipText("Pesquisar");
        btnEditar.setContentAreaFilled(false);
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnEditar.setEnabled(false);
        btnEditar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/iconeRemover-removebg-preview.png"))); // NOI18N
        btnRemover.setToolTipText("Deletar");
        btnRemover.setContentAreaFilled(false);
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRemover.setEnabled(false);
        btnRemover.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/ImpresoraIcon.png"))); // NOI18N
        btnImprimir.setToolTipText("Imprimir");
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnImprimir.setEnabled(false);
        btnImprimir.setPreferredSize(new java.awt.Dimension(80, 80));
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnResetar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/LeGnusERP/icones/iconeRestart-removebg-preview.png"))); // NOI18N
        btnResetar.setToolTipText("Editar");
        btnResetar.setContentAreaFilled(false);
        btnResetar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnResetar.setEnabled(false);
        btnResetar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnResetar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetarActionPerformed(evt);
            }
        });

        jPanel3.setBackground(java.awt.SystemColor.control);
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Tipo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Orçamento", "Ordem de Serviço" }));
        cbTipo.setEnabled(false);
        cbTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTipoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbTipo, 0, 175, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel8.setBackground(java.awt.SystemColor.control);
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Funcionario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbFuncionario.setEnabled(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFuncionario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1.setBackground(java.awt.SystemColor.control);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtCliente.setEnabled(false);
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtCliente)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel5.setBackground(java.awt.SystemColor.control);
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Equipamento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtOsEquip.setEnabled(false);
        txtOsEquip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOsEquipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtOsEquip)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(txtOsEquip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel6.setBackground(java.awt.SystemColor.control);
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Defeito", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtOsDef.setColumns(20);
        txtOsDef.setRows(5);
        txtOsDef.setEnabled(false);
        jScrollPane3.setViewportView(txtOsDef);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
        );

        jPanel4.setBackground(java.awt.SystemColor.control);
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Previsão Entrega", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        dtPrevisao.setDateFormatString("yyyy-MM-dd");
        dtPrevisao.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dtPrevisao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dtPrevisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel7.setBackground(java.awt.SystemColor.control);
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "*Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtOsServ.setColumns(20);
        txtOsServ.setRows(5);
        txtOsServ.setEnabled(false);
        jScrollPane2.setViewportView(txtOsServ);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane2)
                .addGap(0, 0, 0))
        );

        jPanel2.setBackground(java.awt.SystemColor.control);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Situação", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cboOsSit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Na bancada", "Entrega OK", "Orçamento REPROVADO", "Aguardando Aprovação", "Aguardando peças", "Abandonado pelo cliente", "Retornou" }));
        cboOsSit.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cboOsSit, 0, 193, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cboOsSit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnTbPrincipal.setBackground(java.awt.SystemColor.control);
        pnTbPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
        scTbPrincipal.setViewportView(tbPrincipal);

        lblPesquisar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisar.setText("Pesquisar:");

        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        rbClientes.setBackground(java.awt.SystemColor.control);
        grupoTbPrincipal.add(rbClientes);
        rbClientes.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbClientes.setText("Clientes");
        rbClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbClientesActionPerformed(evt);
            }
        });

        rbOS_Orcamento.setBackground(java.awt.SystemColor.control);
        grupoTbPrincipal.add(rbOS_Orcamento);
        rbOS_Orcamento.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbOS_Orcamento.setText("Orçamento/Ordem de Serviço");
        rbOS_Orcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbOS_OrcamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTbPrincipalLayout = new javax.swing.GroupLayout(pnTbPrincipal);
        pnTbPrincipal.setLayout(pnTbPrincipalLayout);
        pnTbPrincipalLayout.setHorizontalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scTbPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                        .addComponent(rbClientes)
                        .addGap(18, 18, 18)
                        .addComponent(rbOS_Orcamento)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                        .addComponent(lblPesquisar)
                        .addGap(16, 16, 16)
                        .addComponent(txtCliPesquisar)))
                .addGap(16, 16, 16))
        );
        pnTbPrincipalLayout.setVerticalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPesquisar)
                    .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbClientes)
                    .addComponent(rbOS_Orcamento))
                .addGap(16, 16, 16)
                .addComponent(scTbPrincipal)
                .addGap(16, 16, 16))
        );

        lblCamposObrigatorios.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblCamposObrigatorios.setText("* Campos Obrigatorios");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnTbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(btnResetar, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16)
                        .addComponent(btnCliente))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblCamposObrigatorios))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnTbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lblCamposObrigatorios)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnImprimir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemover, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEditar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAdicionar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnResetar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        emitir_os();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        alterar_os();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnResetarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetarActionPerformed
        // TODO add your handling code here:
        limpar();
    }//GEN-LAST:event_btnResetarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        // TODO add your handling code here:
        excluir_os();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbPrincipalMouseClicked

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void cbTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTipoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTipoActionPerformed

    private void txtOsEquipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOsEquipActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOsEquipActionPerformed

    private void rbOS_OrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbOS_OrcamentoActionPerformed
        // TODO add your handling code here:
        instanciarTabelaOrcamento();
    }//GEN-LAST:event_rbOS_OrcamentoActionPerformed

    private void rbClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbClientesActionPerformed
        // TODO add your handling code here:
        InstanciarTabelaCliente();
    }//GEN-LAST:event_rbClientesActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        // TODO add your handling code here:
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        // TODO add your handling code here:
       imprimir_os();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        InstanciarCombobox();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        fecharOS();
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
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadOS().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JToggleButton btnCliente;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnRemover;
    private javax.swing.JButton btnResetar;
    private javax.swing.JComboBox<String> cbFuncionario;
    private javax.swing.JComboBox<String> cbTipo;
    private javax.swing.JComboBox<String> cboOsSit;
    private com.toedter.calendar.JDateChooser dtPrevisao;
    private javax.swing.ButtonGroup grupoTbPrincipal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblPesquisar;
    public javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnTbPrincipal;
    private javax.swing.JRadioButton rbClientes;
    private javax.swing.JRadioButton rbOS_Orcamento;
    private javax.swing.JScrollPane scTbPrincipal;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtCliId;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtOs;
    private javax.swing.JTextArea txtOsDef;
    private javax.swing.JTextField txtOsEquip;
    private javax.swing.JTextArea txtOsServ;
    private javax.swing.JTextField txtOsValor;
    // End of variables declaration//GEN-END:variables
}
