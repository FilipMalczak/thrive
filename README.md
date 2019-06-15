# Thrive

Thrive is a highly opinionated infrastructure framework for Spring Boot/Cloud 
based microservices systems.

It started off as part of my private project `knowingly`. That's the reason for missing commits before the initial one.

It provides main components for a system, where you just write a Spring Boot
app, annotate it in build script (only Gradle supported for now) with capabilites
like "swagger", "API" (for REST API) or "WS" (for websockets), run inside docker
compose project (together with aforementioned components) and it automagically
registers with discovery service, exposes its capabilites through gateway service
and is merged into swagger docs that cover all the services in the project.

> TL;DR: You write service, Swagger, Gateway, Admin, etc are handled for you.

Secondary goal of the project is to sanitize commonly used services (like DBMS, queues, etc) for `docker-compose` usage. Ideally, you could just add something like
`mongoCluster secondaryReplicas: 5` to your build.gradle and all the services that
want to use MongoDB would autoconfigure for the cluster that would be automatically
started from generated `docker-compose.yml`.` 

General idea is that with modern containerization and orchestration tools, you seldom
change hostnames and ports, as they are just container names and default service ports. 