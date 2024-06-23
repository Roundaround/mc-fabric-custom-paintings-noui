package me.roundaround.custompaintings.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Arrays;

@FunctionalInterface
public interface ClientClose {
  void handle();

  Event<ClientClose> EVENT = EventFactory.createArrayBacked(ClientClose.class,
      (listeners) -> () -> Arrays.stream(listeners).forEach(ClientClose::handle)
  );
}
