package net.geekgrandad.apps;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;


public class Devices {
    public static void main(String[] args) throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();

        printInetAddress("localHost", localHost);
        
        String hostName = localHost.getHostName();
        String canonicalHostName = localHost.getCanonicalHostName();
        printByName("  by" + hostName, hostName);
        printByName("  by" + canonicalHostName, canonicalHostName);
        
        System.out.println();
        
        System.out.println("Full list of Network Interfaces:");
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        if (en == null) {
            System.out.println("got null from NetworkInterface.getNetworkInterfaces()");
        } else for (int networkInterfaceNumber = 0; en.hasMoreElements(); networkInterfaceNumber++) {
          NetworkInterface intf = en.nextElement();
          
          System.out.println();
          String ifaceId = "networkInterface[" + networkInterfaceNumber + "]";
          System.out.println("  " + ifaceId + ".name: " + intf.getName());
          System.out.println("  " + ifaceId + ".displayName: " + intf.getDisplayName());
          
          Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
          for (int addressNumber = 0; enumIpAddr.hasMoreElements(); addressNumber++) {
            InetAddress ipAddr = enumIpAddr.nextElement();
            System.out.println();
            printInetAddress("    " + ifaceId + ".address[" + addressNumber + "]", ipAddr);
          }
        }
    }

    private static void printByName(String prefix, String canonicalHostName)
            throws UnknownHostException {
        System.out.println();
        InetAddress[] allMyIps = InetAddress.getAllByName(canonicalHostName);
        for (int i = 0; i < allMyIps.length; i++) {
            String subPrefix = prefix + "[" + i + "]";
            System.out.println(subPrefix);
            System.out.println();
            InetAddress myAddress = allMyIps[i];
            printInetAddress("  " + subPrefix, myAddress);
        }
    }

    private static void printInetAddress(String prefix, InetAddress myAddress) {
        System.out.println(prefix + ".toString: " + myAddress);
        System.out.println(prefix + ".hostName: " + myAddress.getHostName());
        System.out.println(prefix + ".canonicalHostName: " + myAddress.getCanonicalHostName());
        System.out.println(prefix + ".getHostAddress: " + myAddress.getHostAddress());
    }
 }
