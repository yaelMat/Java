package System;

import java.util.logging.Logger;

import Parsers.InitDataParser;
import Restaurant.Management;


/**
 * 
 * This class represents the main program.
 * The class initializes the simulation and starts it
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Driver {

	public static void main(String args[]) throws InterruptedException
	{
		Logger logDriverLogger = Logger.getLogger(Driver.class.getName());

		// Checks if received all the arguments
		if(args.length==3)
		{	
			try
			{
				logDriverLogger.info("Initializing simulation process");
				Management mng = InitDataParser.ParseFiles(args[0], args[1], args[2]);
				logDriverLogger.info("Starts simulation");
				mng.startSimulation();
			}
			catch (Exception ex)
			{
				logDriverLogger.severe(ex.getMessage());
			}

			logDriverLogger.info("simulation ended");
		}
		else
		{
			logDriverLogger.warning("Not enought argument");
		}
	}
}
