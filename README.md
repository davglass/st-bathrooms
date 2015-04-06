Control the bathrooms
=====================

This is a custom SmartApp that I wrote to control our bathrooms.

Each of the bathrooms in our house have the following in common:

  * Lights over the counter on a unique switch
  * Fan in the ceiling on a unique switch
  * Motion Detector
  * Open/Close sensor on the door


Objective
---------

  * Use motion to activate the primary light
  * On door close, activate the fan
     * Ignore all motion when door is closed
  * When opened, turn off the fan
  * If the light switch is physically clicked, delay motion activation for `x` seconds


Reasoning
---------

The lights should automatically come on when you enter the room, but should not shut off for a long time.
We chose to allow the lights to remain on for ~30 minutes after there is no motion. There are a few reasons for this:

  * If the door was not completely closed, no one wants the lights out while they are doing their business
  * The motion detector doens't alway pick when people are looking closely in the mirror. Brushing teeth, combing hair, etc.
  * When you exit the room, you may click the light switch as you are leaving and opening the door.
     * This caused the lights to come back on after they were manually turned off.
     * If the door has a robe on a hook, the swinging of this will also trigger the lights.
     * It's best to just let them turn off the light, then activate the motion a few moments later.

