package io.wispforest.limelight.impl;

import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.api.extension.LimelightExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public final class ResultGatherer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/ResultGatherer");

    private ResultGatherer() {

    }

    public static void findResults(ResultGatherContext ctx, Consumer<ResultEntry> resultConsumer) {
        for (var extension : LimelightExtensions.enabledExtensions()) {
            try {
                var gatherer = extension.checkExclusiveGatherer(ctx);

                if (gatherer != null) {
                    try {
                        gatherer.gatherEntries(ctx, resultConsumer);
                    } catch (Exception e) {
                        LOGGER.error("`{}`'s exclusive result gatherer failed", extension.id(), e);
                    }

                    return;
                }
            } catch (Exception e) {
                LOGGER.error("`{}`'s checkExclusiveGatherer method failed", extension.id(), e);
            }
        }

        for (var extension : LimelightExtensions.enabledExtensions()) {
            try {
                extension.gatherEntries(ctx, resultConsumer);
            } catch (Exception e) {
                LOGGER.error("`{}`'s result gatherer failed", extension.id(), e);
            }
        }
    }
}
