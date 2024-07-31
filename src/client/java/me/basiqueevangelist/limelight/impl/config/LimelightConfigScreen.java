package me.basiqueevangelist.limelight.impl.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LimelightConfigScreen {

    public static Screen generateScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("config.limelight.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("config.limelight.general"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("config.limelight.general.pauseGameWhileInScreen"))
                    .binding(false, () -> Limelight.CONFIG.get().pauseGameWhileInScreen, value -> Limelight.CONFIG.get().pauseGameWhileInScreen = value)
                    .controller(TickBoxControllerBuilderImpl::new)
                    .build())
                .build())
            .save(Limelight.CONFIG::save)
            .build()
            .generateScreen(parent);
    }
}
