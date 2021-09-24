import java.util.ArrayList;

public class Role {
    private String name; // Name of role
    private Player playerHoldingRole; // Stores the Player who is occupying this role
    private int roleRank; // Stores the role rank requirement
    private Boolean isActor; // True if the role is a starring actor, false if role is an extra
    private Boolean isAvailable; // True if role is available, else false
    private Scene onSet; // Stores the Scene that this role belongs to.
    private String line; // Line/quote of role
    private ArrayList<Integer> coordList;


    public Role() {}

    public Role(String name, Player p, int rank, boolean actor, boolean available, Scene s, String line, ArrayList<Integer> list) {
        this.name = name;
        this.playerHoldingRole = p;
        this.roleRank = rank;
        this.isActor = actor;
        this.isAvailable = available;
        this.onSet = s;
        this.line = line;
        this.coordList = list;
    }

    // Get name of Role
    public String getName() {
        return name;
    }

    // Boolean to determine if the type of role is an actor (true) or an extra (false)
    public boolean getRoleType() {
        return isActor;
    }

    // Return the Player object who is holding the role
    public Player getActor() {
        return playerHoldingRole;
    }

    // Get the rank requirement for the role
    public int getRoleRank() {
        return roleRank;
    }

    // Check if the role is a starring actor (true) or an extra (false)
    public boolean isActor() {
        return isActor;
    }

    // Check to see if the role is available (true) or not (false)
    public boolean isAvailable() {
        return isAvailable;
    }

    // Set the availability of the role: true for available/open, false for unavailable/closed
    public void setAvailability(boolean b) {
        isAvailable = b;
    }

    // Assign new role to player
    public void assignRole(Player p) {
        playerHoldingRole = p;
    }

    // get Role line
    public String getLine() {
        return line;
    }

    // Return list of coords of role
    public ArrayList<Integer> getCoords() {
        return coordList;
    }
}
