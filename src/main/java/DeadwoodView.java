import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DeadwoodView implements ActionListener {

    public ArrayList<DeadwoodViewObserver> observers = new ArrayList(); // For collecting user inputs
    JLayeredPane layeredPane; // For layering objects (images, text, labels, etc.)
    JLabel board, playerName, playerDollars, playerCredits, playerRehearsalChips, day; // Board & info labels
    ArrayList<JLabel> players = new ArrayList<JLabel>(); // To hold Player labels (each Player's die)
    ArrayList<JLabel> cardsBack = new ArrayList<JLabel>(); // To hold the back of cards (for flipping when time arrives)
    ArrayList<JLabel> sceneCards = new ArrayList<JLabel>(); // To hold each scene card on the board
    ArrayList<JLabel> shots = new ArrayList<JLabel>(); // To hold shots (to remove certain shots when time arrives)
    ArrayList<JButton> neighbors = new ArrayList<JButton>(); // To hold neighbor buttons for Player to choose (to also remove later)
    ArrayList<JButton> eligibleRoles = new ArrayList<>(); // To hold buttons of eligible roles for Player to choose (to also remove later)
    JPanel buttonPanel; // For buttons
    JFrame frame; // Frame of window

    /**
     * Initializing view sets up the GUI.
     * This involves creating a board image as background, player information panel,
     * and (user) action buttons including the title of that action buttons panel.
     */
    public DeadwoodView() {
        // Board image and size
        board = new JLabel(new ImageIcon("src/main/resources/img/board.png"));
        board.setBounds(0, 0, 1200, 900);

        // Layers to layer objects
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1400, 928);

        // Board at default (most lowest/behind)
        layeredPane.add(board, Integer.valueOf(0));

        // Player information title
        JLabel playerInfoTitle = new JLabel("<HTML><U>PLAYER INFORMATION:</U></HTML>", JLabel.CENTER);
        playerInfoTitle.setBounds(1200, 0, 200, 30);
        playerInfoTitle.setOpaque(true);
        playerInfoTitle.setFont(new Font("HelveticaNeue", Font.BOLD, 16));
        playerInfoTitle.setBackground(Color.LIGHT_GRAY);
        layeredPane.add(playerInfoTitle, Integer.valueOf(1));

        // Action buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1));
        buttonPanel.setBounds(1200, 150, 200, 270);
        buttonPanel.setBackground(Color.ORANGE);

        // Action title
        JLabel actionTitle = new JLabel("<HTML><U>Actions:</U></HTML>");
        actionTitle.setFont(new Font("HelveticaNeue", Font.BOLD, 16));
        buttonPanel.add(actionTitle);

        // Add each action button
        String[] buttonText = {"Move", "Take Role", "Act", "Rehearse", "Upgrade", "End"};
        for (int i = 0; i < buttonText.length; i++) {
            JButton b = new JButton(buttonText[i]);
            b.setBounds(0, 0, 200, 30);
            b.addActionListener(this);
            buttonPanel.add(b);
        }

        layeredPane.add(buttonPanel, Integer.valueOf(1));
    }

    /**
     * Register/adds button observer to get user input.
     * @param ob The observer/controller.
     */
    public void register(DeadwoodViewObserver ob) {
        observers.add(ob);
    }

    /**
     * Notify the controller.
     * @param input The button the player/user pressed.
     */
    public void notify(String input) {
        for (DeadwoodViewObserver ob : observers) {
            ob.gotUserInput(input);
        }
    }

    /**
     * Show window of game.
     * Initializes frame and displays it.
     */
    public void show() {
        // Frame of whole window
        frame = new JFrame("Deadwood");
        frame.add(layeredPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1400, 928));
        frame.setLayout(null);
        frame.setVisible(true);
    }

    /**
     * Retrieves data of cards from model and updates view by displaying cards in their areas.
     * @param model The data used to setup cards.
     */
    public void setupCards(Deadwood model) {
        int i = 0; // To increment through back of card ArrayList & the actual cards ArrayList

        for (Area a : model.getAreas()) {
            if (!a.getName().equalsIgnoreCase("Office") && !a.getName().equalsIgnoreCase("Trailer")) {
                JLabel cardBack = new JLabel(new ImageIcon("src/main/resources/img/cardback.png")); // Image of back of card
                JLabel card = new JLabel(new ImageIcon("src/main/resources/img/card_" + a.getScene().getImage())); // Image of card in area

                // Bounds of cards
                cardBack.setBounds(a.getX(), a.getY(), a.getW(), a.getH());
                card.setBounds(a.getX(), a.getY(), a.getW(), a.getH());

                // Add cards to ArrayLists
                cardsBack.add(cardBack);
                sceneCards.add(card);

                // Add cards to pane
                layeredPane.add(sceneCards.get(i), Integer.valueOf(1));
                layeredPane.add(cardsBack.get(i), Integer.valueOf(2)); // Back of card is on top of scene card (to show card hasn't been flipped yet)
                i++;
            }
        }
    }

    /**
     * Retrieves data of players from model and updates view and displays players in trailer.
     * @param model The data used to setup players.
     */
    public void setupPlayers(Deadwood model) {
        int x, y, w, h;

        for (int i = 0; i < model.getPlayers().size(); i++) {
            // Player 1 at center
            x = model.getPlayers().get(i).currentArea.getX() + 75;
            y = model.getPlayers().get(i).currentArea.getY() + 70;
            w = model.getPlayers().get(i).currentArea.getW();
            h = model.getPlayers().get(i).currentArea.getH();

            // For the rest of the Players, place based on given coordinates
            if (i > 0) {
                x = returnGroupCoords(i, x, y)[0];
                y = returnGroupCoords(i, x, y)[1];
            }

            // Create label of Player
            JLabel player = new JLabel();
            player.setIcon(new ImageIcon(model.getPlayers().get(i).getPlayerDieColor()));
            player.setBounds(x+3, y-74, w, h);

            players.add(player);
            layeredPane.add(players.get(i), Integer.valueOf(3));
        }
    }

    /**
     * Retrieves data of players from model and sets up each player's die color.
     * @param model The data used to setup each player's die color.
     */
    public void setupPlayersDice(Deadwood model) {
        // Game requires 2 players
        model.getPlayers().get(0).setPlayerDieColor("src/main/resources/img/dice_b" + model.getPlayers().get(0).getRank() + ".png");
        model.getPlayers().get(1).setPlayerDieColor("src/main/resources/img/dice_c" + model.getPlayers().get(1).getRank() + ".png");

        if (model.getPlayers().size() >= 3) {
            model.getPlayers().get(2).setPlayerDieColor("src/main/resources/img/dice_g" + model.getPlayers().get(2).getRank() + ".png");
        }

        if (model.getPlayers().size() >= 4) {
            model.getPlayers().get(3).setPlayerDieColor("src/main/resources/img/dice_o" + model.getPlayers().get(3).getRank() + ".png");
        }

        if (model.getPlayers().size() >= 5) {
            model.getPlayers().get(4).setPlayerDieColor("src/main/resources/img/dice_p" + model.getPlayers().get(4).getRank() + ".png");
        }

        if (model.getPlayers().size() >= 6) {
            model.getPlayers().get(5).setPlayerDieColor("src/main/resources/img/dice_r" + model.getPlayers().get(5).getRank() + ".png");
        }

        if (model.getPlayers().size() >= 7) {
            model.getPlayers().get(6).setPlayerDieColor("src/main/resources/img/dice_v" + model.getPlayers().get(6).getRank() + ".png");
        }

        if (model.getPlayers().size() >= 8) {
            model.getPlayers().get(7).setPlayerDieColor("src/main/resources/img/dice_y" + model.getPlayers().get(7).getRank() + ".png");
        }
    }

    /**
     * Retrieves data of shots from model and updates shots (can be used to initialize shots).
     * @param model The data used to update shots for each area.
     */
    public void updateShots(Deadwood model) {
        // Remove all shots from view
        for (int i = 0; i < shots.size(); i++) {
            layeredPane.remove(shots.get(i));
        }

        // Clear the ArrayList of shots
        shots.clear();

        // Go through each area
        for (Area a : model.getAreas()) {
            // Only check areas that are not the trailer or office (those areas don't have shots)
            if (!a.getName().equalsIgnoreCase("Trailer") && !a.getName().equalsIgnoreCase("Office")) {
                // Each coordinate in the ArrayList of shot coordinates
                // Note: Start at last group of shot coordinate so removing shots would remove the last shot and not the first
                int x = a.getShotCoordinates().size() - 4;
                int y = a.getShotCoordinates().size() - 3;
                int w = a.getShotCoordinates().size() - 2;
                int h = a.getShotCoordinates().size() - 1;

                // Go through each area and add shots to ArrayList along with their coordinates
                for (int i = a.getShots(); i > 0; i--) {
                    JLabel shot = new JLabel(new ImageIcon("src/main/resources/img/shot.png"));
                    shot.setBounds(a.getShotCoordinates().get(x), a.getShotCoordinates().get(y), a.getShotCoordinates().get(w), a.getShotCoordinates().get(h));

                    shots.add(shot);

                    // Increment through ArrayList of shot coordinates (x, y, w, h, x, y, w, h, ...)
                    x -= 4;
                    y -= 4;
                    w -= 4;
                    h -= 4;
                }
            }
        }

        // For each shot in ArrayList, display in view
        for (int i = 0; i < shots.size(); i++) {
            layeredPane.add(shots.get(i), Integer.valueOf(2));
        }
    }

    /**
     * Takes in first player and displays their information in view.
     * @param p The player to display the info of.
     */
    public void displayPlayerInfo(Player p) {
        // Player name
        playerName = new JLabel(" Player: " + p.getName(), JLabel.LEFT);
        playerName.setBounds(1200, 30, 200, 30);
        playerName.setOpaque(true);
        playerName.setFont(new Font("HelveticaNeue", Font.BOLD, 16));
        playerName.setBackground(Color.LIGHT_GRAY);

        // Player dollars
        playerDollars = new JLabel(" Dollars: $" + p.getDollars(), JLabel.LEFT);
        playerDollars.setBounds(1200, 60, 200, 30);
        playerDollars.setOpaque(true);
        playerDollars.setFont(new Font("HelveticaNeue", Font.BOLD, 16));
        playerDollars.setBackground(Color.LIGHT_GRAY);

        // Player credits
        playerCredits = new JLabel(" Credits: " + p.getCredits() + " credits", JLabel.LEFT);
        playerCredits.setBounds(1200, 90, 200, 30);
        playerCredits.setOpaque(true);
        playerCredits.setFont(new Font("HelveticaNeue", Font.BOLD, 16));
        playerCredits.setBackground(Color.LIGHT_GRAY);

        // Player rehearsal chips
        playerRehearsalChips = new JLabel(" Rehearsal Chips: " + p.getRehearseChips() + " chips", JLabel.LEFT);
        playerRehearsalChips.setBounds(1200, 120, 200, 30);
        playerRehearsalChips.setOpaque(true);
        playerRehearsalChips.setFont(new Font("HelveticaNeue", Font.BOLD, 16));
        playerRehearsalChips.setBackground(Color.LIGHT_GRAY);

        layeredPane.add(playerName, Integer.valueOf(1));
        layeredPane.add(playerDollars, Integer.valueOf(1));
        layeredPane.add(playerCredits, Integer.valueOf(1));
        layeredPane.add(playerRehearsalChips, Integer.valueOf(1));
    }

    /**
     * Updates player information in view.
     * @param p The player to display the info of.
     */
    public void updatePlayerInfo(Player p) {
        playerName.setText(" Player: " + p.getName());
        playerDollars.setText(" Dollars: $" + p.getDollars());
        playerCredits.setText(" Credits: " + p.getCredits() + " credits");
        playerRehearsalChips.setText(" Rehearsal Chips: " + p.getRehearseChips() + " chips");
    }

    /**
     * Retrieves data from model and updates cards in view by removing wrapped scenes.
     * @param model The data used to update cards.
     */
    public void updateCards(Deadwood model) {
        int i = 0; // To increment through ArrayList of scene cards on view

        // Go through each area
        for (Area a : model.getAreas()) {

            // If a scene does not exist in the area, remove the scene card. Note: Ignore trailer and office
            if (!a.sceneExists() && !a.getName().equalsIgnoreCase("Trailer") && !a.getName().equalsIgnoreCase("Office")) {
                layeredPane.remove(sceneCards.get(i));
            }

            i++;
        }
    }

    /**
     * Display the first day (Day 0) in view.
     * @param model The data used to to grab day.
     */
    public void displayDay(Deadwood model) {
        day = new JLabel(" DAY " + model.getCurrentDay(), JLabel.CENTER);
        day.setBounds(1200, 420, 200, 30);
        day.setOpaque(true);
        day.setFont(new Font("HelveticaNeue", Font.BOLD, 16));
        day.setBackground(Color.YELLOW);

        layeredPane.add(day, Integer.valueOf(1));
    }

    /**
     * Retrieves data from model and updates day in view.
     * @param model The data used to update the current day.
     */
    public void updateDay(Deadwood model) {
        day.setText(" DAY " + model.getCurrentDay());
    }

    /**
     * Retrieves data from players from model and updates locations of Players in view.
     * @param model The data used to take player locations.
     */
    public void updatePlayerLocations(Deadwood model) {
        int x, y, w, h;

        for (int i = 0; i < model.getPlayers().size(); i++) {
            // If on a role, get coordinates of role
            // Else, if on trailer or casting office, get coordinates of trailer and casting office
            // Else, get coordinates of area
            if (!model.getPlayers().get(i).getRoleStatus()) {
                x = model.getPlayers().get(i).currentRole.getCoords().get(0);
                y = model.getPlayers().get(i).currentRole.getCoords().get(1);
                w = model.getPlayers().get(i).currentRole.getCoords().get(2);
                h = model.getPlayers().get(i).currentRole.getCoords().get(3);

                // If Player is on a starring role, offset coordinates to match scene card coordinates
                if (model.getPlayers().get(i).currentRole.isActor()) {
                    x += model.getPlayers().get(i).currentArea.getX();
                    y += model.getPlayers().get(i).currentArea.getY();
                }
            } else if (model.getPlayers().get(i).currentArea.getName().equalsIgnoreCase("Trailer") || model.getPlayers().get(i).currentArea.getName().equalsIgnoreCase("Office")) {
                // Player 1 at center
                x = model.getPlayers().get(i).currentArea.getX() + 78;
                y = model.getPlayers().get(i).currentArea.getY() - 4;
                w = model.getPlayers().get(i).currentArea.getW();
                h = model.getPlayers().get(i).currentArea.getH();

                // For the rest of the Players, place based on given coordinates
                if (i > 0) {
                    x = returnGroupCoords(i, x, y)[0];
                    y = returnGroupCoords(i, x, y)[1];
                }
            } else {
                x = model.getPlayers().get(i).currentArea.getX();

                // If more than 1 Player is in an area, increment x so Players don't cover each other's die
                if (model.getPlayers().get(i).currentArea.getAllPlayers().size() > 1) {
                    x += (40 * i);
                }

                y = model.getPlayers().get(i).currentArea.getY() + 85; // Added 85 for offset downwards of area given
                w = model.getPlayers().get(i).currentArea.getW();
                h = model.getPlayers().get(i).currentArea.getH();
            }

            players.get(i).setBounds(x, y, w, h);
        }
    }

    /**
     * Return x & y coordinates when Player's are grouped together in Trailer and Casting Office.
     * @param i Current player.
     * @param x X coordinate of current player.
     * @param y Y coordinate of current player.
     * @return Array containing x & y coordinates of current player -> [x, y].
     */
    public int[] returnGroupCoords(int i, int x, int y) {
        if (i == 1) {
            // Player 2 at center right
            x += 45;
        } else if (i == 2) {
            // Player 3 at center left
            x -= 45;
        } else if (i == 3) {
            // Player 4 at top right
            x += 45;
            y -= 45;
        } else if (i == 4) {
            // Player 5 at top left
            x -= 45;
            y -= 45;
        } else if (i == 5) {
            // Player 6 at bottom right
            x += 45;
            y += 45;
        } else if (i == 6) {
            // Player 7 at bottom left
            x -= 45;
            y += 45;
        } else if (i == 7) {
            // Player 8 at bottom center
            y += 45;
        }

        return new int[]{x, y};
    }

    /**
     * Retrieves data of players and updates every player's die rank in view.
     * @param model Data used to grab each player's rank.
     */
    public void updatePlayerRanks(Deadwood model) {
        for (int i = 0; i < model.getPlayers().size(); i++) {
            String playerDie = model.getPlayers().get(i).playerDieColor; // Get file string of Player die
            String newPlayerDie = playerDie.replaceAll("[0-9]", String.valueOf(model.getPlayers().get(i).getRank())); // Create new file string of Player die
            model.getPlayers().get(i).setPlayerDieColor(newPlayerDie); // Set the new file string to Player

            players.get(i).setIcon(new ImageIcon(model.getPlayers().get(i).getPlayerDieColor())); // Update the die
        }
    }

    /**
     * Retrieves data from player and updates the view to show the areas the player can move to.
     * @param p The player to grab their neighbors.
     */
    public void displayMoveOptions(Player p) {
        for (int i = 0; i < p.currentArea.getNeighbors().size(); i++) {
            JButton areaBorder = new JButton();

            if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Main Street")) {
                areaBorder.setActionCommand("Main Street");
                areaBorder.setBounds(969, 28, 205, 115);
            }  else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Trailer")) {
                areaBorder.setActionCommand("Trailer");
                areaBorder.setBounds(991, 248, 208, 209);
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Hotel")) {
                areaBorder.setActionCommand("Hotel");
                areaBorder.setBounds(969, 740, 205, 115);
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Saloon")) {
                areaBorder.setActionCommand("Saloon");
                areaBorder.setBounds(632, 280, 205, 115);
                layeredPane.add(areaBorder, Integer.valueOf(2));
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Bank")) {
                areaBorder.setActionCommand("Bank");
                areaBorder.setBounds(623, 475, 205, 115);
                layeredPane.add(areaBorder, Integer.valueOf(2));
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Church")) {
                areaBorder.setActionCommand("Church");
                areaBorder.setBounds(623, 734, 205, 115);
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Jail")) {
                areaBorder.setActionCommand("Jail");
                areaBorder.setBounds(281, 27, 205, 115);
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("General Store")) {
                areaBorder.setActionCommand("General Store");
                areaBorder.setBounds(370, 282, 205, 115);
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Ranch")) {
                areaBorder.setActionCommand("Ranch");
                areaBorder.setBounds(252, 478, 205, 115);
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Train Station")) {
                areaBorder.setActionCommand("Train Station");
                areaBorder.setBounds(21, 69, 205, 115);
            }  else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Office")) {
                areaBorder.setActionCommand("Office");
                areaBorder.setBounds(9, 459, 208, 209);
            } else if (p.currentArea.getNeighbors().get(i).equalsIgnoreCase("Secret Hideout")) {
                areaBorder.setActionCommand("Secret Hideout");
                areaBorder.setBounds(27, 732, 205, 115);
            }

            areaBorder.setOpaque(false); // Set button invisible
            areaBorder.setContentAreaFilled(false); // Make content area transparent
            areaBorder.setBorderPainted(true); // Make borders visible
            areaBorder.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5)); // Give border color and thickness
            areaBorder.addActionListener(this); // Add an action listener when clicked on

            neighbors.add(areaBorder);

            layeredPane.add(neighbors.get(i), Integer.valueOf(4)); // Add to view
        }
    }

    /**
     * Gets rid of all the move options from view.
     */
    public void removeMoveDisplay() {
        // Remove each neighbor options (blue borders for areas) on display
        for (int i = 0; i < neighbors.size(); i++) {
            layeredPane.remove(neighbors.get(i));
        }

        // Reset GUI based on new components (this case, got rid of neighbor options)
        layeredPane.revalidate();
        // Refresh view
        layeredPane.repaint();

        // Clear ArrayList of neighbor options
        neighbors.clear();
    }

    /**
     * Retrieves data from player and updates the view to show the roles the Player can take.
     * @param p The player to grab their available roles.
     */
    public void displayRoleOptions(Player p) {
        int x, y, w, h;

        for (int i = 0; i < p.currentArea.getEligibleRoles(p).size(); i++) {
            // Get coordinates of role
            x = p.currentArea.getEligibleRoles(p).get(i).getCoords().get(0);
            y = p.currentArea.getEligibleRoles(p).get(i).getCoords().get(1);
            w = p.currentArea.getEligibleRoles(p).get(i).getCoords().get(2);
            h = p.currentArea.getEligibleRoles(p).get(i).getCoords().get(3);

            // If coordinate x is at a specific location (extra roles) offset to the area
            if (x == 20 || x == 83 || x == 145 || x == 53 || x == 115) {
                x += p.currentArea.getX();
                y += p.currentArea.getY();
            }

            JButton roleBorder = new JButton();

            roleBorder.setActionCommand(p.currentArea.getEligibleRoles(p).get(i).getName());
            roleBorder.setBounds(x, y, w, h);
            roleBorder.setOpaque(false); // Set button invisible
            roleBorder.setContentAreaFilled(false); // Make content area transparent
            roleBorder.setBorderPainted(true); // Make borders visible
            roleBorder.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5)); // Give border color and thickness
            roleBorder.addActionListener(this); // Add an action listener when clicked on

            eligibleRoles.add(roleBorder);

            layeredPane.add(eligibleRoles.get(i), Integer.valueOf(4)); // Add to view
        }
    }

    /**
     * Gets rid of all the role options from view.
     */
    public void removeRoleDisplay() {
        // Remove each role options (blue borders for roles) on display
        for (int i = 0; i < eligibleRoles.size(); i++) {
            layeredPane.remove(eligibleRoles.get(i));
        }

        // Reset GUI based on new components (this case, got rid of role options)
        layeredPane.revalidate();
        // Refresh view
        layeredPane.repaint();

        // Clear ArrayList of role options
        eligibleRoles.clear();
    }

    /**
     * Remove back of card in view given the area.
     * @param a Area to remove back of card (flip card).
     */
    public void flipCard(Area a) {
        if (a.getName().equalsIgnoreCase("Train Station")) {
            layeredPane.remove(cardsBack.get(0));
        } else if (a.getName().equalsIgnoreCase("Secret Hideout")) {
            layeredPane.remove(cardsBack.get(1));
        } else if (a.getName().equalsIgnoreCase("Church")) {
            layeredPane.remove(cardsBack.get(2));
        } else if (a.getName().equalsIgnoreCase("Hotel")) {
            layeredPane.remove(cardsBack.get(3));
        } else if (a.getName().equalsIgnoreCase("Main Street")) {
            layeredPane.remove(cardsBack.get(4));
        } else if (a.getName().equalsIgnoreCase("Jail")) {
            layeredPane.remove(cardsBack.get(5));
        } else if (a.getName().equalsIgnoreCase("General Store")) {
            layeredPane.remove(cardsBack.get(6));
        } else if (a.getName().equalsIgnoreCase("Ranch")) {
            layeredPane.remove(cardsBack.get(7));
        } else if (a.getName().equalsIgnoreCase("Bank")) {
            layeredPane.remove(cardsBack.get(8));
        } else if (a.getName().equalsIgnoreCase("Saloon")) {
            layeredPane.remove(cardsBack.get(9));
        }

        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /**
     * Display the upgrade options.
     * @return Rank choice (2-6) or 0 if cancel.
     */
    public int displayCastingOffice() {
        Integer[] options = new Integer[] {2, 3, 4, 5, 6};

        try {
            int input = (Integer) JOptionPane.showInputDialog(frame, "<HTML><B><U>RANK</U> <U>DOLLARS</U> <U>CREDITS</U></B></HTML>\n" +
                            "    2         4              5\n" + "    3         10            10\n" + "    4         18            15\n" +
                            "    5         28            20\n" + "    6         40            25",
                    "Casting Office", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            return input;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Display option of currency to use.
     * @return 0 for Cancel, 1 for Credits, or 2 for Dollars.
     */
    public int displayCurrencyChoice() {
        String[] buttons = {"Cancel", "Credits", "Dollars"};

        int choice = JOptionPane.showOptionDialog(frame, "Choose type of currency to pay with:", "Currency Choice", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, null);

        return choice;
    }

    /**
     * Display a popup message given the string.
     * @param message String to display onto popup message.
     */
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    /**
     * Converts pushed button into string and notifies controller.
     * @param e Button pressed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String userInput = e.getActionCommand();

        notify(userInput);
    }
}
