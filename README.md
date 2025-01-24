# Vocabulary

## Expand your vocabulary.

### Getting data

You can get words in Romanian from [dexonline](https://dexonline.ro/)

Download the SQL DB (1.2 GB, 269 MB compressed) from [dexonline wiki](https://wiki.dexonline.ro/wiki/Informa%C8%9Bii#Desc%C4%83rcare)

Since the SQL file is too big and most of the tables are unnecessary, we need to split it and only get the data we need.

We only require the tables `Entry`, `TreeEntry`, `Tree` and `Meaning`, so we'll use `sed` to extract only the parts we require.

We append `;(n + 1)q` at the end so that `sed` stops processing the file when it gets to that line.

```sh
sed -n '306,348p;349q'    $SQL_FILE > ~/db-entry.sql
sed -n '1654,1730p;1731q' $SQL_FILE > ~/db-meaning.sql
sed -n '2416,2453p;2454q' $SQL_FILE > ~/db-tree-entry.sql
sed -n '2457,2495p;2496q' $SQL_FILE > ~/db-tree.sql
```
Replace `SQL_FILE` with the path to the downloaded SQL file.

Now we need to source the `.sql` files in a database.

**Make sure to create a database first (e.g., `CREATE DATABASE dictionary;`)**

To source the files, type `source $PATH_TO_SQL` (absolute path) in the SQL console for each file created above.

Now the app requires CSV data, so we need to export CSV data from SQL then transfer it to the phone.

First create a directory at `/var/lib/mysql-files`

Then run this code in the SQL console

```sql
select Entry.description, Meaning.internalRep
from Entry
inner join TreeEntry on TreeEntry.entryId = Entry.id
inner join Tree on Tree.id = TreeEntry.treeId
inner join Meaning on Meaning.treeId = Tree.id
where Meaning.type = 0 and Meaning.internalRep <> ''
order by RAND()
limit 5000
into outfile '/var/lib/mysql-files/output.csv'
fields terminated by ',' enclosed by '"' lines terminated by '\n';
```

#### Explanation
- Select the `Entry`'s description (name) and `meaning`'s internalRep (definition)
- Join with table `TreeEntry` (`TreeEntry` links `Tree` with `Entry`)
- Join with table `Tree`
- Join with table `Meaning`
- `Meaning.type=0` means the meaning is an actual definition, and `Meaning.internalRep <> ''` makes sure the definition isn't empty
- `order by RAND()` randomizes the entries
- `limit 5000` limits the number of exported entries to 5000
- Export the CSV Data
