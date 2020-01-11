/*
 * PROJECT LICENSE
 * This project was submitted by Iva Ivanova as part of the Nanodegree at Udacity.
 *
 * According to Udacity Honor Code we agree that we will not plagiarize (a form of cheating) the work of others. :
 * Plagiarism at Udacity can range from submitting a project you didn’t create to copying code into a program without
 * citation. Any action in which you misleadingly claim an idea or piece of work as your own when it is not constitutes
 * plagiarism.
 * Read more here: https://udacity.zendesk.com/hc/en-us/articles/360001451091-What-is-plagiarism-
 *
 * MIT License
 *
 * Copyright (c) 2018 Iva Ivanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package dev.iotarho.artplace.app.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dev.iotarho.artplace.app.model.CustomArtsyDeserializer;
import dev.iotarho.artplace.app.model.artists.CustomArtistsDeserializer;
import dev.iotarho.artplace.app.model.artists.EmbeddedArtists;
import dev.iotarho.artplace.app.model.artworks.ArtworkWrapperResponse;
import dev.iotarho.artplace.app.model.artworks.CustomArtworksDeserializer;
import dev.iotarho.artplace.app.model.artworks.EmbeddedArtworks;
import dev.iotarho.artplace.app.remote.authentication.TokenAuthenticator;
import dev.iotarho.artplace.app.utils.PreferenceUtils;
import dev.iotarho.artplace.app.utils.TokenManager;
import dev.iotarho.artplace.app.utils.Utils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// TODO: Fix this class! It's too messy now!
public class ArtsyApiManager {

    private final static OkHttpClient sClient = buildClient();
    private final static Retrofit sRetrofit = buildRetrofit(sClient);
    private PreferenceUtils mPreferenceUtils;

    private static final String TAG = ArtsyApiManager.class.getSimpleName();


    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        return builder.build();
    }

    /**
     * First Retrofit object
     * TODO: Figure out if I can use only one object
     * @return
     */
    private static Retrofit buildRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Utils.BASE_ARTSY_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Create service for fetching the token
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T createService(Class<T> service) {
        return sRetrofit.create(service);
    }

    // Provide the Retrofit call
    public static <T> T createApiCall(Class<T> service, final TokenManager tokenManager, final PreferenceUtils preferenceUtils) {

        // For logging the url that is being made
        HttpLoggingInterceptor logsInterceptor = new HttpLoggingInterceptor();
        logsInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient newClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest;

                    // The token needs to be saved first before it can be fetched from shared prefs
                    String tokenString = preferenceUtils.getToken();
                    // If there si no token saved in shared preferences, that means the app is
                    // launched for the first time and we need to fetch the token
                    if (tokenString == null || tokenString.equals("")) {
                        Log.d(TAG, "Token in intercept is null or empty");
                        // The token should be fetch here only the first time, after that the
                        // Authenticator will handle refreshing it
                        tokenString = tokenManager.fetchTokenFirstTime();

                        Log.d(TAG, "Token fetched for the first time: " + tokenString);
                    }

                    newRequest = chain
                            .request()
                            .newBuilder()
                            // Temp way of hard-coding the token
                            //.addHeader("X-XAPP-Token", token)
                            .addHeader(Utils.HEADER_TOKEN_KEY, tokenString)
                            .build();
                    Log.d(TAG, "Token in intercept is: " + tokenString);

                    return chain.proceed(newRequest);
                })
                // Add the authenticator where the refreshed token is being fetched
                .authenticator(TokenAuthenticator.getInstance())
                .addInterceptor(logsInterceptor)
                .build();

        Retrofit newRetrofit = sRetrofit
                .newBuilder()
                .client(newClient)
                .build();

        return newRetrofit.create(service);
    }

    /**
     * Second Retrofit object
     * @param newClient
     * @return
     *//*
    @NonNull
    private static Retrofit getRetrofit(OkHttpClient newClient) {
        return new Retrofit.Builder()
                .baseUrl(Utils.BASE_ARTSY_URL)
                .client(newClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }*/

    /*
    Register the TypeAdapter here for deserializing the model
     */
    private static Gson makeGson() {

        // Register two different TypeAdapters!
        // resource: https://stackoverflow.com/a/33459073/8132331
        return new GsonBuilder()
                .registerTypeAdapter(ArtworkWrapperResponse.class, new CustomArtsyDeserializer())
                .registerTypeAdapter(EmbeddedArtworks.class, new CustomArtworksDeserializer())
                .registerTypeAdapter(EmbeddedArtists.class, new CustomArtistsDeserializer())
                .create();
    }

}
