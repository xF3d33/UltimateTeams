package dev.xf3d3.ultimateteams.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dev.xf3d3.ultimateteams.models.Preferences;
import dev.xf3d3.ultimateteams.network.Message;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public interface GsonUtils {

	default Gson getGson() {
		final Type OFFSET_DATE_TIME_TYPE = new TypeToken<OffsetDateTime>(){}.getType();

        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(OFFSET_DATE_TIME_TYPE, new OffsetDateTimeConverter())
            .create();
	}

	@NotNull
	default Message getMessageFromJson(@NotNull String json) throws JsonSyntaxException {
		return getGson().fromJson(json, Message.class);
	}

	@NotNull
	default Preferences getPreferencesFromJson(@NotNull String json) throws JsonSyntaxException {
		return getGson().fromJson(json, Preferences.class);
	}

}