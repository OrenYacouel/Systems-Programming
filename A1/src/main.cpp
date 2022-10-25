#include <iostream>
#include "../include/Studio.h"

Studio* backup = nullptr;

using namespace std;
template<typename Base, typename T>
inline bool instanceof(const T*) {
    return is_base_of<Base, T>::value;
}


int findNthOccur(string str,char ch, int N){
    int occur = 0;
    // Loop to find the Nth
    // occurrence of the character
    for (int i = 0; i < static_cast<int>(str.length()); i++) {
        if (str[i] == ch) {
            occur += 1;
        }
        if (occur == N)
            return i;
    }
    return -1;
}

BaseAction * openTrainerAction(string action, Studio & studio ){
    int clientId = studio.getNumOfClients();
    std::string clientType ;
    std::string clientName ;
    std::vector<Customer*> _customerList;
    int j=0;
    while ( isdigit(action[j]) )
        j++;
    int _trainerId (std::stoi(action.substr(0, j))); //detects the id of the trainer in the input
    std::string rest = action.substr(j+1); //cuts the id and the space after it

    int capac = studio.getTrainer(_trainerId)->getCapacity();

    int i = 0;
    int counter = 1;
    while( i < static_cast<int>(rest.size())) {

        clientName = rest.substr(i, findNthOccur(rest, ',', counter) - i);
//        action = action.substr( action.find(",")+1, action.length()); //cuts the name of the client and the comma after it
        clientType = rest.substr(findNthOccur(rest, ',', counter) + 1, 3);
        i = i + clientName.size() + 5;
        counter++;
        if (capac > 0) {
            if (clientType == "swt") { //checks if the
                Customer *swtClient = new SweatyCustomer(clientName, clientId);
                _customerList.push_back(swtClient);
                capac--;
            } else if (clientType == "chp") {
                Customer *chpClient = new CheapCustomer(clientName, clientId);
                _customerList.push_back(chpClient);
                capac--;
            } else if (clientType == "mcl") {
                Customer *mclClient = new HeavyMuscleCustomer(clientName, clientId);
                _customerList.push_back(mclClient);
                capac--;
            } else if (clientType == "fbd") {
                Customer *fbdClient = new FullBodyCustomer(clientName, clientId);
                _customerList.push_back(fbdClient);
                capac--;
            }
            clientId = clientId + 1; //increased the id of the next client
        }
    }
    studio.setNumOfClients(_customerList.size());
    OpenTrainer* output = new OpenTrainer( _trainerId, _customerList);
    return output;
}

BaseAction * orderAction( string action){
    int _trainerId = std::stoi(action.substr(0, 1));
    Order * output =  new Order(_trainerId);
    return output;
}

BaseAction * moveCustomerAction(string action){
    int srcTrainer, dstTrainer, id; //maybe i should delete the int cuz those are fields in the class
    srcTrainer = std::stoi(action.substr(0, 1)); //detects the id of the trainer in the input
    dstTrainer = std::stoi(action.substr(2, 1));
    id = std::stoi(action.substr(4, 1)); //this is the clientId
    MoveCustomer * output =  new MoveCustomer(srcTrainer, dstTrainer, id);
    return output;
}

BaseAction * closeAction(string action){
    int trainerId = std::stoi(action.substr(0, 1)); //detects the trainer id
    Close *output = new Close( trainerId);
    return output;
}

BaseAction * closeAllAction(){
    CloseAll * output = new CloseAll();
    return output;
}

BaseAction * printWorkoutOptionsAction(){
    PrintWorkoutOptions *output=  new PrintWorkoutOptions();
    return output;
}

BaseAction * printTrainerStatusAction(string action){
    int _trainerId = std::stoi(action.substr(0, 1));
    PrintTrainerStatus *output=  new PrintTrainerStatus(_trainerId);
    return output;
}

BaseAction * logAction(){
    PrintActionsLog * output= new PrintActionsLog();
    return output;
}

BaseAction * backupAction(){
    BackupStudio * output =  new BackupStudio();
    return output;
}

BaseAction * restoreAction(){
    RestoreStudio* output =  new RestoreStudio();
    return output;
}

BaseAction *buildAction(const string& input, Studio& studio){ //I ADDED
    std::string firstWord(input.substr(0, input.find(' ')));
    if ( firstWord == "open" ) {
        return openTrainerAction(input.substr(input.find(' ') + 1), studio); //THOSE FUNCTIONS SHOULD ONLY OPEN THE NEW ACTION OBJECT, THE ACT SHOLD PERFORM THE ACTION ITSELF, i need to make some adaptations in order to set it up, meaning to copy some of the code to the act, in order to use the changes in the input maybe i should erase the & so it wont receive a reference but the action itself, so the function will have the ability to clean the unesessary details
    }
    else if ( firstWord == "order" ) {
        return orderAction(input.substr(input.find(' ') + 1));
    }
    else if ( firstWord == "move") {
        return moveCustomerAction(input.substr(input.find(' ') + 1));
    }
    else if ( firstWord == "close")
        return closeAction(input.substr(input.find(' ') + 1));
    else if ( firstWord == "closeall")
        return closeAllAction();
    else if ( firstWord == "workout_options")
        return printWorkoutOptionsAction();
    else if ( firstWord == "status")
        return printTrainerStatusAction(input.substr(input.find(' ') + 1));
    else if ( firstWord == "log")
        return logAction();
    else if ( firstWord == "backup")
        return backupAction();
    else if ( firstWord == "restore")
        return restoreAction();
    return nullptr;
}


int main(int argc, char** argv){
    if(argc!=2){
        std::cout << "usage: studio <config_path>" << std::endl;
        return 0;
    }
    string configurationFile = argv[1];
    Studio studio(configurationFile);
    studio.start();
    bool run = true;
    BaseAction* base;
    while(run){
        std::string input;
        std::getline(std::cin, input);
        base = buildAction(input,studio);
        studio.pushActionsLog(base);
        std::string str("closeall");
        std::size_t found = input.find(str);
        if (found!=std::string::npos){
            run = false;
            base->act(studio);
        }

        else {
            base->act(studio);
        }
    }
    if(backup != nullptr){
//        maybe we should call destructor of Backup if possible
        delete backup;
        backup = nullptr;
    }

    return 0;
}