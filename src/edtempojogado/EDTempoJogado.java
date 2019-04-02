/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edtempojogado;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author EduardoMGP
 */
public class EDTempoJogado extends JavaPlugin{

    private static EDTempoJogado plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getConsoleSender().sendMessage("[EDTempoJogado] Plugin habilitado com sucesso");
        Bukkit.getConsoleSender().sendMessage("[EDTempoJogado] Versao 1.0");
        getCommand("horas").setExecutor(new Comandos());
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[EDTempoJogado] Plugin desabilitado com sucesso");
    }
    
    
    public String getMessage(String m){
        return plugin.getConfig()
                .getString("Mensagens.horasVer")
                .replaceAll("&", "§");
    }
    
    public static EDTempoJogado getInstance() {
        return plugin;
    }
    
    
    
}
