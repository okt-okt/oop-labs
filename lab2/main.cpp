#define _MAIN_FILE_
#include <chrono>
#include "DatasetCSV.h"
#include "DatasetXML.h"
#include <codecvt>



int main()
{
    Dataset* dataset {};
    wchar_t buffer[256], * start, * caret;
    std::chrono::high_resolution_clock::time_point times[4];
    std::wifstream in;
    in.imbue(std::locale(std::locale::empty(), new std::codecvt_utf8<wchar_t>));
    std::setlocale(LC_ALL, "ru");

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

        if (!wcscmp(start, L"0"))
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
            if (in.open(start), in.is_open())
            {
                if (!wcscmp((caret -= 4), L".csv"))
                    dataset = new DatasetCSV {};
                else if (!wcscmp(caret, L".xml"))
                    dataset = new DatasetXML {};
            }
            if (dataset)
            {
                times[0] = std::chrono::high_resolution_clock::now();
                dataset->input(in);
                times[1] = std::chrono::high_resolution_clock::now();
                dataset->print_dups(std::wcout);
                times[2] = std::chrono::high_resolution_clock::now();
                dataset->count_house(std::wcout);
                times[3] = std::chrono::high_resolution_clock::now();
                std::wcout << L"Время выполнения:\n" <<
                    L"   ввода данных - " << std::chrono::duration_cast<std::chrono::milliseconds>(times[1] - times[0]).count() << L" мс\n" <<
                    L"   печати дубликатов - " << std::chrono::duration_cast<std::chrono::milliseconds>(times[2] - times[1]).count() << L" мс\n" <<
                    L"   поиска и печати домов одной этажности в городах - " << std::chrono::duration_cast<std::chrono::milliseconds>(times[3] - times[2]).count() << L" мс\n" <<
                    L"   (ИТОГО) всей программы - " << std::chrono::duration_cast<std::chrono::milliseconds>(times[3] - times[0]).count() << L" мс\n\n";
                delete dataset;
                dataset = {};
                continue;
            }
        }
        std::wcout << L"Был введён недопустимый путь к файлу.\n\n";
    }

    return 0;
}
