#pragma once
#include "Dataset.h"



class DatasetXML : public Dataset
{
public:

    virtual void input(std::ifstream& file);
};
