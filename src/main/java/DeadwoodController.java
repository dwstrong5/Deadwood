public class DeadwoodController implements DeadwoodViewObserver {

    Deadwood model = null;
    DeadwoodView view = null;

    boolean didMove = false; // To keep track if Player moved already
    boolean didWork = false; // To keep track if Player worked (either act or rehearse) already
    boolean newRole = false; // To keep track of Player recently getting a new role so Player can't work on same turn
    int currentPlayer = 0; // For current Player
    String lastInput = ""; // To hold the last input the Player chose

    /**
     * Initializes deadwood controller by creating a new Deadwood model with given number of players,
     * initializes view, & displays GUI by setting up cards, shots, player dice, players in trailer,
     * player info, and current day.
     * @param args Argument from console to setup XML files and number of players.
     * @param view Initialize view to be signaled by this class (controller).
     */
    public DeadwoodController(String[] args, DeadwoodView view) {
        model = new Deadwood(args); // Initialize model/board
        this.view = view; // For controller to signal the view

        // Display cards throughout board
        view.setupCards(model);

        // Display initial shots for each area
        view.updateShots(model);

        // Display each Player's distinctive colored die
        view.setupPlayersDice(model);

        // Display Players in trailer
        view.setupPlayers(model);

        // Display first Player's information
        view.displayPlayerInfo(model.getPlayers().get(0));

        // Display current day
        view.displayDay(model);
    }

    /**
     * Based on the input (button pressed), either move, take role, act, rehearse, upgrade, or end.
     * @param input String of button user pressed.
     */
    public void gotUserInput(String input) {

        // Reset view
        view.removeMoveDisplay();
        view.removeRoleDisplay();

        // Buttons are either Move, Take Role, Act, Rehearse, Upgrade, or End
        // Else, the button has to be a neighboring area or available role
        if (input.equalsIgnoreCase("Move")) {

            // If Player is not taking a role, check if player moved already.
            // Else, cannot move while taking a role.
            if (model.getPlayers().get(currentPlayer).getRoleStatus()) {

                // If Player didn't move yet, display neighbors and flag that the player just chose move.
                // Else, can't move because already moved.
                if (!didMove) {

                    view.displayMoveOptions(model.getPlayers().get(currentPlayer));

                    lastInput = "Move";

                } else {

                    view.displayMessage("Cannot move because already moved.");

                }

            } else {

                view.displayMessage("Cannot move while taking a role.");

            }

        } else if (input.equalsIgnoreCase("Take Role")) {

            // If Player is not on a role, display available roles.
            // Else, display that the Player is already on a role.
            if (model.getPlayers().get(currentPlayer).getRoleStatus()) {

                // If current area has a scene card, display available starring roles and extra roles and flag that player just chose take role.
                // Else, display that there is no set.
                if (model.getPlayers().get(currentPlayer).currentArea.sceneExists()) {

                    // Print only the available roles that the Player is eligible for
                    view.displayRoleOptions(model.getPlayers().get(currentPlayer));

                    lastInput = "Take Role";

                } else {

                    view.displayMessage("No set in this area.\n");

                }

            } else {

                view.displayMessage("Already on a role!");

            }

        } else if (input.equalsIgnoreCase("Act")) {

            // If Player is on a role, check if Player recently took a role.
            // Else, display that the Player is not on a role.
            if (!model.getPlayers().get(currentPlayer).getRoleStatus()) {

                // If Player did not recently take a role, check if acted/rehearsed already.
                // Else, can't act because Player just got a new role.
                if (!newRole) {

                    // If didn't act/rehearse yet, Player acts and display success or failed.
                    // Else, can't act because already acted/rehearsed.
                    if (!didWork) {

                        view.displayMessage(model.getPlayers().get(currentPlayer).act());

                        // If current area shot reaches 0, remove scene card.
                        // Else, display amount of shots left.
                        if (model.getPlayers().get(currentPlayer).currentArea.getShots() == 0) {

                            // Print confirmation to the player
                            view.displayMessage("Last shot removed! Scene is wrapped for this area!");

                            // If area has more than 1 actor (includes starring roles and extras), reward actors
                            if (model.getPlayers().get(currentPlayer).currentArea.getNumOfActors() > 1) {

                                // Reward starring actors
                                view.displayMessage(model.rewardActors(model.getPlayers().get(currentPlayer)));

                                // Reward extras
                                view.displayMessage(model.rewardExtras(model.getPlayers().get(currentPlayer).currentArea));

                            }

                            // Remove the Scene from the Area since set is wrapped
                            model.getPlayers().get(currentPlayer).currentArea.updateScene(null);

                            // Clear Extras to avoid any being carried over to next day
                            model.getPlayers().get(currentPlayer).currentArea.clearExtras();

                            // Free all players currently on this area and reset rehearsal chips
                            for (Player p : model.getPlayers().get(currentPlayer).currentArea.getAllPlayers()) {

                                p.vacateRole();
                                p.resetRehearsalChips();

                            }

                            // Update view by removing the card since the scene is wrapped.
                            // Also update locations to not on a role.
                            view.updateCards(model);
                            view.updatePlayerLocations(model);

                        } else {

                            view.displayMessage("Shots left for this area: " + model.getPlayers().get(currentPlayer).currentArea.getShots() + ".");

                        }

                        didWork = true; // Signify that the Player already worked for their turn

                        // Update view of current Player info and number of shots
                        view.updatePlayerInfo(model.getPlayers().get(currentPlayer));
                        view.updateShots(model);

                    } else {

                        view.displayMessage("Already acted/rehearsed. Can't rehearse and act on same turn or act twice on same turn.");

                    }

                } else {

                    view.displayMessage("Cannot act because recently just got a role.");

                }

            } else {

                view.displayMessage("Cannot act because not on a role.");

            }

        } else if (input.equalsIgnoreCase("Rehearse")) {

            // If Player is on a role, check if Player recently took a role.
            // Else, display that the Player is not on a role.
            if (!model.getPlayers().get(currentPlayer).getRoleStatus()) {

                // If Player did not recently take a role, check if acted/rehearsed already.
                // Else, can't rehearse because Player just got a new role.
                if (!newRole){

                    // If didn't act/rehearse yet, check if Player can get a rehearsal chip.
                    // Else, can't rehearse because already acted or rehearse.
                    if (!didWork) {

                        // If rehearse chips is less than 6, rehearse for Player and update view.
                        // Else, Player has already earned max rehearse chips.
                        if (model.getPlayers().get(currentPlayer).getRehearseChips() < 5) {

                            model.getPlayers().get(currentPlayer).rehearse();

                            view.displayMessage(model.getPlayers().get(currentPlayer).getName() + " earned a rehearse chip!");

                            view.updatePlayerInfo(model.getPlayers().get(currentPlayer));

                            didWork = true; // Signify that the Player already worked for their turn

                        } else {

                            view.displayMessage(model.getPlayers().get(currentPlayer).getName() + " has earned the max rehearse chips of 5.");

                        }

                    } else {

                        view.displayMessage("Already acted/rehearsed. Can't rehearse and act on same turn or rehearse twice on same turn.");

                    }

                } else{

                    view.displayMessage("Cannot rehearse because recently just got a role.");

                }

            } else {

                view.displayMessage("Cannot rehearse because not on a role.");

            }

        } else if (input.equalsIgnoreCase("Upgrade")) {

            // If Player is not on Casting Office, display message.
            // Else, display upgrade options and allow for input.
            if (!model.getPlayers().get(currentPlayer).getCurrentArea().getName().equalsIgnoreCase("office")) {

                view.displayMessage("Must be located at the Casting Office to upgrade!");

            } else {

                // Display and take in choice
                int rankChoice = view.displayCastingOffice();

                // If choice is greater than 0, check if rank is allowed for Player.
                if (rankChoice > 0) {

                    // If no available rank, display message.
                    // Else, go through each available rank of Player
                    if (model.office.getOptions(model.getPlayers().get(currentPlayer)).size() == 0) {

                        view.displayMessage("Rank choice is under current rank or\nnot enough dollars/credits.");

                    } else {

                        // Go through each available rank of Player
                        for (Integer rank : model.office.getOptions(model.getPlayers().get(currentPlayer))) {

                            // If rank is part of the list, upgrade Player
                            // Else, display message that Player cannot upgrade to that rank
                            if (rankChoice == rank) {

                                // String where dollars were paid, credits were paid, or choose own currency
                                String payment = model.office.upgradeRank(model.getPlayers().get(currentPlayer), rankChoice);

                                // If payment ends up being choose, display and allow Player to choose
                                if (payment.equalsIgnoreCase("Choose")) {

                                    // 2 == Dollars, 1 == Credits, 0 == Cancel (Cancel should do nothing)
                                    int currencyChoice = view.displayCurrencyChoice();

                                    // If currency choice is dollars or credits, upgrade Player
                                    if (currencyChoice == 2 || currencyChoice == 1) {
                                        view.displayMessage(model.office.deductCurrency(model.getPlayers().get(currentPlayer), rankChoice, currencyChoice)); // Display payment
                                    }

                                } else {

                                    view.displayMessage(payment); // Display payment
                                    view.displayMessage(model.getPlayers().get(currentPlayer).getName() + " upgraded to rank " + rankChoice + " successfully!");

                                }

                                // Update view
                                view.updatePlayerRanks(model);
                                view.updatePlayerInfo(model.getPlayers().get(currentPlayer));

                            } else {

                                view.displayMessage("Rank choice is under current rank or\nnot enough dollars/credits.");

                            }

                        }

                    }

                }

            }

        } else if (input.equalsIgnoreCase("End")) {

            // If not the last Player, increment to next Player
            // Else, reset to first Player
            if (currentPlayer != model.getPlayers().size() - 1) {
                currentPlayer++;
            } else {
                currentPlayer = 0;
            }

            view.updatePlayerInfo(model.getPlayers().get(currentPlayer));

            // If there is 1 card left in view, increment day and initialize board and everything
            if (view.sceneCards.size() == 1 && model.getCurrentDay() < model.getMaxDays()) {

                model.incrementCurrentDay();

                view.updateDay(model);

                // Clear board for new day
                model.clearBoard(model.getAreas());

                // (Re)populate with new Scenes
                model.populateAreas(model.getAreas(), model.getScenes());

                // Return Players to the trailers and start a new day
                model.playerAreaSetup(model.getAreas(), model.getPlayers());

                view.setupCards(model);
                view.updateShots(model);
                view.updatePlayerLocations(model);

            } else if (model.getCurrentDay() == (model.getMaxDays() - 1)) {
                view.displayMessage(model.printWinner(model.getPlayers()));

                System.exit(0);
            }

            // RESET
            didMove = false;
            didWork = false;
            newRole = false;

        } else {

            // If the input before the current input was "Move", allow Player to move to an area
            // Else if the input before the current input was "Take Role", allow Player to take role
            if (lastInput.equalsIgnoreCase("Move")) {

                // Remove player from list of current players on current area
                model.getPlayers().get(currentPlayer).currentArea.removePlayer(model.getPlayers().get(currentPlayer));

                // Go through each area and find the area the Player chose
                for (Area a : model.getAreas()) {

                    // If the input matches, move Player
                    if (a.getName().equalsIgnoreCase(input)) {

                        // Move Player to new area
                        model.getPlayers().get(currentPlayer).move(a);

                        // Add Player to new areas list of current Players
                        a.addPlayer(model.getPlayers().get(currentPlayer));

                        // Flip card in view (areas that aren't the Trailer or Office since those 2 don't have scene cards)
                        if (!a.getName().equalsIgnoreCase("Trailer") && !a.getName().equalsIgnoreCase("Office")) {

                            view.flipCard(a);

                        }

                    }

                }

                didMove = true; // True so Player doesn't move on same turn

                view.updatePlayerLocations(model); // Update Player locations

            } else if (lastInput.equalsIgnoreCase("Take Role")) {

                // Go through each available role in the area and set role to Player
                for (Role r : model.getPlayers().get(currentPlayer).currentArea.getEligibleRoles(model.getPlayers().get(currentPlayer))) {

                    // We found the role that the user wants, we must accept the role, mark it
                    // as unavailable, and add the player to the actor or extras list depending
                    // on the type of the role
                    if (r.getName().equalsIgnoreCase(input)) {

                        model.getPlayers().get(currentPlayer).acceptRole(r);
                        r.setAvailability(false);
                        r.assignRole(model.getPlayers().get(currentPlayer));

                        // If the player takes a starring (acting) role, add to list of actors
                        // Else add to list of extras
                        if (r.getRoleType()) {

                            model.getPlayers().get(currentPlayer).currentArea.getScene().addPlayerToActors(model.getPlayers().get(currentPlayer));

                        } else {

                            model.getPlayers().get(currentPlayer).currentArea.addExtra(model.getPlayers().get(currentPlayer));

                        }

                    }

                }

                System.out.println("Successfully taken role " + input + "!\n");

                newRole = true; // Signify that the Player just got a new role on current turn

                view.updatePlayerLocations(model);

                }

        }

    }

}
