package me.roundaround.custompaintings.mixin;

import me.roundaround.custompaintings.client.event.ClientClose;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Inject(
      method = "close",
      at = @At(value = "INVOKE", target = "net/minecraft/client/resource/PeriodicNotificationManager.close()V")
  )
  private void close(CallbackInfo info) {
    ClientClose.EVENT.invoker().handle();
  }
}
