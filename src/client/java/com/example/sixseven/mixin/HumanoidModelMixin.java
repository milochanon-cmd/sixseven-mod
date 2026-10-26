package com.example.sixseven.mixin;

import com.example.sixseven.SixSevenClient;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
            at = @At("TAIL"))
    private void sixseven$pose(HumanoidRenderState state, CallbackInfo ci) {
        if (!SixSevenClient.ACTIVE.containsKey(state.id)) return;

        float wave = (float) Math.sin(state.ageInTicks * 1.6F) * 0.55F;
        float base = -1.35F;
        this.rightArm.xRot = base + wave;
        this.leftArm.xRot  = base - wave;
        this.rightArm.yRot = -0.15F;
        this.leftArm.yRot  = 0.15F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot  = 0.0F;
    }
}
