//
// Created by oreny on 13/11/2021.
//
#include "../include/Trainer.h"
#include <vector>
#include <string>
#include "iostream"
using namespace std;

Trainer:: Trainer(int t_capacity): capacity(t_capacity), open(false), orderList(std::vector<OrderPair>()), customersList(std::vector<Customer*>()){
}//constructor

//start of need to fix all
 Trainer:: ~Trainer(){
    orderList.clear();
    for (int i = 0; i < static_cast<int>(customersList.size()); i++)
        delete customersList[i];
    customersList.clear();
}//destructor

Trainer::Trainer(const Trainer & other){ //copy constructor

    capacity = other.capacity;
    open = other.open;
    for (Customer* customer : other.customersList){
        Customer *c = customer->clone();
        customersList.push_back(c);
    }
    for (OrderPair orderPair : other.orderList){
        OrderPair pair(orderPair.first , orderPair.second);
        this->orderList.push_back(pair);
    }
}

Trainer::Trainer(Trainer && other){ //move constructor
    open = other.open;
    capacity = other.capacity;
    for(int i=0; i < static_cast<int>(customersList.size()); i++){
        customersList[i]= other.customersList[i];
    }
    orderList = other.orderList;
    std::vector<Customer*>().swap((customersList));
    std::vector<OrderPair>().swap((orderList));
}
Trainer& Trainer::operator=(const Trainer& other){ //copy assignment operator
    if(this != & other) {
        if ( !(customersList.empty() ) ) std::vector<Customer*>().swap((customersList)); //not sure if we can delete vector this way
        if( !(orderList.empty()) ) std::vector<OrderPair>().swap((orderList));
        open = other.open;
        capacity = other.capacity;
        for (int i = 0; i < static_cast<int>(other.customersList.size()); i++)
            customersList.push_back(other.customersList[i]);
        for (int i = 0; i < static_cast<int>(other.orderList.size()); i++)
            orderList.push_back(other.orderList[i]);
    }
    return *this;
}
const Trainer& Trainer:: operator=(Trainer && other){  //move assignment operator
    if (!(customersList.empty() ) ) std::vector<Customer*>().swap((customersList));
    if( !(orderList.empty() ) ) std::vector<OrderPair>().swap((orderList));
    open = other.open;
    capacity = other.capacity;
    customersList = other.customersList;
    orderList = other.orderList;
    std::vector<Customer*>().swap((other.customersList));
    std::vector<OrderPair>().swap((other.orderList));
    return *this;
}

//end of need to fix all


int Trainer:: getCapacity() const{
    return this->capacity;
}
void Trainer::addCustomer(Customer* customer) {
    if (capacity > 0){
        customersList.push_back(customer);
        capacity--;
    }
// send an error?
}
void Trainer::removeCustomer(int id){//we need to remove this client workout prices from the salary of this trainer
    for(int i =0;i < static_cast<int>(customersList.size());i++){
        if (customersList[i]->getId()== id) {
            customersList.erase(customersList.begin() + i);
            capacity++;
            removeOrders(id);
        }
    }
    if (getCustomers().empty())
        closeTrainer();
}
void Trainer:: removeOrders(int id){
    std::vector<OrderPair> newOrders;
    for(OrderPair order: orderList){
        if (order.first != id)
            newOrders.push_back(order);
    }
    orderList = newOrders;
}


Customer* Trainer::getCustomer(int id){
    for(int i =0;i < static_cast<int>(customersList.size());i++){
        if (customersList[i]->getId()== id)
            return customersList[i];
    }
    //else return error? or what?
}

void Trainer::closeTrainer() {
    open = false;
    int newPaycheck = 0;
    for ( int i=0; i< static_cast<int>(orderList.size()); i++){
        newPaycheck = newPaycheck + orderList[i].second.getPrice() ;
    }
    salary = salary + newPaycheck;
    for (Customer* customer : customersList) {
        delete customer;
    }
    customersList.clear();
    orderList.clear();
}
int Trainer::getSalary() {
    int output=0;
    for( int i=0; i < static_cast<int>(orderList.size()); i++){
        output = output + orderList[i].second.getPrice();
    }
    return output + salary ;
}

bool Trainer::isOpen() {
    return open;
}
bool Trainer:: isInSession(int costumerID){
    for(int i=0;i < static_cast<int>(getCustomers().size());i++)
        if (getCustomers()[i]->getId() == costumerID)
            return true;
    return false;
}
bool Trainer::hasAvailableSpot(){
    if (getCapacity() > static_cast<int>(getOrders().size()))
        return true;
    return false;
}

void Trainer::order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout> &workout_options) {
    for (int i = 0; i < static_cast<int>(workout_ids.size()); ++i) {
        orderList.push_back(OrderPair(customer_id,workout_options[workout_ids[i]]));
    }
}


std::vector<OrderPair> &Trainer::getOrders() {
    return orderList;
}

std::vector<Customer *> &Trainer::getCustomers() {
    return customersList;
}

void Trainer::openTrainer() {
    open = true;
}

