import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class Deadwood {

    private ArrayList<Area> areas = new ArrayList<Area>(); // Hold areas
    private ArrayList<Player> players = new ArrayList<Player>(); // Hold Players
    private ArrayList<Scene> scenes = new ArrayList<>();
    private int maxDays = 0; // Max days Players can play
    private int currentDay = 0; // Track current day
    public CastingOffice office = new CastingOffice(); // Casting office

    /**
     *  Initialize board, cards, players, areas, scenes, roles, and max days data.
     * @param args Arguments for XML files and number of players.
     */
    public Deadwood(String[] args) {
        String boardXMLFile = ""; // Board File string (path/file name)
        String cardXMLFile = ""; // Card File string (path/file name)
        int numPlayers = 0; // Number of Players

        // Code to get arguments provided by Professor Shri Mare
        if (args.length >= 2) {
            boardXMLFile = args[0];
            cardXMLFile = args[1];
            numPlayers = Integer.parseInt(args[2]);
        } else {
            boardXMLFile = "src/main/resources/board.xml";
            cardXMLFile = "src/main/resources/cards.xml";
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the number of players (ex: '3'): ");
            numPlayers = input.nextInt();
        }

        // Calculate maxDays based on how many Players
        if (numPlayers == 2 || numPlayers == 3) {
            maxDays = 3;
        }
        else {
            maxDays = 4;
        }

        // Initialize the lists that store Players, Roles, Scenes, and Areas
        players = getPlayers(numPlayers);
        ArrayList<Role> roles = new ArrayList<Role>();

        // Read the XML document to setup Cards
        try {
            Document cardDoc = getDocFromFile(cardXMLFile);
            Document boardDoc = getDocFromFile(boardXMLFile);

            // Read XML and create all roles and scenes, add to lists passed as parameters
            readCardData(cardDoc, roles, scenes);

            // Create areas based on information in XML board document
            areas = setupBoard(boardDoc);
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
            return;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        // Clear board for new day
        clearBoard(areas);

        // (Re)populate with new Scenes
        populateAreas(areas, scenes);

        // Return Players to the trailers and start a new day
        playerAreaSetup(areas, players);
    }

    /**
     * To play the game through the console.
     * Currently unused code. Left here for choice to play through console.
     * @param args Arguments for setting up XML files and number of players.
     */
    public void console(String[] args) {
        String boardXMLFile = ""; // Board File string (path/file name)
        String cardXMLFile = ""; // Card File string (path/file name)
        String choice; // Player's choice of action
        int maxDays = 0; // Max days Player can play
        int numPlayers = 0; // Number of Players
        Scanner console = new Scanner(System.in); // To take Player's input

        // Code to get arguments provided by Professor Shri Mare
        if (args.length >= 2) {
            boardXMLFile = args[0];
            cardXMLFile = args[1];
            numPlayers = Integer.parseInt(args[2]);
        } else {
            boardXMLFile = "src/main/resources/board.xml";
            cardXMLFile = "src/main/resources/cards.xml";
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the number of players (ex: '3'): ");
            numPlayers = input.nextInt();
        }

        // Calculate maxDays based on how many Players
        if (numPlayers == 2 || numPlayers == 3) {
            maxDays = 3;
        }
        else {
            maxDays = 4;
        }

        players = getPlayers(numPlayers);
        ArrayList<Role> roles = new ArrayList<Role>();

        // Read the XML document to setup Cards
        try {
            Document cardDoc = getDocFromFile(cardXMLFile);
            Document boardDoc = getDocFromFile(boardXMLFile);

            // Read XML and create all roles and scenes, add to lists passed as parameters
            readCardData(cardDoc, roles, scenes);

            // Create areas based on information in XML board document
            areas = setupBoard(boardDoc);
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
            return;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        // Loop until max days
        while (currentDay < maxDays) {

            // Clear board for new day
            clearBoard(areas);

            // (Re)populate with new Scenes
            populateAreas(areas, scenes);

            // Return Players to the trailers and start a new day
            playerAreaSetup(areas, players);

            // Loop until 1 scene card is left on the board
            while (totalScenesLeft(areas) != 1) {

                // Loop so each Player can have a turn
                for (int i = 0; i < players.size(); i++) {

                    // Display current player
                    System.out.println("\nCurrent player: " + players.get(i).getName());

                    // Reset flags
                    boolean didMove = false; // To keep track if Player moved already
                    boolean didWork = false; // To keep track if Player worked (either act or rehearse) already
                    boolean newRole = false; // To keep track of Player recently getting a new role
                    boolean endTurn = false; // To tell if Player chose to end turn

                    // Continue through Player's options until Player chooses to end
                    while (!endTurn) {

                        System.out.println("Options: 'Act', 'Rehearse', 'Take Role', 'Move', 'Who', 'Where', 'Upgrade', 'End'");
                        System.out.print("> ");

                        choice = console.nextLine();

                        if (choice.equalsIgnoreCase("Move")) {

                            // If Player is not taking a role, check if player moved already.
                            // Else, cannot move while taking a role.
                            if (players.get(i).getRoleStatus()) {

                                // If Player didn't move yet, display neighbors and let Player choose a neighbor or go back to main options.
                                // Else, can't move because already moved.
                                if (!didMove) {
                                    System.out.print("Neighboring areas you can move to: ");
                                    players.get(i).getCurrentArea().printNeighbors();
                                    System.out.println("\nPlease input one of the given neighbors or input \"go back\" to go back.\n");
                                    System.out.print("> ");

                                    choice = console.nextLine();

                                    // While loop until Player inputs either a given nieghbor of the area or go back
                                    while (!players.get(i).getCurrentArea().isNeighbor(choice) && !choice.equalsIgnoreCase("Go back")) {
                                        System.out.println("That is not a neighbor of this area!");
                                        System.out.println("Please input one of the given neighbors or input \"go back\" to go back.\n");
                                        System.out.print("> ");

                                        choice = console.nextLine();
                                    }

                                    // If choice is go back, display message.
                                    // Else, Player moves to that choice area.
                                    if (choice.equalsIgnoreCase("Go back")) {
                                        System.out.println("Successfully got back to main options. Please input an option.\n");
                                    }
                                    else {
                                        // Go through each area and find the name of the area to move Player to that area
                                        for (Area a : areas) {
                                            if (a.getName().equalsIgnoreCase(choice)) {
                                                // Remove player from list of current players on current area
                                                players.get(i).currentArea.removePlayer(players.get(i));
                                                // Move player to new area
                                                players.get(i).move(a);
                                                // Add player to new areas list of current players
                                                a.addPlayer(players.get(i));
                                            }
                                        }

                                        System.out.println("Successfully moved to area " + choice + "!\n");

                                        didMove = true; // True so Player can't move again on same turn
                                    }
                                }
                                else {
                                    System.out.println("Cannot move because already moved.\n");
                                }
                            }
                            else {
                                System.out.println("Cannot move while taking a role.\n");
                            }
                        }
                        else if (choice.equalsIgnoreCase("Take Role")) {

                            // If Player is not on a role, display available roles.
                            // Else, display that the Player is already on a role.
                            if (players.get(i).getRoleStatus()) {

                                // If current area has a scene card, display available starring roles and extra roles.
                                // Else, display that there is no set.
                                if (players.get(i).currentArea.sceneExists()) {

                                    // Print only the available roles that the Player is eligible for
                                    players.get(i).currentArea.printEligibleRoles(players.get(i));
                                    System.out.println("Please input one of the given roles to take or input \"go back\" to go back.\n");
                                    System.out.print("> ");

                                    choice = console.nextLine();

                                    // While loop until Player inputs either an eligible role in the area for that Player or go back
                                    while (!players.get(i).currentArea.isEligibleRole(players.get(i), choice) && !choice.equalsIgnoreCase("Go back")) {
                                        System.out.println("Either not a role for this area or rank is too low!");
                                        System.out.println("Please input one of the given roles to take or input \"go back\" to go back.\n");
                                        System.out.print("> ");

                                        choice = console.nextLine();
                                    }

                                    // If choice is go back, display message.
                                    // Else, Player accepts role.
                                    if (choice.equalsIgnoreCase("Go back")) {
                                        System.out.println("Successfully got back to main options. Please input an option.\n");
                                    }
                                    else {

                                        // Go through each available role in the area and set role to Player
                                        for (Role r : players.get(i).currentArea.getEligibleRoles(players.get(i))) {

                                            // We found the role that the user wants, we must accept the role, mark it
                                            // as unavailable, and add the player to the actor or extras list depending
                                            // on the type of the role
                                            if (r.getName().equalsIgnoreCase(choice)) {
                                                players.get(i).acceptRole(r);
                                                r.setAvailability(false);
                                                r.assignRole(players.get(i));

                                                // If the player takes a starring (acting) role, add to list of actors
                                                // Else add to list of extras
                                                if (r.getRoleType()) {
                                                    players.get(i).currentArea.getScene().addPlayerToActors(players.get(i));
                                                }
                                                else {
                                                    players.get(i).currentArea.addExtra(players.get(i));
                                                }
                                            }
                                        }

                                        System.out.println("Successfully taken role " + choice + "!\n");

                                        newRole = true; // Signify that the Player just got a new role on current turn
                                    }
                                }
                                else {
                                    System.out.println("No set in this area.\n");
                                }
                            }
                            else {
                                System.out.println("Already on a role!\n");
                            }
                        }
                        else if (choice.equalsIgnoreCase("Act")) {

                            // If Player is on a role, check if Player recently took a role.
                            // Else, display that the Player is not on a role.
                            if (!players.get(i).getRoleStatus()) {

                                // If Player did not recently take a role, check if acted/rehearsed already.
                                // Else, can't act because Player just got a new role.
                                if (!newRole) {

                                    // If didn't act/rehearse yet, Player acts.
                                    // Else, can't act because already acted/rehearsed.
                                    if (!didWork) {
                                        players.get(i).act();

                                        // If current area shot reaches 0, remove scene card.
                                        // Else, display amount of shots left.
                                        if (players.get(i).currentArea.getShots() == 0) {

                                            // Print confirmation to the player
                                            System.out.print("Last shot removed! Scene is wrapped for this area!");

                                            // If area has more than 1 actor (includes starring roles and extras), reward actors
                                            if (players.get(i).currentArea.getNumOfActors() > 1) {
                                                // Reward starring actors
                                                rewardActors(players.get(i));

                                                // Reward extras
                                                rewardExtras(players.get(i).currentArea);
                                            }

                                            // Remove the Scene from the Area since set is wrapped
                                            players.get(i).currentArea.updateScene(null);

                                            // Clear Extras to avoid any being carried over to next day
                                            players.get(i).currentArea.clearExtras();

                                            // Free all players currently on this area and reset rehearsal chips
                                            for (Player p : players.get(i).currentArea.getAllPlayers()) {
                                                p.vacateRole();
                                                p.resetRehearsalChips();
                                            }
                                        }
                                        else {
                                            System.out.print("Shots left for this area: " + players.get(i).currentArea.getShots() + ".");
                                        }

                                        System.out.println("\n");

                                        didWork = true; // Signify that the Player already worked for their turn
                                    }
                                    else {
                                        System.out.println("Already acted/rehearsed. Can't rehearse and act on same turn or act twice on same turn.\n");
                                    }
                                }
                                else {
                                    System.out.println("Cannot act because recently just got a role.\n");
                                }
                            }
                            else {
                                System.out.println("Cannot act because not on a role.\n");
                            }
                        }
                        else if (choice.equalsIgnoreCase("Rehearse")) {

                            // If Player is on a role, check if Player recently took a role.
                            // Else, display that the Player is not on a role.
                            if (!players.get(i).getRoleStatus()) {

                                // If Player did not recently take a role, check if acted/rehearsed already.
                                // Else, can't rehearse because Player just got a new role.
                                if (!newRole){

                                    // If didn't act/rehearse yet, check if Player can get a rehearsal chip.
                                    // Else, can't rehearse because already acted or rehearse.
                                    if (!didWork) {

                                        // If rehearse chips is less than 6, rehearse for Player.
                                        // Else, Player has already earned max rehearse chips.
                                        if (players.get(i).getRehearseChips() < 5) {
                                            players.get(i).rehearse();

                                            System.out.println(players.get(i).getName() + " earned a rehearse chip!\n");

                                            didWork = true; // Signify that the Player already worked for their turn
                                        }
                                        else {
                                            System.out.println(players.get(i).getName() + " has earned the max rehearse chips of 5.\n");
                                        }
                                    }
                                    else {
                                        System.out.println("Already acted/rehearsed. Can't rehearse and act on same turn or rehearse twice on same turn.\n");
                                    }
                                }
                                else{
                                    System.out.println("Cannot rehearse because recently just got a role.\n");
                                }
                            }
                            else {
                                System.out.println("Cannot rehearse because not on a role.\n");
                            }
                        }
                        else if (choice.equalsIgnoreCase("Upgrade")) {
                            int num = -1;

                            // Check if Player is on casting office
                            if (!players.get(i).getCurrentArea().getName().equalsIgnoreCase("office")) {
                                System.out.println("Must be located at the Casting Office to upgrade!\n");
                            }
                            else {
                                // Get options from Casting Office
                                ArrayList<Integer> options = office.getOptions(players.get(i));

                                // Display options for the User
                                System.out.println("Available upgrades: ");
                                for(int x : options) {
                                    System.out.println("\tLevel: " + x);
                                }

                                // Read user input
                                choice = console.nextLine();
                                try {
                                    num = Integer.parseInt(choice);
                                } catch (NumberFormatException ex) {
                                    System.out.println("Invalid input! Expecting a number.");
                                }

                                // Repeat until valid input
                                while(!options.contains(num) && num != 0) {
                                    System.out.println("Enter your selection, or input \"0\" to go back.\n");
                                    choice = console.nextLine();
                                    try {
                                        num = Integer.parseInt(choice);
                                    } catch (NumberFormatException ex) {
                                        System.out.println("Invalid input! Expecting a number.");
                                    }
                                }

                                // If user wants to return back to Casting Office
                                // Else if user purchases a rank upgrade
                                if (num == 0) {
                                    System.out.println("Successfully got back to main options. Please input an option.\n");
                                }
                                else {
                                    office.upgradeRank(players.get(i), num);
                                    System.out.println("Rank upgrade successful!\n");
                                }
                            }

                        }
                        else if (choice.equalsIgnoreCase("End")) {
                            endTurn = true; // Signify that the Player chose to end their turn
                        }
                        else if (choice.equalsIgnoreCase("Who")) {
                            // Display player info
                            System.out.print(players.get(i).getName() + "'s turn! ");
                            System.out.println("Rank: " + players.get(i).getRank() +" ($" + players.get(i).getDollars() + ", " +
                                    players.get(i).getCredits() + " cr, " + players.get(i).getRehearseChips() + " rehearse chips)");

                            // If on a role, print the role the player is on
                            if (!players.get(i).getRoleStatus()) {
                                System.out.print("Currently working " + players.get(i).getCurrentRole().getName());
                                System.out.println(", \"" + players.get(i).getCurrentRole().getLine() + "\"");
                            }

                            System.out.println();
                        }
                        else if (choice.equalsIgnoreCase("Where")) {
                            System.out.print("Current area: " + players.get(i).getCurrentArea().getName());

                            // If scene card exists in area, print the scene being shot and the budget
                            if (players.get(i).currentArea.sceneExists()) {
                                System.out.print(" shooting " + players.get(i).currentArea.getScene().getName());
                                System.out.print(" with budget " + players.get(i).currentArea.getScene().getBudget() + " million.");
                            }
                            System.out.println("\n");
                        }

                        // Secret print menu for debugging
                        else if (choice.equalsIgnoreCase("Print")) {

                            System.out.print("Players on area " + players.get(i).currentArea.getName() + ": ");
                            for (Player p : players.get(i).currentArea.getAllPlayers()) {
                                System.out.print(p.getName() + " ");
                            }

                            // Only works if area has a card
                            System.out.println("\nNumber of actors in area: " + players.get(i).currentArea.getNumOfActors());
                            System.out.println();
                        }
                        else {
                            System.out.println("Invalid option.\n");
                        }
                    }
                }
            }

            currentDay++;
        }

        // We've reached the max day, print the player at this point!
        printWinner(players);
    }

    /**
     * Method that initializes the game and gets all user information for each player.
     * It prompts each player for a unique username, ensures that each name is unique,
     * creates and adds the Player object to a list of all players that will be iterated
     * through throughout the course of the gameplay.
     * @param numPlayers Number of players to create.
     * @return ArrayList of players created.
     * @throws InputMismatchException Allows numbers as names.
     */
    private ArrayList<Player> getPlayers(int numPlayers) throws InputMismatchException{
        String name;
        boolean isUnique;
        Scanner console = new Scanner(System.in);
        int rank = 1;
        int dollars = 0;
        int credits = 0;
        int rehearseChips = 0;

        // Check and set the number of days for gameplay depending on how many players
        if(numPlayers < 2 || numPlayers > 8) {
            System.exit(-1);
        } else {

            switch (numPlayers) {
                case 2:
                    rank = 1;           // Beginning rank for each player
                    dollars = 0;        // Beginning money for each player
                    credits = 0;        // Beginning credits for each player
                    rehearseChips = 0;  // Beginning rehearseChips for each player
                    break;

                case 3:
                    rank = 1;
                    dollars = 0;
                    credits = 0;
                    rehearseChips = 0;
                    break;

                case 4:
                    rank = 1;
                    dollars = 0;
                    credits = 0;
                    rehearseChips = 0;
                    break;

                case 5:
                    rank = 1;
                    dollars = 0;
                    credits = 2;
                    rehearseChips = 0;
                    break;

                case 6:
                    rank = 1;
                    dollars = 0;
                    credits = 4;
                    rehearseChips = 0;
                    break;

                case 7:
                    rank = 2;
                    dollars = 0;
                    credits = 0;
                    rehearseChips = 0;
                    break;

                case 8:
                    rank = 2;
                    dollars = 0;
                    credits = 0;
                    rehearseChips = 0;
                    break;
            }
        }

        // Initialize storage to hold each Player object
        ArrayList<Player> playerList = new ArrayList<Player>();

        // Get unique username for each Player
        for (int i = 0; i < numPlayers; i++) {

            // Obtain username
            System.out.println("Enter a unique Player name: ");
            name = console.nextLine();

            // Ensure it is unique
            isUnique = checkUniqueUserName(name, playerList);

            // While loop to check and ensure name is unique
            while (!isUnique) {
                System.out.println("Username is not unique. Please try again.");
                name = console.nextLine();
                isUnique = checkUniqueUserName(name, playerList);
            }
            System.out.println("Username " + name + " accepted!\n");

            // Create Player with starting values for rank, dollars, credits, and chips
            Player p = new Player(name, rank, dollars, credits, rehearseChips);

            // Add p to storage
            playerList.add(p);
        }
        return playerList;
    }

    /**
     * Checks to make sure that the current player's desired username is not already in use.
     * @param name String given for name.
     * @param list ArrayList of players to look at names.
     * @return True if it is unique, else false.
     */
    private boolean checkUniqueUserName(String name, ArrayList<Player> list) {
        boolean flag = true;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (name.equalsIgnoreCase(list.get(i).getName())) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * XML Parser method provided by Professor Shri Mare.
     * Reads the data from the XML file.
     * @param filename XML file path.
     * @return Document.
     * @throws ParserConfigurationException
     */
    public Document getDocFromFile(String filename) throws ParserConfigurationException {
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = null;

            try {
                doc = db.parse(filename);
            } catch (Exception ex) {
                System.out.println("XML Parse failure");
                ex.printStackTrace();
            }
            return doc;
        }
    }

    /**
     * XML Parser method provided by Professor Shri Mare.
     * Reads the data from the XML for card data.
     * @param d Document.
     * @param roles ArrayList of roles of scene cards.
     * @param scenes ArrayList of scene cards.
     */
    public void readCardData(Document d, ArrayList<Role> roles, ArrayList<Scene> scenes) {
        String cardName, cardDesc;
        int cardsLen, childLen, budget;

        Element root = d.getDocumentElement();

        NodeList cards = root.getElementsByTagName("card");

        cardsLen = cards.getLength();

        for (int i = 0; i < cardsLen; i++) {

            // Create empty Scene
            Scene s = new Scene();
            ArrayList<Role> sceneRoles = new ArrayList<Role>();
            ArrayList<Player> playersActing = new ArrayList<Player>();
            s.setRoles(sceneRoles);
            String img = "";

            // Reads data
            Node card = cards.item(i);

            // Get name and budget for the Scene
            cardName = card.getAttributes().getNamedItem("name").getNodeValue();
            img = card.getAttributes().getNamedItem("img").getNodeValue();
            budget = Integer.parseInt(card.getAttributes().getNamedItem("budget").getNodeValue());

            // Add attributes to the Scene
            s.setName(cardName);
            s.setImage(img);
            s.setBudget(budget);
            s.setActorsList(playersActing);
            s.setFlipped(false);

            NodeList children = card.getChildNodes();
            childLen = children.getLength();

            for (int j = 0; j < childLen; j++) {

                Node sub = children.item(j);

                // If the child node starts with a <scene> tag
                if ("scene".equals(sub.getNodeName())) {
                    cardDesc = sub.getFirstChild().getNodeValue();
                    s.setDescription(cardDesc);
                }
                // If the child node starts with a <part> tag
                else if ("part".equals(sub.getNodeName())) {
                    ArrayList<Integer> roleCoords = new ArrayList<Integer>();
                    int x, y, w, h;
                    String roleName = sub.getAttributes().getNamedItem("name").getNodeValue();
                    int roleLevel = Integer.parseInt(sub.getAttributes().getNamedItem("level").getNodeValue());

                    Element element = (Element) sub;

                    String line = element.getElementsByTagName("line").item(0).getTextContent();

                    Node testNodeX = element.getElementsByTagName("area").item(0);
                    x = Integer.parseInt(testNodeX.getAttributes().getNamedItem("x").getNodeValue());
                    roleCoords.add(x);
                    y = Integer.parseInt(testNodeX.getAttributes().getNamedItem("y").getNodeValue());
                    roleCoords.add(y);
                    h = Integer.parseInt(testNodeX.getAttributes().getNamedItem("h").getNodeValue());
                    roleCoords.add(h);
                    w = Integer.parseInt(testNodeX.getAttributes().getNamedItem("w").getNodeValue());
                    roleCoords.add(w);

                    // Create Role and add to list
                    //System.out.println(roleName + " " + x + " " + y + " " + h + " " + w);
                    Role r = new Role(roleName, null, roleLevel, true, true, s, line,roleCoords);
                    s.addRole(r);
                }
            }
            scenes.add(s);
        }
    }

    /**
     * Method that reads in the board data from an XML file. It creates the Scenes, Roles, and Areas
     * used for gameplay and returns an ArrayList of all areas on the board.
     * @param d Document
     * @return ArrayList of all areas on the board.
     * @throws Exception
     */
    public ArrayList<Area> setupBoard(Document d) throws Exception {
        ArrayList<Area> areas = new ArrayList<Area>();
        String areaName;
        int numOfShots = 0;
        int numOfRoles = 0;
        String roleName = "";
        int roleLevel = 0;
        int areaLen;
        int childLen;
        String line = "";
        Element element;

        Element root = d.getDocumentElement();

        NodeList sets = root.getElementsByTagName("set");

        areaLen = sets.getLength();

        // For each area found in XML doc
        for (int i = 0; i < areaLen; i++) {
            ArrayList<Role> extras = new ArrayList<Role>();
            ArrayList<String> neighbors = new ArrayList<String>();
            ArrayList<Player> pl = new ArrayList<Player>();
            ArrayList<Player> extraPl = new ArrayList<Player>();
            ArrayList<Integer> takesCoordinates = new ArrayList<Integer>();
            int x = 0;
            int y = 0;
            int h = 0;
            int w = 0;
            int areaW = 0;
            int areaH = 0;
            int areaX = 0;
            int areaY = 0;

            Node set = sets.item(i);

            areaName = set.getAttributes().getNamedItem("name").getNodeValue();

            NodeList children = set.getChildNodes();
            childLen = children.getLength();

            for (int j = 0; j < childLen; j++) {

                Node sub = children.item(j);

                // Obtain the areas neighbors and create a list of neighbors to be added to Area object
                if("neighbors".equals(sub.getNodeName())) {
                    element = (Element) sub;
                    int len = element.getElementsByTagName("neighbor").getLength();
                    for(int k = 0; k < len; k++) {
                        Node testNode = element.getElementsByTagName("neighbor").item(k);
                        String neighborName = testNode.getAttributes().getNamedItem("name").getNodeValue();
                        neighbors.add(neighborName);
                    }
                }
                // Obtain the number of takes/shots for this specific area
                else if("takes".equals(sub.getNodeName())) {
                    element = (Element) sub;
                    Node testNode = element.getElementsByTagName("take").item(0);
                    numOfShots = Integer.parseInt(testNode.getAttributes().getNamedItem("number").getNodeValue());

                    // for each take, obtain the area dimensions for the board gui
                    for (int k = 0; k < numOfShots; k++) {
                        Node testNodeX = element.getElementsByTagName("area").item(k);
                        x = Integer.parseInt(testNodeX.getAttributes().getNamedItem("x").getNodeValue());
                        takesCoordinates.add(x);
                        y = Integer.parseInt(testNodeX.getAttributes().getNamedItem("y").getNodeValue());
                        takesCoordinates.add(y);
                        h = Integer.parseInt(testNodeX.getAttributes().getNamedItem("h").getNodeValue());
                        takesCoordinates.add(h);
                        w = Integer.parseInt(testNodeX.getAttributes().getNamedItem("w").getNodeValue());
                        takesCoordinates.add(w);
                    }

                    // System.out.println(takesCoordinates.toString()); // TESTING
                }

                // Obtain all of the "Extra" roles for this area
                else if("parts".equals(sub.getNodeName())) {
                    element = (Element) sub;
                    numOfRoles = element.getElementsByTagName("part").getLength();

                    // For each role, get the part name, the rank, and the line. Add to list of Roles for this area
                    for(int k = 0; k < numOfRoles; k++) {
                        ArrayList<Integer> coords = new ArrayList<Integer>();
                        Node testNode = element.getElementsByTagName("part").item(k);
                        roleName = testNode.getAttributes().getNamedItem("name").getNodeValue();

                        roleLevel = Integer.parseInt(testNode.getAttributes().getNamedItem("level").getNodeValue());

                        Element element2 = (Element) testNode;
                        line = element2.getElementsByTagName("line").item(0).getTextContent();

                        Node testNodeX = element.getElementsByTagName("area").item(k);
                        x = Integer.parseInt(testNodeX.getAttributes().getNamedItem("x").getNodeValue());
                        coords.add(x);
                        y = Integer.parseInt(testNodeX.getAttributes().getNamedItem("y").getNodeValue());
                        coords.add(y);
                        h = Integer.parseInt(testNodeX.getAttributes().getNamedItem("h").getNodeValue());
                        coords.add(h);
                        w = Integer.parseInt(testNodeX.getAttributes().getNamedItem("w").getNodeValue());
                        coords.add(w);

                        // Create new Role and add to list of roles for this area
                        Role r = new Role(roleName,null,roleLevel,false,true,null,line, coords);
                        extras.add(r);
                    }
                }
                else if ("area".equals(sub.getNodeName())) {
                    areaW = Integer.parseInt(sub.getAttributes().getNamedItem("w").getNodeValue());
                    areaH = Integer.parseInt(sub.getAttributes().getNamedItem("h").getNodeValue());
                    areaY = Integer.parseInt(sub.getAttributes().getNamedItem("y").getNodeValue());
                    areaX = Integer.parseInt(sub.getAttributes().getNamedItem("x").getNodeValue());
                }

            }


            // Create the new Area object and add to list of Areas that populate the board
            Area area = new Area(areaName, null, neighbors, extras, pl, extraPl, numOfShots, areaX, areaY, areaH, areaW, takesCoordinates);
            areas.add(area);
        }

        // Temporarily manually creating and adding trailer
        ArrayList<String> tNeighbors = new ArrayList<String>();
        ArrayList<Player> tPl = new ArrayList<Player>();
        ArrayList<Player> tePl = new ArrayList<Player>();
        tNeighbors.add("Main Street");
        tNeighbors.add("Saloon");
        tNeighbors.add("Hotel");
        areas.add(new Area("trailer", null, tNeighbors,null, tPl, tePl, 0, 991,248,194,201, null));

        // Temporarily manually creating and adding office
        ArrayList<String> oNeighbors = new ArrayList<String>();
        ArrayList<Player> oPl = new ArrayList<Player>();
        ArrayList<Player> oePl = new ArrayList<Player>();
        oNeighbors.add("Ranch");
        oNeighbors.add("Secret Hideout");
        oNeighbors.add("Train Station");
        areas.add(new Area("office", null, oNeighbors, null, oePl, tPl, 0, 9, 459, 208, 209, null));

        return areas;
    }

    /**
     * Return total scenes left on the board.
     * @param areas ArrayList of areas to count number of scene cards left on the board.
     * @return Total number of scene cards on the board.
     */
    public int totalScenesLeft(ArrayList<Area> areas) {
        int totalScenes = 0;

        for (Area a : areas) {
            if (a.getScene() != null) {
                totalScenes++;
            }
        }

        return totalScenes;
    }

    /**
     * Method that loops through the list of all active players and places them in the trailers.
     * This method should be called to setup the game AND when a day is completed.
     * @param a ArrayList of areas to grab trailer.
     * @param p ArrayList of players to move to trailer.
     */
    public void playerAreaSetup(ArrayList<Area> a, ArrayList<Player> p) {
        int size = a.size();
        int index = 0;

        for (int i = 0; i < size; i++) {
            if (a.get(i).getName().equalsIgnoreCase("trailer")) {
                index = i;
            }
        }

        // Set every player to start at trailer
        for (Player y : p) {
            y.currentArea = a.get(index);
            a.get(index).addPlayer(y);
        }
    }

    /**
     * Method that clears all areas on the board of scenes.
     * @param areas ArrayList of areas to clear scene cards.
     */
    public void clearBoard(ArrayList<Area> areas) {

        // Clear each area on the board of any remaining scenes.
        for (Area a : areas) {
            // Set scene to null
            a.updateScene(null);
        }
    }

    /**
     * Boolean to check and ensure that the board is cleared properly before we attempt to repopulate it.
     * @param areas ArrayList of areas.
     * @return True if all areas don't have any scenes. If at least one area still has a scene, returns false.
     */
    public boolean isBoardClear(ArrayList<Area> areas) {
        boolean flag = true;

        for (Area a : areas) {
            if (a.getScene() != null && a.getScene().isFlipped()) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * Method that populates areas with random scenes. The scenes (once used) are removed from the ArrayList of scenes.
     * @param areas ArrayList of areas.
     * @param scenes ArrayList of scenes.
     */
    public void populateAreas(ArrayList<Area> areas, ArrayList<Scene> scenes) {

        if(isBoardClear(areas)) {
            // Shuffle scenes to ensure random order
            Collections.shuffle(scenes);
            int len = scenes.size();
            int aSize = areas.size();

            // For each area, if we are NOT the trailer nor the office, populate area with new scene
            for (int i = 0; i < aSize; i++) {
                if(!areas.get(i).getName().equalsIgnoreCase("trailer") && !areas.get(i).getName().equalsIgnoreCase("office")) {

                    // populate this area with the last scene in scenes list
                    areas.get(i).updateScene(scenes.get(len - 1));

                    // remove last scene from list to prevent duplicate usage
                    scenes.remove(len - 1);
                    len--;
                }
            }
        }

    }

    /**
     * Method that rewards a player if they were an actor on a scene and the scene wrapped up filming.
     * @param p Player to check area.
     * @return String to print to screen/display to GUI.
     */
    public String rewardActors(Player p) {
        int role; // To hold which role to give dollars to
        ArrayList<Integer> rolls = Dice.roll(p.currentArea.getScene().getBudget()); // List of dice rolls

        Collections.sort(rolls); // Sort from low to high
        Collections.reverse(rolls); // Reverse to high to low

        // Display each dice roll
        System.out.println("\nRolls: ");
        for (Integer n : rolls) {
            System.out.println(n);
        }

        // Go through each die roll
        for (int i = 0; i < rolls.size(); i++) {

            // Start at highest role and go down based on die
            role = ((i + 1) * (p.currentArea.getScene().getNumberOfRoles() - 1)) % p.currentArea.getScene().getNumberOfRoles();

            // If the role is not available (aka, a Player is taking that role), reward die rolled in dollars to that Player
            if (!p.currentArea.getScene().getAllRoles().get(role).isAvailable()) {
                p.currentArea.getScene().getAllRoles().get(role).getActor().updateDollars(rolls.get(i));
            }
        }

        return "Distributing dollars to starring roles.";
    }

    /**
     * Method that takes in an area and rewards each person filling an extra role at the time of wrapping.
     * If no one (extra) is present on the set, no rewards are dished out.
     * @param a Area to check for extras.
     * @return String to print to screen/display to GUI.
     */
    public String rewardExtras(Area a) {
        ArrayList<Player> extras = a.getExtras();

        // If there is someone on this area when it completes...
        if(extras.size() != 0 && extras != null) {
            for (Player p : extras) {
                int award = p.getCurrentRole().getRoleRank();
                p.updateDollars(award);
            }
        }

        return "Distributing dollars to extras.";
    }

    /**
     * Method to be called at the end of the game. This function will calculate each users score then print
     * the winner and all players based on their score.
     * @param players ArrayList of players.
     * @return String to print to screen/display to GUI of the winner of the game.
     */
    public String printWinner(ArrayList<Player> players) {

        int highest = 0;
        String winner = "";

        // For each player, calculate the score and set the value to final score
        for (Player p : players) {
            p.setFinalScore(p.getCredits() + p.getDollars() + (5 * p.getRank()));
            if (p.getFinalScore() > highest) {
                highest = p.getFinalScore();
                winner = p.getName();
            }
        }

        return "\n***** Winner: " + winner + "\tScore:  " + highest + "! *****\n";
        //System.out.println("Results: ");
        //for (Player p : players) {
        //    System.out.println("\tPlayer: " + p.getName() + "\tScore: " + p.getFinalScore());
        //}
    }

    // Return list of areas
    public ArrayList<Area> getAreas() {
        return areas;
    }

    // Return list of scenes
    public ArrayList<Scene> getScenes() {
        return scenes;
    }

    // Return max days
    public int getMaxDays() {
        return maxDays;
    }

    // Return list of players
    public ArrayList<Player> getPlayers() {
        return players;
    }

    // Return the current day
    public int getCurrentDay() {
        return currentDay;
    }

    /**
     * Increments the current day by 1.
     */
    public void incrementCurrentDay() {
        currentDay++;
    }
}