package me.roundaround.custompaintings.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
  @Accessor("random")
  Random getRandom();
}
