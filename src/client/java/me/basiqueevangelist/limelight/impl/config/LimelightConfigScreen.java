// Basically stolen from https://github.com/wisp-forest/owo-whats-this/blob/master/src/main/java/io/wispforest/owowhatsthis/client/OwoWhatsThisConfigScreen.java#L37

package me.basiqueevangelist.limelight.impl.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.OptionComponentFactory;
import io.wispforest.owo.config.ui.component.ConfigEnumButton;
import io.wispforest.owo.config.ui.component.ConfigToggleButton;
import io.wispforest.owo.config.ui.component.OptionValueProvider;
import io.wispforest.owo.config.ui.component.SearchAnchorComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import me.basiqueevangelist.limelight.api.module.LimelightModules;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LimelightConfigScreen extends ConfigScreen {
    public LimelightConfigScreen(@Nullable Screen parent) {
        super(DEFAULT_MODEL_ID, Limelight.CONFIG, parent);

        this.extraFactories.put(option -> option.backingField().field().getName().equals("enabledModules"), ENABLED_MODULES_OPTION);
    }

    private static final OptionComponentFactory<Map<Identifier, Boolean>> ENABLED_MODULES_OPTION = (model, option) -> {
        var container = new ProviderConfigContainer(option);
        return new OptionComponentFactory.Result<>(container, container);
    };

    private static class ProviderConfigContainer extends FlowLayout implements OptionValueProvider {

        protected final Map<Identifier, Boolean> backingMap;

        protected ProviderConfigContainer(Option<Map<Identifier, Boolean>> option) {
            super(Sizing.fill(100), Sizing.content(), Algorithm.VERTICAL);
            this.backingMap = new HashMap<>(option.value());

            var layout = Containers.verticalFlow(Sizing.content(), Sizing.content());

            var modules = LimelightModules.allModules();

            var optionGrid = Containers.grid(Sizing.fill(100), Sizing.content(), MathHelper.ceilDiv(modules.size(), 2), 2);
            layout.child(optionGrid);

            for (int i = 0; i < modules.size(); i++) {
                var module = modules.get(i);

                optionGrid.child(
                    Containers.horizontalFlow(Sizing.fill(50), Sizing.fixed(30)).<FlowLayout>configure(optionLayout -> {
                        optionLayout.padding(Insets.of(5)).verticalAlignment(VerticalAlignment.CENTER);

                        optionLayout.child(new SearchAnchorComponent(optionLayout, Option.Key.ROOT, module.name()::getString));
                        optionLayout.child(Components.label(module.name())
                                .tooltip(module.tooltip()))
                            .child(new ModuleConfigButton().<ModuleConfigButton>configure(button -> {
                                button.enabled(this.backingMap.getOrDefault(module.id(), true));
                                button.onChanged(state -> {
                                    this.backingMap.put(module.id(), state);
                                });
                                button.renderer(ButtonComponent.Renderer.flat(0, 0x77000000, 0));
                                button.positioning(Positioning.relative(100, 50));
                                button.margins(Insets.right(5)).sizing(Sizing.fixed(25), Sizing.fixed(15));
                            }));
                    }), i / 2, i % 2
                );
            }

            this.child(layout);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Object parsedValue() {
            return this.backingMap;
        }
    }
}
