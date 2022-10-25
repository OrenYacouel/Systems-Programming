CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread -lm


all: BGSClient
	g++ -o bin/BGSclient bin/socketReader.o bin/ConnectionHandler.o bin/keyboardReader.o bin/echoClient.o $(LDFLAGS)

BGSClient:  bin/socketReader.o bin/ConnectionHandler.o bin/keyboardReader.o bin/echoClient.o

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/socketReader.o: src/socketReader.cpp
	g++ $(CFLAGS) -o bin/socketReader.o src/socketReader.cpp

bin/keyboardReader.o: src/keyboardReader.cpp
	g++ $(CFLAGS) -o bin/keyboardReader.o src/keyboardReader.cpp

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

.PHONY: clean
clean:
	rm -f bin/*