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
        
        if (cmd.getName().equalsIgnoreCase("addhoraall")) {

            if (sender instanceof Player) {
                sender.sendMessage("§6[EDTempoJogado] §fEste comando pode ser executado apenas pelo console");
                return true;
            } 
            
            Conexao c = new Conexao();
            c.addTempoJogadoAll();
            return true;
            
        }
        if (cmd.getName().equalsIgnoreCase("horas")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("[EDTempoJogado] Este comando pode ser executado apenas por um jogador");
                return true;
            }

            Player p = (Player) sender;

            if (args.length <= 0) {

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
            if (args[0].equalsIgnoreCase("top")) {
                Conexao c = new Conexao();
                c.visualizarTopJogado(p);
            }

            if (args[0].equalsIgnoreCase("ver")) {
                Conexao c = new Conexao();
                int minutos = 0;
                int horas = 0;
                int dias = 0;
                if (args.length >= 2) {
                    if (!p.hasPermission("edtempojogado.horas_ver")) {
                        p.sendMessage(plugin.getMessage("Mensagens.sempermissao"));
                        return true;
                    }
                    try {
                        minutos = Integer.parseInt(c.visualizarTempoJogado(args[1]));
                    } catch (Exception e) {
                        p.sendMessage(plugin.getMessage("Mensagens.usuarioNaoExiste"));
                        return true;
                    }
                    horas = 0;
                    dias = 0;
                    if (minutos > 60) {
                        horas = minutos / 60;
                        minutos = minutos % 60;
                    }
                    if (horas > 24) {
                        dias = horas / 24;
                        horas = horas % 24;
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.horasVerPlayer")
                            .replaceAll("%d%", dias + "")
                            .replaceAll("%h%", horas + "")
                            .replaceAll("%m%", minutos + "")
                            .replaceAll("%p%", args[1])
                    );
                } else {
                    try {
                        minutos = Integer.parseInt(c.visualizarTempoJogado(p.getName()));
                    } catch (Exception e) {
                        p.sendMessage(plugin.getMessage("Mensagens.usuarioNaoExiste"));
                        return true;
                    }
                    horas = 0;
                    dias = 0;
                    if (minutos > 60) {
                        horas = minutos / 60;
                        minutos = minutos % 60;
                    }
                    if (horas > 24) {
                        dias = horas / 24;
                        horas = horas % 24;
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.horasVer")
                            .replaceAll("%d%", dias + "")
                            .replaceAll("%h%", horas + "")
                            .replaceAll("%m%", minutos + "")
                    );
                }

            }

        }

        return true;
    }

}
