package me.tye;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;

public class Main {
public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
  if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
    help();
    return;
  }

  //return if a command was entered that doesn't exist.
  if (!(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("exists"))) {
    help();
    return;
  }

  System.out.println("Finding default gateway.");

  //Gets the gateway to send the Upnp packet to
  GatewayDiscover discover = new GatewayDiscover();
  discover.discover();
  GatewayDevice validGateway = discover.getValidGateway();

  if (validGateway == null) {
    System.out.println("No valid gateway found!");
    return;
  }

  switch (args[0].toLowerCase()) {

    case "add" -> {
      try {
        int internalPort = Integer.parseInt(args[1]);
        int externalPort = Integer.parseInt(args[2]);
        addPort(validGateway, internalPort, externalPort, args[3].toUpperCase()); //Makes sure that UDP/TCP is uppercase.

      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid port!\nE.g: 25565");
      }
    }

    case "remove" -> {
      try {
        int externalPort = Integer.parseInt(args[1]);
        removePort(validGateway, externalPort); //Makes sure that UDP/TCP is uppercase.

      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid port!\nE.g: 25565");
      }
    }

    case "exists" -> {
      try {
        int externalPort = Integer.parseInt(args[1]);
        portExists(validGateway, externalPort); //Makes sure that UDP/TCP is uppercase.

      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid port!\nE.g: 25565");
      }
    }

  default -> {
    help();
  }

  }


}

/**
 Displays the help message text
 */
private static void help() {
  System.out.println("""
        Commands:
        java -jar <Filename> help - Shows this command.
        java -jar <Filename> add <Internal IPV4 Port> <External IPV4 Port> <TCP/UDP> - Opens a new port entry for the given IPV4 port with either TCP or UDP, whichever is specified, for this device.
        java -jar <Filename> remove <External IPV4 Port> - Removes the mapping for the given external IPV4 port for this device if it exist.
        java -jar <Filename> exists <External IPV4 Port> - Checks if a external IPV4 port is already forwarded for this device.
        
        
        Examples:
        java -jar <Filename> add 25565 25565 TCP - Adds the port mapping for 25565 to 25565 for TCP.
        java -jar <Filename> remove 25565 - Removes the port mapping for the external port 25565.
        java -jar <Filename> exists 25565 - Checks if the external IPV4 port exists or not.
        """);
}


/**
 Creates a new port mapping for the given ports for the given protocol on the gateway device.
 * @param validGateway The gateway to add the mapping to.
 * @param internalPort The internalPort to open.
 * @param externalPort The externalPort to open.
 * @param protocol The protocol to open the ports with.
 */
private static void addPort(GatewayDevice validGateway, int internalPort, int externalPort, String protocol) throws IOException, SAXException {
  System.out.println("Adding port mapping \""+internalPort+":"+externalPort+"\" \""+protocol+"\".");


  if (!(protocol.equals("UDP") || protocol.equals("TCP"))) {
    System.out.println("Invalid protocol. Please enter either \"UDP\" or \"TCP\".");
  }

  boolean portExists = validGateway.getSpecificPortMappingEntry(externalPort, protocol, new PortMappingEntry());

  if (portExists) {
    System.out.println("A mapping already exist for the external port \""+externalPort+"\"!");
    return;
  }


  boolean portAdded = validGateway.addPortMapping(externalPort, internalPort, validGateway.getLocalAddress().getHostAddress(), protocol,
      "A port opened by "+InetAddress.getLocalHost().getHostAddress()); //Puts the local ip address of the device that opened the port in the description.

  if (!portAdded) {
    System.out.println("The port mapping \""+internalPort+":"+externalPort+"\" \""+protocol+"\" could not be added to the gateway.");
    return;
  }

  System.out.println("The port mapping \""+internalPort+":"+externalPort+"\" \""+protocol+"\" was successfully added to the gateway.");
}

/**
 Removes the port mapping for the given external port.
 * @param validGateway The gateway to remove the port mapping from.
 * @param externalPort The external port to remove the mapping of.
 */
private static void removePort(GatewayDevice validGateway, int externalPort) throws IOException, SAXException {
  System.out.println("Deleting the port mapping for \""+externalPort+"\".");

  //Checks if the mapping is UDP or TCP.
  boolean portExistsUDP = validGateway.getSpecificPortMappingEntry(externalPort, "UDP", new PortMappingEntry());
  boolean portExistsTCP = validGateway.getSpecificPortMappingEntry(externalPort, "TCP", new PortMappingEntry());

  String protocol = null;

  if (portExistsTCP) protocol = "TCP";
  if (portExistsUDP) protocol = "UDP";

  if (protocol == null) {
    System.out.println("\""+externalPort+"\" isn't in any mapping");
    return;
  }


  boolean deletedPort = validGateway.deletePortMapping(externalPort, protocol);

  if (!deletedPort) {
    System.out.println("The port mapping for \""+externalPort+"\" could not be deleted.");
    return;
  }

  System.out.println("Deleted the port mapping for \""+externalPort+"\".");

}

/**
 Checks if the port mapping for the given external port exists.
 * @param validGateway The gateway to check the port mapping from.
 * @param externalPort The external port to check the mapping of.
 */
private static void portExists(GatewayDevice validGateway, int externalPort) throws IOException, SAXException {
  System.out.println("Checking if \""+externalPort+"\" is mapped.");


  boolean portExistsUDP = validGateway.getSpecificPortMappingEntry(externalPort, "UDP", new PortMappingEntry());
  boolean portExistsTCP = validGateway.getSpecificPortMappingEntry(externalPort, "TCP", new PortMappingEntry());

  if (portExistsUDP) {
    System.out.println("\""+externalPort+"\" is mapped with \"UDP\".");
    return;
  }

  if (portExistsTCP) {
    System.out.println("\""+externalPort+"\" is mapped with \"TCP\".");
    return;
  }

  System.out.println("\""+externalPort+"\" isn't mapped.");
}

}