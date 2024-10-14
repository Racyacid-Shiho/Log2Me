package cn.racyacid.log2me.mixin;

import cn.racyacid.log2me.logging.appender.ChatAppender;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void doNotLog(ChatHudLine message, CallbackInfo ci) {
        if (message.content().getString().contains(ChatAppender.LOG_IDENTIFIER)) ci.cancel();
    }
}