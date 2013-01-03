package me.botsko.prism.commandlibs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import me.botsko.prism.Prism;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Executor implements CommandExecutor {
		
	/**
	 * 
	 */
	public Prism plugin;
	
	/**
	 * 
	 */
	public java.util.Map<String, SubCommand> subcommands = new LinkedHashMap<String, SubCommand>();

	
	/**
	 * 
	 * @param prism
	 */
	public Executor(Prism prism) {
		this.plugin = prism;
	}

	
	/**
	 * 
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		
		// If no subcommand given
		if (args.length == 0) {
			sender.sendMessage( plugin.playerError("Missing command. Check /prism ? for help.") );
			return true;
		}
		
		// Find subcommand
		String subcommandName = args[0].toLowerCase();
		plugin.debug("Seeking subcommand: " + subcommandName);
		SubCommand sub = subcommands.get(subcommandName);
		if (sub == null) {
			plugin.debug("Subcommand not found: " + subcommandName);
			sender.sendMessage( plugin.playerError("Invalid subcommand. Check /prism ? for help.") );
			return true;
		}
		// Ensure they have permission
		else if ( !sender.hasPermission( "prism.*" ) || !sender.hasPermission( sub.getPermNode() ) ) {
			sender.sendMessage( plugin.msgNoPermission() );
			return true;
		}
		// Ensure min number of arguments
		else if ((args.length - 1 ) < sub.getMinArgs()) {
			plugin.debug("Not enough arguments for subcommand: " + subcommandName);
			sender.sendMessage( plugin.playerError("Missing arguments. Check /prism ? for help.") );
			return true;
		}
		
		// Pass along call to handler
		CallInfo call = new CallInfo(sender, player, args);
		sub.getHandler().handle(call);
	
		return true;
		
	}
	
	
	/**
	 * 
	 * @param name
	 * @param permission
	 * @param handler
	 * @return
	 */
	protected SubCommand addSub(String name, String permission, SubHandler handler) {
		SubCommand cmd = new SubCommand(name, permission, handler);
		subcommands.put(name, cmd);
		return cmd;
	}
	
	
	/**
	 * 
	 * @param name
	 * @param permission
	 * @return
	 */
	protected SubCommand addSub(String name, String permission) {
		return addSub(name, permission, null);
	}
	
	
	/**
	 * 
	 * @param sender
	 * @param player
	 * @return
	 */
	protected List<SubCommand> availableCommands(CommandSender sender, Player player) {
		ArrayList<SubCommand> items = new ArrayList<SubCommand>();
		boolean has_player = (player != null);
		for (SubCommand sub: subcommands.values()) {
			if ((has_player || sub.isConsoleAllowed()) && (sender.hasPermission( "prism.*" ) || sender.hasPermission( sub.getPermNode() ) )) {
				items.add(sub);
			}
		}
		return items;
	}
}