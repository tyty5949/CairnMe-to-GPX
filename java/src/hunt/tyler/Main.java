package hunt.tyler;

import hunt.tyler.core.CairnMeWebScraper;
import hunt.tyler.core.GPXWriter;
import hunt.tyler.core.TheCairn;

public class Main {

    public static void main(String[] args) {
        // Help
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            System.out.println("------------------------------");
            System.out.println("-- CairnMe to GPX Help File --");
            System.out.println("------------------------------");
            System.out.println("Syntax:");
            System.out.println("\tcairnme-to-gpx.jar \'CairnID\' \'Output_File\' [--split-tracks [time]] [--metadata] [--points]");
            System.out.println("\t\tCairnID\t\t\t\t\tThe ID pulled from the CairnMe share page.");
            System.out.println("\t\tOutput_File\t\t\t\tThe pathname of the .gpx file to written.");
            System.out.println("\nOptions:");
            System.out.println("\t--split-tracks [time]\t\tThe integer time in minutes between two points when the track will be split into separate segments.");
            System.out.println("\t--metadata\t\t\t\t\tThe program will print extended information about the cairn to the console.");
            System.out.println("\t--points\t\t\t\t\tThe program will print all the points to the console (WARNING: Lots of data!).");
            System.exit(0);
        }

        // Verify arguments
        if (args.length < 2) {
            System.out.println("Invalid syntax! Requires at least 2 arguments. Use \'cairnme-to-gpx.jar help\' for more information.");
            System.exit(0);
        }

        // Extract cairn id
        String cairnID = args[0];

        // Extract gpxFile
        String pathName = args[1];
        if (!(pathName.endsWith(".gpx") || pathName.endsWith(".GPX"))) {
            System.out.println("Invalid syntax! Pathname must be a .gpx file.");
            System.exit(0);
        }

        // Extract options
        int splitTrackTime = -1;
        boolean metadata = false;
        boolean points = false;
        for (int i = 2; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--split-tracks")) {
                if (args.length >= i + 1) {
                    try {
                        splitTrackTime = Integer.parseInt(args[i + 1]);
                    } catch (Exception e) {
                        System.out.println("Invalid syntax! The value of --split-tracks must be an integer.");
                        System.exit(0);
                    }
                }
                i++;
            } else if (args[i].equalsIgnoreCase("--metadata")) {
                metadata = true;
            } else if (args[i].equalsIgnoreCase("--points")) {
                points = true;
            } else {
                System.out.println("Unknown option \'" + args[i] + "\'");
            }
        }

        // Convert
        TheCairn theCairn = CairnMeWebScraper.scrapeTheCairn(cairnID);
        if (theCairn == null) {
            System.out.println("Unable to find the cairn with ID: " + cairnID);
            System.exit(0);
        }

        // Split tracks

        // Write to file
        GPXWriter.writeTheCairn(pathName, theCairn);

        // Verbose
        if (metadata) {
            System.out.println(theCairn);
        }

        if (points) {
            System.out.println(theCairn.toPointString());
        }

        System.out.println("Done!");
        System.exit(0);
    }
}