//
// Created by oreny on 13/11/2021.
//
#include "math.h"
#include "../include/Studio.h"
#include "../include/Action.h"
#include <iostream>
#include <sstream>
#include <fstream>
#include "../include/Studio.h"

using namespace std;
template<typename Base, typename T>
inline bool instanceof(const T*) {
    return is_base_of<Base, T>::value;
}

//start of rule of 5
Studio::Studio():open(false),trainers({}),workout_options({}), numOfClients(0) ,actionsLog({}){}
Studio::~Studio() {//destructor
    workout_options.clear();
    for (int i = 0; i < static_cast<int>(trainers.size()); i++)
        delete trainers[i];
    trainers.clear();
    for (int i = 0; i < static_cast<int>(actionsLog.size()); i++)
        delete actionsLog[i];
    actionsLog.clear();
}
Studio::Studio(const Studio &other) { //copy constructor
    open = other.open;
    numOfClients = other.numOfClients;

    for(Trainer * trainer : other.trainers){
        Trainer * t = new Trainer(*trainer);
        trainers.push_back(t);
    }
    for(Workout work : other.workout_options){
        Workout w(work);
        workout_options.push_back(w);
    }
    for( BaseAction * act : other.actionsLog ){
        BaseAction * base = act->clone();
        actionsLog.push_back(base);
    }
}
Studio& Studio::operator=(const Studio& other) {//A copy assignment operator
    if (this == &other) {
        return *this;
    }
//    this is how we delete all the pointers
    for (Trainer *t: trainers) delete t;
    for (BaseAction *a: actionsLog) delete a;
    trainers.clear();
    workout_options.clear();
    actionsLog.clear();
    open = other.open;

    for(Trainer * trainer : other.trainers){
        Trainer * t = new Trainer(*trainer);
        trainers.push_back(t);
    }
    for(Workout work : other.workout_options){
        Workout w(work) ;
        workout_options.push_back(w);
    }
    for( BaseAction * act : other.actionsLog ){
        BaseAction * base = act->clone();
        actionsLog.push_back(base);
    }

    return *this;
}
//end of rule of 5


Studio::Studio(const std::string &configFilePath): Studio::Studio() {
    std::ifstream studioFile(configFilePath);
    std::vector<std::string> my_string;
    std::string row;
    while (!studioFile.eof()) {
        getline(studioFile, row);
        if (row.empty()|| row[0] == '#' )
            continue;
        else {
            my_string.push_back(row);
        }
    }
    int toAdd = 0;
    int numLength = 0;
    size_t prevIndex = 0;
    size_t i = 0;
    std::string capacities = my_string[1];
    while (i < capacities.length()) {
        prevIndex = i;
        numLength = 0;
        while (capacities[i]!= ',' && i < capacities.size() ) {
            numLength++;
            i++;
        }
        toAdd = 0;
        for (int j = 0; j < numLength; j++) {
            toAdd += ( capacities[prevIndex] - 48 ) * std::pow(10, numLength - j - 1 );
            prevIndex++;
        }
        Trainer *addT = new Trainer(toAdd);
        this->trainers.push_back(addT);
        i++;
    }
    for (size_t i = 2; i < my_string.size(); ++i) {
        std::string line = my_string[i];
        std::string name = "";
        WorkoutType wType;
        int numbLength = 0;
        int price = 0;
        size_t index = 0;
        for (int j = 0; line[j]!=','; j++ ) {
            name += line[j];
            index = j;
        }
        index += 3;
        if ( line.at(index) == 'A') {
            wType = ANAEROBIC;
            index += 11;
        }
        else if (line.at(index) == 'M') {
            wType = MIXED;
            index += 7;
        }
        else {
            wType = CARDIO;
            index += 8;
        } prevIndex = index;
        while (index < line.length() ){
            numbLength++;
            index++;
        }
        for (int j = 0; j < numbLength; j++) {
            price += ( line[prevIndex] - 48) * std::pow(10, numbLength - j - 1);
            prevIndex++;
        }
        Workout toAdd(i - 2, name, price, wType);
        this-> workout_options.push_back(toAdd);
    }
    this->actionsLog = {};

}

void Studio::start() {
    open = true;
    std::cout << "Studio is now open!\n";
}


std::vector<Trainer*>& Studio:: getListOfTrainers(){
    return trainers;
}

int Studio::getNumOfTrainers() const {
    return trainers.size();
}

Trainer *Studio::getTrainer(int tid) {
    if(tid < getNumOfTrainers() ){
        return trainers[tid];
    }
}

const std::vector<BaseAction *> &Studio::getActionsLog() const {
    return actionsLog;
}

int Studio:: getActionsLogSize(){
    return actionsLog.size();
}

BaseAction* Studio:: getActionsLogIndex(int i){
    return actionsLog[i];
}


std::vector<Workout> &Studio::getWorkoutOptions() {
    return workout_options;
}

std::string Studio::getWorkoutName(int workoutId) {
    for (int i = 0; i < static_cast<int>(workout_options.size()); i++) {
        if (workout_options[i].getId() == workoutId)
            return workout_options[i].getName();
    }
}

void Studio::close() {
    open = false;
}

void Studio::pushActionsLog(BaseAction *action) {
    this->actionsLog.push_back(action);
}

int Studio::getNumOfClients() {
    return numOfClients;
}

void Studio::setNumOfClients(int increment) {
    numOfClients += increment;
}