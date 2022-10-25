//
// Created by OR on 01/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_KEYBOARDREADER_H
#define BOOST_ECHO_CLIENT_KEYBOARDREADER_H


#include <boost/asio.hpp>
#include <iostream>
#include <string>
#include "ConnectionHandler.h"

using boost::asio::ip::tcp;
using namespace std;

class KeyboardReader {
private:
    char dividor[2];
    ConnectionHandler &handler;
    bool shouldStop;
public:
    KeyboardReader(ConnectionHandler &_handler);

    virtual ~KeyboardReader();

    void run();

    void shortToBytes(short num, char *bytesArr);

    // All this methods return true if they were successful

    bool sendRegister(vector<string> input);

    bool sendLogin(vector<string> input);

    bool sendLogout(vector<string> input);

    bool sendFollow(vector<string> input);

    bool sendPost(vector<string> input);

    bool sendPM(vector<string> input);

    bool sendLogStat(vector<string> input);

    bool sendStat(vector<string> input);

    bool sendBlock(vector<string> input);

    bool sendString(string &line);

};


#endif //BOOST_ECHO_CLIENT_KEYBOARDREADER_H

