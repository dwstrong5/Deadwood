import java.lang.reflect.Array;
import java.util.ArrayList;

public class Area {

    private String name;  // Name of area
    private Scene areaScene; // Scene card for the area
    private ArrayList<String> adjacentAreas; // Neighboring areas
    private ArrayList<Role> roles; // Extra roles for area
    private ArrayList<Player> allPlayers = new ArrayList<Player>(); // List of all Players in area
    private ArrayList<Player> extras = new ArrayList<Player>(); // List of Players that are extras in this area
    private int shots; // Hold shots for area
    private int x; // X coordinate to place scene card
    private int y; // Y coordinate to place scene card
    private int h; // Height of scene card
    private int w; // Width of scene card
    private ArrayList<Integer> shotCoords; // Shot coordinates (x, y, h, w, x, y, h, w, ...)

    /**
     * Empty constructor.
     */
    public Area(){}

    /**
     * Constructor that takes in parameters upon setup.
     * @param name Name of area.
     * @param scene Scene card in area.
     * @param areas Neighboring areas.
     * @param roles Roles in area.
     * @param players Players in area.
     * @param extras Extras in area.
     * @param shots Number of shots in area.
     * @param x X coordinate to place scene card.
     * @param y Y coordinate to place scene card.
     * @param h Height of scene card.
     * @param w Width of scene card.
     * @param coords ArrayList of shot coordinates (x, y, h, w, x, y, h, w, ...).
     */
    public Area(String name, Scene scene, ArrayList<String> areas, ArrayList<Role> roles, ArrayList<Player> players,
                ArrayList<Player> extras, int shots, int x, int y, int h, int w, ArrayList<Integer> coords) {
        this.name = name;
        this.areaScene = scene;
        this.adjacentAreas = areas;
        this.roles = roles;
        this.allPlayers = players;
        this.extras = extras;
        this.shots = shots;
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
        this.shotCoords = coords;
    }

    // Return list of shot coordinates
    public ArrayList<Integer> getShotCoordinates() {
        return shotCoords;
    }

    // Return x coordinate of scene card
    public int getX() { return x; }

    // Return y coordinate of scene card
    public int getY() { return y; }

    // Return height of scene card
    public int getH() { return h; }

    // Return width of scene card
    public int getW() { return w; }

    // Method that returns the list of Extras for this particular area
    public ArrayList<Player> getExtras() {
        return extras;
    }

    // Return the Scene hosted in this area
    public Scene getScene() {
        return areaScene;
    }

    /**
     * Update the area with a new scene.
     * @param s Scene for area.
     */
    public void updateScene(Scene s) {
        areaScene = s;
    }

    // Get number of players working on a role in an area
    public int getNumOfActors() {
        return extras.size() + areaScene.getOccupiedRoles().size();
    }

    /**
     * Add a new player to the area. Player is NEITHER an actor or extra.
     * Method first checks to see if player is already in the list of players.
     * @param p Player.
     */
    public void addPlayer(Player p) {
        if (allPlayers != null && !allPlayers.contains(p)) {
            allPlayers.add(p);
        } else {
            System.out.println("Player is already in the area.");
        }
    }

    /**
     * Remove a player from the area. Player is NEITHER an actor or extra.
     * @param p Player.
     */
    public void removePlayer(Player p) {
     if (extras.contains(p)) {
            System.out.println("Cannot remove Player, Player is an extra!");
        } else {
            allPlayers.remove(p);
        }
    }

    /**
     * Method first checks to see if the player is already in the list of actors or extras.
     * If not, add player to the list. Else print to the screen that the player is an extra.
     * @param p Player.
     */
    public void addExtra(Player p) {
        if(!extras.contains(p)) {
            extras.add(p);
        } else {
            System.out.println("Player is already an actor or an extra.");
        }
    }

    // get Name of Area
    public String getName() {
        return name;
    }

    // get the current number of shots remaining for this area
    public int getShots() {
        return shots;
    }

    // Set the shot counter for this area
    public void setShots(int n) {
        shots = n;
    }

    /**
     * Method takes in the current player and determines which roles (both on card and off card) the player is
     * eligible for based on the current players rank. If none, prints none.
     * @param p Player.
     */
    public void printEligibleRoles(Player p) {
        int pRank = p.getRank();

        // Get available extra-roles for the Player
        ArrayList<Role> eRoles = new ArrayList<Role>();
        ArrayList<Role> aRoles = new ArrayList<Role>();
        for (Role r : roles) {
            if (r.getRoleRank() <= pRank && r.isAvailable()) {
                eRoles.add(r);
            }
        }

        // Get available acting roles for the Player
        if (!areaScene.isFlipped() && areaScene != null) {
            for (Role r : areaScene.getAvailableRoles()) {
                if(r.getRoleRank() <= pRank && r.isAvailable()) {
                    aRoles.add(r);
                }
            }
        }

        // Check if there are any roles available
        if (eRoles.size() == 0 && aRoles.size() == 0) {
            System.out.println("No available roles for your rank!");
        } else {
            if (aRoles.size() != 0) {
                System.out.println("Available starring roles: ");
                for (Role r : aRoles) {
                    System.out.println(r.getName() + ", Level: " + r.getRoleRank());
                }
            }
            if (eRoles.size() != 0) {
                System.out.println("Available extra roles: ");
                for (Role r : eRoles) {
                    System.out.println(r.getName() + ", Level: " + r.getRoleRank());
                }
            }
        }

    }

    /**
     * Loops through the area and returns a list of extras and acting roles on the hosted scene card
     * that are eligible for the player to hold based on the player's current rank.
     * @param p Player
     * @return ArrayList of roles that the player can take.
     */
    public ArrayList<Role> getEligibleRoles(Player p) {
        int pRank = p.getRank();

        // Get available extra-roles for the Player
        ArrayList<Role> allEligibleRoles = new ArrayList<Role>();
        for (Role r : roles) {
            if (r.getRoleRank() <= pRank && r.isAvailable()) {
                allEligibleRoles.add(r);
            }
        }

        // Get available acting roles for the Player
        if (!areaScene.isFlipped() && areaScene != null) {
            for (Role r : areaScene.getAvailableRoles()) {
                if (r.getRoleRank() <= pRank && r.isAvailable()) {
                    allEligibleRoles.add(r);
                }
            }
        }
        return allEligibleRoles;
    }

    /**
     * If scene exists.
     * @return True if scene exists, else, no scene in area, false.
     */
    public Boolean sceneExists() {
        return !(areaScene == null);
    }

    /**
     * Checks if choice is a neighbor.
     * @param choice String of area name.
     * @return If it is a neighbor, return true, else (not a neighbor) false.
     */
    public Boolean isNeighbor(String choice) {
        for (String n : adjacentAreas) {
            if (choice.equalsIgnoreCase(n)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if choice of player is an eligible role.
     * @param p Player.
     * @param choice String of role name.
     * @return True if eligible role, else, (not an eligible role) false.
     */
    public Boolean isEligibleRole(Player p, String choice) {
        // Check through each eligible role on that scene card for that Player and return true if choice exists
        for (Role r : getEligibleRoles(p)) {
            if (r.getName().equalsIgnoreCase(choice)) {
                return true;
            }
        }

        // Otherwise, false
        return false;
    }

    /**
     * Print neighbors of current area.
     */
    public void printNeighbors() {
        for (String n : adjacentAreas) {
            System.out.print(n + ". ");
        }
    }

    // Return the list of adjacent neighbors of area
    public ArrayList<String> getNeighbors() {
        return adjacentAreas;
    }

    // Return the list of all players on this area (including from scene card)
    public ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Clear extras from this area once the set is wrapped.
     */
    public void clearExtras() {
        extras.clear();
    }
}
