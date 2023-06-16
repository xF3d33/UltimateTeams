package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.scheduling.GracefulScheduling;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public interface TaskRunner {
    UltimateTeams plugin = UltimateTeams.getPlugin();

    default void runAsync(@NotNull Runnable runnable) {
        final int taskId = getTasks().size();
        getTasks().put(taskId, getScheduler().asyncScheduler().run(runnable));
    }

    default <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> supplier) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        getScheduler().asyncScheduler().run(() -> future.complete(supplier.get()));
        return future;
    }

    default int runAsyncRepeating(@NotNull Runnable runnable, long period) {
        final int taskId = getNextTaskId();
        getScheduler().asyncScheduler().runAtFixedRate(runnable, Duration.ZERO, getDurationTicks(period));
        return taskId;
    }

    default void runSyncRepeating(@NotNull Runnable runnable, long period) {
        getScheduler().globalRegionalScheduler().runAtFixedRate(runnable, 0, period);
    }

    default void runSync(@NotNull Runnable runnable) {
        getScheduler().globalRegionalScheduler().run(runnable);
    }

    @NotNull
    GracefulScheduling getScheduler();

    @NotNull
    ConcurrentHashMap<Integer, ScheduledTask> getTasks();

    default int getNextTaskId() {
        int taskId = 0;
        while (getTasks().containsKey(taskId)) {
            taskId++;
        }
        return taskId;
    }

    default void cancelTask(int taskId) {
        if (getTasks().containsKey(taskId)) {
            getTasks().get(taskId).cancel();
            getTasks().remove(taskId);
        }
    }

    default void cancelAllTasks() {
        getScheduler().cancelGlobalTasks();
        getTasks().values().forEach(ScheduledTask::cancel);
        getTasks().clear();
    }

    @NotNull
    default Duration getDurationTicks(long ticks) {
        return Duration.of(ticks * 50, ChronoUnit.MILLIS);
    }
}
