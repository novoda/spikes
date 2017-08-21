package com.novoda.enews;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (type instanceof Campaign) {
            return new CampaignConverter();
        } else if (type instanceof CampaignContent) {
            return new CampaignContentConverter();
        } else {
            return null;
        }
    }

    private static class CampaignConverter implements Converter<Campaign, RequestBody> {

        @Override
        public RequestBody convert(Campaign campaign) throws IOException {
            return null;
        }
    }

    private static class CampaignContentConverter implements Converter<CampaignContent, RequestBody> {

        @Override
        public RequestBody convert(CampaignContent value) throws IOException {
            return null;
        }
    }

}
