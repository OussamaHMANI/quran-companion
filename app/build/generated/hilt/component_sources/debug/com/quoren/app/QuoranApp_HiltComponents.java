package com.quoren.app;

import androidx.hilt.work.HiltWrapper_WorkerFactoryModule;
import com.quoren.app.data.audio.PlaybackService_GeneratedInjector;
import com.quoren.app.data.download.SurahDownloadWorker_HiltModule;
import com.quoren.app.data.prayer.PrayerReminderRefreshWorker_HiltModule;
import com.quoren.app.di.AudioModule;
import com.quoren.app.di.DatabaseModule;
import com.quoren.app.di.RepositoryModule;
import com.quoren.app.feature.audio.AudioSettingsViewModel_HiltModules;
import com.quoren.app.feature.audio.DownloadsViewModel_HiltModules;
import com.quoren.app.feature.audio.PlayerViewModel_HiltModules;
import com.quoren.app.feature.backup.LocalBackupViewModel_HiltModules;
import com.quoren.app.feature.bookmarks.BookmarksViewModel_HiltModules;
import com.quoren.app.feature.memorization.HifdhDashboardViewModel_HiltModules;
import com.quoren.app.feature.memorization.HifdhSessionViewModel_HiltModules;
import com.quoren.app.feature.mosque.MosqueViewModel_HiltModules;
import com.quoren.app.feature.prayer.PrayerViewModel_HiltModules;
import com.quoren.app.feature.qibla.QiblaViewModel_HiltModules;
import com.quoren.app.feature.quran.QuranIndexViewModel_HiltModules;
import com.quoren.app.feature.reader.ReaderViewModel_HiltModules;
import com.quoren.app.feature.search.SearchViewModel_HiltModules;
import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Subcomponent;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.components.ViewComponent;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.components.ViewWithFragmentComponent;
import dagger.hilt.android.flags.FragmentGetContextFix;
import dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_DefaultViewModelFactories_ActivityModule;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_HiltViewModelFactory_ActivityCreatorEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_HiltViewModelFactory_ViewModelModule;
import dagger.hilt.android.internal.managers.ActivityComponentManager;
import dagger.hilt.android.internal.managers.FragmentComponentManager;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedLifecycleEntryPoint;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_LifecycleModule;
import dagger.hilt.android.internal.managers.HiltWrapper_SavedStateHandleModule;
import dagger.hilt.android.internal.managers.ServiceComponentManager;
import dagger.hilt.android.internal.managers.ViewComponentManager;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.HiltWrapper_ActivityModule;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.android.scopes.FragmentScoped;
import dagger.hilt.android.scopes.ServiceScoped;
import dagger.hilt.android.scopes.ViewModelScoped;
import dagger.hilt.android.scopes.ViewScoped;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedComponent;
import dagger.hilt.migration.DisableInstallInCheck;
import javax.annotation.processing.Generated;
import javax.inject.Singleton;

@Generated("dagger.hilt.processor.internal.root.RootProcessor")
public final class QuoranApp_HiltComponents {
  private QuoranApp_HiltComponents() {
  }

  @Module(
      subcomponents = ServiceC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ServiceCBuilderModule {
    @Binds
    ServiceComponentBuilder bind(ServiceC.Builder builder);
  }

  @Module(
      subcomponents = ActivityRetainedC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ActivityRetainedCBuilderModule {
    @Binds
    ActivityRetainedComponentBuilder bind(ActivityRetainedC.Builder builder);
  }

  @Module(
      subcomponents = ActivityC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ActivityCBuilderModule {
    @Binds
    ActivityComponentBuilder bind(ActivityC.Builder builder);
  }

  @Module(
      subcomponents = ViewModelC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewModelCBuilderModule {
    @Binds
    ViewModelComponentBuilder bind(ViewModelC.Builder builder);
  }

  @Module(
      subcomponents = ViewC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewCBuilderModule {
    @Binds
    ViewComponentBuilder bind(ViewC.Builder builder);
  }

  @Module(
      subcomponents = FragmentC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface FragmentCBuilderModule {
    @Binds
    FragmentComponentBuilder bind(FragmentC.Builder builder);
  }

  @Module(
      subcomponents = ViewWithFragmentC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewWithFragmentCBuilderModule {
    @Binds
    ViewWithFragmentComponentBuilder bind(ViewWithFragmentC.Builder builder);
  }

  @Component(
      modules = {
          ApplicationContextModule.class,
          AudioModule.class,
          DatabaseModule.class,
          HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule.class,
          HiltWrapper_WorkerFactoryModule.class,
          PrayerReminderRefreshWorker_HiltModule.class,
          ActivityRetainedCBuilderModule.class,
          ServiceCBuilderModule.class,
          RepositoryModule.class,
          SurahDownloadWorker_HiltModule.class
      }
  )
  @Singleton
  public abstract static class SingletonC implements QuoranApp_GeneratedInjector,
      FragmentGetContextFix.FragmentGetContextFixEntryPoint,
      HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint,
      ServiceComponentManager.ServiceComponentBuilderEntryPoint,
      SingletonComponent,
      GeneratedComponent {
  }

  @Subcomponent
  @ServiceScoped
  public abstract static class ServiceC implements PlaybackService_GeneratedInjector,
      ServiceComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ServiceComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          AudioSettingsViewModel_HiltModules.KeyModule.class,
          BookmarksViewModel_HiltModules.KeyModule.class,
          DownloadsViewModel_HiltModules.KeyModule.class,
          HifdhDashboardViewModel_HiltModules.KeyModule.class,
          HifdhSessionViewModel_HiltModules.KeyModule.class,
          HiltWrapper_ActivityRetainedComponentManager_LifecycleModule.class,
          HiltWrapper_SavedStateHandleModule.class,
          LocalBackupViewModel_HiltModules.KeyModule.class,
          MainViewModel_HiltModules.KeyModule.class,
          MosqueViewModel_HiltModules.KeyModule.class,
          PlayerViewModel_HiltModules.KeyModule.class,
          PrayerViewModel_HiltModules.KeyModule.class,
          QiblaViewModel_HiltModules.KeyModule.class,
          ActivityCBuilderModule.class,
          ViewModelCBuilderModule.class,
          QuranIndexViewModel_HiltModules.KeyModule.class,
          ReaderViewModel_HiltModules.KeyModule.class,
          SearchViewModel_HiltModules.KeyModule.class
      }
  )
  @ActivityRetainedScoped
  public abstract static class ActivityRetainedC implements ActivityRetainedComponent,
      ActivityComponentManager.ActivityComponentBuilderEntryPoint,
      HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedLifecycleEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ActivityRetainedComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          HiltWrapper_ActivityModule.class,
          HiltWrapper_DefaultViewModelFactories_ActivityModule.class,
          FragmentCBuilderModule.class,
          ViewCBuilderModule.class
      }
  )
  @ActivityScoped
  public abstract static class ActivityC implements MainActivity_GeneratedInjector,
      ActivityComponent,
      DefaultViewModelFactories.ActivityEntryPoint,
      HiltWrapper_HiltViewModelFactory_ActivityCreatorEntryPoint,
      FragmentComponentManager.FragmentComponentBuilderEntryPoint,
      ViewComponentManager.ViewComponentBuilderEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ActivityComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          AudioSettingsViewModel_HiltModules.BindsModule.class,
          BookmarksViewModel_HiltModules.BindsModule.class,
          DownloadsViewModel_HiltModules.BindsModule.class,
          HifdhDashboardViewModel_HiltModules.BindsModule.class,
          HifdhSessionViewModel_HiltModules.BindsModule.class,
          HiltWrapper_HiltViewModelFactory_ViewModelModule.class,
          LocalBackupViewModel_HiltModules.BindsModule.class,
          MainViewModel_HiltModules.BindsModule.class,
          MosqueViewModel_HiltModules.BindsModule.class,
          PlayerViewModel_HiltModules.BindsModule.class,
          PrayerViewModel_HiltModules.BindsModule.class,
          QiblaViewModel_HiltModules.BindsModule.class,
          QuranIndexViewModel_HiltModules.BindsModule.class,
          ReaderViewModel_HiltModules.BindsModule.class,
          SearchViewModel_HiltModules.BindsModule.class
      }
  )
  @ViewModelScoped
  public abstract static class ViewModelC implements ViewModelComponent,
      HiltViewModelFactory.ViewModelFactoriesEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewModelComponentBuilder {
    }
  }

  @Subcomponent
  @ViewScoped
  public abstract static class ViewC implements ViewComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewComponentBuilder {
    }
  }

  @Subcomponent(
      modules = ViewWithFragmentCBuilderModule.class
  )
  @FragmentScoped
  public abstract static class FragmentC implements FragmentComponent,
      DefaultViewModelFactories.FragmentEntryPoint,
      ViewComponentManager.ViewWithFragmentComponentBuilderEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends FragmentComponentBuilder {
    }
  }

  @Subcomponent
  @ViewScoped
  public abstract static class ViewWithFragmentC implements ViewWithFragmentComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewWithFragmentComponentBuilder {
    }
  }
}
