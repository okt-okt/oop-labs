#include "Dataset.h"



void Dataset::insert_data(const wchar_t* city, const wchar_t* street, unsigned int house, unsigned int floor)
{
    auto c = cities.find(city);
    const std::wstring* s = &*(c->second.streets.insert(street).first);
    auto h = c->second.houses[floor > 5 || !floor ? 0 : floor].insert({ s, house });
    if (!(h.second)) // found duplicate
        ++const_cast<unsigned int&>(duplicates.insert({ &(c->first), &*(h.first), floor, 1 }).first->repetitions);
}



void Dataset::print_dups(std::wostream& out)
{
    if (duplicates.size())
    {
        out << L"¬ наборе данных были обнаружены следующие дубликаты:\n";
        for (auto i = duplicates.begin(); i != duplicates.end(); ++i)
            out << L"   " << i->repetitions << L" x [city=\"" << *(i->city) << L"\", street=\"" << *(i->house->street) << L"\", house=" << i->house->number << L", floor=" << i->floor << L"]\n";
        out << L'\n';
    }
    else
        out << L"ƒубликатов не было обнаружено.\n\n";
}



void Dataset::count_house(std::wostream& out)
{
    for (auto c = cities.begin(); c != cities.end(); ++c)
    {
        out << L"¬ городе \"" << c->first << L"\":\n";
        for (unsigned int i = 1; i < 6; ++i)
            out << L"   " << i << L"-этажных домов: " << c->second.houses[i].size() << L'\n';
        if (c->second.houses[0].size())
            out << L"   домов иной этажности: " << c->second.houses[0].size() << L'\n';
        out << L'\n';
    }
}
