Antigravity Racing
==================

By Alec Williams, Ryan Welborn, and Trevor Holland

-----------------------

Setup
------------

In order to play the game, please follow these steps:
1. Set the desired number of players in GameServer (ln 44)
2. Set the IP of the computer hosting the server in GravGame (ln 177) (localhost
if server is on the same machine as the client
3. Launch the server
4. Launch the same number of clients as configured in Step 1
5. Select "Connect" (can be done with mouse or arrow keys)
   1. If the client is not player 1, wait for player 1 to select level
6. If Player 1, select the desired level
7. Play!

Features
--------

Controls:  
W/S: Drive forwards/backwards  
A/D: Turn left, right  
L. Shift: Boost  
Space: Use power-up

The goal of the game is to finish the course with the fastest time possible. Each player has
a set amount of energy. Colliding with walls, being struck with offensive power-ups, using
boost, and falling off of the stage deduct from the player's energy total. When the player
runs out of energy, they will be reset to the last checkpoint with a full energy meter.

There are four types of course obstacle  
1. Boost tiles (blue-colored triangles), which briefly increases the player's maximum speed
2. Slowdown tiles (jagged red squares), which slow the player while they are passing through them
3. Power-up spawners (yellow striped squares), which spawn power-ups for the player to collect
4. Jump tiles (green cones), which launch the player into the air on contact

In addition, there are three types of power-up
1. Boost, which gives the player a free boost charge
2. Spike Trap, which lays a trap that damages and slows opponents on contact
3. Rocket, which launches a projectile that slows and damages opponents on contact

In addition to different themes, each course features several shortcuts that are difficult
to navigate but will save vast amounts of time for those who are willing to challenge them.

Low-bar Goals
-----------
✓ Realtime Game  
✓ Networking (Dumb client)  
✓ Multiplayer (Up to 4 players)  
✓ Power-ups  
✓ Isometric Art (Player will be rendered behind stage when falling off)  
✓ Scrolling World  
✓ UI (Did not manage to implement speed, but current power-up, 
health, # of laps, and race times are present)  
✓ Energy System & Collision (player vs. player collision, player vs. power-up collision, 
"health" and energy are unified, player can use 
health to gain speed, making them faster but increasing their chance of death
and being reset)  
✓ Course Obstacles & Features ("spinning" during a jump to gain boost was ultimately 
removed, as making the player fast enough to rotate 360* would make driving on the track
too difficult to control)