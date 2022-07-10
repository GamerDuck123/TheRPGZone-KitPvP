package com.gamerduck.kitpvp.api.listeners.impl;

import com.gamerduck.kitpvp.api.listeners.EventHandler;
import com.gamerduck.kitpvp.api.listeners.Listener;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventsManager {
	private static final EventNode<Event> NODE = EventNode.all("mainnode");
	@SuppressWarnings("unchecked")
	public void registerListener(Listener listen) {
		for (Method method : listen.getClass().getMethods()) {
			if (method.isAnnotationPresent(EventHandler.class)
					&& method.getParameterCount() == 1 
					&& Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
				method.setAccessible(true);
				MinecraftServer.getGlobalEventHandler().addListener(eventClass, e -> {
					try {method.invoke(listen, e);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					e1.printStackTrace();
					}
				});
			}
		}
	}
}
