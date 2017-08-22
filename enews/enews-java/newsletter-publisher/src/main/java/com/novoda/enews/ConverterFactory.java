package com.novoda.enews;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

class ConverterFactory extends Converter.Factory {

    private final Converter.Factory chainedConverter = GsonConverterFactory.create();

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return chainedConverter.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (type.getTypeName().equals(CampaignSettings.class.getCanonicalName())) {
            return new CampaignConverter();
        } else if (type.getTypeName().equals(CampaignContent.class.getCanonicalName())) {
            return new CampaignContentConverter();
        } else if (type.getTypeName().equals(CampaignSchedule.class.getCanonicalName())) {
            return new CampaignScheduleConverter();
        } else {
            return chainedConverter.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        }
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return chainedConverter.stringConverter(type, annotations, retrofit);
    }

    private static class CampaignConverter implements Converter<CampaignSettings, RequestBody> {

        @Override
        public RequestBody convert(CampaignSettings campaign) throws IOException {
            JsonObject obj = new JsonObject();
            obj.add("type", new JsonPrimitive("regular"));
            JsonObject recipients = new JsonObject();
            recipients.add("list_id", new JsonPrimitive(campaign.getListId()));
            obj.add("recipients", recipients);
            JsonObject settings = new JsonObject();
            settings.add("subject_line", new JsonPrimitive(campaign.getSubjectLine()));
            settings.add("from_name", new JsonPrimitive(campaign.getFromName()));
            settings.add("reply_to", new JsonPrimitive(campaign.getReplyToEmail()));
            settings.add("auto_footer", new JsonPrimitive(true));
            settings.add("inline_css", new JsonPrimitive(true));
            obj.add("settings", settings);
            return RequestBody.create(MediaType.parse("application/json"), obj.getAsJsonObject().toString());
        }
    }

    private static class CampaignContentConverter implements Converter<CampaignContent, RequestBody> {

        @Override
        public RequestBody convert(CampaignContent value) throws IOException {
            JsonObject obj = new JsonObject();
            obj.add("html", new JsonPrimitive(value.getHtml()));
            return RequestBody.create(MediaType.parse("application/json"), obj.getAsJsonObject().toString());
        }
    }

    private static class CampaignScheduleConverter implements Converter<CampaignSchedule, RequestBody> {

        @Override
        public RequestBody convert(CampaignSchedule value) throws IOException {
            JsonObject obj = new JsonObject();
            obj.add("schedule_time", new JsonPrimitive(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00+00:00").format(value.getScheduleDateTime())));
            obj.add("timewarp", new JsonPrimitive(value.isScheduleAsTimezoneLocal()));
            return RequestBody.create(MediaType.parse("application/json"), obj.getAsJsonObject().toString());
        }
    }

}
