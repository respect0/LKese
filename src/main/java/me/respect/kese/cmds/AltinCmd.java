package me.respect.kese.cmds;

import me.respect.kese.Kese;
import me.respect.kese.vault.KeseVaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class AltinCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    private Kese main;
    public AltinCmd(Kese main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notConsole")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("gonder")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notNumber")));
                    return true;
                }

                if (amount < 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notZero")));
                    return true;
                }

                if (economy.has(player, amount)) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.noPlayerFound")));
                        return true;
                    }


                    if (target == player) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.noYourself")));
                        return true;
                    }

                    if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.anErrorOccurred")));
                        return true;
                    }

                    if (!economy.depositPlayer(target, amount).transactionSuccess()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.anErrorOccurred")));
                        return true;
                    }


                    String formatted = economy.format(amount);
                    //economy.format(economy.getBalance(player))
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.goldAmount").replaceAll("%gold%", economy.format(economy.getBalance(player)))));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.sentGold").replaceAll("%gold%", formatted)));
                    //economy.format(economy.getBalance(target))
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.goldAmount").replaceAll("%gold%", economy.format(economy.getBalance(target)))));
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.incomingGold").replaceAll("%gold%", formatted)));
                    return true;
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',this.main.config.getString("messages.insufficientGold")));
                    return true;
                }

            } else {
                sendHelpMessage(player);
                return true;
            }
        } else {
            sendHelpMessage(player);
        }


        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("gonder");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }


    private void sendHelpMessage(Player player) {
        List<String> messages = this.main.config.getStringList("gui.player");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", messages)));
    }


}
