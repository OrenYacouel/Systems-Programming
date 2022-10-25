#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
#include "Customer.h"
#include "Studio.h"
enum  ActionStatus{
    COMPLETED, ERROR
};
//Forward declaration
class Studio;
class BaseAction{
public:
    BaseAction();
    virtual ~BaseAction();
    ActionStatus getStatus() const;
    virtual void act(Studio& studio)=0;
    virtual std::string toString() const=0;
    void setStatus(std::string _status);
    virtual BaseAction* clone() =0;
protected:
    void complete();
    void error(std::string _errorMsg);
    std::string getErrorMsg() const;
private:
    std::string errorMsg;
    ActionStatus status;
};


class OpenTrainer : public BaseAction {
public:
    OpenTrainer(int id, std::vector<Customer *> &customersList);
    ~OpenTrainer();
    OpenTrainer(const OpenTrainer & other);//copy constructor changed to reference
//    OpenTrainer operator=(const OpenTrainer& other);
    void act(Studio &studio);
    OpenTrainer* clone();
    std::string toString() const;
private:
    const int trainerId;
	std::vector<Customer *> customers;
};


class Order : public BaseAction {
public:
    Order(int id);
    Order(const Order & other); // changed to reference
    ~Order();
    void act(Studio &studio);
    Order* clone();
    int getTrainerId() const;
    std::string toString() const;
private:
    const int trainerId;
};


class MoveCustomer : public BaseAction {
public:
    MoveCustomer(int src, int dst, int customerId);
    ~MoveCustomer();
    MoveCustomer(const MoveCustomer & other);
    void act(Studio &studio);
    std::string toString() const;
    MoveCustomer* clone();
private:
    const int srcTrainer;
    const int dstTrainer;
    const int id;
};


class Close : public BaseAction {
public:
    Close(int id);
    ~Close();
    Close(const Close & other);//changed to reference
    void act(Studio &studio);
    std::string toString() const;
    Close* clone();
private:
    const int trainerId;
};


class CloseAll : public BaseAction {
public:
    CloseAll();
    CloseAll(const CloseAll & other);
    ~CloseAll();
    void act(Studio &studio);
    std::string toString() const;
    CloseAll* clone();
private:
};


class PrintWorkoutOptions : public BaseAction {
public:
    PrintWorkoutOptions();
    PrintWorkoutOptions(const PrintWorkoutOptions & other);
    ~PrintWorkoutOptions();
    void act(Studio &studio);
    std::string toString() const;
    PrintWorkoutOptions* clone();
private:
};


class PrintTrainerStatus : public BaseAction {
public:
    PrintTrainerStatus(int id);
    ~PrintTrainerStatus();
    PrintTrainerStatus(const PrintTrainerStatus & other);
    void act(Studio &studio);
    int getTrainerId() const;
    std::string toString() const;
    PrintTrainerStatus* clone();
private:
    const int trainerId;
};


class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();
    PrintActionsLog(const PrintActionsLog & other);
    ~PrintActionsLog();
    void act(Studio &studio);
    std::string toString() const;
    PrintActionsLog* clone();
private:
};


class BackupStudio : public BaseAction {
public:
    BackupStudio();
    BackupStudio(const BackupStudio & other);
    ~BackupStudio();
    void act(Studio &studio);
    std::string toString() const;
    BackupStudio* clone();
private:
};


class RestoreStudio : public BaseAction {
public:
    RestoreStudio();
    RestoreStudio(const RestoreStudio & other);
    ~RestoreStudio();
    void act(Studio &studio);
    std::string toString() const;
    RestoreStudio* clone();

};

#endif