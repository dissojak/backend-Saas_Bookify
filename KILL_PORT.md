kill -9 $(lsof -t -i :8088)

windows :
netstat -ano | findstr :8088 ##############
taskkill /F /PID (Get-NetTCPConnection -LocalPort 8088).OwningProcess


start mysql without error 
mysqld --skip-grant-tables

```
mysqld --skip-grant-tables
```
