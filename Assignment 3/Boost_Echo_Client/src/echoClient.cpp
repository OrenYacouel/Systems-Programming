#include "KeyboardReader.h"
#include "socketReader.h"
#include <boost/lambda/lambda.hpp>
#include <stdlib.h>
#include <boost/algorithm/string/split.hpp>
#include <thread>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    KeyboardReader keyboardReader(connectionHandler);
    socketReader socketReader(connectionHandler);
    std::thread th1(&KeyboardReader::run, &keyboardReader);
    std::thread th2(&socketReader::run, &socketReader);
    th1.join();
    th2.join();
    return 0;
}