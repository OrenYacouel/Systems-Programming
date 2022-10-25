//
// Created by oreny on 13/11/2021.
//
#include <string>
#include <iostream>


#include "../include/Customer.h"
Customer:: Customer(std::string c_name, int c_id):id(c_id), name(c_name){} //constructor
std::string Customer:: getName() const{
    return name;
}
int Customer:: getId()  const{
    return id;
}
void Customer::sortWorkouts(std::vector<int> priceV, std::vector<int>idV) {
    int n = static_cast<int>(priceV.size());
    int key, j;
    for (int i = 1; i < n; i++) {
        key = priceV[i];//take value
        j = i;
        while (j > 0 && priceV[j - 1] > key) {
            priceV[j] = priceV[j - 1];
            idV[j] = idV[j - 1];
            j--;
        }
        priceV[j] = key;   //insert in right place
        idV[j] = key;
    }

    for(int i=0; i<static_cast<int>(priceV.size()); i++)
        std::cout << priceV[i] << std::endl;
}

Customer::~Customer() = default;
//Customer:: ~Customer(){ //destructor
//    delete[] name;//not sure how to delete strings
//}

//start of sweaty
SweatyCustomer::SweatyCustomer(std::string name, int Id) : Customer(name, Id) {}
std::vector<int> SweatyCustomer::order(const std::vector<Workout> &workout_options) {
    std::vector<int> output ;
    if( workout_options.size() != 0 ) {
        for (size_t i = 0; i < workout_options.size(); i++) {
            if (workout_options[i].getType() == CARDIO) {
                output.push_back(workout_options[i].getId());
            }
        }
    }
    else return output;
}

std::string SweatyCustomer::toString() const {//we can fix this easily
    std::string output = " "+getName()+",swt";
    return output;
}

SweatyCustomer *SweatyCustomer::clone(){
    return new SweatyCustomer(this->getName(),this->getId());
}

SweatyCustomer::~SweatyCustomer() = default;
//end of sweaty

//start of cheap
CheapCustomer::CheapCustomer(std::string name, int Id) : Customer(name, Id) {}

CheapCustomer *CheapCustomer::clone(){
    return new CheapCustomer(this->getName(),this->getId());
}
std::vector<int> CheapCustomer::order(const std::vector<Workout> &workout_options) {
    std::vector<int> output;
    if( workout_options.size() != 0 ) {
        int minPrice = workout_options[0].getPrice();
        int minWorkId = workout_options[0].getId();//we  think it will create a memory leak
        for (size_t i = 1; i < workout_options.size(); i++) {
            if ( workout_options[i].getPrice() < minPrice ) {
                minWorkId = workout_options[i].getId();
            }
        }
        output.push_back(minWorkId);
    }
    return output; // IN THIS CASE THE FUNCTIONS SHOULD DO NOTHING , WERE NOT SURE IF TO RETURN NULL IS THE CORRECT OPTION.
}

std::string CheapCustomer::toString() const {
    std::string output = " "+ getName()+",chp";
    return output;
}

CheapCustomer::~CheapCustomer() = default;

//end of cheap

//start of heavy
HeavyMuscleCustomer::HeavyMuscleCustomer(std::string name, int Id) : Customer(name, Id) {}
HeavyMuscleCustomer *HeavyMuscleCustomer::clone(){
    return new HeavyMuscleCustomer(this->getName(),this->getId());
}
std::vector<int> HeavyMuscleCustomer::order(const std::vector<Workout> &workout_options) {
    std::vector<int> idV; //vector of the id's of the workouts
    if (workout_options.size() > 0) {
        std::vector<Workout> toBeSorted;
        for(size_t i=0; i<workout_options.size() && workout_options[i].getType() == ANAEROBIC; i++)
            toBeSorted.push_back(workout_options[i]);
        std::vector<int> pricesV; //vector of the Prices of the workouts

        for (size_t i = 0; i < toBeSorted.size(); i++) //initialize the pricesV
            pricesV.push_back(toBeSorted[i].getPrice()); //initialize the idV
        for (size_t i = 0; i < toBeSorted.size(); i++)
            idV.push_back(toBeSorted[i].getId());
        int n = static_cast<int>(pricesV.size());    //this is the sorting code
        int key, j, key1;
        for (int i = 1; i < n; i++) {
            key = pricesV[i];//take value
            key1 = idV[i];
            j = i;
            while (j > 0 && pricesV[j - 1] > key) {
                pricesV[j] = pricesV[j - 1];
                idV[j] = idV[j - 1];
                j--;
            }
            pricesV[j] = key;   //insert in right place
            idV[j] = key1;
        }

        return idV;
    }
    return idV;
}

std::string HeavyMuscleCustomer::toString() const {
    std::string output = " "+getName()+",mcl";
    return output;
}

HeavyMuscleCustomer::~HeavyMuscleCustomer() = default;
//end of heavy

//start of full body
FullBodyCustomer::FullBodyCustomer(std::string name, int Id) : Customer(name, Id) {}
FullBodyCustomer *FullBodyCustomer::clone(){
    return new FullBodyCustomer(this->getName(),this->getId());
}
//in this function we casted in the second line of "IF" and we are not sure about
std::vector<int> FullBodyCustomer::order(const std::vector<Workout> &workout_options) {
    std::vector<int> output;
    if(  workout_options.size() != 0 ){
        int  cardioId, mixedId, anaId ,chpCardio=workout_options[workout_options.size()-1].getPrice(), expMixed=workout_options[0].getPrice(), chpAna=workout_options[workout_options.size()-1].getPrice();
        for( size_t i=0; i< workout_options.size(); i++){
            if ( workout_options[i].getType() == CARDIO && workout_options[i].getPrice() < chpCardio ){
                chpCardio = workout_options[i].getPrice();
                cardioId = workout_options[i].getId();
            }
            if (workout_options[i].getType() == MIXED && workout_options[i].getPrice() > expMixed){
                expMixed = workout_options[i].getPrice();
                mixedId = workout_options[i].getId();
            }
            if (workout_options[i].getType() == ANAEROBIC && workout_options[i].getPrice() < chpAna){
                chpAna = workout_options[i].getPrice();
                anaId = workout_options[i].getId();
            }
        }
        output.push_back(cardioId);
        output.push_back(mixedId);
        output.push_back(anaId);
    }
    return output;
}

std::string FullBodyCustomer::toString() const {
    std::string output = " "+getName()+",fbd";
    return output;
}

FullBodyCustomer::~FullBodyCustomer() = default;
//end of full body
