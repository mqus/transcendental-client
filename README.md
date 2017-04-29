# transcendental-client
this repository contains a java client-library and a simple implementation of a client for the transcendental clipboard sync server, which shares your Clipboard across devices and supports not only text, but also **images and other content**.

The Client now mostly works but there may be quite some bugs which i have overlooked.

To let the client run, a running server is necessary, the server is provided in [https://github.com/mqus/transcendental](https://github.com/mqus/transcendental)

Current Version: 0.6.0
## TODO
  - [ ] Tests
  - [ ] One-Transmission-Text-Transfer (TEXT)
  - [ ] Maybe respect and use SendRetryPolicy (current policy: retry, even if the connection is lost for a short time)
  - [ ] An advanced client for the Tray
  - [ ] A client for android
  
## Dependencies
  - Java 1.7 (openjdk or Oracle Java)

for building:
  - Gradle >=3.4 (older may also work, couldn't test it)


## Build instructions:
To run the client you can either (1) run the client from source or (2) build a distribution package and run from that.
### (1) Run the Client from Source
<pre>$ gradle run</pre>
This will use the default options, but you can supply commandline Options with the following way:
<pre>$ gradle run -PappArgs="['server:port', '--room', 'MySecretRoom']"</pre>
### (2) Compile to the distribution binary
<pre>$ gradle distZip</pre>
This will create a zip file in build/distributions, which you can unpack wherever you like. You then have to run bin/transcendental-client (or bin/transcendental-client.bat on Windows)
<pre>
/tmp/uyrcuyr/transcendental-client-0.6.0 $ bin/transcendental-client --help
Transcendental-SimpleClient v0.6.0
	a simple deamon-like Client for sharing the local Clipboard with other Devices.
Usage: transcendental-client [OPTIONS] [<server>:<port>]
  -h	--help	 	 outputs this help
  -v	--version	 	 outputs the version
  -s	--server	<server>[:<port>]	 specifies the server which distributes the data, default:localhost
  -p	--port	<port>	 specifies the server port, default:19192
  -r|-k	--password | --room | --key	<passwd>	 specifies the encryption keyword and at the same time the room*, default:RaumRaumRaumRaum

   (*) a room is the separation unit in which the server distributes the clipboard content, there can be multiple rooms on a server.
</pre>
