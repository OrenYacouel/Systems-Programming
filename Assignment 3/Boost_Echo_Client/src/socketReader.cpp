//
// Created by orkados@wincs.cs.bgu.ac.il on 03/01/2022.
//

#include "../include/socketReader.h"
#include "../include/ConnectionHandler.h"

socketReader::socketReader(ConnectionHandler &handler) : handler(handler), shouldStop(false) {}

socketReader::~socketReader() {}

void socketReader::run() {
    while(!shouldStop){
        std::string output;
        if( !getLine( output ) ){
            std::cout << "Disconnected. Exiting..\n" << std::endl;
            break;
        }
        std::cout << output << std::endl;
    }
}

bool socketReader::getLine(std::string &line) {
    char c, opcodeArray[2];
    short opcode = -1;
    int currIndex= 0;
    try{
        while ( currIndex < 2 ){
            handler.getBytes(&c, 1);
            if (currIndex < 2)
                opcodeArray[currIndex] = c;
            if(currIndex == 1){
                opcode = bytesToShort(opcodeArray);
            }
            currIndex++;
        }

        // relevant cases 9-11

        switch (opcode) {
            case -1: //meaning there is an issue with the input
                std::cout << "There is a problem with the input " << std::endl;
                break;

            case 9: // meaning the msg is a notification
                readNotification(line);
                break;

            case 10: // meaning the msg is a ack
                readAck(line);
                break;

            case 11: // meaning the msg is a error
                readError(line);
                break;

            default:
                std::cout << "answer analysis failed" << std::endl;
                break;
        }
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool socketReader::readNotification(std::string &line) {
    line = "NOTIFICATION ";
    char c;
    handler.getBytes(&c, 1);
    if (c == '\0')
        line += "PM ";
    else if (c == '\1')
        line += "Public ";
    else
        return false;
    handler.getBytes(&c, 1);
    while (c != '\0') {
        line += c;
        handler.getBytes(&c, 1);
    }
    line += ' ';
    handler.getBytes(&c, 1);
    while (c != '\0') {
        line += c;
        handler.getBytes(&c, 1);
    }
    return true;
}


bool socketReader::readAck(std::string &line) {
    line = "ACK ";
    short msgOpcode = readTwoBytes(line);
    switch( msgOpcode ){
        case -1:
            std::cout << "ack analysis fail" << std::endl;
            break;

        case 2:
            handler.LogIn();
            break;

        case 3:
            shouldStop = true;
            handler.setLoggedIn(false);
            handler.close();
            break;

        case 4:
            AckFollow(line);
            break;

        case 7:
            AckLogStat(line);
            break;

        case 8:
            readAckStat(line);
            break;

        default:
            break;
    }
    return false;
}

bool socketReader::readError(std::string &line) {
    line = "ERROR ";
    readTwoBytes(line);
    return false;
}

short socketReader::readTwoBytes(std::string &line) {
    char c;
    int currInx = 0;
    short num = -1;
    char numArr[2];
    while (currInx < 2) {
        handler.getBytes(&c, 1);
        if (currInx < 2)
            numArr[currInx] = c;
        if (currInx == 1)
            num = bytesToShort(numArr);
        currInx++;
    }
    line.operator += (std::to_string(num));
    return num;
}

std::string socketReader:: AckFollow(std::string &line){
    char c;
    handler.getBytes(&c, 1);
    line += " "; //adds the follow-code (follow / unfollow);
    while (c != '\0') {
        line += (c);
        handler.getBytes(&c, 1);
    }
    return line;


}


std::string socketReader::AckLogStat(std::string &line){
    line += " ";
    readTwoBytes(line); //age
    line += " ";
    readTwoBytes(line); //numPosts
    line += " ";
    readTwoBytes(line); //numFollowers
    line += " ";
    readTwoBytes(line); //numFollowing
    return line;


}

std::string socketReader::readAckStat(std::string &line){
    line += " ";
    readTwoBytes(line); //age
    line += " ";
    readTwoBytes(line); //numPosts
    line += " ";
    readTwoBytes(line); //numFollowers
    line += " ";
    readTwoBytes(line); //numFollowing
    return line;
}

short socketReader::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}
