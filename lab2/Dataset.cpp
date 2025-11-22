#include "Dataset.h"



void Dataset::insert_data(const wchar_t* city, const wchar_t* address, unsigned int floor)
{
    auto& c = cities[city];
    c.house_stats[floor - 1]++;
    auto& h = c.houses[address];
    h.floor = floor;
    h.repetetions++;
}



void Dataset::print_dups(std::wostream& out)
{
    bool first = true;
    for (auto c = cities.begin(); c != cities.end(); ++c)
        for (auto a = c->second.houses.begin(); a != c->second.houses.end(); ++a)
            if (a->second.repetetions > 1)
            {
                if (first)
                {
                    out << L"Во время обработки файла обнаружены следующие дублирующиеся записи:\n";
                    first = false;
                }
                //
            }
    if (first)
        out << L"Дубликатов не было обнаружено.\n\n";
}



void Dataset::count_house(std::wostream& out)
{
    for (auto c = cities.begin(); c != cities.end(); ++c)
    {
        out << L"В городе \"" << c->first << L"\":\n";
        for (unsigned int i = 0; i < 5; ++i)
            if (c->second.house_stats[i])
                out << L"   " << (i + 1) << L"-этажных домов: " << c->second.house_stats[i] << L'\n';
        out << L'\n';
    }
}
