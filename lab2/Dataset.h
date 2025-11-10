#pragma once
#include <iostream>
#include <fstream>
#include <set>
#include <hash_map>
#include <string>



struct House
{
    unsigned int number, floor;
};


struct Street
{
    std::string name;
    size_t hash;
    std::set<House> houses;
};


struct City
{
    std::string name;
    size_t hash;
    std::set<Street> streets;
};



class Dataset
{
public:

    struct DuplicateRecord
    {
    public:

        City* city;
        Street* street;
        House* house;
        size_t hash;
        unsigned repeatitions;


    public:

        bool operator<(const DuplicateRecord& b)
        {
            return hash < b.hash;
        }
    };


public:

    std::set<DuplicateRecord> duplicates;

    std::set<City> cities;


public:

    virtual void input(std::ifstream& file) = 0;

    void insert_data(const char* city, const char* street, unsigned int house, unsigned int floor);

    void print_dups(std::ostream& out);
    
    void count_house(std::ostream& out);
};
