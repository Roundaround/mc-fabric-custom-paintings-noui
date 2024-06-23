package me.roundaround.custompaintings.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Arrays;

@FunctionalInterface
public interface AfterClientInit {
  void handle();

  Event<AfterClientInit> EVENT = EventFactory.createArrayBacked(AfterClientInit.class,
      (listeners) -> () -> Arrays.stream(listeners).forEach(AfterClientInit::handle)
  );
}
