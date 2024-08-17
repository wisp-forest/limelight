package me.basiqueevangelist.limelight.impl.util;

import com.google.common.collect.ForwardingList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeastRecentlyUsedList<T> extends ForwardingList<T> {
    private final List<T> backingList;
    private final List<T> backingListView;
    private final int capacity;

    public LeastRecentlyUsedList(int capacity) {
        this.capacity = capacity;
        this.backingList = new ArrayList<>(capacity);
        this.backingListView = Collections.unmodifiableList(backingList);
    }

    @Override
    protected @NotNull List<T> delegate() {
        return backingListView;
    }

    public int capacity() {
        return capacity;
    }

    public void bump(T element) {
        if (!backingList.isEmpty() && backingList.get(0).equals(element)) return;

        backingList.remove(element);

        while (backingList.size() >= capacity) backingList.remove(backingList.size() - 1);

        backingList.add(0, element);
    }
}
