package com.unifiedserver.dashboard.ui.screens;

import com.unifiedserver.dashboard.data.PreferencesManager;
import com.unifiedserver.dashboard.data.repository.ShowsRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<TorrentRepository> torrentRepositoryProvider;

  private final Provider<ShowsRepository> showsRepositoryProvider;

  public SettingsViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<TorrentRepository> torrentRepositoryProvider,
      Provider<ShowsRepository> showsRepositoryProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.torrentRepositoryProvider = torrentRepositoryProvider;
    this.showsRepositoryProvider = showsRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesManagerProvider.get(), torrentRepositoryProvider.get(), showsRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<TorrentRepository> torrentRepositoryProvider,
      Provider<ShowsRepository> showsRepositoryProvider) {
    return new SettingsViewModel_Factory(preferencesManagerProvider, torrentRepositoryProvider, showsRepositoryProvider);
  }

  public static SettingsViewModel newInstance(PreferencesManager preferencesManager,
      TorrentRepository torrentRepository, ShowsRepository showsRepository) {
    return new SettingsViewModel(preferencesManager, torrentRepository, showsRepository);
  }
}
