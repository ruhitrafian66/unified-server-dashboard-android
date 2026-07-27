package com.unifiedserver.dashboard.ui.screens;

import com.unifiedserver.dashboard.data.PreferencesManager;
import com.unifiedserver.dashboard.data.repository.DashboardRepository;
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
public final class SetupViewModel_Factory implements Factory<SetupViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<DashboardRepository> dashboardRepositoryProvider;

  public SetupViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<DashboardRepository> dashboardRepositoryProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.dashboardRepositoryProvider = dashboardRepositoryProvider;
  }

  @Override
  public SetupViewModel get() {
    return newInstance(preferencesManagerProvider.get(), dashboardRepositoryProvider.get());
  }

  public static SetupViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<DashboardRepository> dashboardRepositoryProvider) {
    return new SetupViewModel_Factory(preferencesManagerProvider, dashboardRepositoryProvider);
  }

  public static SetupViewModel newInstance(PreferencesManager preferencesManager,
      DashboardRepository dashboardRepository) {
    return new SetupViewModel(preferencesManager, dashboardRepository);
  }
}
