#include "DatasetCSV.h"



void DatasetCSV::input(std::wifstream& file)
{
    unsigned int number[2];
    wchar_t buffer[1000], * start[3], * caret, step;
    file.getline(buffer, 1000);
    while (!(file.eof()))
    {
        file.getline(buffer, 1000);
        step = 0;
        caret = buffer;
        if (!(*(caret = buffer)))
            continue;
        while (step < 2)
        {
            while (*caret != L'\"')
                ++caret;
            start[step] = ++caret;
            while (*caret != L'\"')
                ++caret;
            *caret = L'\0';
            ++step;
        }
        while (step < 4)
        {
            while (!(L'0' <= *caret && *caret <= L'9'))
                ++caret;
            number[step - 2] = wcstoul(caret, &caret, 10);
            ++step;
        }
        if (!(file.eof()))
            this->insert_data(start[0], start[1], number[0], number[1]);
    }
}
