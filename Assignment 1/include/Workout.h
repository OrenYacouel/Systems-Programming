#ifndef WORKOUT_H_
#define WORKOUT_H_

#include <string>

enum WorkoutType{
    ANAEROBIC, MIXED, CARDIO
};

class Workout{
public:
    Workout(int w_id, std::string w_name, int w_price, WorkoutType w_type);
    Workout(const Workout & other);
    Workout& operator=(const Workout& other);
    std::string getName() const;
    int getPrice() const;
    WorkoutType getType() const;
    int getId() const;
    bool operator >(Workout const &obj);
private:
    const int price;
    const WorkoutType type;
    const int id;
    const std::string name;
};


#endif