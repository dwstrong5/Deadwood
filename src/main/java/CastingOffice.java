import java.util.*;

public class CastingOffice {

    /**
     * Iterates through the list of ranks and their respective purchase requirements. Returns a list of all
     * eligible ranks that can be purchased based on Players current quantities for credits/dollars.
     * @param p Player for getting available options.
     * @return ArrayList of the ranks player can upgrade to.
     */
    public ArrayList<Integer> getOptions(Player p) {
        // Obtain the Player's current rank
        int low = p.getRank();
        int dollars = p.getDollars();
        int credits = p.getCredits();

        ArrayList<Integer> options = new ArrayList<Integer>();

        // Iterate through each remaining possible rank and see if the player can purchase with either dollars or points.
        // If so, add the rank to the list to be returned to the user.
        for(int i = low+1; i < 7; i++) {

            if(i == 2) {
                if(dollars >= 4 || credits >= 5) {
                    options.add(2);
                }
            } else if (i == 3) {
                if(dollars >= 10 || credits >= 10) {
                    options.add(3);
                }
            } else if (i == 4) {
                if(dollars >= 18 || credits >= 15) {
                    options.add(4);
                }
            } else if (i == 5) {
                if(dollars >= 28 || credits >= 20) {
                    options.add(5);
                }
            } else {
                if(dollars >= 40 || credits >= 25) {
                    options.add(6);
                }
            }
        }
        return options;
    }

    /**
     * Upgrades the player's rank based on the choice passed in as arg. If the choice is not a valid option,
     * returns invalid to the user. Otherwise the rank adjustment is made and the necessary credits/dollars
     * is deducted from the player's total.
     * @param p Player to upgrade rank.
     * @param choice Rank to upgrade player to.
     * @return String to print/display to GUI of what currency was used.
     */
    public String upgradeRank(Player p, int choice) {
        int dCost, cCost; // To hold dollar cost and credit cost
        String currencyChoice; // Payment choice

        int pDollars = p.getDollars(); // Player dollars
        int pCredits = p.getCredits(); // Player credits

        Scanner console = new Scanner(System.in); // Take in currency choice

        ArrayList<Integer> list = getOptions(p); // List of options

        if (list.contains(choice)) {
            dCost = dollarCost(choice); // Dollar cost
            cCost = creditCost(choice); // Credit cost

            // If has enough dollars, but not enough credits, deduct dollars.
            // Else, if not enough dollars, but enough credits, deduct credits.
            if (pDollars >= dCost && pCredits < cCost) {
                p.deductMoney(dCost);
                p.upgradeRank(choice); // Upgrade player
                return "Paid in dollars!";
            } else if (pDollars < dCost && pCredits >= cCost) {
                p.deductCredits(cCost);
                p.upgradeRank(choice); // Upgrade player
                return "Paid in credits!";
            }
        }

        // If both dollars and credits are enough for the rank, return "Choose"
        // To allow Player to choose the currency
        return "Choose";

    }

    /**
     * Return the cost in dollars to upgrade to a certain rank.
     * @param rank Rank to find dollar cost.
     * @return Dollar cost to upgrade to rank.
     */
    private int dollarCost(int rank) {
        int cost = 0;

        if (rank == 2) {
            cost = 4;
        } else if (rank == 3) {
            cost = 10;
        } else if (rank == 4) {
            cost = 18;
        } else if (rank == 5) {
            cost = 28;
        } else if (rank == 6) {
            cost = 40;
        }

        return cost;
    }

    /**
     * Return the cost in credits to upgrade to a certain rank.
     * @param rank Rank to find credit cost.
     * @return Credit cost to upgrade to rank.
     */
    private int creditCost(int rank) {
        int cost = 0;

        if (rank == 2) {
            cost = 5;
        } else if (rank == 3) {
            cost = 10;
        } else if (rank == 4) {
            cost = 15;
        } else if (rank == 5) {
            cost = 20;
        } else if (rank == 6) {
            cost = 25;
        }

        return cost;
    }

    /**
     * Deduct based on the given currency.
     * @param p Player to upgrade and deduct.
     * @param choice Rank to upgrade player to.
     * @param currencyChoice 1 meaning dollars. 2 meaning credits.
     * @return String to print/display to GUI of what currency was used.
     */
    public String deductCurrency(Player p, int choice, int currencyChoice) {
        // 2 == Dollars, 1 == Credits
        if (currencyChoice == 2) {
            p.deductMoney(dollarCost(choice));
            p.upgradeRank(choice);
            return "Paid in dollars!";
        } else if (currencyChoice == 1) {
            p.deductCredits(creditCost(choice));
            p.upgradeRank(choice);
            return "Paid in credits!";
        }

        return "Failed";
    }
}
