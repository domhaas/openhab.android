# Introduction

This is a fork of the OpenHAB Android Client which adds an api for calling commands on the android device.

## Idea
I wanted to have some kind of notification if there's something happening inside my home. Also I wanted to turn off the display of my Android Interface when I'm not at home or if I'm sleeping.
Android got a very nice TTS onboard, without the need of some kind of Cloud TTS crap. So I decided to pimp the (very nice) Android Client of OpenHAB. The good thing in this solution is, that you just need a (cheap) Android Tablet. A rooted device will give you also abilities like turning off the display.
I also tried the OpenHAB2 tts, but I was not really happy, because you need extra software on your OpenHAB-Server which is (I'm using a pi) was not very nice for me.
Also, I didn't like those extra margins, so I removed them from the client ;)

## API-Calls
Currently supported api-calls:
- "/" : check if webservice is running
- "/display" : get information if display is on/off (return: ON/OFF)
- "/display/on" : turn on display
- "/display/off" : turn of display
- "/notify/sound" : plays the default notification sound
- "/speak?text=" : take use of the tts

Some commands may require a rooted devices.
Tested on (rooted) Shield K1 Tablet.

## Settings
The API can be enabled inside the habdroid settings. You can find also settings for the port the API is listening to (default 5000).

## Example
You can send commands to the Android device by using sendHttpGetRequest inside scripts. Example:
- sendHttpGetRequest("http://192.168.2.5:5000/notify/sound")
- sendHttpGetRequest("http://192.168.2.5:5000/speak?text=this%20is%20a%20test")
- And of course you can just test the api by callig those urls inside your browser.

## Warnings
This is just a proof of concept I'm currently running within my smarthome. I will give no guarantees at all. Also the apk is maybe overwriting your currently installed habdroid installation. So take care, you have been warned :)

## The future
- adding more features
- improve reliability: currently the Api is crashing if the Network Connection is lost - this happens sometimes if the android device is losing connection.
- putting those raw http calls inside an OpenHAB Plugin would be nice.

## Download
[Test-Release v0.1](https://github.com/domhaas/openhab.android/releases/download/0.1/mobile-debug.apk)

## Trademark Disclaimer

Product names, logos, brands and other trademarks referred to within the openHAB website are the
property of their respective trademark holders. These trademark holders are not affiliated with
openHAB or our website. They do not sponsor or endorse our materials.
