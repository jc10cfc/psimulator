# System requirements #

  * **Java Runtime Environment version 7+**
> > To run the simulator it is necessary to download JRE version 7 or higher from address: http://www.oracle.com/technetwork/java/javase/downloads/

  * **Telnet client**

> You can use the built-in client but if you don't like it you can download putty (http://www.putty.org/) on Windows or just use telnet in case of linux/unix system.



# Instructions for installing psimulator2 #

Download and unzip: http://code.google.com/p/psimulator/downloads/list

User guide is available in pdf in the archive.


# Instructions for running psimulator2 #
Psimulator2 is devided into two parts - frontend and backend. Frontend is designed to easily create network topology and display sent or dropped packets in network. Backend handles simulations and everything else under the hood.

## Create and configure network, send packets, display sent and dropped packets ##
### Create the network ###
Run the simulator frontend (GUI) in the command line:
```
java -jar psimulator2_frontend.jar
```
> (with the correct path to the jar file) or with double-click (it depends how your OS is set).

In frontend choose `New Project` then create your own desired network (hint: F1 -> 1. Network creation).
I assume you created your network topology. If you want you can set IP addresses on interfaces with right-click on devices.

Now you can save created network to file via menu: File -> Save.

### Configure the network ###
In toolbar press the start button to run backend with current topology.

After startup, backend prints telnet ports of all virtual network devices you can connect to (typically started on 11000) and the telnet port on which the frontend is automatically connected to listen to simulator events (typically on 12000).

This information can be displayed by display log button, which is accessible from menu bar, when the simulation is active.

To connect to virtual network device type in linux command line:
```
telnet localhost <port of the requested device>
```
> on windows use putty with telnet protocol and port of the requested device.

Devices can be configured via built-in graphical telnet client:
  1. right-click on desired device
  1. click on `Open telnet`


Then you can configure linux and cisco routers in command line environment.


### Display sent and dropped packets ###
After entering the simulation mode you can turn on packet capturing: `Capture` button.


After successful connection to the Event server you can turn on packet capturing: `Capture` button.


Send ICMP request from one device to another:
  1. connect to device1 via telnet
  1. type `ping IP`, with IP address of some interface on device2
Now you can watch captured packets in frontend.


### Saving configuration ###
For saving configuration of all devices you can run save command on any device:
```
save
```
This command saves configured network to the currently open configuration file.
Or you can save it to a different file:
```
save new_configuration_file.xml
```





## Configure already created network with built-in graphical telnet client ##
  1. Run frontend, then open already created network file.
  1. Start the simulation mode.
  1. Right-click on device and "Open telnet".
And now you are ready to type commands with this new telnet window.




## Connect Psimulator2 to real network ##

### System requirements and instalation ###

This extra requirements are needed for connecting simulator to real network:

  1. library libpcap or wincap - included in the project archive. Install libpcap on linux-based system and winpcap on Windows systems.
  1. library jnetpcap - java wrapper for libpcap and winpcap. Download corresponding version from http://jnetpcap.com/download and extract to Psimulator2 folder.
  1. On OS Windows place jnetpcap.dll into C:\windows\system\
  1. On linux-based system place jnetpcap.so into system shared libraries folder (typically /usr/lib).
  1. On linux-based systems it is necessary to launch simulator frontend with super user privileges.


### Configuration ###

  1. In frontend add `Real PC` to virtual network (icon with house), attach cable to this new Real PC and some other device. In properties of Real PC select interface on the host pc, that is connected to real network.
  1. Start the simulation.
  1. After starting the simulation, use command rnetconn on any virtual device to open up connection with real network. For more information about this command use rnetconn help.

In order to successfully connect to a real network, the user is required to prevent limitations defined on the real network. This means setting the same address of the second and third layers on the interface connected to the real network. User is also needed to set routing on a simulated device
accordingly. In the case that user would want to communicate to the real network from multiple different addresses or networks, it is needed to modify the system routing table accordingly.




&lt;hr&gt;



**If you encounter any exception, please send us exception log ([mailing list](http://groups.google.com/group/psimulator2-developers)) - in current directory should be file named "psimulator2\_exceptions\_yyyy-mm-dd.txt" with short description what you were just doing. Thank you!**