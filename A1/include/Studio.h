#ifndef STUDIO_H_
#define STUDIO_H_

#include <vector>
#include <string>
#include "Workout.h"
#include "Trainer.h"
#include "Action.h"
class Trainer;
class BaseAction;

class Studio{		
public:
    //start of rule of 5
	Studio();
    ~Studio();
    Studio(const Studio& other);
    Studio& operator=(const Studio& other);

    //end of rule of 5
    Studio(const std::string &configFilePath);
    void start();
    std::vector<Trainer*>& getListOfTrainers();
    int getNumOfTrainers() const;
    Trainer* getTrainer(int tid);
	const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    int getActionsLogSize();
    BaseAction* getActionsLogIndex(int i);
    std::vector<Workout>& getWorkoutOptions();
    std::string getWorkoutName(int workoutId);
    void pushActionsLog(BaseAction* action);
    void close();
    int getNumOfClients();
    void setNumOfClients(int increment);

private:
    int numOfClients;
    bool open;
    std::vector<Trainer*> trainers;
    std::vector<Workout> workout_options;
    std::vector<BaseAction*> actionsLog;
};

#endif