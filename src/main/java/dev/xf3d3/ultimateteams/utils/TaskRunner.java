package dev.xf3d3.ultimateteams.utils;

import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.xf3d3.ultimateteams.UltimateTeams;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public interface TaskRunner {
    UltimateTeams plugin = UltimateTeams.getPlugin();

    default void runAsync(@NotNull Consumer<WrappedTask> runnable) {
        getScheduler().runAsync(runnable);
    }

    /*default <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> supplier) {
        final CompletableFuture<T> future = new CompletableFuture<>();

        getScheduler().runAsync(() -> future.complete(supplier.get()));
        return future;
    }



    default int runAsyncRepeating(@NotNull Runnable runnable, long period) {
        final int taskId = getNextTaskId();
        getScheduler().asyncScheduler().runAtFixedRate(runnable, Duration.ZERO, getDurationTicks(period));
        return taskId;
    } */

    default void runLater(@NotNull Runnable runnable, long delay) {
        getScheduler().runLater(runnable, delay * 20);
    }

    default void runSync(@NotNull Consumer<WrappedTask> runnable) {
        getScheduler().runNextTick(runnable);
    }

    default void runSyncRepeating(@NotNull Runnable runnable, long period) {
        getScheduler().runTimer(runnable, 0, period);
    }

    @NotNull
    PlatformScheduler getScheduler();



    @NotNull
    default Duration getDurationTicks(long ticks) {
        return Duration.of(ticks * 50, ChronoUnit.MILLIS);
    }
}
