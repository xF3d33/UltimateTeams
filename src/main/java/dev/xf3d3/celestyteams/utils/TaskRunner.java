package dev.xf3d3.celestyteams.utils;

import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.scheduling.GracefulScheduling;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public interface TaskRunner {

    /*Logger logger = CelestyTeams.getPlugin().getLogger();
    static FoliaLib foliaLib = new FoliaLib(CelestyTeams.getPlugin());

    WrappedTask task1 = null;
    WrappedTask task2 = null;
    WrappedTask task3 = null;
    WrappedTask task4 = null;*/

    default void runAsync(@NotNull Runnable runnable) {
        final int taskId = getTasks().size();
        getTasks().put(taskId, getScheduler().asyncScheduler().run(runnable));
    }

    default <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> supplier) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        getScheduler().asyncScheduler().run(() -> future.complete(supplier.get()));
        return future;
    }

    default void runSync(@NotNull Runnable runnable) {
        getScheduler().globalRegionalScheduler().run(runnable);
    }

    @NotNull
    GracefulScheduling getScheduler();

    @NotNull
    ConcurrentHashMap<Integer, ScheduledTask> getTasks();

    @NotNull
    default Duration getDurationTicks(long ticks) {
        return Duration.of(ticks * 50, ChronoUnit.MILLIS);
    }


    /*public static void runClansAutoSaveOne(){
        task1 = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 900;
            @Override
            public void run() {
                if (time == 1){
                    try {
                        ClansStorageUtil.saveClans();
                        if (CelestyTeams.getPlugin().getConfig().getBoolean("general.show-auto-save-task-message.enabled")){
                            logger.info(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-save-complete")));
                        }
                    } catch (IOException e) {
                        logger.severe(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-save-failed")));
                        e.printStackTrace();
                    }
                    runClansAutoSaveTwo();
                    task1.cancel();
                    return;
                }
                else {
                    time --;
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public static void runClansAutoSaveTwo(){
        task2 = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 900;
            @Override
            public void run() {
                if (time == 1){
                    try {
                        ClansStorageUtil.saveClans();
                        if (CelestyTeams.getPlugin().getConfig().getBoolean("general.show-auto-save-task-message.enabled")){
                            logger.info(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-save-complete")));
                        }
                    } catch (IOException e) {
                        logger.severe(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-save-failed")));
                        e.printStackTrace();
                    }
                    runClansAutoSaveOne();
                    task2.cancel();
                    return;
                }
                else {
                    time --;
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public static void runClanInviteClearOne(){
        task3 = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 900;
            @Override
            public void run() {
                if (time == 1){
                    try {
                        ClanInviteUtil.emptyInviteList();
                        if (CelestyTeams.getPlugin().getConfig().getBoolean("general.show-auto-invite-wipe-message.enabled")){
                            logger.info(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-complete")));
                        }
                    }catch (UnsupportedOperationException exception){
                        logger.info(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("invite-wipe-failed")));
                    }
                    runClanInviteClearTwo();
                    task3.cancel();
                    return;
                }else {
                    time --;
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public static void runClanInviteClearTwo(){
        task4 = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 900;
            @Override
            public void run() {
                if (time == 1){
                    try {
                        ClanInviteUtil.emptyInviteList();
                        if (CelestyTeams.getPlugin().getConfig().getBoolean("general.show-auto-invite-wipe-message.enabled")){
                            logger.info(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-complete")));
                        }
                    }catch (UnsupportedOperationException exception){
                        logger.info(ColorUtils.translateColorCodes(CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig().getString("invite-wipe-failed")));
                    }
                    runClanInviteClearOne();
                    task4.cancel();
                    return;
                }else {
                    time --;
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }*/
}
