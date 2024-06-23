package me.roundaround.custompaintings.mixin;

import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PaintingEntity.class)
public interface PaintingEntityAccessor {
  @Invoker("setVariant")
  void invokeSetVariant(RegistryEntry<PaintingVariant> variant);
}
