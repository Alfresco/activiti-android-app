/*
 *  Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco Activiti Mobile for Android.
 *
 * Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.activiti.android.sdk;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import com.activiti.android.Version;
import com.activiti.android.sdk.services.ServiceRegistry;
import com.activiti.android.utils.Base64;
import com.activiti.android.utils.Messagesl18n;
import com.activiti.client.api.constant.ISO8601Utils;
import com.activiti.client.api.constant.Server;
import com.activiti.client.api.model.editor.form.AmountFieldRepresentation;
import com.activiti.client.api.model.editor.form.ContainerRepresentation;
import com.activiti.client.api.model.editor.form.DynamicTableRepresentation;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.editor.form.FormFieldTypes;
import com.activiti.client.api.model.editor.form.HyperlinkRepresentation;
import com.activiti.client.api.model.editor.form.RestFieldRepresentation;
import com.activiti.client.api.model.idm.UserRepresentation;
import com.activiti.client.api.model.runtime.AppVersionRepresentation;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import io.gsonfire.GsonFireBuilder;
import io.gsonfire.TypeSelector;

/**
 * Created by jpascal on 17/03/2015.
 */
public class ActivitiSession
{
    protected static final Object LOCK = new Object();

    protected static ActivitiSession mInstance;

    protected RestManager restManager;

    protected ServiceRegistry registry;

    protected OkHttpClient okHttpClient;

    protected UserRepresentation userRepresentationProfile;

    protected AppVersionRepresentation version;

    protected boolean isActivitiAlfresco = false;

    private ActivitiSession(RestManager restManager, OkHttpClient okHttpClient)
    {
        this.restManager = restManager;
        this.okHttpClient = okHttpClient;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public static ActivitiSession getInstance()
    {
        synchronized (LOCK)
        {
            return mInstance;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS
    // ///////////////////////////////////////////////////////////////////////////
    public ServiceRegistry getServiceRegistry()
    {
        if (registry == null)
        {
            registry = new ServiceRegistry(restManager);
        }
        return registry;
    }

    public OkHttpClient getOkHttpClient()
    {
        return okHttpClient;
    }

    public void updateCredentials(final String username, final String password)
    {
        getOkHttpClient().interceptors().clear();
        okHttpClient.interceptors().add(new com.squareup.okhttp.Interceptor()
        {
            @Override
            public Response intercept(Chain chain) throws IOException
            {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", getBasicAuth(username, password)).build();
                return chain.proceed(newRequest);
            }
        });
    }

    public boolean isActivitiAlfresco()
    {
        if (restManager == null) { return false; }
        return Server.SERVER_URL_ENDPOINT.equals(restManager.endpoint);
    }

    private static String getBasicAuth(String username, String password)
    {
        // Prepare Basic AUTH
        if (username != null && password != null)
        {
            String credentials = username + ":" + password;
            return "Basic " + Base64.encodeBytes(credentials.getBytes());
        }
        throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "username"));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static class Builder
    {
        private String endpoint, username, password, auth;

        private OkHttpClient okHttpClient;

        public Builder connect(String endpoint, String username, String password)
        {
            this.endpoint = endpoint;
            this.username = username;
            this.password = password;
            return this;
        }

        public Builder connect(String endpoint)
        {
            this.endpoint = endpoint;
            return this;
        }

        public Builder okHttpClient(OkHttpClient okHttpClient)
        {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public ActivitiSession build()
        {
            // Check Parameters
            if (endpoint == null || endpoint.isEmpty()) { throw new IllegalArgumentException(String.format(
                    Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "url")); }

            // Prepare Basic AUTH
            if (username != null && password != null)
            {
                String credentials = username + ":" + password;
                auth = "Basic " + Base64.encodeBytes(credentials.getBytes());
            }

            // Prepare HTTP Layer
            if (okHttpClient == null)
            {
                okHttpClient = new OkHttpClient();
                ArrayList<Protocol> protocols = new ArrayList<>(1);
                protocols.add(Protocol.HTTP_1_1);
                okHttpClient.setProtocols(protocols);
                okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
                if (auth != null)
                {
                    okHttpClient.interceptors().add(new com.squareup.okhttp.Interceptor()
                    {
                        @Override
                        public Response intercept(Chain chain) throws IOException
                        {
                            Request newRequest = chain.request().newBuilder().addHeader("Authorization", auth)
                                    .removeHeader("User-Agent")
                                    .addHeader("User-Agent", "Activiti-Mobile/" + Version.SDK + " Android").build();
                            return chain.proceed(newRequest);
                        }
                    });
                }
            }

            // Prepare Retrofit
            GsonFireBuilder fireBuilder = new GsonFireBuilder().registerTypeSelector(FormFieldRepresentation.class,
                    new TypeSelector<FormFieldRepresentation>()
                    {
                        @Override
                        public Class<? extends FormFieldRepresentation> getClassForElement(JsonElement readElement)
                        {
                            try
                            {
                                String type = readElement.getAsJsonObject().get("type").getAsString();
                                if (type.equals(FormFieldTypes.HYPERLINK))
                                {
                                    return HyperlinkRepresentation.class;
                                }
                                else if (type.equals(FormFieldTypes.DYNAMIC_TABLE)) { return DynamicTableRepresentation.class; }
                                if (readElement.getAsJsonObject().get("fieldType") == null) { return null; }
                                String fieldType = readElement.getAsJsonObject().get("fieldType").getAsString();
                                if (fieldType.equals("HyperlinkRepresentation"))
                                {
                                    return HyperlinkRepresentation.class;
                                }
                                else if (fieldType.equals("DynamicTableRepresentation"))
                                {
                                    return DynamicTableRepresentation.class;
                                }
                                else if (fieldType.equals("RestFieldRepresentation"))
                                {
                                    return RestFieldRepresentation.class;
                                }
                                else if (fieldType.equals("AmountFieldRepresentation"))
                                {
                                    return AmountFieldRepresentation.class;
                                }
                                else if (fieldType.equals("ContainerRepresentation"))
                                {
                                    return ContainerRepresentation.class;
                                }
                                else
                                {
                                    return null;
                                }
                            }
                            catch (Exception e)
                            {
                                return null;
                            }
                        }
                    });

            Type mapStringObjectType = new TypeToken<Map<String, Object>>()
            {
            }.getType();

            Gson gson = fireBuilder.createGsonBuilder().setDateFormat(ISO8601Utils.DATE_ISO_FORMAT)
                    .registerTypeAdapter(mapStringObjectType, new RandomMapKeysAdapter()).create();

            RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(endpoint)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setClient(new OkClient(okHttpClient)).setConverter(new GsonConverter(gson));
            RestAdapter restAdapter = builder.build();

            mInstance = new ActivitiSession(new RestManager(endpoint, restAdapter), okHttpClient);
            return mInstance;
        }
    }

    public static class RandomMapKeysAdapter implements JsonDeserializer<Map<String, Object>>
    {
        @Override
        public Map<String, Object> deserialize(JsonElement json, Type unused, JsonDeserializationContext context)
                throws JsonParseException
        {
            // if not handling primitives, nulls and arrays, then just
            if (!json.isJsonObject()) throw new JsonParseException("some meaningful message");

            Map<String, Object> result = new HashMap<String, Object>();
            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
            {
                String key = entry.getKey();
                JsonElement element = entry.getValue();
                if (element.isJsonPrimitive())
                {
                    result.put(key, element.getAsString());
                }
                else if (element.isJsonObject())
                {
                    result.put(key, context.deserialize(element, unused));
                }
                // if not handling nulls and arrays
                else
                {
                    // Do nothing
                }
            }
            return result;
        }
    }
}
