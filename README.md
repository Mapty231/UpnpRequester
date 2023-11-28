# UpnpRequester

UpnpRequester is a simple command-line based tool for opening & closing ports via Upnp.  
Upnp requires java 15+ to run.
  
  
## Functionality:
Commands:  
- java -jar \<Filename\> help - Shows this command.
- java -jar \<Filename\> add \<Internal IPV4 Port\> \<External IPV4 Port\> \<TCP/UDP\> - Opens a new port entry for the given IPV4 port with either TCP or UDP, whichever is specified, for this device.
- java -jar \<Filename\> remove \<External IPV4 Port\> - Removes the mapping for the given external IPV4 port for this device if it exist.
- java -jar \<Filename\> exists \<External IPV4 Port\> - Checks if a external IPV4 port is already forwarded for this device.  
  
  
Examples:
- java -jar \<Filename\> add 25565 25565 TCP - Adds the port mapping for 25565 to 25565 for TCP.
- java -jar \<Filename\> remove 25565 - Removes the port mapping for the external port 25565.
- java -jar \<Filename\> exists 25565 - Checks if the external IPV4 port exists or not.
  
  
## Credit:
This project relies entirely on the fork of [weupnp](https://github.com/fireduck64/weupnp) by [fireDuck64](https://github.com/fireduck64). Almost all credit goes to fireDuck & the original project of [weupnp](https://github.com/bitletorg/weupnp). I've only made a simple command-line utility to interact with all the work they've put in.
  
  
## Building:
To build this project yourself clone it from git, & run "maven package".