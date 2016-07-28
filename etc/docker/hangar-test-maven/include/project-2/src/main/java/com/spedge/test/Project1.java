package com.spedge.test;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class Project1
{
    public static void main(String[] args)
    {
        System.out.println("This is simply a way of making sure things compile");
        Command ec = new Command("Stuff", "Things"){

            @Override
            public void configure(Subparser subparser){}
            

            @Override
            public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception{}
            
        };
        System.out.println("" + ec.getName());
    }
}
