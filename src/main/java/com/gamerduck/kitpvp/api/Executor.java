package com.gamerduck.kitpvp.api;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;

public class Executor {
	public static void runAsync(Runnable run) {
    	MinecraftServer.getSchedulerManager().buildTask(run).executionType(ExecutionType.ASYNC).schedule();
	}
	
	public static void runAsyncLater(Runnable run, Duration delay) {
    	MinecraftServer.getSchedulerManager().buildTask(run).executionType(ExecutionType.ASYNC).delay(delay).schedule();
	}
	
	public static void runAsyncRepeating(Runnable run, Duration delay, Duration repeat) {
    	MinecraftServer.getSchedulerManager().buildTask(run).executionType(ExecutionType.ASYNC).delay(delay).repeat(repeat).schedule();
	}
	public static void runAsyncLater(Runnable run, int delay) {
		runAsyncLater(run, Duration.of(delay, TimeUnit.SECOND));
	}
	
	public static void runAsyncRepeating(Runnable run, int delay, int repeat) {
		runAsyncRepeating(run, Duration.of(delay, TimeUnit.SECOND), Duration.of(repeat, TimeUnit.SECOND));
	}
	
	public static void runSync(Runnable run) {
    	MinecraftServer.getSchedulerManager().buildTask(run).executionType(ExecutionType.SYNC).schedule();
	}
	
	public static void runSyncLater(Runnable run, Duration delay) {
    	MinecraftServer.getSchedulerManager().buildTask(run).executionType(ExecutionType.SYNC).delay(delay).schedule();
	}
	
	public static void runSyncRepeating(Runnable run, Duration delay, Duration repeat) {
    	MinecraftServer.getSchedulerManager().buildTask(run).executionType(ExecutionType.SYNC).delay(delay).repeat(repeat).schedule();
	}
	
	public static void runSyncLater(Runnable run, int delay) {
		runSyncLater(run, Duration.of(delay, TimeUnit.SECOND));
	}
	
	public static void runSyncRepeating(Runnable run, int delay, int repeat) {
		runSyncRepeating(run, Duration.of(delay, TimeUnit.SECOND), Duration.of(repeat, TimeUnit.SECOND));
	}
}
