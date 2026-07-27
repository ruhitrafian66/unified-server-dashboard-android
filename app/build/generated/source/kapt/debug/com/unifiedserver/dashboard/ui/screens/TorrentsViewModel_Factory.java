package com.unifiedserver.dashboard.ui.screens;

import com.unifiedserver.dashboard.data.repository.TorrentRepository;
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
public final class TorrentsViewModel_Factory implements Factory<TorrentsViewModel> {
  private final Provider<TorrentRepository> repositoryProvider;

  public TorrentsViewModel_Factory(Provider<TorrentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public TorrentsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static TorrentsViewModel_Factory create(Provider<TorrentRepository> repositoryProvider) {
    return new TorrentsViewModel_Factory(repositoryProvider);
  }

  public static TorrentsViewModel newInstance(TorrentRepository repository) {
    return new TorrentsViewModel(repository);
  }
}
