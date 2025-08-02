package top.shenjack.mixin;

import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.shenjack.MallocRand;
import top.shenjack.WeirdoWorldGen;


@Mixin(ChunkNoiseSampler.class)
public abstract class MixinNoiseSampler {
    @Unique
    private MallocRand random;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.random = new MallocRand();
    }

    @ModifyArg(method = "method_40530", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/AquiferSampler;apply(Lnet/minecraft/world/gen/densityfunction/DensityFunction$NoisePos;D)Lnet/minecraft/block/BlockState;"))
    private double mallocSample(DensityFunction.NoisePos pos, double sample) {
        if (((pos.blockZ() & 15) != 0 || WeirdoWorldGen.fullyMalloc) && !(pos.blockY() > 200)) {
            int x = pos.blockX();
            int y = pos.blockY();
            int z = pos.blockZ();
            if (this.random.sample_int_in_range_pos(x, y + 1, z, 3) != 0) {
                return 0;
            }
            return this.random.sample() * 2.0D - 1.0D;
        }
        return sample;
    }
}
