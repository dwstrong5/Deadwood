CSCI 345 - Winter 2021

Authors:    Luther Nguyen
	        Don Strong
	   
Professor:	Shri Mare

TA:	   		Jason Cook

Project:   	Deadwood Studios

1) The only bug that we experienced during our testing is evident when removing the last shot counter on the Train Station. For some reason, when completing that set/scene only, the shot counter doesn’t actually get removed until that current player’s turn ends. We’re not fully sure why this is the case, though we suspect that it has something to do with the storage (ArrayList) used to store each shot counter. We initially suspected that the issue was due to the shot being removed outside of one of the player loops. We moved the call to removeShot() to an inner and outer loop, but both of these actions only made the problem worse. If given more time, we would certainly dive deeper and further investigate the problem and ensure that it isn’t happening with any other scenes (although it doesn’t appear to be). Beyond that, we would certainly benefit from additional time to fix other bugs that are bound to arise and make general improvements to the gameplay.

2) For the most part, the project specifications were fairly straightforward and easy to follow. One assumption that needed to be made was the positioning of the players in the event that one area became too “crowded.” For this, we tried positioning all player’ die away from the cards, the shots, and any other graphical component for the area and scene. Instead, they are positioned in a line, sequentially. This may lead to some bleeding over the edge of the board, but it was the only way to ensure none of the gameplay specific graphical components were obscured.

3) We tested our code in numerous ways such as calling the methods into the main and testing each individual method. This allowed us to check whether the method is returning what we wanted. Not only did we call the methods into the main, we also used it in our buttons to check if the buttons work properly. If the GUI works as intended, we continue to the next method to test.

4) The biggest challenge was utilizing the MVC design pattern. At first, we were greatly confused on where the brains of the program were supposed to be in. In assignment 4, the main Deadwood file served as our model and contained the “brains” for our game. However, when implementing the MVC pattern, it wasn’t exactly clear where the brains would be “moved to.”

5) For me (Don) personally, I’m not very familiar or comfortable with Java Swing. I know the debate between which means of implementing GUI in Java is fairly exhausted, but I would enjoy trying to achieve the same result using JavaFX. I personally find JavaFX easier to work with and think the feel of the graphics are a bit more modern. I have more experience working with JavaFX on various projects in the past and was hoping that it was an option when I first learned that we would be creating a GUI. Additionally, I would really enjoy learning how to make this available in a mobile application. I know that IntelliJ has plugins for Kotlin (which seems the be the new standard for Android development), and would be interested in maybe porting it over to be used on Android devices.
