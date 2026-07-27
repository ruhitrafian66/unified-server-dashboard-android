package com.unifiedserver.dashboard.ui.screens;

import com.unifiedserver.dashboard.data.repository.ShowsRepository;
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
public final class ShowsViewModel_Factory implements Factory<ShowsViewModel> {
  private final Provider<ShowsRepository> repositoryProvider;

  public ShowsViewModel_Factory(Provider<ShowsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ShowsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ShowsViewModel_Factory create(Provider<ShowsRepository> repositoryProvider) {
    return new ShowsViewModel_Factory(repositoryProvider);
  }

  public static ShowsViewModel newInstance(ShowsRepository repository) {
    return new ShowsViewModel(repository);
  }
}
