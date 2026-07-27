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
public final class DashboardRepository_Factory implements Factory<DashboardRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public DashboardRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public DashboardRepository get() {
    return newInstance(apiServiceProvider.get(), preferencesManagerProvider.get());
  }

  public static DashboardRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new DashboardRepository_Factory(apiServiceProvider, preferencesManagerProvider);
  }

  public static DashboardRepository newInstance(ApiService apiService,
      PreferencesManager preferencesManager) {
    return new DashboardRepository(apiService, preferencesManager);
  }
}
