package com.sibilantsolutions.iptools.cli;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

public class CommandAggregator
{

    @Argument( handler = SubCommandHandler.class, required = true, usage = "Select the command to run" )
    @SubCommands( {
        @SubCommand( name = CommandIrc.COMMAND_NAME, impl = CommandIrc.class ),
        @SubCommand( name = CommandRedir.COMMAND_NAME, impl = CommandRedir.class ),
        @SubCommand( name = CommandSocketTwoPane.COMMAND_NAME, impl = CommandSocketTwoPane.class ),
        @SubCommand( name = CommandHttp.COMMAND_NAME, impl = CommandHttp.class )
    } )
    public Object cmd;

    @Option( name = "taco", required = false, usage = "eat tacos" )
    public String taco;
}
