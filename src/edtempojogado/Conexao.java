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

            Connection conexao = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORTA + "/" + DATABASE, USERNAME, SENHA);
            return conexao;
        } catch (Exception e) {
            System.out.println("Erro: " + e);
        }

        return null;
    }

    public void criarTabela() {

        try {

            Connection conexao = abrirConexao();
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
            conexao.close();
        } catch (Exception e) {

            Bukkit.getConsoleSender().sendMessage("§a[EDTempoJogado] §fNao foi possivel criar tabela no MySQL");
            Bukkit.getConsoleSender().sendMessage("§a[EDTempoJogado] §fDesabilitando Plugin");
            Bukkit.getConsoleSender().sendMessage("§c" + e);
            Bukkit.getPluginManager().disablePlugin(plugin);

        }
    }

    public String visualizarTempoJogado(String p) {

        try {

            Connection conexao = abrirConexao();

            PreparedStatement ps = conexao.prepareStatement("SELECT tempoJogado FROM edtempojogado WHERE usuario = ?;");
            ps.setString(1, p);
            ResultSet resultado = ps.executeQuery();
            ArrayList r = new ArrayList();
            if (resultado.next()) {
                r.add(resultado.getString("tempoJogado"));
            } else {
                conexao.close();
                return null;
            }
            conexao.close();
            return r.get(0).toString();

        } catch (Exception e) {
        }
        return null;
    }

    public String visualizarTopJogado(Player p) {

        try {

            Connection conexao = abrirConexao();

            PreparedStatement ps = conexao.prepareStatement("SELECT * from edtempojogado ORDER BY tempoJogado DESC LIMIT 5");
            ResultSet resultado = ps.executeQuery();
            p.sendMessage("");
            p.sendMessage(plugin.getMessage("Mensagens.topOnline"));
            ArrayList r = new ArrayList();
            int minutos = 0;
            int horas = 0;
            int dias = 0;

            p.sendMessage("");
            while (resultado.next()) {
                minutos = resultado.getInt("tempoJogado");
                if (minutos > 60) {
                    horas = minutos / 60;
                    minutos = minutos % 60;
                }
                if (horas > 24) {
                    dias = horas / 24;
                    horas = horas % 24;
                }

                p.sendMessage(plugin.getMessage("Mensagens.topOnlinePlayers")
                        .replaceAll("%p%", resultado.getString("usuario"))
                        .replaceAll("%d%", dias + "")
                        .replaceAll("%h%", horas + "")
                        .replaceAll("%m%", minutos + "")
                );
            }
            p.sendMessage("");

            conexao.close();
            return r.get(0).toString();

        } catch (Exception e) {
        }
        return null;
    }

    public void addTempoJogadoAll() {

        try {

            Connection conexao = abrirConexao();
            Player[] p = Bukkit.getServer().getOnlinePlayers();
            for(int i = 0; i < p.length; i++){
                PreparedStatement ps = conexao.prepareStatement("CALL addTempoJogadoAll(?)");
                ps.setString(1, p[i].getName());
                ps.execute();
            }
            conexao.close();

        } catch (Exception e) {
        }
    }

}
