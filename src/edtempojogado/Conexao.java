/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edtempojogado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author EduardoMGP
 */
public class Conexao {

    private static EDTempoJogado plugin;

    public Conexao() {
        plugin = EDTempoJogado.getInstance();
    }

    public Connection abrirConexao() {

        String USERNAME = plugin.getConfig().getString("Conexao.usuario");
        String HOST = plugin.getConfig().getString("Conexao.host");
        String DATABASE = plugin.getConfig().getString("Conexao.dataBase");
        String SENHA = plugin.getConfig().getString("Conexao.senha");
        int PORTA = plugin.getConfig().getInt("Conexao.porta");

        try {

            Connection conexao = null;
            conexao = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORTA + "/" + DATABASE, USERNAME, SENHA);

        } catch (Exception e) {
        }

        return null;
    }

    public void criarTabela() {

        try {

            Connection conexao = abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(
                    "CREATE TABLE EDTempoJogado ("
                    + "idUsuario INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "usuario VARCHAR(50) NOT NULL,"
                    + "tempoJogado INT DEFAULT 0"
                    + ");"
            );
            ps.execute();

            ps.execute(
                    "DROP PROCEDURE IF EXISTS `visualizarTempoJogado`;"
            );

            ps.execute(
                    "CREATE PROCEDURE IF NOT EXISTS visualizarTempoJogado(usuario VARCHAR(50))\n"
                    + "BEGIN "
                    + "    SELECT COUNT(EDTempoJogado.usuario) INTO @PlayerExists FROM EDTempoJogado WHERE EDTempoJogado.usuario = usuario; "
                    + " 	IF (@PlayerExists > 0) THEN "
                    + "		    SELECT tempoJogado FROM EDTempoJogado WHERE EDTempoJogado.usuario = usuario; "
                    + "		ELSE "
                    + "		    INSERT INTO EDTempoJogado (usuario, tempoJogado) VALUES (usuario, 0); "
                    + "		    SELECT 0 AS tempoJogado;\n " 
                    + "END; "
            );

            conexao.close();
        } catch (Exception e) {

            Bukkit.getConsoleSender().sendMessage("§a[EDTempoJogado] §fNao foi possivel criar tabela no MySQL");
            Bukkit.getConsoleSender().sendMessage("§a[EDTempoJogado] §fDesabilitando Plugin");
            Bukkit.getConsoleSender().sendMessage("§c" + e);
            Bukkit.getPluginManager().disablePlugin(plugin);

        }
    }

    public int visualizarTempoJogado(Player p) {

        try {

            Connection conexao = abrirConexao();

            PreparedStatement ps = conexao.prepareStatement("CALL visualizarTempoJogado(?);");
            ps.setString(1, p.getName());
            ResultSet resultado = ps.executeQuery();
            ArrayList r = new ArrayList();
            if(resultado.next()){
                r.add(resultado.getString("tempoJogado"));
            }
            conexao.close();
            return Integer.parseInt(r.get(0).toString());

        } catch (Exception e) {
        }
        return 0;
    }

}
