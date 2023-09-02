/*
 * The MIT License
 *
 * Copyright 2022 Leandro Clemente.
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
package br.com.LeGnusERP.dal;

import java.sql.*;
import br.com.LeGnusERP.dal.ConexaoLocal;
import br.com.LeGnusERP.telas.TelaLimitada;
import br.com.LeGnusERP.telas.TelaPrincipal;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 *
 * @author Leandro Clemente
 */
public class ModuloConexao {

    static Connection conexao = null;
    static PreparedStatement pst = null;
    static ResultSet rs = null;
    static String uri;
            
    public static void ModuloConexao() {
        try {
            
            conexao = ConexaoLocal.conector();
            String sql = "select uri from tbUri where id = 1";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                uri = rs.getString(1);
            }else{
                JOptionPane.showMessageDialog(null, "Banco Invalido");
            }
        } catch (Exception e) {

        }

    }

    public static Connection conector() {
        ModuloConexao();
        java.sql.Connection conexao = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = uri;
        String user = "dba";
        String password = "Legnu.131807";
        //Legnu.131807

        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            //A Linha abaixo serve para esclarecer o erro.
            //System.out.println(e);
            return null;
        }
    }
}
