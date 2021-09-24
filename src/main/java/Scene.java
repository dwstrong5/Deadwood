import java.util.ArrayList;

public class Scene {

    private String name; // Name of scene card
    private String description; // Description of scene card
    private Boolean flipped; // Stores the status of the Scene. If flipped (done) status is true. If still in progress,then status is false
    private ArrayList<Role> roles; // List of all roles on this Scene (actors only)
    private ArrayList<Player> occupiedRoles; // List of occupied roles (actors only)
    private int budget; // Budget for this Scene
    private String image;

    // Empty Constructor
    public Scene() {}

    // Main Constructor
    public Scene(String n, String d, boolean flipped, ArrayList<Role> roles, ArrayList<Player> oRole,int b, int s, String img) {
        this.name = n;
        this.description = d;
        this.flipped = flipped;
        this.roles = roles;
        this.occupiedRoles = oRole;
        this.budget = b;
        this.image = img;
    }

    // Set the string for the image file
    public void setImage(String s) {
        image = s;
    }

    // Get the string representing the image name
    public String getImage() {
        return image;
    }
    // Set the list of actors actively occupying roles for this Scene
    public void setActorsList(ArrayList<Player> p ) {
        occupiedRoles = p;
    }

    // Get the name of the Scene
    public String getName() {
        return name;
    }

    // Set the name for the Scene
    public void setName(String s) {
        name = s;
    }

    // Set budget for this scene
    public void setBudget(int num) {
        if (num > 0) {
            budget = num;
        }
    }

    // Adds a Player p to the list of actors when p takes a role
    public void addPlayerToActors(Player p) {
        occupiedRoles.add(p);
    }

    // Set the description for the scene
    public void setDescription(String s) {
        description = s;
    }

    // Get status whether the card is flipped (true) or not (false)
    public boolean isFlipped() {
        return flipped;
    }

    // Get the budget for this Scene
    public int getBudget() {
        return budget;
    }

    // Return the total number of roles on this Scene.
    public int getNumberOfRoles() {
        return roles.size();
    }

    // Return a list of all available roles on this Scene
    public ArrayList<Role> getAvailableRoles() {
        // Initialize list
        ArrayList<Role> list = new ArrayList<Role>();

        // Loop through each Role on Scene and add available ones to list
        for(Role r : roles) {
            if(r.isAvailable()) {
                list.add(r);
            }
        }
        return list;
    }

    // Generate and return a list of all Roles that are occupied by Players
    public ArrayList<Role> getOccupiedRoles() {
        // Initialize list
        ArrayList<Role> list = new ArrayList<Role>();

        // Loop through each Role on Scene and add occupied ones to list
        for(Role r : roles) {
            if(!r.isAvailable()) {
                list.add(r);
            }
        }
        return list;
    }

    // Return the list of all roles of scene card
    public ArrayList<Role> getAllRoles() {
        return roles;
    }

    // Set whether this scene is flipped or not
    public void setFlipped(boolean f) {
        flipped = f;
    }

    // Method to set Roles to a predefined list
    public void setRoles(ArrayList<Role> r) {
        roles = r;
    }

    // Method to add a single Role to the list of roles
    public void addRole(Role r) {
        roles.add(r);
    }




}



