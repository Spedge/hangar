package com.spedge.hangar;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.config.HangarInjector;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.requests.TestRequest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.List;

/**
 * The main entry point for the Hangar application.
 * 
 * @author Spedge
 *
 */
public class Hangar extends Application<HangarConfiguration>
{

    public static void main(String[] args) throws Exception
    {
        Injector injector = Guice.createInjector(new HangarInjector());

        injector.getInstance(Hangar.class).run(args);
    }

    @Override
    public String getName()
    {
        return "hangar";
    }

    @Override
    public void initialize(Bootstrap<HangarConfiguration> bootstrap)
    {
        // nothing to do yet
    }

    @Override
    public void run(HangarConfiguration configuration, Environment environment) throws Exception
    {
        //
        // When the application starts, we create the repositories we want to
        // manage
        // from the configuration file that was passed in at startup.
        List<IRepository> repos = configuration.getRepositories();

        // Once we've got the list, we want to register all of the healthchecks
        // that come
        // with these configurations - as well as registering the repository
        // endpoints themselves.
        for (IRepository repo : repos)
        {
            repo.loadRepository(configuration, environment);

            for (String key : repo.getHealthChecks().keySet())
            {
                environment.healthChecks().register(key, repo.getHealthChecks().get(key));
            }
            environment.jersey().register(repo);
        }

        // This is a path that I can hit to see the details of a request,
        // something I've found particularly hard to capture with some of the
        // crazy multi-requests that go on in dependency management.
        //
        // TODO : Allow this only in debug mode.
        environment.jersey().register(new TestRequest());
    }

}
