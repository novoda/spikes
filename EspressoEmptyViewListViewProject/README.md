Espresso Emtpy View ListView
---

This is a spike to see if Espresso works when you have an empty view while items load in a ListView (i.e. it should wait before running the tests until the data has loaded and then start running the tests) using an AsyncTask. The test also involves using different Adapters (one for when there are no items and one for when there are items).

The result is Espresso waits and the Empty View is displayed correctly and once the data is loaded (the AsyncTask finishes), Espresso will start running the tests as expected.