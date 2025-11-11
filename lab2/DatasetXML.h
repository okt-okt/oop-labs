#pragma once
#include "Dataset.h"



class DatasetXML : public Dataset
{
public:

    virtual void input(std::wifstream& file);

    DatasetXML() = default;
    ~DatasetXML() = default;
};
