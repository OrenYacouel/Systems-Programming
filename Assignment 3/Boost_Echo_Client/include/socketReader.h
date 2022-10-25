//
// Created by orkados@wincs.cs.bgu.ac.il on 03/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_SOCKETREADER_H
#define BOOST_ECHO_CLIENT_SOCKETREADER_H

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "ConnectionHandler.h"

using boost::asio::ip::tcp;

class socketReader {
private:
    ConnectionHandler &handler;
    bool shouldStop;
public:
    socketReader(ConnectionHandler &handler);

    virtual ~socketReader();

    void run();

    bool getLine(std::string &line);

    bool readAck(std::string &line);

    std::string AckFollow(std::string &line);

    std::string AckLogStat(std::string &line);


    std::string readAckStat(std::string &line);

    bool readNotification(std::string &line);

    bool readError(std::string &line);

    short bytesToShort(char *bytesArr);

    short readTwoBytes(std::string &line);
};

#endif //BOOST_ECHO_CLIENT_SOCKETREADER_H
