CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system

all: BGSUserClient
	g++ -o bin/BulbulHaKabulbulInc bin/connectionHandler.o bin/BGSUserClient.o $(LDFLAGS)

BGSUserClient: bin/connectionHandler.o bin/BGSUserClient.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSUserClient.o: src/BGSUserClient.cpp
	g++ $(CFLAGS) -o bin/BGSUserClient.o src/BGSUserClient.cpp

.PHONY: clean
clean:
	rm -f bin/*
