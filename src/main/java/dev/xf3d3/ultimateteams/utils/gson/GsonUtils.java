package dev.xf3d3.ultimateteams.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public class GsonUtils {

	public static Gson getGson() {
		final Type OFFSET_DATE_TIME_TYPE = new TypeToken<OffsetDateTime>(){}.getType();

        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(OFFSET_DATE_TIME_TYPE, new OffsetDateTimeConverter())
            .create();
	}

}