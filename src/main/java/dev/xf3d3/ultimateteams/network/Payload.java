package dev.xf3d3.ultimateteams.network;

import com.google.gson.annotations.Expose;
import dev.xf3d3.ultimateteams.models.TeamInvite;
import dev.xf3d3.ultimateteams.models.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Payload {
    @Nullable
    @Expose
    private UUID uuid;

    @Nullable
    @Expose
    private Integer integer;

    @Nullable
    @Expose
    private TeamInvite invite;

    @Nullable
    @Expose
    private Boolean bool;

    @Nullable
    @Expose
    private String string;

    @Nullable
    @Expose
    private List<User> userList;

    private Payload() {
    }

    @NotNull
    public static Payload uuid(@NotNull UUID uuid) {
        final Payload payload = new Payload();
        payload.uuid = uuid;
        return payload;
    }

    @NotNull
    public static Payload integer(int integer) {
        final Payload payload = new Payload();
        payload.integer = integer;
        return payload;
    }

    @NotNull
    public static Payload invite(@NotNull TeamInvite invite) {
        final Payload payload = new Payload();
        payload.invite = invite;
        return payload;
    }

    @NotNull
    public static Payload empty() {
        return new Payload();
    }

    public static Payload bool(boolean bool) {
        final Payload payload = new Payload();
        payload.bool = bool;
        return payload;
    }

    @NotNull
    public static Payload string(@NotNull String string) {
        final Payload payload = new Payload();
        payload.string = string;
        return payload;
    }

    @NotNull
    public static Payload userList(@NotNull List<User> list) {
        final Payload payload = new Payload();
        payload.userList = list;
        return payload;
    }

    public Optional<UUID> getUuid() {
        return Optional.ofNullable(uuid);
    }

    public Optional<Integer> getInteger() {
        return Optional.ofNullable(integer);
    }

    public Optional<TeamInvite> getInvite() {
        return Optional.ofNullable(invite);
    }


    public Optional<Boolean> getBool() {
        return Optional.ofNullable(bool);
    }

    public Optional<String> getString() {
        return Optional.ofNullable(string);
    }

    public Optional<List<User>> getUserList() {
        return Optional.ofNullable(userList);
    }

}
