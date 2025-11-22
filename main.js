class SingleResult {
    constructor(title, pageid) {
        this.title = title
        this.pageid = pageid
    }
}
class SearchResults {
    query_start = 'https://ru.wikipedia.org/w/index.php?curid='

    constructor(articles) {
        this.number = articles.length
        this.articles = []
        this.query = ''
        articles.forEach((a) => this.articles.push(new SingleResult(a.title, a.pageid)))
    }

    list(writeFunc) {
        this.articles.forEach((a, i) => writeFunc((i + 1) + ') ' + a.title + '\n'))
    }

    select(index) {
        this.query = this.query_start + this.articles[index - 1].pageid
    }
}

class WikiSearcher {
    query_start = 'https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&origin=*&srsearch='

    constructor(search_query) {
        this.query = this.query_start + encodeURI(search_query)
    }

    async search() {
        try {
            const response = await fetch(this.query)
            if (!response.ok) {
                throw new Error('HTTP Error (status=' + response.status + ')')
            }
            this.success = true
            return new SearchResults((await response.json()).query.search)
        }
        catch (err) {
            this.success = false
            return err
        }
    }
}

function writeOutput(what) {
    document.body.innerHTML += String(what).replaceAll('\n', '<br>')
}

function waitInput() {
    return new Promise((resolve) => {
        const input = document.createElement('input')
        input.type = 'text'
        input.style.display = 'inline'
        input.style.border = input.style.outline = 'none'
        input.style.width = '50%'

        document.body.appendChild(input);
        input.focus();
        input.onkeypress = (e) => {
            if (e.key == 'Enter' && input.value != '') {
                const value = input.value;
                document.body.removeChild(input);
                writeOutput(value + '\n');
                resolve(value);
            }
        };
    })
}

async function main() {
    writeOutput('Введите поисковый запрос: ')
    var input = await waitInput()
    searcher = new WikiSearcher(input)
    writeOutput('Обращение по адресу \'' + searcher.query + '\'...\n')
    const result = await searcher.search()
    if (!searcher.success) {
        writeOutput('Произошла ошибка: ' + result.toString())
        return
    }
    if (result.number > 0) {
        writeOutput('\nРезультаты поиска:\n')
        result.list(writeOutput)
        while (true) {
            writeOutput('\nВведите номер статьи, которую желаете открыть: ')
            input = Number(await waitInput())
            if (input > 0 && input <= result.number)
                break
            writeOutput('Такого номера нет!')
        }
        result.select(input)
        writeOutput('Перенаправление на \'' + result.query + '\'...\n')
        window.open(result.query)
    }
    else {
        writeOutput('Поиск не дал результатов.')
    }
}

main()