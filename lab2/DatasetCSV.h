#pragma once
#include "Dataset.h"



class DatasetCSV : public Dataset
{
public:

    virtual void input(std::wifstream& file);

    DatasetCSV() = default;
    ~DatasetCSV() = default;
};
