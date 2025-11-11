#pragma once
#include <iostream>
#include <fstream>
#include <unordered_map>
#include <unordered_set>
#include <string>




struct House
{
    const std::wstring* street;
    unsigned int number;


    bool operator==(const House& other)
    {
        return street == other.street && number == other.number;
    }


    struct Hasher
    {
        size_t operator()(const House& house)
        {
            return reinterpret_cast<size_t>(house.street) ^ (static_cast<size_t>(house.number) << 24);
        }
    };
};



struct City
{
    std::wstring name;
    std::unordered_set<std::wstring> streets;
    std::unordered_set<House, House::Hasher> houses[6]; // grouped by floors: 6+, 1, 2, 3, 4, 5
};



class Dataset
{
public:

    struct DuplicateRecord
    {
        const std::wstring* city;
        const House* house;
        unsigned int floor, repetitions;


        bool operator==(const DuplicateRecord& other)
        {
            return city == other.city && house == other.house && floor == other.floor;
        }


        struct Hasher
        {
            size_t operator()(const DuplicateRecord& record)
            {
                return reinterpret_cast<size_t>(record.city) ^ reinterpret_cast<size_t>(record.house) ^ (size_t(record.floor) << 31);
            }
        };
    };


public:

    std::unordered_set<DuplicateRecord, DuplicateRecord::Hasher> duplicates;

    // Key=city name, value=City
    std::unordered_map<std::wstring, City> cities;


public:

    Dataset() = default;
    ~Dataset() = default;

    virtual void input(std::wifstream& file) = 0;

    void insert_data(const wchar_t* city, const wchar_t* street, unsigned int house, unsigned int floor);

    void print_dups(std::wostream& out);
    
    void count_house(std::wostream& out);
};
