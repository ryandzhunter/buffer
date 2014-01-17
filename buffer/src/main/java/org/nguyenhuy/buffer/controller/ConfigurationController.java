package org.nguyenhuy.buffer.controller;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import org.nguyenhuy.buffer.event.DataSource;
import org.nguyenhuy.buffer.event.GotConfigurationEvent;
import org.nguyenhuy.buffer.job.GetConfigurationJob;
import org.nguyenhuy.buffer.model.configuration.Configuration;

/**
 * This class manages {@link org.nguyenhuy.buffer.model.configuration.Configuration}
 * provided by Buffer API, including loading the configuration from network.
 * When the configuration is changed,
 * a {@link org.nguyenhuy.buffer.event.GotConfigurationEvent} will be posted.
 * Note that by design, configuration is cached in memory but not persistent store.
 * Also, this controller must be tied to lifecycle of an application or an activity
 * (depends on which object hosts it). So {@link #onStart()} and {@link #onStop()}
 * must be called according to lifecycle of the host.
 */
public class ConfigurationController {
    private Configuration configuration;
    private Bus bus;
    private JobManager jobManager;

    public ConfigurationController(Bus bus, JobManager jobManager) {
        this.bus = bus;
        this.jobManager = jobManager;
    }

    public void onStart() {
        bus.register(this);
    }

    public void onStop() {
        bus.unregister(this);
    }

    /**
     * Loads configuration. Subscribers will receive
     * a {@link org.nguyenhuy.buffer.event.GotConfigurationEvent} later.
     */
    public void loadConfiguration() {
        if (configuration != null) {
            bus.post(new GotConfigurationEvent(DataSource.CACHED, configuration));
        } else {
            jobManager.addJobInBackground(new GetConfigurationJob());
        }
    }

    @Subscribe
    public void onGotNewConfiguration(GotConfigurationEvent event) {
        configuration = event.getConfiguration();
    }

    public void removeConfiguration() {
        configuration = null;
    }
}
