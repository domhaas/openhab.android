## Introduction

This is fork of the OpenHAB Android Client which adds an api for calling commands on the android device.
Currently supported api-calls:
- "/" : check if webservice is running
- "/display" : get information if display is on/off (return: ON/OFF)
- "/display/on" : turn on display
- "/display/off" : turn of display
- "/notify/sound" : plays the default notification sound
- "/speak?text=" : take use of the tts

Some commands may require a rooted devices.
Tested on (rooted) Shield K1 Tablet.

## Trademark Disclaimer

Product names, logos, brands and other trademarks referred to within the openHAB website are the
property of their respective trademark holders. These trademark holders are not affiliated with
openHAB or our website. They do not sponsor or endorse our materials.
