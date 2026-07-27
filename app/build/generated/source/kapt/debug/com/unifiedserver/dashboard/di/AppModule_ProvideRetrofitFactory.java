package com.unifiedserver.dashboard.di;

import com.unifiedserver.dashboard.data.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AppModule_ProvideRetrofitFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public Retrofit get() {
    return provideRetrofit(okHttpClientProvider.get(), preferencesManagerProvider.get());
  }

  public static AppModule_ProvideRetrofitFactory create(Provider<OkHttpClient> okHttpClientProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new AppModule_ProvideRetrofitFactory(okHttpClientProvider, preferencesManagerProvider);
  }

  public static Retrofit provideRetrofit(OkHttpClient okHttpClient,
      PreferencesManager preferencesManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRetrofit(okHttpClient, preferencesManager));
  }
}
