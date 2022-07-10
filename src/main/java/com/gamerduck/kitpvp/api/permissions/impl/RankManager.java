package com.gamerduck.kitpvp.api.permissions.impl;

import com.gamerduck.kitpvp.api.permissions.Rank;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RankManager {
    @Getter final Set<Rank> ranks;
	public RankManager() {
        ranks = new HashSet<Rank>();
        deserialize();
	}
	
	public boolean rankExists(Rank name) {
		Optional<Rank> rank = ranks.stream().filter(r -> r == name).findFirst();
		return rank.isEmpty() ? false : true;
	}
	
	public boolean rankExists(String name) {
		Optional<Rank> rank = ranks.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
		return rank.isEmpty() ? false : true;
	}
	
	public Rank getRank(String name) {
		Optional<Rank> rank = ranks.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
		return rank.isEmpty() ? null : rank.get();
	}
	
	public boolean addRank(Rank rank) {
		if (rankExists(rank)) return false;
		ranks.add(rank);
		return ranks.contains(rank);	
	}
	
	public boolean addRank(String name, String prefix, String suffix) {
		return addRank(new Rank(name, prefix, suffix));	
	}
	
	public boolean addRank(String name, Component prefix, Component suffix) {
		return addRank(new Rank(name, prefix, suffix));	
	}
	
	public boolean removeRank(Rank rank) {
		if (!rankExists(rank)) return false;
		ranks.remove(rank);
		return ranks.contains(rank);	
	}
	
	public boolean removeRank(String name) {
		return removeRank(getRank(name));
	}
	
	public void serialize() {
		
	}
	public void deserialize() {
		
	}	

}
