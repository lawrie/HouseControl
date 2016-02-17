package net.geekgrandad.rf;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * Serial Communication with an Arduino or Jeenode or equivalent, with an attached RF transceiver
 * 
 * @author Lawrie Griffiths
 *
 */
public class RFControl {
  private String portName;
  private SerialPort serialPort;
  private int timeOut;
  private int dataRate;
  private String name;

  private OutputStream output;
  private InputStream input;

  public RFControl(String port, int dataRate, int timeOut, String name) throws IOException{
	portName = port;
	this.dataRate = dataRate;
	this.timeOut = timeOut;
	this.name = name;
    initialize();
  }

  public void initialize() throws IOException {
    /*CommPortIdentifier portId = null;
    @SuppressWarnings("rawtypes")
	Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

    // iterate through, looking for the port
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
      if (currPortId.getName().equals(portName)) {
        portId = currPortId;
        break;
      }
    }

    if (portId == null) {
      throw new IOException("Could not find " + name + " COM port");
    }*/

    try {
      // open serial port, and use class name for the appName.
      CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(portName);
      serialPort = (SerialPort) id.open(this.getClass().getName(), timeOut);

      // set port parameters
      serialPort.setSerialPortParams(dataRate, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

      // open the streams
      output = serialPort.getOutputStream();
      input = serialPort.getInputStream();

    } catch (Exception e) {
      System.err.println(e.toString());
      throw new IOException("Could not open " + name + " COM port: " + portName + ": " + e);
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
