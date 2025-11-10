#pragma once
#include "Dataset.h"



class DatasetCSV : public Dataset
{
public:

    virtual void input(std::ifstream& file);
};
