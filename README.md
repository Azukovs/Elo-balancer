Utility to sort cs2 players into random teams as equally as possible.
Based on player reported current faceit elo rating (adn current premiere rating if faceit is equal)

Assumptions:
* The sheet must be named `elo.xlsx` and be placed in the same directory as this utility.
* The columns must be named precisely:
  * Discord Nickname
  * Steam Nickname
  * Premier Elo
  * Max premier Elo
  * Faceit Elo
  * Max faceit Elo
* All elo cells must be digits only. No `k`, no commas, no other symbols. If no elo value is known - put zero.
* Assumed that players in the sheet are in signup order. If there are extra players that don't make a full team,
  those are put in the reserve from the bottom of the list.

Functionality:
* All players are read into player objects and put in a list, extra players put in reserve
* Player list is ordered by current faceit elo.
* Teams are initially filled one-by-one to fill the teams and have an approximate balance.
* Rebalancing flow is as follows:
  * Evaluate current faceit elo sum for every team, and save the difference between min and max.
  * Pick any 2 random teams.
  * Swap 2 random players in those teams.
  * Re-evaluate difference. If it is smaller - save the current player setup.
  * Repeat for 100'000 random swaps.
* Repeat the rebalancing flow 20 times to have different team setup options.
* Sort the options by the minimum faceit elo difference
* Save the best 5 team setups and write players to a text file.

Check the example `elo.xlsx` file how it should be provided.
Check the example `teams.txt` file for example output data.

To build an executable .exe via Launch4j:
* launch `mvn package` to build .jre package
* Copy the .jre into another new folder
* Copy your jre directory into the same folder and name it `jre`
* Open Launch4j and read the attached config file `launch4j_config.xml`
* In Basic tab, set jar to the directory where jar was copied.
* Execute.

After .exe is built - can zip and share to whoever. 