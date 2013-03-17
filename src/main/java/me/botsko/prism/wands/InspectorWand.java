package me.botsko.prism.wands;

import java.util.ArrayList;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionMessage;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.MatchRule;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InspectorWand extends QueryWandBase implements Wand {

	
	
	/**
	 * 
	 * @param plugin
	 */
	public InspectorWand( Prism plugin ){
		super(plugin);
	}
	
	
	/**
	 * 
	 */
	public void playerLeftClick(Player player, Block block) {
		showBlockHistory(player, block, block.getLocation());
	}

	
	/**
	 * 
	 */
	public void playerRightClick(Player player, Block block) {
		showBlockHistory(player, block, block.getLocation());
	}
	
	
	/**
	 * 
	 * @param player
	 * @param block
	 * @param loc
	 */
	protected void showBlockHistory( Player player, Block block, Location loc ){

		// Build params
		QueryParameters params;
		
		try {
			params = parameters.clone();
		} catch (CloneNotSupportedException ex) {
			params = new QueryParameters();
			player.sendMessage(Prism.messenger.playerError(ChatColor.YELLOW + "Warning: An error occurred while trying to retrieve the params from this wand. Checking with default parameters."));
		}
		params.setWorld( player.getWorld().getName() );
		params.setSpecificBlockLocation(loc);
		
		// Ignoring any actions via config?
		@SuppressWarnings("unchecked")
		ArrayList<String> ignoreActions = (ArrayList<String>) plugin.getConfig().getList("prism.wands.inspect.ignore-actions");
		if( ignoreActions != null && !ignoreActions.isEmpty() ){
			for(String ignore : ignoreActions){
				params.addActionType(ignore, MatchRule.EXCLUDE);
			}
		}
		
		// Query
		ActionsQuery aq = new ActionsQuery(plugin);
		QueryResult results = aq.lookup( params, player );
		if(!results.getActionResults().isEmpty()){
			String blockname = plugin.getItems().getAlias(block.getTypeId(), block.getData());
			player.sendMessage( Prism.messenger.playerHeaderMsg( ChatColor.GOLD + "--- Inspecting "+blockname+" at "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ()+" ---" ) );
			if(results.getActionResults().size() > 5){
				player.sendMessage( Prism.messenger.playerHeaderMsg("Showing "+results.getTotal_results()+" results. Page 1 of "+results.getTotal_pages()) );
			}
			for(me.botsko.prism.actions.Handler a : results.getPaginatedActionResults()){
				ActionMessage am = new ActionMessage(a);
				player.sendMessage( Prism.messenger.playerMsg( am.getMessage() ) );
			}
		} else {
			String space_name = (block.getType().equals(Material.AIR) ? "space" : block.getType().toString().toLowerCase() + " block");
			player.sendMessage( Prism.messenger.playerError( "No history for this " + space_name + " found." ) );
		}
	}


	/**
	 * 
	 */
	public void playerRightClick(Player player, Entity entity) {
		return;
	}
}