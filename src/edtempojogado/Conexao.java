/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edtempojogado;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author EduardoMGP
 */
public class Conexao {

    private static EDTempoJogado plugin;

    public Conexao() {
        plugin = EDTempoJogado.getInstance();
    }

    public void criarTabela() {

        try {
            Connection conexao = plugin.getConexao();
            PreparedStatement ps = conexao.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS edtempojogado ("
                    + "idUsuario INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "usuario VARCHAR(50) NOT NULL,"
                    + "tempoJogado INT DEFAULT 0"
                    + ")"
            );
            ps.execute();
            
            
             ps = conexao.prepareStatement(
                    "DROP PROCEDURE IF EXISTS `addTempoJogadoAll`;"
            );
            ps.execute();
            
            ps = conexao.prepareStatement(
                    "CREATE PROCEDURE IF NOT EXISTS addTempoJogadoAll(usuario VARCHAR(50))\n "
                            +   "BEGIN "
                            +       "SELECT COUNT(edtempojogado.usuario) INTO @PlayerExists FROM edtempojogado WHERE edtempojogado.usuario = usuario; "
                            +       "IF (@PlayerExists > 0) THEN "
                            +           "SELECT tempoJogado INTO @tempojogado FROM edtempojogado WHERE edtempojogado.usuario = usuario; "
                            +           "UPDATE edtempojogado SET tempoJogado = @tempojogado + 1 WHERE edtempojogado.usuario = usuario; "
                            +       "ELSE "
                            +           "INSERT INTO edtempojogado (usuario, tempoJogado) VALUES (usuario, 1); "
                            +       "END IF;\n "
                            +   "END;"
            );
            ps.execute();

            ps = conexao.prepareStatement(
                    "CREATE VIEW IF NOT EXISTS onlinetop AS "
                            + "    SELECT usuario, tempojogado, "
                            + "           FLOOR(tempoJogado/1440) AS dias, "
                            + "           FLOOR((tempoJogado%1440)/60) AS horas, "
                            + "           FLOOR(tempoJogado%60) AS minutos "
                            + "    FROM edtempojogado ORDER BY tempoJogado DESC LIMIT 5"
            );
            ps.execute();
            conexao.close();

        } catch (Exception e) {

            Bukkit.getConsoleSender().sendMessage("§a[EDTempoJogado] §fNao foi possivel criar tabela no MySQL");
            Bukkit.getConsoleSender().sendMessage("§a[EDTempoJogado] §fDesabilitando Plugin");
            Bukkit.getConsoleSender().sendMessage("§c" + e);
            Bukkit.getPluginManager().disablePlugin(plugin);

        }
    }

    public String getTempoJogado(String p) {

        try {

            Connection conexao = plugin.getConexao();
            PreparedStatement ps = conexao.prepareStatement(
                    "SELECT usuario, tempojogado," +
                    "           FLOOR(tempoJogado/1440) AS dias," +
                    "           FLOOR((tempoJogado%1440)/60) AS horas," +
                    "           FLOOR(tempoJogado%60) AS minutos FROM edtempojogado WHERE usuario = ?;");
            ps.setString(1, p);
            ResultSet resultado = ps.executeQuery();
            String tempoJogado = null;
            if (resultado.next()) {
                tempoJogado = resultado.getString("usuario") +";" + resultado.getString("tempojogado") +";"
                        + resultado.getString("dias") +";" + resultado.getString("horas")+";"
                        + resultado.getString("minutos");
            }
            conexao.close();
            return tempoJogado;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public void visualizarTopJogado(Player p) {

        try {
            Connection conexao = plugin.getConexao();
            PreparedStatement ps = conexao.prepareStatement("SELECT * from onlinetop");
            ResultSet resultado = ps.executeQuery();
            p.sendMessage("");
            p.sendMessage(plugin.getMessage("Mensagens.topOnline"));

            p.sendMessage("");
            while (resultado.next()) {

                p.sendMessage(plugin.getMessage("Mensagens.topOnlinePlayers")
                        .replaceAll("%p%", resultado.getString("usuario"))
                        .replaceAll("%d%", resultado.getString("dias") + "")
                        .replaceAll("%h%", resultado.getString("horas") + "")
                        .replaceAll("%m%", resultado.getString("minutos") + "")
                );
            }
            p.sendMessage("");
            conexao.close();
        } catch (Exception e) {
        }
    }

    public ArrayList<String> getOnlineTop() {

        try {

            Connection conexao = plugin.getConexao();
            PreparedStatement ps = conexao.prepareStatement("SELECT * from onlinetop");
            ResultSet resultado = ps.executeQuery();
            ArrayList<String> r = new ArrayList();
            while (resultado.next()) {
                r.add(resultado.getString("usuario") +";" + resultado.getString("tempojogado") +";" + resultado.getString("dias") +";" + resultado.getString("horas")+";" + resultado.getString("minutos"));

            }
            conexao.close();
            return r;

        } catch (Exception e) {
        }
        return null;
    }

    public void addTempoJogadoAll() {

        try {
            Connection conexao = plugin.getConexao();
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                PreparedStatement ps =plugin.getConexao().prepareStatement("CALL addTempoJogadoAll(?)");
                ps.setString(1, player.getName());
                ps.execute();
            }
            conexao.close();

        } catch (Exception e) {
        }
    }

}
