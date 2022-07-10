package com.gamerduck.kitpvp.api.economy;

import lombok.Getter;

public enum Currency {
	COINS('$', "Coin", "Coin", "Coins");
	
	@Getter char symbol;
	@Getter String identifier;
	@Getter String singular;
	@Getter String plural;
	
	Currency(char symbol, String identifier, String singular, String plural) {
		this.symbol = symbol;
		this.identifier = identifier;
		this.singular = singular;
		this.plural = plural;
	}
}
