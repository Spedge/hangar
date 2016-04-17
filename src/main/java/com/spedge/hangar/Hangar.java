package com.spedge.hangar;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.List;

import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.repo.IRepository;

/**
 * The main entry point for the Hangar application.
 * 
 * @author Spedge
 *
 */
public class Hangar extends Application<HangarConfiguration> {

	public static void main(String[] args) throws Exception {
        new Hangar().run(args);
    }

    @Override
    public String getName() {
        return "hangar";
    }

    @Override
    public void initialize(Bootstrap<HangarConfiguration> bootstrap) {
        // nothing to do yet
    }
    
	@Override
	public void run(HangarConfiguration configuration, Environment environment)	throws Exception 
	{	
		List<IRepository> repos = configuration.getRepositories();
		for(IRepository repo : repos)
		{
			for(String key : repo.getHealthChecks().keySet())
			{
				environment.healthChecks().register(key,  repo.getHealthChecks().get(key));
			}
			environment.jersey().register(repo);
		}
	}

}
