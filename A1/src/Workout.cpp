//
// Created by oreny on 13/11/2021.
//
#include "../include/Workout.h"

Workout::Workout(int w_id, std::string w_name, int w_price, WorkoutType w_type): name(w_name), type(w_type) , id(w_id) , price(w_price){
}

int Workout::getId() const {
    return id;
}
std::string Workout::getName() const {
    return name;
}

int Workout::getPrice() const {
    return price;
}

WorkoutType Workout::getType() const {
    return type;
}

bool Workout::operator >(Workout const &other){
    return (this->getPrice() > other.getPrice());
}

Workout &Workout::operator=(const Workout &other){
    return *this;
}

Workout::Workout(const Workout &other):id(other.id),price(other.price),name(other.name),type(other.type) {
}


//Workout& Workout:: operator=(const Workout& other){
//    id = other.getId()
//    name(other.getName()), price(other.getPrice()), type(other.getType()){
//        return *this;
//    }



