package main;


import communication.CommunicationManager;
import communication.CommunicationManagerFactory;
import user.Admin;
import user.Attendee;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    static Conference conf;

    public static void main(String[] args) {
        parseArguments(args);
    }

    /**
     * Parse Arguments from main method and create a clean or persistent Conference
     *
     * @param args
     */
    private static void parseArguments(String[] args) {
        if (args.length != 2) {
            printUsage();
        } else {
            if (!args[0].toLowerCase().equals("test") && !args[0].toLowerCase().equals("start")) {
                printUsage();
            } else if (args[0].toLowerCase().equals("test")) {
                switch (args[1]) {
                    case "normal":
                        startNormalConference(true);
                        break;
                    case "normal-persistent":
                        startNormalConference(false);
                        break;
                }
            } else {
                try {
                    File f = new File(args[1]);
                    FileInputStream fis = new FileInputStream(f);
                    byte[] data = new byte[(int) f.length()];
                    fis.read(data);
                    fis.close();
                    String str = new String(data, StandardCharsets.UTF_8);
                    conf = ConfigParser.parseConfigFile(str);
                    CommunicationManager communicationManager = new CommunicationManagerFactory(conf).create();
                    communicationManager.start();
                    listenForCommands(communicationManager);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage : 'test' <testID> | 'start' <path-to-config-file>");
        System.out.println("Valid test ids : 'normal', 'normal-persistent'");
        System.exit(0);
    }

    /**
     * Starts a conference with a single admin (username and password 'admin')
     * and a single user (username and password 'user')
     **/
    private static void startNormalConference(boolean clean) {
        conf = new Conference(clean);
        if (conf.getAllAdmins().isEmpty()) {
            conf.addAdmin(new Admin("Admin Administrator", "admin@administrator", "admin", "NoGroup", "AdminAllee", "onlyAdmin"), "admin");
            conf.addAttendee(new Attendee("Test User", "user@test.de", "user", "Tester", "Testerhood", "TestUser"), "user");
        }

        conf.generateAllQRCodes();
        conf.generateAllQRCodes();
        conf.getAllQrCodes();

        CommunicationManager communicationManager = new CommunicationManagerFactory(conf).enableDebugging().create();
        communicationManager.start();
        listenForCommands(communicationManager);
    }

    private static void listenForCommands(CommunicationManager communicationManager) {
        System.out.println("Type \"help\" for help.");
        System.out.println("Press 'q' to close the server");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            String[] args = input.split(" ");
            switch (args[0]) {
                case "q":
                    communicationManager.stop();
                    System.exit(0);
                    break;
                case "deladmin":
                    if(args.length == 2) {
                        Admin admin = null;
                        for(Admin admins : conf.getAllAdmins()) {
                            if(admins.getUserName().equals(args[1])) {
                                admin = admins;
                                break;
                            }
                        }
                        if(admin != null) {
                            conf.removeAdmin(admin.getID());
                            System.out.println("Administrator " + admin.getUserName() + " has been removed.");
                        } else {
                            System.out.println("There is no administrator with username \"" + args[1] + "\"");
                        }
                    } else {
                        System.out.println("Type \"help\" for help.");
                    }
                    break;
                case "listadmins":
                    conf.getAllAdmins().forEach(a -> {
                        System.out.println("");
                        System.out.println(a.toString());
                        System.out.println("password: " + conf.getUserPassword(a.getID()).second());
                    });
                    break;
                case "passwd":
                    if(args.length >= 2) {
                        Admin admin = null;
                        for(Admin admins : conf.getAllAdmins()) {
                            if(admins.getUserName().equals(args[1])) {
                                admin = admins;
                                break;
                            }
                        }
                        if(admin != null) {
                            conf.generateNewUserPassword(admin.getID());
                            System.out.println("New password: " + conf.getUserPassword(admin.getID()).second());
                        } else {
                            System.out.println("There is no administrator with username \"" + args[1] + "\"");
                        }
                    } else {
                        System.out.println("Type \"help\" for help.");
                    }
                    break;
                case "logoutUsers":
                    conf.logoutNonAdmins(true);
                    break;
                case "help":
                    System.out.println("q - stops the application");
                    System.out.println("deladmin <username> - deletes admin with given username (does not delete admins from config)");
                    System.out.println("listadmins - lists the data of all admins");
                    System.out.println("passwd <username> - regenerates the password for the given username of an admin");
                    System.out.println("logoutUsers - ends the session for all non-Admins, new passwords are generated which can be used to login again");
            }
        }
    }

}
