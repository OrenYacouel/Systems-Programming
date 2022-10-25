#ifndef TRAINER_H_
#define TRAINER_H_

#include <vector>
#include "Customer.h"
#include "Workout.h"

typedef std::pair<int, Workout> OrderPair;

class Trainer{
public:
    //start of rule of 5
    Trainer(int t_capacity);
    ~Trainer();
    Trainer(const Trainer & other);
    Trainer(Trainer && other);
    Trainer& operator=(const Trainer& other);
    const Trainer & operator=(Trainer && other);

//    end of rule of 5
    int getCapacity() const;
    void addCustomer(Customer* customer);//we need to add this client workout prices for the salary of this trainer
    void removeCustomer(int id);//we need to remove this client workout prices from the salary of this trainer
    void removeOrders(int id);// this function removes all the orders of a certain costumer
    Customer* getCustomer(int id);
    std::vector<Customer*>& getCustomers();
    std::vector<OrderPair>& getOrders();
    void order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options);
    void openTrainer();
    void closeTrainer();
    int getSalary();
    bool isOpen();
    bool isInSession(int costumerID); // i added this to know whether a costumer is in the ordersList or not (in session?)
    bool hasAvailableSpot();//i added this
private:
//    int trainerId; //I added
    int salary; //I added
    int capacity;
    bool open;
    std::vector<Customer*> customersList;
    std::vector<OrderPair> orderList; //A list of pairs for each order for the trainer - (customer_id, Workout)
};


#endif

