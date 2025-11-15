#pragma once
#include <iostream>
#include <fstream>
#include <unordered_map>
#include <string>




struct House
{
    unsigned int floor, repetetions = 0;
};



struct City
{
    // Key=address, value=House
    std::unordered_map<std::wstring, House> houses;
    unsigned int house_stats[5] {};
};



class Dataset
{
public:
    // Key=city name, value=City
    std::unordered_map<std::wstring, City> cities;


public:

    Dataset() = default;
    ~Dataset() = default;

    virtual void input(std::wifstream& file) = 0;

    void insert_data(const wchar_t* city, const wchar_t* address, unsigned int floor);

    void print_dups(std::wostream& out);
    
    void count_house(std::wostream& out);
};
