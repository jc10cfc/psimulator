# WARNING: these informations are for old psimulator1 project #

# Prerequisities #
  * installed needed parts (see SystemRequirements)
  * unpacked zip archive with simulator

Examples are written for linux, on windows just change ".sh" suffix with ".bat".

# Server startup #
Start server in terminal with command:
```
./start_server.sh <config> <port> -n
```
Where
  * **`<config>`** is XML file with network settings (routers, interfaces, )
  * **`<port>`** is number specifying from which port number will routers will be listening on. If not specified value 4000 is used.
  * **`-n`** is for loading only skelet of routers, interfaces, NAT, .. (completely without settings), **this is optional argument**

Sometimes there is handy to have some network device, which no one can change their configuration. Such routers are used for network structure.

After running script the server will write list of computers/routers with port numbers on which they listen

Server writes also some service information about what is going on in virtual network.

## Example ##
For starting server with empty configuration (just with skelet of network = cables between routers):
```
./start_server.sh laborka.xml 3000 -n
```

For starting server with full configuration:
```
./start_server.sh laborka.xml 3000 
```


# Connecting clients to server #
Use one terminal window for every connection to router (linux, cisco). There is a possibility to connect remotely.

  * to connect to a linux router
```
./linux.sh <port>
```

  * to connect to a cisco router
```
./cisco.sh <port>
```

Where `<port>` is port number where given router is listening - find it out from server console.

On both routers are implemented **help** command (in czech language) and **help\_en** (in english).