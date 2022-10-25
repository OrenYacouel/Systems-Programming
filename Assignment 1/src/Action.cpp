//
// Created by oreny on 13/11/2021.
//
//
#include "../include/Action.h"
#include <string>
#include <iostream>

extern Studio* backup;

//BaseAction Start
BaseAction:: BaseAction()= default;
ActionStatus BaseAction:: getStatus() const{
    return status;
}
void BaseAction::setStatus(std::string _status) {
    if( _status == "completed")
        status = COMPLETED;
    else
        status = ERROR;
}

void BaseAction:: complete(){
    this->status = COMPLETED;
}

void BaseAction:: error(std::string _errorMsg){
    this->errorMsg = _errorMsg;
}
std::string BaseAction::getErrorMsg() const{
    return errorMsg;
}


BaseAction::~BaseAction() = default;

//BaseAction end
OpenTrainer:: OpenTrainer(int id, std::vector<Customer *> &customersList): BaseAction(), trainerId(id),customers(customersList){
    error("Workout session does not exist or is already open"); //this function updates the errorMsg of the action
}
OpenTrainer:: OpenTrainer(const OpenTrainer& other):trainerId(other.trainerId) {//copy constructor
    for( Customer *customer : other.customers ){
        customers.push_back(customer->clone());
    }
}
void OpenTrainer:: act(Studio &studio) {
    Trainer *t = studio.getTrainer(trainerId);
    if (t == nullptr || t->isOpen() || t->getCapacity() == 0) {
        this->setStatus("error");
        std::cout << getErrorMsg();
    } else {
        t->openTrainer();
        complete();
        for (std::vector<Customer *>::iterator it = customers.begin(); it != customers.end(); it++) {
            if (t->getCapacity() > 0) {
                t->addCustomer(*it); //adds the customer to the trainer's customersList
            }
        }
    }
}
OpenTrainer* OpenTrainer:: clone(){
    return new OpenTrainer(*this);
}

std::string OpenTrainer:: toString() const{
    std::string message ="open "+( std::to_string(trainerId) );
    for (int i=0; i < static_cast<int>(customers.size()); i++){
        message = message + customers[i]->toString();
    }
    if(getStatus() == COMPLETED) {
        return message + " Completed\n";
    }
    else{
        return message+" Error: "+getErrorMsg()+"\n";
    }
}

OpenTrainer::~OpenTrainer() = default;

//OpenTrainer OpenTrainer::operator=(const OpenTrainer &other) {
//    return OpenTrainer(other.trainerId, other.customers);
//}
Order::Order(int id):BaseAction(), trainerId(id) {
    error("Trainer does not exist or is not open. "); //this function updates the errorMsg of the action
}

Order::Order(const Order& other):trainerId(other.trainerId) {}
    void Order:: act(Studio &studio) {
        Trainer* _trainer = studio.getTrainer(trainerId);
        std::string output;
        if (_trainer != nullptr && _trainer->isOpen()) {
            for (int i = 0; i < static_cast<int>(_trainer->getCustomers().size()); i++) {
                std::vector<int> specificClientWorkoutsList = _trainer->getCustomers()[i]->order(studio.getWorkoutOptions()); //NEED TO IMPLEMENT THE FUNCTION ORDER
                _trainer->order( _trainer->getCustomers()[i]->getId(),specificClientWorkoutsList, studio.getWorkoutOptions() );
                std::string clientName = _trainer->getCustomers()[i]->getName();
                for(size_t j=0; j<specificClientWorkoutsList.size(); j++) {
                    int workoutId = specificClientWorkoutsList[j];
                    std::cout << clientName + " Is Doing " +studio.getWorkoutName(workoutId) << std::endl;
                }
            }
            complete();
        }
        else {
            setStatus("error");
            std::cout<<getErrorMsg();
        }
    }
    std::string Order:: toString() const{
        if( getStatus() == COMPLETED) {
            return "order "+std::to_string(trainerId)+ " Completed\n";
        }
        else{
            return "order "+std::to_string(trainerId)+" Error: "+getErrorMsg()+"\n";
        }
    }

Order *Order::clone() {
    return new Order(*this);

}

int Order::getTrainerId() const{
    return trainerId;
}
Order::~Order() = default;

MoveCustomer::MoveCustomer (int src, int dst, int customerId):BaseAction(),srcTrainer(src), dstTrainer(dst), id(customerId){
    error("Cannot move customer \n"); //this function updates the errorMsg of the action
}

MoveCustomer::MoveCustomer(const MoveCustomer& other) : srcTrainer(other.srcTrainer), dstTrainer(other.dstTrainer), id(other.id) {}

    void MoveCustomer::act(Studio &studio) {
        if (studio.getTrainer(dstTrainer) == nullptr || studio.getTrainer(srcTrainer) == nullptr ||
            !studio.getTrainer(srcTrainer)->isOpen() || !studio.getTrainer(dstTrainer)->isOpen()) {
            this->setStatus("error");
            std::cout << getErrorMsg();
        } else if (!studio.getTrainer(srcTrainer)->isInSession(id) ||
                   !studio.getTrainer(dstTrainer)->hasAvailableSpot()) {
            this->setStatus("error");
            std::cout << getErrorMsg();
        }
        else {
            Customer *movingCostumer = studio.getTrainer(srcTrainer)->getCustomer(id);
            std::vector<int> workoutIds;
            std::vector<Workout> workoutsOfTheClient;
            for (int i = 0; i < static_cast <int> (studio.getTrainer(srcTrainer)->getOrders().size()) ; i++) {
                if (studio.getTrainer(srcTrainer)->getOrders()[i].first == id) {
                    workoutsOfTheClient.push_back(studio.getTrainer(srcTrainer)->getOrders()[i].second);
                    workoutIds.push_back(studio.getTrainer(srcTrainer)->getOrders()[i].second.getId());

                }
            }
            int costOfWorkouts = 0;
            for (size_t i = 0; i < workoutsOfTheClient.size(); i++)
                costOfWorkouts = costOfWorkouts +workoutsOfTheClient[i].getPrice(); //calculates the price of the workouts of this client
            studio.getTrainer(srcTrainer)->removeCustomer(movingCostumer->getId()); //removes from the customerList
            if (studio.getTrainer(srcTrainer)->getCustomers().size() == 0)
                studio.getTrainer(srcTrainer)->closeTrainer();
            studio.getTrainer(dstTrainer)->addCustomer(movingCostumer); //adds the customer to the dstTrainer
            studio.getTrainer(dstTrainer)->order(movingCostumer->getId(), workoutIds,
                                                 studio.getWorkoutOptions()); //orders the moving customer orders to the new trainer
            complete();
        }
    }
    std::string MoveCustomer::toString() const{
        std::string output = "move "+(std::to_string(srcTrainer) )+" "+(std::to_string(dstTrainer) )+" "+(std::to_string(id) );
        if(getStatus() == COMPLETED) {
            return output +" Completed\n";
        }
        else{
            return output+" Error: " +getErrorMsg()+"\n";
        }
    }

MoveCustomer *MoveCustomer::clone() {
    return new MoveCustomer(*this);

}

MoveCustomer::~MoveCustomer() = default;

Close::Close(int id):BaseAction(), trainerId(id){
    error("Trainer does not exist or is not open"); //this function updates the errorMsg of the action
}

Close::Close(const Close& other):trainerId(other.trainerId) {}
    void Close::act(Studio &studio){
        Trainer * trainer = studio.getTrainer(trainerId);
        if (( trainer != nullptr ) && ( trainer->isOpen()) ){
            trainer->closeTrainer();
            complete();
            std::cout << "Trainer "+std::to_string(trainerId)+" closed. Salary "+std::to_string(trainer->getSalary())+" NIS"+"\n" ;
        }
        else{
            this->setStatus("error");
            std::cout<<getErrorMsg();
        }
    }
    std::string Close::toString() const{
        if( getStatus() == COMPLETED)
            return "close "+(std::to_string(trainerId))+"\n";
        else
            return "close "+ (std::to_string(trainerId))+" Error: "+getErrorMsg()+"\n";
    }

Close *Close::clone() {
    return new Close(*this);

}

Close::~Close() = default;

CloseAll::CloseAll():BaseAction(){}
    void CloseAll::act(Studio &studio) {
        complete();
        std::vector<Trainer *> trainers = studio.getListOfTrainers();
        for (int i = 0; i < static_cast<int>(trainers.size()); i++) {
            if (trainers[i]->isOpen()) {
                Close close_action = Close(i);
                close_action.act(studio); //operates the close action
            }
            studio.close();
        }
    }
    std::string CloseAll:: toString() const{
        return "";
}

CloseAll *CloseAll::clone() {
    return new CloseAll(*this);

}

CloseAll::CloseAll(const CloseAll &other) {
}

CloseAll::~CloseAll() = default;

PrintWorkoutOptions::PrintWorkoutOptions():BaseAction(){}

    void PrintWorkoutOptions:: act(Studio &studio){
        std::vector<Workout> workoutOptions = studio.getWorkoutOptions();
        std::string output = "";
        complete();
        for(size_t i=0; i < workoutOptions.size(); i++){
            output.append(workoutOptions[i].getName());
            if( workoutOptions[i].getType() == ANAEROBIC)
                output.append(", Anaerobic, ");
            else if ( workoutOptions[i].getType() == MIXED)
                output.append(", Mixed, ");
            else
                output.append(", Cardio, ");
            output.append( std::to_string( workoutOptions[i].getPrice() ) );
            output.append("\n");
        }
        std::cout<< output << std::endl;
    }
    std::string PrintWorkoutOptions::toString() const{
        return "workout_options Completed\n";
    }

PrintWorkoutOptions *PrintWorkoutOptions::clone() {
    return new PrintWorkoutOptions(*this);
}

PrintWorkoutOptions::PrintWorkoutOptions(const PrintWorkoutOptions &other) {
}

PrintWorkoutOptions::~PrintWorkoutOptions() = default;

PrintTrainerStatus:: PrintTrainerStatus(int id): BaseAction(),trainerId(id){}

    void PrintTrainerStatus:: act(Studio &studio){
        std::string output = "Trainer ";
        if (studio.getTrainer(trainerId) != nullptr && studio.getTrainer(trainerId)->isOpen() ){ //This action can only occur after the workout session has started (OpenTrainer).
            complete();
            std::vector<Customer*> customersList = studio.getTrainer(trainerId)->getCustomers();
            std::vector<OrderPair> orderList = studio.getTrainer(trainerId)->getOrders();
            output += std::to_string(trainerId);
            output = output+" status: "+"open"+"\n"+"Customers:\n";
            for (int i=0; i < static_cast<int>(customersList.size()); i++){ //add the customers details
                output += std::to_string(customersList[i]->getId());
                output = output+" "+ customersList[i]->getName()+"\n";
            }
            output = output + "Orders:\n";
            for(int i=0; i < static_cast<int>(orderList.size()); i++){
                Workout _workout = orderList[i].second;
                output = output + _workout.getName() +" ";
                output += std::to_string(_workout.getPrice());
                output = output+"NIS ";
                output+= std::to_string(orderList[i].first)+"\n";
            }
            output +="Current Trainer’s Salary: ";
            if( studio.getTrainer(trainerId)!= nullptr ) {
                std::string mySalary = std::to_string(studio.getTrainer(trainerId)->getSalary());
                output += mySalary + "NIS";
            }
        }
        else if (!studio.getTrainer(trainerId)->isOpen() ){
            output += std::to_string(trainerId);
            output += " status: closed ";
        }
        std::cout<< output << std::endl;
        output.clear();
    }
    std::string PrintTrainerStatus:: toString() const{
        if( getStatus() == COMPLETED){
            return "status "+(std::to_string(trainerId))+" Completed\n";
        }
        else
            return "status "+(std::to_string(trainerId))+" "+getErrorMsg()+"\n";
    }

PrintTrainerStatus *PrintTrainerStatus::clone() {
    return new PrintTrainerStatus(*this);
}

PrintTrainerStatus::PrintTrainerStatus(const PrintTrainerStatus &other):trainerId(other.getTrainerId()) {
}

int PrintTrainerStatus::getTrainerId() const {
    return trainerId;
}

PrintTrainerStatus::~PrintTrainerStatus() = default;

PrintActionsLog::PrintActionsLog():BaseAction(){}
    void PrintActionsLog:: act(Studio &studio){
        complete();
        std::string output;
        for (int i = 0; i < static_cast<int>(studio.getActionsLog().size()-1 ); ++i) {
            output += studio.getActionsLog()[i]->toString();
        }
        std::cout << output;

    }
    std::string PrintActionsLog:: toString() const{
        return "log\n";
    }

PrintActionsLog *PrintActionsLog::clone() {
    return new PrintActionsLog(*this);
}

PrintActionsLog::PrintActionsLog(const PrintActionsLog &other) {
}

PrintActionsLog::~PrintActionsLog() = default;

BackupStudio::BackupStudio():BaseAction(){
    }
    void BackupStudio::act(Studio &studio){
        if(backup != nullptr){
            delete backup;
        }
        backup = new Studio(studio);
        complete();
    }
    std::string BackupStudio::toString() const{
        return "backup\n";
    }

BackupStudio *BackupStudio::clone() {
    return new BackupStudio(*this);
}

BackupStudio::BackupStudio(const BackupStudio &other) {
}

BackupStudio::~BackupStudio() = default;
//this func never results an error

RestoreStudio::RestoreStudio() :BaseAction() {
    error("No backup available");
    }
    void RestoreStudio:: act(Studio &studio){
        if(backup == nullptr) {
            setStatus("error");
            std::cout<<getErrorMsg();
        }
        else{
            complete();
            studio = *backup;
//          not sure if this deep copies backup to studio
        }
//        we should enter this action to this action's log
    }
    std::string RestoreStudio:: toString() const{
        if( getStatus() == COMPLETED)
            return "restore\n";
        else
            return "restore Error: "+getErrorMsg()+"\n";
    }

RestoreStudio *RestoreStudio::clone() {
    return new RestoreStudio(*this);
}

RestoreStudio::RestoreStudio(const RestoreStudio &other) {
}
RestoreStudio::~RestoreStudio() = default;
