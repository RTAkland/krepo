Local development notes

Make sure you have installed `Azure Functions Core Tools` and jdk with `JAVA_HOME` set.
If you have different jdk installed open the language worker config file set the jdk path
manually on windows the default install path is
`C:\Program Files\Microsoft\Azure Functions Core Tools\workers\java\worker.config.json`
set `defaultExecutablePath` to the correct java home path.

Following this page to learn how to install Azure Functions Core Tools

https://learn.microsoft.com/en-us/azure/azure-functions/functions-run-local?tabs=windows%2Cisolated-process%2Cnode-v4%2Cpython-v2%2Chttp-trigger%2Ccontainer-apps&pivots=programming-language-java

Envs description

1. `FUNCTIONS_WORKER_RUNTIME` ~must be `java`
2. `AzureFunctionsJobHost__logging__console__isEnabled` ~must be `true`
3. `AzureFunctionsJobHost__logging__console__logLevel__default` ~must be `Debug`
4. `S3API` s3 api host ~required
5. `S3_ACCESS_KEY` s3 access key ~required
6. `S3_SECRET_KEY` s3 secret key ~required
7. `S3_BUCKET` s3 bucket name ~required
8. `S3_REGION` s3 region ~required
9. `AZURE_CLIENT_ID` azure oauth app client id ~required when azure sign in is enabled
10. `AZURE_CLIENT_SECRET` azure oauth client secret ~required when azure sign in is enabled
11. `AZURE_REDIRECT_URL` azure oauth redirect uri ~required when azure sign in is enabled
12. `CF_ACCOUNT_ID` cloudflare account id ~optional
13. `CF_EMAIL` cloudflare email ~not used
14. `CF_KV_TOKEN` cloudflare kv token ~optional
15. `CF_KV_NAMESPACE_ID` cloudflare kv namespace id ~optional
16. `AUTH_STORAGE_TYPE` when enable azure sign in, store `state`, must be `CF_KV` ~required
17. `REDIS_URI` redis uri ~optional
18. `FRONTEND_URL` redirect when access repository name
19. `JWT_SECRET` json web token secret ~required

