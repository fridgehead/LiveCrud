LIVECRUD  v0.2 "it gets worse"
------

a crappy livecoding environment based on processing


CONTROLS
-------

SHIFT+ENTER = compile current code but do not run it
CTRL+ENTER = compile and run current tab

arrow keys = move around
CTRL-left/right = move to next space 
SHIFT- left/right = move to start/end of line

backspace = durrrrrrrr
CTRL+BACKSPACE = clear back to start of line
SHIFT+BACKSPACE = clear entire line
DELETE - HURRRRRRR

CTRL+1-5 = switch to that tab

shift + space = show snippet window, press number to insert that snippet. Anything else to clear it


GUI
------

Icons in top left are the states of the different tabs, red is failed compilation, yellow means tab is uncompiled, red is an error. There is a marker underneath to show which is active and which is running

Error highlighting is bad, it only highlights the line with the problem

Top right is the beat marker, hit CTRL+SPACE for 5 beats to set the rate. This will then call onBeat() with every beat.

FUNCTIONS
------

onBeat() - called with every beat
onHalfBeat()
onQuarterBeat()
setup()	 -setup code that is run first
draw()   - main drwing loop

OBJECTS
-------

all processing methods must be prefixed with "p.", thats the PApplet object
"fft" is a minim based FFT, read the freq bands for run



LIMITATIONS
-----------
dont try and import any extra libraries, it'll fail

