package net.geekgrandad.plugin;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class RFControl {
  private String portName;
  private SerialPort serialPort;
  private static final int TIME_OUT = 2000;
  private static final int DATA_RATE = 57600;
  /** Buffered input stream from the port */

  private OutputStream output;
  private InputStream input;

  public RFControl(String port) throws IOException{
	portName = port;
    initialize();
  }

  public void initialize() throws IOException {
    CommPortIdentifier portId = null;
    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

    // iterate through, looking for the port
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
          .nextElement();
      //System.out.println("Port: " + currPortId.getName());
      if (currPortId.getName().equals(portName)) {
        portId = currPortId;
        break;
      }
    }

    if (portId == null) {
      throw new IOException("Could not find COM port");
    }

    try {
      // open serial port, and use class name for the appName.
      serialPort = (SerialPort) portId
          .open(this.getClass().getName(), TIME_OUT);

      // set port parameters
      serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

      // open the streams
      output = serialPort.getOutputStream();
      input = serialPort.getInputStream();

    } catch (Exception e) {
      System.err.println(e.toString());
      throw new IOException("Could not open 434 COM port");
    }
  }

  public void sendCmd(byte cmd) throws IOException {
    output.write(cmd);
    output.flush();
  }

  public int readByte() throws IOException {
    for (;;) {
      int i = input.read();
      if (i >= 0)
        return (i & 0xFF);
    }
  }
}
