#ifndef CUSTOMER_H_
#define CUSTOMER_H_

#include <vector>
#include <string>
#include "Workout.h"

class Customer{
public:
    Customer(std::string c_name, int c_id);
    virtual std::vector<int> order(const std::vector<Workout> &workout_options)=0;
    virtual std::string toString() const = 0;
    std::string getName() const;
    int getId() const;
    void sortWorkouts(std::vector<int>priceV, std::vector<int>idV);
    virtual Customer* clone() = 0; //copy constructor
    virtual ~Customer(); //destructor

private:
    const std::string name;
    const int id;
};


class SweatyCustomer : public Customer {
public:
	SweatyCustomer(std::string name, int id);
    SweatyCustomer* clone(); //copy constructor
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    ~SweatyCustomer() override;
private:
};


class CheapCustomer : public Customer {
public:
	CheapCustomer(std::string name, int id);
    CheapCustomer* clone(); //copy constructor
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    ~CheapCustomer() override;
private:
};

class HeavyMuscleCustomer : public Customer {
public:
	HeavyMuscleCustomer(std::string name, int id);
    HeavyMuscleCustomer* clone(); //copy constructor
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    ~HeavyMuscleCustomer() override;
private:
};


class FullBodyCustomer : public Customer {
public:
	FullBodyCustomer(std::string name, int id);
    FullBodyCustomer* clone(); //copy constructor
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    ~FullBodyCustomer() override;
private:
};


#endif