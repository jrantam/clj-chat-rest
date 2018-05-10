# Clojure Chat REST (clj-chat-rest)
 
A simple prototype REST project that introduces e.g. Clojure, H2 in-memory database and JSON usage.
 
Currently there is no frontend implementation for this chat service, but message sending can be done using e.g. Postman (https://www.getpostman.com/) or any other HTTP request poster.

## Prerequisites

Install Leiningen 2.0.0 or above. Check installation instructions at: https://leiningen.org/#install

Additional information for Windows installation:
- Download "leiningen-2.8.1-standalone.zip" and rename it as .jar file
- Set jar into folder C:\Users\xxx\.lein\self-installs\
- Make sure you have set "lein.bat" in PATH.

## Running

1. Start server by calling "lein ring server".
2. Send few POST requests to url "localhost:3000/messages", and receive "201 CREATED". Requests must contain "application/json" body in the following format:
```
{
"sender" : "Mike",
"room" : "1408",
"message" : "Hello!"
}
```

3. Send GET request to "localhost:3000/messages" and receive messages from ALL rooms.
4. Send GET request to "localhost:3000/messages/1408" and receive messages ONLY from room "1408".

Have a nice chat! :)
