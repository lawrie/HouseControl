# HouseControl Overview

HouseControl is a server written in Java that controls home automation devices including those from LightwaveRF.

It is aimed mainly at the UK market.

It is designed to complement the OpenRemote project, which provides Android, iOS and web home automation remote control applications.

Some of the devices that HouseControl supports are:

* LightwaveRF sockets, light switches, light bulbs, window switches, sensors, relays etc.
* EDF or Current Cost Individual appliance monitors
* Jeelabs room nodes and other Jeenode based devices including:
 * OpenEnergyMonitor emonTx
 * HomeAutomationHub Airwick room node
* Music server computers running Spotify
* IP cameras

These devices are supported using home made or open source radio transceivers or via Wifi. For LightwaveRF the Wifi link is not needed.

HouseControl also supports speech synthesis, sending email, Google calendar integration and integration with COSM.

HouseControl implements a simple home control language known as "Housish". There is a simple chat appication that Housish can be typed into.

There is also an android application that supports voice control using Housish.

Other products such as OpenRemote can also send Housish commands.

HouseControl can control your TVs, set top boxes and other media devices using an Arduino based RF to infrared gateway.

HouseControl is configured by an XML file that describes, the floors, rooms and devices in your house. 
This allows control and sensor aggregation for rooms and floors in the house.

HouseControl has a plugin architecture. You can add Java classes that control new devices or which add new services.

in conjunction with OpenRemote, HouseControl can give you a complete open source home automation system.

# Installing HouseControl

1. Download the zip file from github.com/lawrie/HouseControl
2. Unzip it 
3. Ensure Java 6 or 7 is available, e.g. type "java -version" in a console
4. Ensure that you have the required wireless transceivers plugged in and know their COM ports.
5. Edit the conf/house.xml file to describe your house and give port names and numbers
6. Execute bin/housecontrol.bat or bin/housecontrol.sh to execute the server
7. Execute bin/housechat.bat or bin/housechat.sh to run the HouseChat application
8. Type in commands like "Light 1 on".