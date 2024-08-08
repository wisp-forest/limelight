package me.basiqueevangelist.limelight.api.builtin;

import me.basiqueevangelist.limelight.api.entry.ResultEntryGatherer;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public interface BangsProvider {
    List<Bang> bangs();

    record Bang(String key, Text title, ResultEntryGatherer gatherer) {

    }
}
