package com.unifiedserver.dashboard.ui.screens;

import com.unifiedserver.dashboard.data.repository.SystemRepository;
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
public final class SystemViewModel_Factory implements Factory<SystemViewModel> {
  private final Provider<SystemRepository> repositoryProvider;

  public SystemViewModel_Factory(Provider<SystemRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SystemViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static SystemViewModel_Factory create(Provider<SystemRepository> repositoryProvider) {
    return new SystemViewModel_Factory(repositoryProvider);
  }

  public static SystemViewModel newInstance(SystemRepository repository) {
    return new SystemViewModel(repository);
  }
}
