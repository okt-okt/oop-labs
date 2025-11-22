#include "DatasetCSV.h"



void DatasetCSV::input(std::wifstream& file)
{
    wchar_t buffer[1000], * city, * address, * caret, floor;
    file.getline(buffer, 1000);
    while (!(file.eof()))
    {
        file.getline(buffer, 1000);
        if (!(*(caret = buffer))) // empty line
            continue;

        // city
        while (*caret != L'\"')
            ++caret;
        city = ++caret;
        while (*caret != L'\"')
            ++caret;
        *caret = L'\0';

        // address
        while (*caret != L'\"')
            ++caret;
        address = ++caret;
        while (*caret != L'\"')
            ++caret;
        while (!(L'0' <= *caret && *caret <= L'9'))
            ++caret;
        while (L'0' <= *caret && *caret <= L'9')
            ++caret;
        *caret = L'\0';

        // floor
        while (!(L'0' <= *caret && *caret <= L'9'))
            ++caret;
        floor = wcstoul(caret, NULL, 10);

        this->insert_data(city, address, floor);
    }
}
