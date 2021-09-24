To compile and run the game on the lab machines (tested on 28 Feb 2021 @ 7:312pm PST):

Create/change directory to desired directory, open terminal, and execute the following commands:

clone git@gitlab.cs.wwu.edu:cs345_21wi/team_swordfish_345-21wi.git

cd team_swordfish_345-21wi

./compile.sh

./run.sh src/main/resources/board.xml src/main/resources/cards.xml num_of_players

After cloning the repo, you must traverse down one level into the team_swordfish_345-21wi directory. From there, you must execute the compile script to assemble the Deadwood program. Then, you must execute the run.sh script with the usage provided, where num_of_players is replaced with an integer value between 2 and 8 (inclusive). Pay special attention to the names (and order) of the xml files in the final command, otherwise you will experience a variety of errors.
