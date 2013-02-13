package me.botsko.prism.appliers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actions.Action;
import me.botsko.prism.actions.ActionType;
import me.botsko.prism.commandlibs.Flag;
import me.botsko.prism.events.BlockStateChange;
import me.botsko.prism.utils.BlockUtils;
import me.botsko.prism.utils.EntityUtils;

public class Rollback extends Preview {
	
	
	/**
	 * 
	 * @param plugin
	 * @return 
	 */
	public Rollback( Prism plugin, Player player, PrismProcessType processType, List<Action> results, QueryParameters parameters ){
		super(plugin, player, processType, results, parameters);
	}
	
	
	/**
	 * Set preview move and then do a rollback
	 * @return
	 */
	public void preview(){
		is_preview = true;
		apply();
	}
	
	
	/**
	 * 
	 */
	public void apply(){
		
		// Remove any fire at this location
		if(plugin.getConfig().getBoolean("prism.appliers.remove-fire-on-burn-rollback") && parameters.getActionTypes().containsKey(ActionType.BLOCK_BURN)){
			if( !parameters.hasFlag(Flag.NO_EXT) ){
				ArrayList<BlockStateChange> blockStateChanges = BlockUtils.extinguish(player.getLocation(),parameters.getRadius());
				if( blockStateChanges != null && !blockStateChanges.isEmpty() ){
					player.sendMessage( plugin.playerHeaderMsg("Extinguishing fire!" + ChatColor.GRAY + " Like a boss.") );
				}
			}
		}
		
		// Remove item drops in this radius
		if(plugin.getConfig().getBoolean("prism.appliers.remove-drops-on-explode-rollback") && (parameters.getActionTypes().containsKey(ActionType.TNT_EXPLODE) || parameters.getActionTypes().containsKey(ActionType.CREEPER_EXPLODE)) ){
			if( !parameters.hasFlag(Flag.NO_ITEMCLEAR) ){
				int removed = EntityUtils.removeNearbyItemDrops(player, parameters.getRadius());
				if(removed > 0){
					player.sendMessage( plugin.playerHeaderMsg("Removed " + removed + " drops in affected area." + ChatColor.GRAY + " Like a boss.") );
				}
			}
		}
		
		// Remove any liquid at this location
		ArrayList<BlockStateChange> drained = null;
		if( parameters.hasFlag(Flag.DRAIN) ){
			drained = BlockUtils.drain(player.getLocation(),parameters.getRadius());
		}
		if( parameters.hasFlag(Flag.DRAIN_LAVA) ){
			drained = BlockUtils.drainlava(player.getLocation(),parameters.getRadius());
		}
		if( parameters.hasFlag(Flag.DRAIN_WATER) ){
			drained = BlockUtils.drainwater(player.getLocation(),parameters.getRadius());
		}
		if(drained != null && drained.size() > 0){
			player.sendMessage( plugin.playerHeaderMsg("Draining liquid!" + ChatColor.GRAY + " Like a boss.") );
		}
			
		// Give the results to the changequeue
		super.apply();
		
	}
}