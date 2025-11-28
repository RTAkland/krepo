Local development notes

Make sure you have installed `Azure Functions Core Tools` and jdk with `JAVA_HOME` set.
If you have different jdk installed, open the language worker config file, set the jdk path
manually, on windows, the default install path is `C:\Program Files\Microsoft\Azure Functions Core Tools\workers\java\worker.config.json`,
set `defaultExecutablePath` to the correct java home path.

Following this page to learn how to install Azure Functions Core Tools

https://learn.microsoft.com/en-us/azure/azure-functions/functions-run-local?tabs=windows%2Cisolated-process%2Cnode-v4%2Cpython-v2%2Chttp-trigger%2Ccontainer-apps&pivots=programming-language-java
