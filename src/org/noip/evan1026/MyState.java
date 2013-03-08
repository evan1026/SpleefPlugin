package org.noip.evan1026;

import org.bukkit.block.BlockState;

public class MyState {
	
	private BlockState state;
	private String arena;
	
	public MyState(BlockState State, String Arena){
		arena = Arena;
		state = State;
	}
	
	public BlockState getState(){
		return state;
	}
	
	public String getArena(){
		return arena;
	}

}
