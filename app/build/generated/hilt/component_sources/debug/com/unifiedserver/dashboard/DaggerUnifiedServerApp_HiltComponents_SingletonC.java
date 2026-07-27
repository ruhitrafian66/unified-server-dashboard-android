package com.unifiedserver.dashboard;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.unifiedserver.dashboard.data.ApiService;
import com.unifiedserver.dashboard.data.PreferencesManager;
import com.unifiedserver.dashboard.data.repository.DashboardRepository;
import com.unifiedserver.dashboard.data.repository.ShowsRepository;
import com.unifiedserver.dashboard.data.repository.SystemRepository;
import com.unifiedserver.dashboard.data.repository.TorrentRepository;
import com.unifiedserver.dashboard.di.AppModule_ProvideApiServiceFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvideDashboardRepositoryFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvideDataStoreFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvideOkHttpClientFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvidePreferencesManagerFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvideRetrofitFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvideShowsRepositoryFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvideSystemRepositoryFactory;
import com.unifiedserver.dashboard.di.AppModule_ProvideTorrentRepositoryFactory;
import com.unifiedserver.dashboard.ui.screens.DashboardViewModel;
import com.unifiedserver.dashboard.ui.screens.DashboardViewModel_HiltModules;
import com.unifiedserver.dashboard.ui.screens.SettingsViewModel;
import com.unifiedserver.dashboard.ui.screens.SettingsViewModel_HiltModules;
import com.unifiedserver.dashboard.ui.screens.SetupViewModel;
import com.unifiedserver.dashboard.ui.screens.SetupViewModel_HiltModules;
import com.unifiedserver.dashboard.ui.screens.ShowsViewModel;
import com.unifiedserver.dashboard.ui.screens.ShowsViewModel_HiltModules;
import com.unifiedserver.dashboard.ui.screens.SystemViewModel;
import com.unifiedserver.dashboard.ui.screens.SystemViewModel_HiltModules;
import com.unifiedserver.dashboard.ui.screens.TorrentsViewModel;
import com.unifiedserver.dashboard.ui.screens.TorrentsViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerUnifiedServerApp_HiltComponents_SingletonC {
  private DaggerUnifiedServerApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public UnifiedServerApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements UnifiedServerApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public UnifiedServerApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements UnifiedServerApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public UnifiedServerApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements UnifiedServerApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public UnifiedServerApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements UnifiedServerApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public UnifiedServerApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements UnifiedServerApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public UnifiedServerApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements UnifiedServerApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public UnifiedServerApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements UnifiedServerApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public UnifiedServerApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends UnifiedServerApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends UnifiedServerApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends UnifiedServerApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends UnifiedServerApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
      injectMainActivity2(arg0);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(6).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_SetupViewModel, SetupViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_ShowsViewModel, ShowsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_SystemViewModel, SystemViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_TorrentsViewModel, TorrentsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectPreferencesManager(instance, singletonCImpl.providePreferencesManagerProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_unifiedserver_dashboard_ui_screens_DashboardViewModel = "com.unifiedserver.dashboard.ui.screens.DashboardViewModel";

      static String com_unifiedserver_dashboard_ui_screens_SystemViewModel = "com.unifiedserver.dashboard.ui.screens.SystemViewModel";

      static String com_unifiedserver_dashboard_ui_screens_SettingsViewModel = "com.unifiedserver.dashboard.ui.screens.SettingsViewModel";

      static String com_unifiedserver_dashboard_ui_screens_SetupViewModel = "com.unifiedserver.dashboard.ui.screens.SetupViewModel";

      static String com_unifiedserver_dashboard_ui_screens_ShowsViewModel = "com.unifiedserver.dashboard.ui.screens.ShowsViewModel";

      static String com_unifiedserver_dashboard_ui_screens_TorrentsViewModel = "com.unifiedserver.dashboard.ui.screens.TorrentsViewModel";

      @KeepFieldType
      DashboardViewModel com_unifiedserver_dashboard_ui_screens_DashboardViewModel2;

      @KeepFieldType
      SystemViewModel com_unifiedserver_dashboard_ui_screens_SystemViewModel2;

      @KeepFieldType
      SettingsViewModel com_unifiedserver_dashboard_ui_screens_SettingsViewModel2;

      @KeepFieldType
      SetupViewModel com_unifiedserver_dashboard_ui_screens_SetupViewModel2;

      @KeepFieldType
      ShowsViewModel com_unifiedserver_dashboard_ui_screens_ShowsViewModel2;

      @KeepFieldType
      TorrentsViewModel com_unifiedserver_dashboard_ui_screens_TorrentsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends UnifiedServerApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SetupViewModel> setupViewModelProvider;

    private Provider<ShowsViewModel> showsViewModelProvider;

    private Provider<SystemViewModel> systemViewModelProvider;

    private Provider<TorrentsViewModel> torrentsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.setupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.showsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.systemViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.torrentsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(6).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_SetupViewModel, ((Provider) setupViewModelProvider)).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_ShowsViewModel, ((Provider) showsViewModelProvider)).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_SystemViewModel, ((Provider) systemViewModelProvider)).put(LazyClassKeyProvider.com_unifiedserver_dashboard_ui_screens_TorrentsViewModel, ((Provider) torrentsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_unifiedserver_dashboard_ui_screens_SystemViewModel = "com.unifiedserver.dashboard.ui.screens.SystemViewModel";

      static String com_unifiedserver_dashboard_ui_screens_DashboardViewModel = "com.unifiedserver.dashboard.ui.screens.DashboardViewModel";

      static String com_unifiedserver_dashboard_ui_screens_ShowsViewModel = "com.unifiedserver.dashboard.ui.screens.ShowsViewModel";

      static String com_unifiedserver_dashboard_ui_screens_TorrentsViewModel = "com.unifiedserver.dashboard.ui.screens.TorrentsViewModel";

      static String com_unifiedserver_dashboard_ui_screens_SettingsViewModel = "com.unifiedserver.dashboard.ui.screens.SettingsViewModel";

      static String com_unifiedserver_dashboard_ui_screens_SetupViewModel = "com.unifiedserver.dashboard.ui.screens.SetupViewModel";

      @KeepFieldType
      SystemViewModel com_unifiedserver_dashboard_ui_screens_SystemViewModel2;

      @KeepFieldType
      DashboardViewModel com_unifiedserver_dashboard_ui_screens_DashboardViewModel2;

      @KeepFieldType
      ShowsViewModel com_unifiedserver_dashboard_ui_screens_ShowsViewModel2;

      @KeepFieldType
      TorrentsViewModel com_unifiedserver_dashboard_ui_screens_TorrentsViewModel2;

      @KeepFieldType
      SettingsViewModel com_unifiedserver_dashboard_ui_screens_SettingsViewModel2;

      @KeepFieldType
      SetupViewModel com_unifiedserver_dashboard_ui_screens_SetupViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.unifiedserver.dashboard.ui.screens.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.provideDashboardRepositoryProvider.get());

          case 1: // com.unifiedserver.dashboard.ui.screens.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.providePreferencesManagerProvider.get(), singletonCImpl.provideTorrentRepositoryProvider.get(), singletonCImpl.provideShowsRepositoryProvider.get());

          case 2: // com.unifiedserver.dashboard.ui.screens.SetupViewModel 
          return (T) new SetupViewModel(singletonCImpl.providePreferencesManagerProvider.get(), singletonCImpl.provideDashboardRepositoryProvider.get());

          case 3: // com.unifiedserver.dashboard.ui.screens.ShowsViewModel 
          return (T) new ShowsViewModel(singletonCImpl.provideShowsRepositoryProvider.get());

          case 4: // com.unifiedserver.dashboard.ui.screens.SystemViewModel 
          return (T) new SystemViewModel(singletonCImpl.provideSystemRepositoryProvider.get());

          case 5: // com.unifiedserver.dashboard.ui.screens.TorrentsViewModel 
          return (T) new TorrentsViewModel(singletonCImpl.provideTorrentRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends UnifiedServerApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends UnifiedServerApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends UnifiedServerApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<DataStore<Preferences>> provideDataStoreProvider;

    private Provider<PreferencesManager> providePreferencesManagerProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<ApiService> provideApiServiceProvider;

    private Provider<DashboardRepository> provideDashboardRepositoryProvider;

    private Provider<TorrentRepository> provideTorrentRepositoryProvider;

    private Provider<ShowsRepository> provideShowsRepositoryProvider;

    private Provider<SystemRepository> provideSystemRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 1));
      this.providePreferencesManagerProvider = DoubleCheck.provider(new SwitchingProvider<PreferencesManager>(singletonCImpl, 0));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 5));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 4));
      this.provideApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<ApiService>(singletonCImpl, 3));
      this.provideDashboardRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DashboardRepository>(singletonCImpl, 2));
      this.provideTorrentRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<TorrentRepository>(singletonCImpl, 6));
      this.provideShowsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ShowsRepository>(singletonCImpl, 7));
      this.provideSystemRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SystemRepository>(singletonCImpl, 8));
    }

    @Override
    public void injectUnifiedServerApp(UnifiedServerApp arg0) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.unifiedserver.dashboard.data.PreferencesManager 
          return (T) AppModule_ProvidePreferencesManagerFactory.providePreferencesManager(singletonCImpl.provideDataStoreProvider.get());

          case 1: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) AppModule_ProvideDataStoreFactory.provideDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.unifiedserver.dashboard.data.repository.DashboardRepository 
          return (T) AppModule_ProvideDashboardRepositoryFactory.provideDashboardRepository(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.providePreferencesManagerProvider.get());

          case 3: // com.unifiedserver.dashboard.data.ApiService 
          return (T) AppModule_ProvideApiServiceFactory.provideApiService(singletonCImpl.provideRetrofitProvider.get());

          case 4: // retrofit2.Retrofit 
          return (T) AppModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.providePreferencesManagerProvider.get());

          case 5: // okhttp3.OkHttpClient 
          return (T) AppModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 6: // com.unifiedserver.dashboard.data.repository.TorrentRepository 
          return (T) AppModule_ProvideTorrentRepositoryFactory.provideTorrentRepository(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.providePreferencesManagerProvider.get());

          case 7: // com.unifiedserver.dashboard.data.repository.ShowsRepository 
          return (T) AppModule_ProvideShowsRepositoryFactory.provideShowsRepository(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.providePreferencesManagerProvider.get());

          case 8: // com.unifiedserver.dashboard.data.repository.SystemRepository 
          return (T) AppModule_ProvideSystemRepositoryFactory.provideSystemRepository(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.providePreferencesManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
