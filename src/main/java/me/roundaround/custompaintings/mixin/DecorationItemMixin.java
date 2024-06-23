package me.roundaround.custompaintings.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.roundaround.custompaintings.CustomPaintingsMod;
import me.roundaround.custompaintings.server.ServerPaintingManager;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(DecorationItem.class)
public abstract class DecorationItemMixin {
  @WrapOperation(
      method = "useOnBlock",
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/entity/decoration/painting/PaintingEntity.placePainting(Lnet/minecraft/world/World;" +
              "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Ljava/util/Optional;"
      )
  )
  private Optional<PaintingEntity> placePainting(
      World world,
      BlockPos pos,
      Direction facing,
      Operation<Optional<PaintingEntity>> original,
      @Local(argsOnly = true) LocalRef<ItemUsageContext> contextRef
  ) {
    PlayerEntity basePlayer = contextRef.get().getPlayer();
    if (!(basePlayer instanceof ServerPlayerEntity player) ||
        !CustomPaintingsMod.knownPaintings.containsKey(basePlayer.getUuid())) {
      return original.call(world, pos, facing);
    }

    return ServerPaintingManager.placePainting(player, world, pos, facing);
  }
}
