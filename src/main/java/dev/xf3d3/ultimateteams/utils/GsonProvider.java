package dev.xf3d3.ultimateteams.utils;


import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.network.objects.Message;
import org.jetbrains.annotations.NotNull;

public interface GsonProvider {

    @NotNull
    default GsonBuilder getGsonBuilder() {
        return Converters.registerOffsetDateTime(new GsonBuilder().excludeFieldsWithoutExposeAnnotation());
    }

    @NotNull
    default Gson getGson() {
        return getGsonBuilder().create();
    }

    @NotNull
    default Team getTeamFromJson(@NotNull String json) throws JsonSyntaxException {
        return getGson().fromJson(json, Team.class);
    }

    @NotNull
    default Message getMessageFromJson(@NotNull String json) throws JsonSyntaxException {
        return getGson().fromJson(json, Message.class);
    }


}
