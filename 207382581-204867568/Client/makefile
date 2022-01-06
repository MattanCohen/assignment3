CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: clean BGSUserClient ConnectionHandler
	g++ -o bin/ConnectionHandler bin/connectionHandler.o $(LDFLAGS)
	g++ -o bin/BGSUserClient bin/connectionHandler.o bin/BGSUserClient.o $(LDFLAGS)

BGSUserClient: bin/connectionHandler.o

ConnectionHandler: bin/connectionHandler.o bin/BGSUserClient.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSUserClient.o: src/BGSUserClient.cpp
	g++ $(CFLAGS) -o bin/BGSUserClient.o src/BGSUserClient.cpp

.PHONY: clean
clean:
	rm -f bin/*