
#include "../include/KeyboardReader.h"
#include <boost/algorithm/string.hpp>

using std::cin;
using namespace std;

KeyboardReader::KeyboardReader(ConnectionHandler &_handler) : handler(_handler), shouldStop(false) {
    shortToBytes(0, dividor);
}

KeyboardReader::~KeyboardReader() {}

void KeyboardReader::run() {
    while (!shouldStop) {
        string line;
        getline(cin, line);
        sendString(line);
    }
}

bool KeyboardReader::sendString(std::string &line) {
    std::vector <std::string> input;
    boost::split(input, line, [](char c) { return c == ' '; });
    if( !input.empty() ){
        if(input[0] == "REGISTER"){
            return sendRegister(input); }
        if( input[0] == "LOGIN"){ return sendLogin(input); }
        if( input[0] == "LOGOUT"){
            if(sendLogout(input) ){
                if( handler.isLoggedIn() )
                    shouldStop = true;
                return true;
            }
            return false;
        }
        if( input[0] == "FOLLOW"){ return sendFollow(input); }
        if( input[0] == "POST"){ return sendPost(input); }
        if( input[0] == "PM"){ return sendPM(input); }
        if( input[0] == "LOGSTAT"){ return sendLogStat(input); }
        if( input[0] == "STAT"){ return sendStat(input); }
        if( input[0] == "BLOCK"){ return sendBlock(input); }
    }
    return true;
}

void KeyboardReader::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

bool KeyboardReader::sendRegister(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(1, opcode); //insert 1 to the arg opcode as short
    if( input.size() == 4){
        sent =
                handler.sendBytes(opcode, 2) && handler.sendBytes( input[1].c_str(), input[1].length() ) &&
                handler.sendBytes(dividor, 2) && handler.sendBytes(input[2].c_str(), input[2].length() ) &&
                handler.sendBytes(dividor, 2) && handler.sendBytes(input[3].c_str(), input[3].length() ) &&
                handler.sendBytes(dividor, 1);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendLogin(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(2, opcode);
    if( input.size() == 4){
        sent =
                handler.sendBytes(opcode, 2) && handler.sendBytes( input[1].c_str(), input[1].length() ) &&
                handler.sendBytes(dividor, 2) && handler.sendBytes(input[2].c_str(), input[2].length() ) &&
                handler.sendBytes(dividor, 2) && handler.sendBytes(input[3].c_str(), input[3].length() ) && handler.sendBytes(dividor, 1);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendLogout(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(3, opcode);
    if( input.size() == 1){
        sent =
                handler.sendBytes(opcode, 2);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendFollow(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(4, opcode);
    if( input.size() == 3){
        sent =
                handler.sendBytes(opcode, 2) && handler.sendBytes( input[1].c_str(), input[1].length() ) && handler.sendBytes(dividor, 2) &&
                handler.sendBytes( input[2].c_str(), input[2].length() ) &&
                handler.sendBytes(dividor, 1);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendPost(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(5, opcode);
    if( input.size() > 1){
        sent = handler.sendBytes(opcode, 2) ;
        for (unsigned int i = 1; i < input.size() - 1; i++) {
            sent = sent &&
                   handler.sendBytes((input[i] + ' ').c_str(), input[i].length() + 1);
        }
        sent = sent &&
               handler.sendBytes(input[input.size() - 1].c_str(), input[input.size() - 1].length()) &&
               handler.sendBytes(dividor, 1);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendPM(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(6, opcode);
    if( input.size() > 1){

        sent =
                handler.sendBytes(opcode, 2) &&
                handler.sendBytes((input[1]).c_str(), input[1].length()) &&
                handler.sendBytes(dividor, 1);
        for (unsigned int i = 2; i < input.size() - 1; i++) {
            sent = sent &&
                   handler.sendBytes((input[i] + ' ').c_str(), input[i].length() + 1);
        }
        sent = sent &&
               handler.sendBytes(input[input.size() - 1].c_str(), input[input.size() - 1].length()) &&
               handler.sendBytes(dividor, 1);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendLogStat(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(7, opcode);
    if( input.size() == 1){
        sent =
                handler.sendBytes(opcode, 2);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendStat(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(8, opcode);
    if( input.size() == 2){
        sent =
                handler.sendBytes(opcode, 2)    && handler.sendBytes( input[1].c_str(), input[1].length() ) &&
                handler.sendBytes(dividor, 1) ;
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}

bool KeyboardReader::sendBlock(std::vector<std::string> input) {
    bool sent = false;
    char opcode[2];
    shortToBytes(12, opcode);
    if( input.size() == 2){
        sent =
                handler.sendBytes(opcode, 2)    && handler.sendBytes( input[1].c_str(), input[1].length() ) &&
                handler.sendBytes(dividor, 1);
    }
    else
        cout << "The parameters are wrong" << endl;
    return sent;
}


