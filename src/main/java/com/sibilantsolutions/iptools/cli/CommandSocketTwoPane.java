package com.sibilantsolutions.iptools.cli;

import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Socket Two Pane" )
public class CommandSocketTwoPane
{

    public static final String COMMAND_NAME = "socketTwoPane";

    public static final String[] ALIASES = {};

    public final static CommandSocketTwoPane INSTANCE = new CommandSocketTwoPane();

    private CommandSocketTwoPane() {}

}
