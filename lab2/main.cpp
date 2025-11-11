#define _MAIN_FILE_
#include <chrono>
#include "DatasetCSV.h"
#include "DatasetXML.h"



int main()
{
    Dataset* dataset {};
    wchar_t buffer[256], * start, * caret;
    std::wifstream in;

    std::wcout << L"    ООП ЛР-2\nАвтор: Н.А. Чехонадских\n\n";

    for (;;)
    {
        in.close();
        std::wcout << L"Введите путь к файлу, либо 0 для выхода из программы: ";
        std::wcin.getline(buffer, 256);
        
        // Select filename (command) from buffer
        for (start = buffer; *start && *start == L' '; ++start);
        if (*start == L'\"')
            for (caret = ++start; *caret && *caret != L'\"'; ++caret);
        else
            for (caret = start; *caret && *caret != L' '; ++caret);
        *caret = L'\0';

        if (wcscmp(start, L"0"))
            break;

        if (caret - start >= 4)
        {
            const auto lowercase = [](wchar_t* c)
                {
                    if (L'A' <= *c && *c <= L'Z')
                        *c += L'a' - L'A';
                };
            lowercase(caret - 1);
            lowercase(caret - 2);
            lowercase(caret - 3);
            if (wcscmp((caret -= 4), L".csv"))
                (dataset = new DatasetCSV {})->input((in.open(start), in));
            else if (wcscmp(caret, L".xml"))
                (dataset = new DatasetXML {})->input((in.open(start), in));

            if (dataset)
            {
                dataset->print_dups(std::wcout);
                dataset->count_house(std::wcout);
                delete dataset;
                dataset = {};
            }
        }
        std::wcout << L"Был введён недопустимый путь к файлу.\n\n";
    }

    return 0;
}
