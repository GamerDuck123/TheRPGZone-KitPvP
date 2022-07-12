package com.gamerduck.kitpvp.api.economy;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Account {

    @Getter
    final UUID uuid;
    @Getter
    @Setter
    double balance;

    public Account(UUID uuid, double balance) {
        this.uuid = uuid;
        this.balance = balance;
    }

    public Account add(double amount) {
        balance += amount;
        return this;
    }

    public Account subtract(double amount) {
        balance -= amount;
        return this;
    }

    public Account multiply(double amount) {
        balance *= amount;
        return this;
    }

    public Account divide(double amount) {
        balance /= amount;
        return this;
    }
}
