public class Player {

    private String playerName; // Player name
    private int rank; // Player rank
    public int dollars; // Player's amount of dollars
    public int credits; // Player's amount of credits
    private int rehearseChips; // Player's amount of rehearsal chips
    private int finalScore; // Player's final score after game ends
    public Role currentRole; // Role Player is on/taking
    public Scene currentScene; // Scene card Player is on
    public Area currentArea; // Area Player is on
    public String playerDieColor; // Player's die color

    // Empty constructor
    public Player(){}

    // Constructor that takes in parameters upon setup
    public Player(String name, int rank, int dollars, int credits, int rehearseChips) {
        this.playerName = name;
        this.rank = rank;
        this.dollars = dollars;
        this.credits = credits;
        this.rehearseChips = rehearseChips;
        this.finalScore = 0;
        currentRole = null;
        currentScene = null;
        currentArea = null;
        playerDieColor = "";
    }

    // Method used at the end of the game, to store the tallied score for each player
    public void setFinalScore(int num) {
        finalScore = num;
    }

    // Method used at the end of the game to display each Players final score
    public int getFinalScore() {
        return finalScore;
    }

    // Return player rank
    public int getRank() {
        return rank;
    }

    // Return player Name
    public String getName() {
        return playerName;
    }

    // Return player's currency
    public int getDollars() {
        return dollars;
    }

    // Return player's credits
    public int getCredits() {
        return credits;
    }

    // Return player's rehearsal chips
    public int getRehearseChips() {
        return rehearseChips;
    }

    // Return player's current Role
    public Role getCurrentRole() {
        return currentRole;
    }

    // Return player's current Area
    public Area getCurrentArea() {
        return currentArea;
    }

    // Upgrades a player's rank. Max rank is 6
    public void upgradeRank(int newRank) {
        rank = newRank;
    }

    // Moves Player to area
    public void move(Area area) {
        currentArea = area;
    }

    // Change Player's role to role given
    public void acceptRole(Role role) {
        currentRole = role;
    }

    // If a Player elects to act, roll a dice and give dollars/credits to Player and remove shot accordingly
    public String act() {

        int roll = Dice.roll();
        roll += rehearseChips;

        // Either success or fail
        if (roll >= currentArea.getScene().getBudget()) {

            // Is a starring actor, earn 2 credits and decrement shot.
            // Else an extra, earn 1 dollar, 1 credit, and decrement shot.
            if (currentRole.isActor()) {
                credits += 2;
                int shots = currentArea.getShots();
                shots--;
                currentArea.setShots(shots);
                return "Success! You got 2 credits.";
            }
            else {
                dollars += 1;
                credits += 1;
                int shots = currentArea.getShots();
                shots--;
                currentArea.setShots(shots);
                return "Success! You got $1 and 1 credit.";
            }
        }
        else {

            // Is a starring actor, earn nothing.
            // Else an extra, earn a dollar.
            if (currentRole.isActor()) {
                return "Fail! You got nothing.";
            }
            else {
                dollars += 1;
                return "Fail! You got $1.";
            }
        }

    }

    // If a Player elects to rehearse, increment their rehearsal chips. Max is 5
    public void rehearse() {
        rehearseChips++;
    }

    // Add money to current Player's total
    public void updateDollars(int num) {
        if(num >= 0) {
            dollars += num;
        }
    }

    // Vacate the currentRole. This method should only be called when the Player completes that role.
    public void vacateRole() {
        currentRole = null;
    }

    // Check to see if a Player is currently occupying a Role. Not on a role is true, on a role is false.
    public Boolean getRoleStatus() {
        return currentRole == null;
    }

    // Method to deduct cash from the Player in the event that they upgrade. This method DOES NOT check
    // to make sure that the currency doesn't fall below zero. The check is performed in the CastingOffice before
    // this method is called.
    public void deductMoney(int num) {
        dollars = dollars - num;
    }

    // Method to deduct credits from the Player in the event that they upgrade. This method DOES NOT check
    // to make sure that the currency doesn't fall below zero. The check is performed in the CastingOffice before
    // this method is called.
    public void deductCredits(int num) {
        credits = credits - num;
    }

    // Method that resets rehearsal chips back to zero. This method is called when a Player finishes off a role on a
    // Scene.
    public void resetRehearsalChips() {
        rehearseChips = 0;
    }

    // Sets a die color to Player (string is the file path)
    public void setPlayerDieColor(String dieColor) {
        this.playerDieColor = dieColor;
    }

    // Gets the Player's die color
    public String getPlayerDieColor() {
        return playerDieColor;
    }
}
