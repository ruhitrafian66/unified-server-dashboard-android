package com.unifiedserver.dashboard.data.repository;

import com.unifiedserver.dashboard.data.ApiService;
import com.unifiedserver.dashboard.data.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class TorrentRepository_Factory implements Factory<TorrentRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public TorrentRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public TorrentRepository get() {
    return newInstance(apiServiceProvider.get(), preferencesManagerProvider.get());
  }

  public static TorrentRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new TorrentRepository_Factory(apiServiceProvider, preferencesManagerProvider);
  }

  public static TorrentRepository newInstance(ApiService apiService,
      PreferencesManager preferencesManager) {
    return new TorrentRepository(apiService, preferencesManager);
  }
}
