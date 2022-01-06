#include <stdlib.h>
#include "../include/connectionHandler.h"
#include <iostream>
#include <thread>
using namespace std;
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

bool shouldTerminate = false;

ConnectionHandler connectionHandler;


// in charge of reading commands from terminal and sending messages to the server
void sender (){
      try{
        while (!shouldTerminate){
            // read command from terminal
            // create buffer
            const short bufsize = 1024;
            char buf[bufsize];
            //
            // get line from user (in terminal)
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            // should encode line before sending to server
            //send line to server (should be encoded to bytes)
            if (!connectionHandler.sendLine(line)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
        }
        }
    catch (...){
        // if exception then socket is closed during getLine() wait so return
        return;
    }
}


void reader (){
    while (!shouldTerminate){
        // get response from server
        //
        // We can use one of three options to read data from the server:
        // 1. Read a fixed number of characters
        // 2. Read a line (up to the newline character using the getline() buffered reader
        // 3. Read up to the null character
               std::string answer;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        int len=answer.length();
        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        answer.resize(len-1);
        // std::cout << "Reply: " << answer << " " << len << " bytes " << std::endl << std::endl;
           std::cout << "Client < " << answer << std::endl;
        if (answer == "ACK 3") {
            std::cout << "Exiting...\n" << std::endl;
            shouldTerminate = true;
            break;
        }
    }
}


int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    BGSUserClient client(host, port,connectionHandler);

    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::thread senderThread((sender));
    std::thread readerThread((reader));
    readerThread.join();
    connectionHandler.close();

    return 0;
}