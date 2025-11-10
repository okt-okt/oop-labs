#define _MAIN_FILE_
#include <chrono>
#include "DatasetCSV.h"
#include "DatasetXML.h"



int main()
{
    char buffer[256], * start;

    for (;;)
    {
        std::cin.getline(buffer, 256);
        
        // Select filename (command) from buffer by pointing start to the first symbol and place '\0' after the last symbol.

        // Exit on exit command ("0").

        // Analyze filename.
    }

    return 0;
}
