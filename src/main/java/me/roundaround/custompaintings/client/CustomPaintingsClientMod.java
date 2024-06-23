package me.roundaround.custompaintings.client;

import me.roundaround.custompaintings.client.event.AfterClientInit;
import me.roundaround.custompaintings.client.event.ClientClose;
import me.roundaround.custompaintings.client.network.ClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

public class CustomPaintingsClientMod implements ClientModInitializer {
  public static CustomPaintingManager customPaintingManager;

  @Override
  public void onInitializeClient() {
    ClientNetworking.registerS2CHandlers();

    AfterClientInit.EVENT.register(() -> {
      if (customPaintingManager != null) {
        customPaintingManager.close();
      }

      MinecraftClient client = MinecraftClient.getInstance();
      customPaintingManager = new CustomPaintingManager(client.getTextureManager());
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(customPaintingManager);
    });

    ClientClose.EVENT.register(() -> {
      if (customPaintingManager == null) {
        return;
      }
      customPaintingManager.close();
      customPaintingManager = null;
    });

    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
      customPaintingManager.sendKnownPaintingsToServer();
    });
  }
}
