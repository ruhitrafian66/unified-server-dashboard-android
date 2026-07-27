package com.unifiedserver.dashboard.di;

import com.unifiedserver.dashboard.data.ApiService;
import com.unifiedserver.dashboard.data.PreferencesManager;
import com.unifiedserver.dashboard.data.repository.SystemRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AppModule_ProvideSystemRepositoryFactory implements Factory<SystemRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AppModule_ProvideSystemRepositoryFactory(Provider<ApiService> apiServiceProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public SystemRepository get() {
    return provideSystemRepository(apiServiceProvider.get(), preferencesManagerProvider.get());
  }

  public static AppModule_ProvideSystemRepositoryFactory create(
      Provider<ApiService> apiServiceProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new AppModule_ProvideSystemRepositoryFactory(apiServiceProvider, preferencesManagerProvider);
  }

  public static SystemRepository provideSystemRepository(ApiService apiService,
      PreferencesManager preferencesManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSystemRepository(apiService, preferencesManager));
  }
}
