/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edtempojogado;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author EduardoMGP
 */
public class Comandos implements CommandExecutor {

    private static EDTempoJogado plugin;

    public Comandos() {
        plugin = EDTempoJogado.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {

        if (cmd.getName().equalsIgnoreCase("horas")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("[EDTempoJogado] Este comando pode ser executado apenas por um jogador");
                return true;
            }

            Player p = (Player) sender;

            if (args[0].length() <= 0) {

                if (p.hasPermission("edtempojogado.admin")) {
                    for (String m : plugin.getConfig().getStringList("Mensagens.argumentosAdm")) {
                        p.sendMessage(
                                m.replaceAll("&", "§")
                        );
                    }
                    return true;
                } else {
                    for (String m : plugin.getConfig().getStringList("Mensagens.argumentos")) {
                        p.sendMessage(
                                m.replaceAll("&", "§")
                        );
                    }
                    return true;
                }

            }
            
            
            if(args[0].equalsIgnoreCase("ver")){
                Conexao c = new Conexao();
                int minutos = c.visualizarTempoJogado(p);
                int horas = 0;
                int dias = 0;
                if(minutos > 60){
                    horas = minutos / 60;
                    minutos = minutos % 60;
                }
                if(horas > 24) {
                    dias = horas / 24;
                    horas = horas % 24;
                }
                p.sendMessage();
            }
        }

        return true;
    }

}
